package journey;

import journey.controller.ReadCSV;
import journey.data.Database;
import journey.gui.MainWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;

/**
 * Default entry point class.

 * @author seng202 teaching team.
 */
public class App {
    private static final Logger log = LogManager.getLogger();

    /**
     * Entry point which runs the javaFX application.
     * Also shows off some different logging levels.

     * @param args program arguments from command line.
     */
    public static void main(String[] args) throws FileNotFoundException {
        // Example of logs:
        log.info("App started");
        MainWindow.main(args);
    }
}
