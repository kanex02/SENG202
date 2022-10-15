package journey.data;

/**
 * Simple class wrapping float of lat and lng values for use with Geolocation.

 * @author Morgan English.
 */
public class GeoLocationResult {
    private float lat;
    private float lng;

    /**
     * Create a new geolocation result with the given lat and long co-ordinates.

     * @param lat latitude of location.
     * @param lng longitude of location.
     */
    public GeoLocationResult(float lat, float lng) {
        this.lat = lat;
        this.lng = lng;
    }

    /**
     * Gets location latitude.

     * @return latitude of location.
     */
    public float getLat() {
        return lat;
    }

    /**
     * Gets location longitude.

     * @return longitude of location.
     */
    public float getLng() {
        return lng;
    }
}
