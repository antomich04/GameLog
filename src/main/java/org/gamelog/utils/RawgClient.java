package org.gamelog.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.gamelog.model.SearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RawgClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    static Dotenv dotenv = Dotenv.load();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String API_BASE_URL = "https://api.rawg.io/api/";
    private static final String API_KEY = dotenv.get("API_KEY");

    public static List<SearchResult> searchGames(String query) {
        String encodedQuery = query.replace(" ", "+");
        String url = API_BASE_URL + "games?key=" + API_KEY + "&search=" + encodedQuery;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                return parseSearchResults(response.body());
            }else{  //No games found
                return Collections.emptyList();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<SearchResult> parseSearchResults(String jsonBody) {
        List<SearchResult> results = new ArrayList<>();
        try {
            JsonNode rootNode = MAPPER.readTree(jsonBody);
            JsonNode resultsNode = rootNode.path("results");

            if (resultsNode.isArray()) {
                for (JsonNode gameNode : resultsNode) {
                    int rawgId = gameNode.path("id").asInt();
                    String name = gameNode.path("name").asText();
                    String releaseDate = gameNode.path("released").asText(null);

                    if (rawgId > 0 && !name.isEmpty()) {
                        results.add(new SearchResult(rawgId, name, releaseDate));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static List<String> fetchGamePlatforms(int rawgId) {
        List<String> platforms = new ArrayList<>();

        String url = API_BASE_URL + "games/" + rawgId + "?key=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode rootNode = MAPPER.readTree(response.body());
                JsonNode platformsNode = rootNode.path("platforms");

                if (platformsNode.isArray()) {
                    for (JsonNode platformWrapperNode : platformsNode) {
                        JsonNode platformNode = platformWrapperNode.path("platform");
                        String platformName = platformNode.path("name").asText();

                        if (!platformName.isEmpty()) {
                            platforms.add(platformName);
                        }
                    }
                }
            }

        }catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return platforms;
    }

    public static int fetchTotalAchievements(int rawg_id){
        String url = API_BASE_URL + "games/" + rawg_id + "?key=" + API_KEY;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode rootNode = MAPPER.readTree(response.body());

                //Extracts achievements count
                int achievementCount = rootNode.path("achievements_count").asInt();

                return Math.max(achievementCount, 1);

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 20;  //Fallback value (Mockup)
    }

}