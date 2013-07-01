@echo off
set PLAYN=C:\Users\Thomas\.m2\repository\com\googlecode\playn\playn-core\1.7\playn-core-1.7.jar
set JAVA=C:\Program Files\Java\jre6\lib\rt.jar
set PY=C:\Users\Thomas\.m2\repository\com\samskivert\pythagoras\1.3.2\pythagoras-1.3.2.jar
set OOO=C:\Users\Thomas\.m2\repository\com\threerings\tripleplay\1.7\tripleplay-1.7.jar
set REACT=C:\Users\Thomas\.m2\repository\com\threerings\react\1.3.1\react-1.3.1.jar


FOR /F "tokens=*" %%A IN ('dir core\src\main\java /ad /b /s') DO (
	call j2objc -d objc --prefixes pfx.txt -classpath "%PLAYN%:%JAVA%:%PY%:%OOO%:%REACT%" -sourcepath core\src\main\java %%A\*
)