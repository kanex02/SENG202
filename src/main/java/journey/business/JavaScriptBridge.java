package journey.business;

import journey.controller.MapController;

/**
 * Simple example class showing the ability to 'bridge' from javascript to java
 * The functions within can be called from our javascript in the map view when we set an object of this class
 * as a member of the javascript
 * Note: This is a very basic example you can use any java code, though you may need to be careful when working
 * with objects

 * @author Morgan English
 */
public class JavaScriptBridge {

    private final GetStationInterface getStationInterface;
    private final GetLatLongInterface getLatLongInterface;
    private final ChangeLatLongInterface changeLatLongInterface;
    private final AddToRouteInterface addToRouteInterface;
    private final EditWaypointInterface editWaypointInterface;
    private final InsertWaypointInterface insertWaypointInterface;

    /**
     * Creates a javascript bridge object with a 'callback' lambda function for displaying the station on the map after creation

     * @param getStationLambda indicate a station to get from database by id, return true if its found and used
     */
    public JavaScriptBridge(GetStationInterface getStationLambda,
                            GetLatLongInterface getLatLongLambda,
                            ChangeLatLongInterface changeLatLongLambda,
                            AddToRouteInterface addToRouteLambda,
                            EditWaypointInterface editWaypointLambda,
                            InsertWaypointInterface insertWaypointLambda) {
        getStationInterface = getStationLambda;
        getLatLongInterface = getLatLongLambda;
        changeLatLongInterface = changeLatLongLambda;
        addToRouteInterface = addToRouteLambda;
        editWaypointInterface = editWaypointLambda;
        insertWaypointInterface = insertWaypointLambda;
    }

    public boolean insertWaypoint(double lat, double lng, int position) {
        return insertWaypointInterface.operation(lat, lng, position);
    }

    /**
     * Takes the id of a sale and passes this to the getSaleInterface implementation.
     * Currently, this takes the ID and adds it to a list within the {@link MapController}

     * @param id id of station
     * @return true if the underlying operation succeeded
     */
    public boolean getStationFromClick(int id) {
        return getStationInterface.operation(id);
    }

    /**
     * Currently this takes the lat and lng within the {@link MapController}

     * @param lat latitude of click
     * @param lng longitude of click
     * @return true if the underlying operation succeeded
     */
    public boolean getLatLongFromClick(double lat, double lng) {
        return getLatLongInterface.operation(lat, lng);
    }

    public boolean addToRoute(double lat, double lng) {
        return addToRouteInterface.operation(lat, lng);
    }

    public boolean changeLatLongFromClick(double lat, double lng, String label) {
        return changeLatLongInterface.operation(lat, lng, label);
    }

    public boolean editWaypoint(double lat, double lng, int position) {
        return editWaypointInterface.operation(lat, lng, position);
    }

    public void log(String text) {
        System.out.println(text);
    }
}
