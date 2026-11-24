package pl.forma.todo;
import android.content.*;import org.json.*;
public class Boot extends BroadcastReceiver {public void onReceive(Context c,Intent i){restore(c);} static void restore(Context c){for(java.util.Map.Entry<String,?> e:c.getSharedPreferences("alarms",0).getAll().entrySet())try{JSONObject j=new JSONObject((String)e.getValue());long t=j.getLong("time");if(t>System.currentTimeMillis())Reminder.set(c,e.getKey(),j.getString("title"),t);else Reminder.cancel(c,e.getKey());}catch(Exception ex){}}}
