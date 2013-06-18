cd /D %~dp0
call mvn -Phtml clean
call mvn -Phtml integration-test
pause