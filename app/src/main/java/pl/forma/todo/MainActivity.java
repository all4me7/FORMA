package pl.forma.todo;
import android.app.*;import android.os.*;import android.webkit.*;import android.content.*;import android.content.pm.PackageManager;import android.net.Uri;import org.json.*;
public class MainActivity extends Activity {
 WebView w;
 private android.window.OnBackInvokedCallback backCallback;
 @Override public void onBackPressed(){handleBack();}
 private void handleBack(){if(w==null){super.onBackPressed();return;}w.evaluateJavascript("typeof requestBack === 'function' && requestBack()",result->{if(!"true".equals(result))finish();});}
 boolean fullscreen=false;
 boolean detailScreen=false;
 private void applyFullscreen(){
  if(w==null)return;
  android.view.Window window=getWindow();
  window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
  window.setStatusBarColor(0xfff3f1ec);
  window.setNavigationBarColor(0xfff3f1ec);
  if(Build.VERSION.SDK_INT>=29)window.setNavigationBarContrastEnforced(false);
  if(Build.VERSION.SDK_INT>=30){
   android.view.WindowInsetsController c=window.getInsetsController();
   if(c!=null){
    int light=android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS | android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS;
    c.setSystemBarsAppearance(light,light);
    c.setSystemBarsBehavior(android.view.WindowInsetsController.BEHAVIOR_DEFAULT);
    c.show(android.view.WindowInsets.Type.systemBars());
   }
  }else{
   int flags=android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
   window.getDecorView().setSystemUiVisibility(flags);
  }
  w.requestApplyInsets();
 }
 @Override public void onWindowFocusChanged(boolean focused){super.onWindowFocusChanged(focused);if(focused)applyFullscreen();}
 @Override protected void onResume(){super.onResume();applyFullscreen();if(exactAllowed())Boot.restore(this);if(w!=null)w.evaluateJavascript("if(typeof updateAlarmStatus==='function')updateAlarmStatus()",null);}
 ValueCallback<Uri[]> chooser; String backup;
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==10&&chooser!=null){chooser.onReceiveValue(result==RESULT_OK&&data!=null?new Uri[]{data.getData()}:null);chooser=null;}if(request==11){if(result==RESULT_OK&&data!=null&&backup!=null)try(java.io.OutputStream out=getContentResolver().openOutputStream(data.getData(),"wt")){out.write(backup.getBytes(java.nio.charset.StandardCharsets.UTF_8));android.widget.Toast.makeText(this,"Zapisano kopię",0).show();}catch(Exception e){android.widget.Toast.makeText(this,"Błąd zapisu kopii",1).show();}backup=null;}}

 public void onCreate(Bundle b){super.onCreate(b);Reminder.channel(this);w=new WebView(this);w.setBackgroundColor(0xfff3f1ec);if(Build.VERSION.SDK_INT>=30)w.setOnApplyWindowInsetsListener((v,i)->{android.graphics.Insets s=i.getInsets(android.view.WindowInsets.Type.systemBars() | android.view.WindowInsets.Type.displayCutout());android.graphics.Insets ime=i.getInsets(android.view.WindowInsets.Type.ime());v.setPadding(s.left,s.top,s.right,Math.max(s.bottom,ime.bottom));return i;});fullscreen=false;if(Build.VERSION.SDK_INT>=30)getWindow().setDecorFitsSystemWindows(false);if(Build.VERSION.SDK_INT>=28){android.view.WindowManager.LayoutParams lp=getWindow().getAttributes();lp.layoutInDisplayCutoutMode=android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;getWindow().setAttributes(lp);}getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);setContentView(w);if(Build.VERSION.SDK_INT>=33){backCallback=this::handleBack;getOnBackInvokedDispatcher().registerOnBackInvokedCallback(android.window.OnBackInvokedDispatcher.PRIORITY_DEFAULT,backCallback);}applyFullscreen();w.getSettings().setJavaScriptEnabled(true);w.getSettings().setDomStorageEnabled(true);w.setWebChromeClient(new WebChromeClient(){@Override public boolean onJsAlert(WebView v,String url,String message,JsResult result){new AlertDialog.Builder(MainActivity.this).setMessage(message).setPositiveButton("OK",(d,x)->result.confirm()).setOnCancelListener(d->result.cancel()).show();return true;} @Override public boolean onJsConfirm(WebView v,String url,String message,JsResult result){new AlertDialog.Builder(MainActivity.this).setMessage(message).setPositiveButton("Tak",(d,x)->result.confirm()).setNegativeButton("Anuluj",(d,x)->result.cancel()).setOnCancelListener(d->result.cancel()).show();return true;}@Override public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams params){if(chooser!=null)chooser.onReceiveValue(null);chooser=cb;try{startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*"),10);}catch(Exception e){chooser.onReceiveValue(null);chooser=null;}return true;}});w.addJavascriptInterface(new Bridge(),"Native");w.loadUrl("file:///android_asset/index.html");}
 private boolean exactAllowed(){return Build.VERSION.SDK_INT<31||getSystemService(AlarmManager.class).canScheduleExactAlarms();}
 private boolean notificationsAllowed(){return Build.VERSION.SDK_INT<33||checkSelfPermission("android.permission.POST_NOTIFICATIONS")==PackageManager.PERMISSION_GRANTED;}
 private void requestReminderPermissions(){if(!notificationsAllowed()){requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},1);return;}if(!exactAllowed())try{startActivity(new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,Uri.parse("package:"+getPackageName())));}catch(Exception e){android.widget.Toast.makeText(this,"Włącz dokładne alarmy w ustawieniach Androida",1).show();}}
 @Override public void onRequestPermissionsResult(int request,String[] permissions,int[] results){super.onRequestPermissionsResult(request,permissions,results);if(request==1&&notificationsAllowed())requestReminderPermissions();}
 class Bridge {
 @JavascriptInterface public void haptic(){runOnUiThread(()->w.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK));}
 @JavascriptInterface public void detailScreen(boolean enabled){runOnUiThread(()->{detailScreen=enabled;applyFullscreen();});}
 @JavascriptInterface public void fullscreen(boolean enabled){runOnUiThread(()->{fullscreen=false;applyFullscreen();});}
 @JavascriptInterface public void keyboard(){runOnUiThread(()->{w.requestFocus();w.postDelayed(()->((android.view.inputmethod.InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(w,android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT),100);});}
 @JavascriptInterface public void exportBackup(String json){runOnUiThread(()->{backup=json;startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("application/json").putExtra(Intent.EXTRA_TITLE,"FORMA-backup.json"),11);});}

 @JavascriptInterface public String load(){return getSharedPreferences("data",0).getString("state","");}
 @JavascriptInterface public void save(String s){getSharedPreferences("data",0).edit().putString("state",s).apply();}
 @JavascriptInterface public String alarmStatus(){NotificationManager n=getSystemService(NotificationManager.class);return "Dokładne alarmy: "+(MainActivity.this.exactAllowed()?"OK":"brak zgody")+" | Powiadomienia: "+(notificationsAllowed()&&n.areNotificationsEnabled()&&Reminder.channel(MainActivity.this).getImportance()!=0?"OK":"zablokowane");} @JavascriptInterface public String testAlarm(){if(!MainActivity.this.exactAllowed()||!notificationsAllowed()){runOnUiThread(()->requestReminderPermissions());return "Przyznaj uprawnienia i ponów test.";}return Reminder.set(MainActivity.this,"test-forma","Test przypomnienia FORMA",System.currentTimeMillis()+15000)?"Test zaplanowany za 15 sekund. Zablokuj ekran.":"Nie udało się zaplanować testu.";} @JavascriptInterface public boolean exactAllowed(){return MainActivity.this.exactAllowed();}
 @JavascriptInterface public void permissions(){runOnUiThread(()->requestReminderPermissions());}
 @JavascriptInterface public String schedule(String id,String title,long time){Reminder.cancel(MainActivity.this,id);if(time<=System.currentTimeMillis())return "Termin przypomnienia minął";boolean scheduled=Reminder.set(MainActivity.this,id,title,time);if(!MainActivity.this.exactAllowed()||!notificationsAllowed()){runOnUiThread(()->requestReminderPermissions());return "Przypomnienie zapisane. Wymaga zgody na dokładne alarmy i powiadomienia.";}return scheduled?"Zapisano przypomnienie":"Błąd planowania dokładnego alarmu";}
 @JavascriptInterface public void cancel(String id){Reminder.cancel(MainActivity.this,id);}
 }
}
