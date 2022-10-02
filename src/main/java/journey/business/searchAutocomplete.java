package journey.business;

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
import java.util.ArrayList;

/**
 * Class to handle requesting addresses from photon API
 */
public class searchAutocomplete {
    private static final Logger log = LogManager.getLogger();

    /**
     * Finds all matching addresses from a given search

     * @param text search inputted by user to be auto completed
     * @return ArrayList of strings containing possible addresses
     */
    public ArrayList<String> getMatchingAddresses(String text) {
        ArrayList<String> matchingAddresses = new ArrayList<>();
        text = text.replace(' ', '+');
        try {
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://photon.komoot.io/api/?q="+text+"&lat=-43.53&lon=-172.63&limit=10")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get list of addresses.
            JSONParser parser = new JSONParser();
            JSONObject jsonResult = (JSONObject) parser.parse(response.body());
            JSONArray results = (JSONArray) jsonResult.get("features");
            for (Object result : results) {
                JSONObject data = (JSONObject) result;
                JSONObject properties = (JSONObject) data.get("properties");
                String district;
                if (properties.get("district") == null) {
                    district = "";

                } else {
                    district = ", " + properties.get("district");
                }
                String address = properties.get("housenumber") + " " + properties.get("street") + district;
                matchingAddresses.add(address);
            }
            return matchingAddresses;
        } catch (IOException | ParseException e) {
            log.error("Error requesting geolocation", e);
        } catch (InterruptedException ie) {
            log.error("Error requesting geolocation", ie);
            Thread.currentThread().interrupt();
        } catch (Exception iie) {
            log.error("Error when loading", iie);
        }

        return new ArrayList<>();
    }
}
