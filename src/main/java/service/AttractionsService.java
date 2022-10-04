package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import journey.business.Feature;

/**
 * Service to find attractions.
 */
public class AttractionsService {
    /**
     * Get attractions in a circle via Geoapify.

     * @param lat latitude of the circle centre
     * @param lng longitude of the circle centre
     * @param range radius of the circle
     * @return A list of features
     * @throws IOException If the website is not found
     */
    public ArrayList<Feature> getAttractions(double lat, double lng, int range) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String urlString = "https://api.geoapify.com/v2/places?categories=tourism&conditions=named"
                + "&filter=circle:" + lng + "," + lat + "," + range
                + "&bias=proximity:" + lng + "," + lat
                + "&limit=20"
                + "&apiKey=" + dotenv.get("GEOAPIFY_API_KEY");
        URL url = new URL(urlString);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestProperty("Accept", "application/json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
        JsonObject result = JsonParser.parseReader(reader).getAsJsonObject();
        JsonArray features = result.getAsJsonArray("features");
        ArrayList<Feature> featureArrayList = new ArrayList<>();
        for (int i = 0; i < features.size(); i++) {
            JsonObject feature = (JsonObject) features.get(i);
            JsonObject properties = (JsonObject) feature.get("properties");
            try {
                Feature featureObj = new Feature();
                featureObj.setLat(properties.get("lat").getAsDouble());
                featureObj.setLon(properties.get("lon").getAsDouble());
                featureObj.setFormattedName(properties.get("formatted").getAsString());
                featureArrayList.add(featureObj);
            } catch (Exception ignore) {
                //Simply don't add the point to the return list.
            }
        }
        http.disconnect();
        return featureArrayList;
    }

    /**
     * Test the function.

     * @throws IOException Website not found.
     */
    public static void main(String[] args) throws IOException {
        AttractionsService attractionsService = new AttractionsService();
        ArrayList<Feature> features = attractionsService.getAttractions(-43.34863173960228, 172.45915551986877, 60000);
        for (Feature feature : features) {
            System.out.println(feature.getFormattedName());
            System.out.println(feature.getLat() + "," + feature.getLon());
        }
    }
}
