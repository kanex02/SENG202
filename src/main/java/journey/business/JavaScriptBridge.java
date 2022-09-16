package journey.business;

import journey.controller.MapController;
import journey.data.Station;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Simple example class showing the ability to 'bridge' from javascript to java
 * The functions within can be called from our javascript in the map view when we set an object of this class
 * as a member of the javascript
 * Note: This is a very basic example you can use any java code, though you may need to be careful when working
 * with objects
 * @author Morgan English
 */
public class JavaScriptBridge {

    private static final Logger log = LogManager.getLogger();
//    private AddStationInterface addStationInterface;
    private GetStationInterface getStationInterface;

    /**
     * Creates a javascript bridge object with a 'callback' lambda function for displaying the station on the map after creation
     * @param getStationLambda indicate a station to get from database by id, return true if its found and used
     */
    public JavaScriptBridge(GetStationInterface getStationLambda) {
        getStationInterface = getStationLambda;
    }

    /**
     * Creates a javascript bridge with a void 'callback' lambda function
     */
    public JavaScriptBridge() {
        // Set the default addStationInterface to do nothing (this is because other map controller rely on the functionality but don't implement the required methods)
//        addStationInterface = (Station station) -> {};
        getStationInterface = (int i) -> false;
    }


    /**
     * Takes the id of a sale and passes this to the getSaleInterface implementation
     * Currently this takes the ID and adds it to a list within the {@link MapController}
     * @param id id of station
     * @return true if the underlying operation succeeded
     */
    public boolean getStationFromClick(int id) {
        return getStationInterface.operation(id);
    }
}
