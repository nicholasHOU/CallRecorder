@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  apkfly startup script for Windows
@rem
@rem ##########################################################################

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

set APKFLY_CACHE_NAME=.apkfly

set apkflyF=%APP_HOME%\%APKFLY_CACHE_NAME%
set apkflyPy2=%apkflyF%\apkfly.py
set apkflyPy3=%apkflyF%\3\apkfly.py

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

if exist "%apkflyF%" goto exec_apkfly

:git_clone
echo apkfly.sh not exist, exec git clone
git clone git@code.gome.inc:mobile-android-admin/apkfly.sh.git %APKFLY_CACHE_NAME%

:exec_apkfly
for /f %%i in ('python -c "import sys; print(sys.version_info.major)"') do set pythonVersion=%%i
if defined pythonVersion (
    if %pythonVersion%==2 (
        echo "apkfly2.x版本不再维护，请安装py3.x并使python命令指向python3.x版本"
        python %apkflyPy2% %CMD_LINE_ARGS%
    ) else (
        python %apkflyPy3% %CMD_LINE_ARGS%
    )
)