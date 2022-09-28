package journey.controller;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;

public class AttractionsService {
    public void getAttractions(double lat, double lng, int range) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String URL = "https://api.geoapify.com/v2/places?categories=tourism" + "&filter=circle:" + lat + "," + lng + "," + range +
                "&limit=20" +
                "&apiKey=" + dotenv.get("GEOAPIFY_API_KEY");
        URL url = new URL(URL);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestProperty("Accept", "application/json");

        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
        http.disconnect();
    }

    public static void main(String[] args) throws IOException {
        AttractionsService attractionsService = new AttractionsService();
        attractionsService.getAttractions(-43.389081939117496, 172.42492675781253, 600);
    }
}
