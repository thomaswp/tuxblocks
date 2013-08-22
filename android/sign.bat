:: Use this file to sign an android apk for distribution. Remember to create the pw.txt file with the contents being the keystore's password.
set /p PW=<pw.txt
mvn clean package -f ..\pom.xml -Pandroid -Psign -Dkeystore.password=%PW% -Dkey.password=%PW% -Dkeystore.path=%~dp0android.keystore