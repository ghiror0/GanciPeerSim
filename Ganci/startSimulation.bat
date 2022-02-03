@ECHO OFF
make



::java -cp "./GanciSImulazione;peersim-lib/*" utility.MakeConfigFile 123456789
java -cp "./GanciSImulazione;peersim-lib/*"  utility.AnalysisClass


echo.
ECHO The simulation results are visible in the 'results' directory
echo.

PAUSE
make clean