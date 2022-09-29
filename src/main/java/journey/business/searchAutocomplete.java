package journey.business;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class searchAutocomplete {
    private static final Logger log = LogManager.getLogger();

    public ArrayList<String> getMatchingAddresses(String text) {

        ArrayList<String> matchingAddresses = new ArrayList<String>();


        try {
            // Creating the http request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create("https://photon.komoot.io/api/?q="+text+"&lat=43.53&lon=172.63&limit=10")
            ).build();
            // Getting the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Parsing the json response to get list of addresses.
            JSONParser parser = new JSONParser();
            JSONArray results = (JSONArray) parser.parse(response.body());
            for(int i=0; i<results.size(); i++) {
                JSONObject data = (JSONObject) results.get(i);
                String address = data.get("housenumber") + " " + data.get("street") + ", " + data.get("district");
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

        return new ArrayList<String>();
    }
}
