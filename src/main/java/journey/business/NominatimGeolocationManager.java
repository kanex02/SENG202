package journey.business;

import journey.data.GeoCodeResult;
import journey.data.GeoLocationResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class to handle requesting location from Nominatim Geolocation API
 * @author Morgan English
 */
public class NominatimGeolocationManager {
    private static final Logger log = LogManager.getLogger();

    /**
     * Runs a query with the address given and finds the most applicable lat, lng co-ordinates
     * @param address address to find lat, lng for
     */
    public GeoLocationResult queryAddress(String address) {
        String logMessage = String.format("Requesting geolocation from Nominatim for address: %s", address);
        log.info(logMessage);
        address = address.replace(' ', '+');
        try {
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://nominatim.openstreetmap.org/search?q=" + address + ",+New+Zealand&format=json")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get the latitude and longitude co-ordinates
            JSONParser parser = new JSONParser();
            JSONArray results = (JSONArray)  parser.parse(response.body());
            if (results.size() == 0) {
                return new GeoLocationResult(0, 0);
            }
            JSONObject bestResult = (JSONObject) results.get(0);
            float lat = (float) Double.parseDouble((String) bestResult.get("lat"));
            float lng = (float) Double.parseDouble((String) bestResult.get("lon"));
            return new GeoLocationResult(lat, lng);
        } catch (IOException | ParseException e) {
            log.error("Error requesting geolocation", e);
        } catch (InterruptedException ie) {
            log.error("Error requesting geolocation", ie);
            Thread.currentThread().interrupt();
        } catch (Exception iie) {
            log.error("Error when loading", iie);
        }
        return new GeoLocationResult(0, 0);
    }
    /**
     * Runs a query with the lat, lng given and finds the most applicable address
     */
    public GeoCodeResult queryLatLng(double lat, double lng) {
        String logMessage = String.format("Requesting geolocation from Nominatim for lat, lng: %s, %s", lat, lng);
        log.info(logMessage);
        try{
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                URI.create("https://nominatim.openstreetmap.org/reverse?lat="+lat+"&lon="+lng+"&format=jsonv2")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get the latitude and longitude co-ordinates
            JSONParser parser = new JSONParser();
            JSONObject results = (JSONObject)  parser.parse(response.body());
            return new GeoCodeResult(((String) results.get("display_name")).replaceAll(", New Zealand / Aotearoa", ""));
        } catch (IOException | ParseException e) {
            log.error("Error requesting geolocation", e);
        } catch (InterruptedException ie) {
            log.error("Error requesting geolocation", ie);
            Thread.currentThread().interrupt();
        }
        return new GeoCodeResult("");
    }


}
