package seng202.team4;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.gui.MainWindow;

/**
 * Default entry point class
 * @author seng202 teaching team
 */
public class App {
    private static final Logger log = LogManager.getLogger();

    /**
     * Entry point which runs the javaFX application
     * Also shows off some different logging levels
     * @param args program arguments from command line
     */
    public static void main(String[] args) {
        //SINGLE LINE COMMENT!!
        //SECOND SINGLE LINE COMMENT!!!
        System.out.println("Test");
        System.out.println("Testing");
        log.info("Hello World!");
        log.warn("This is a warning message! Use this log type to 'warn' if something is not quite right");
        log.error("An error has occurred, thanks logging for helping find it! (This is a terrible error log message, but is only an example!')");
        log.log(Level.INFO, "There are many ways to log!");

        MainWindow.main(args);
    }
}