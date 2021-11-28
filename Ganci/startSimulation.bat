@ECHO OFF
make


::Possibilità di far scegliere quale file di configurazione, all'interno della cartella config, far eseguire
::set /p config_file="Enter config file name:\n"
::java -cp "./GanciSimulazione;peersim-lib/*" peersim.Simulator GanciSimulazione/config/%config_file%.txt

java -cp "./GanciSimulazione;peersim-lib/*" peersim.Simulator GanciSimulazione/config/config.txt


echo.
ECHO The simulation results are visible in the 'results' directory
echo.

PAUSE
make clean