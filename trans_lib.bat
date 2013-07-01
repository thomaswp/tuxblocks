@echo off
set JAVA=C:\Program Files\Java\jre6\lib\rt.jar
set PY=C:\Users\Thomas\.m2\repository\com\samskivert\pythagoras\1.3.2\pythagoras-1.3.2.jar
set PLAYN=C:\Users\Thomas\.m2\repository\com\googlecode\playn\playn-core\1.7\playn-core-1.7.jar
set REACT=C:\Users\Thomas\.m2\repository\com\threerings\react\1.3.1\react-1.3.1.jar
set OOO=C:\Users\Thomas\.m2\repository\com\threerings\tripleplay\1.7\tripleplay-1.7.jar

set PY_S=C:\Users\Thomas\.m2\repository\com\samskivert\pythagoras\1.3.2\pythagoras-1.3.2-sources.jar
set PLAYN_S=C:\Users\Thomas\.m2\repository\com\googlecode\playn\playn-core\1.7\playn-core-1.7-sources.jar
set REACT_S=C:\Users\Thomas\.m2\repository\com\threerings\react\1.3.1\react-1.3.1-sources.jar
set OOO_S=C:\Users\Thomas\.m2\repository\com\threerings\tripleplay\1.7\tripleplay-1.7-sources.jar

:: call j2objc -d objc --prefixes pfx.txt -classpath "%JAVA%" %PY_S%
:: call j2objc -d objc --prefixes pfx.txt -classpath "%JAVA%:%PY%" %PLAYN_S%
:: call j2objc -d objc --prefixes pfx.txt -classpath "%JAVA%" %REACT_S%
:: call j2objc -d objc --prefixes pfx.txt -classpath "%JAVA%:%PLAYN%:%PY%:%REACT%" %OOO_S%