@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
set check=0
FOR /F "tokens=*" %%A IN ('adb devices') DO (
	if not !check! == 0 (
		echo %%A
		for /f "tokens=1" %%i in ("%%A") DO (
			adb -s %%i shell am start -n tuxkids.tuxblocks.android/tuxkids.tuxblocks.android.TuxBlocksGameActivity
		)
	)
	set check=1
)