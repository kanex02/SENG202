package journey.data;

/**
 * Simple class wrapping string of address for use with Geocoding.

 * @author Daniel Neal
 */
public class GeoCodeResult {
    private String address;

    /**
     * Creates a new geocode result with the given address.

     * @param address address of location.
     */
    public GeoCodeResult(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
