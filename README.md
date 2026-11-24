# FORMA 1.0
Projekt Android WebView, Java + HTML/CSS/JS, bez backendu. 

## Budowanie
Rozpakuj ZIP, umieść zawartość katalogu FORMA w głównym katalogu repozytorium GitHub (łącznie z ukrytym .github). Actions → Build FORMA APK → Run workflow. Po zakończeniu pobierz artifact FORMA-APK i rozpakuj APK. Lokalnie: JDK 17, Gradle 8.9, Android SDK 35, `gradle assembleDebug`. Nie dołączono Gradle Wrapper; CI instaluje Gradle.

## Testy na telefonie
Dodaj zadanie, checklistę, komentarz i termin; zamknij aplikację i otwórz ponownie. Sprawdź kolejność tworzenia i 14-dniowy pasek. Ustaw przypomnienie na kilka minut do przodu, przyznaj uprawnienia (Ustawienia FORMA → Uprawnienia przypomnień; powtórz dla dokładnych alarmów), zapisz zadanie. Sprawdź przy zablokowanym ekranie i po restarcie. Bez dokładnych alarmów termin może być opóźniony. Wymuszone zatrzymanie blokuje alarmy. Dodaj wydarzenie do zapisywalnego kalendarza Google, sprawdź synchronizację, zmień termin, usuń wydarzenie. Dodanie/odłączenie kalendarza zapisuje powiązanie natychmiast; pozostałe zmiany przez Zapisz. Ukończenie usuwa wydarzenie; przywrócenie nie dodaje go automatycznie. Powiadomienie otwiera aplikację, bez akcji ukończenia i drzemki. Agenda pokazuje zadania z datą, najnowsze na górze. Dane lokalne w SharedPreferences. Brak testu kompilacji Android w środowisku tworzenia.


Styl kafelkow: 0, 4 lub 18 px; domyslnie 0 px.
