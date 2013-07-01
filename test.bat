@echo off
FOR /F "tokens=*" %%A IN ('dir core\src\main\java /ad /b /s') DO (
	echo %%A
)