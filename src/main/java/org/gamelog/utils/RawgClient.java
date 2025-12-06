package org.gamelog.utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.gamelog.model.GameInfo;
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
    private static final int MAX_ACHIEVEMENTS_LIMIT = 50;

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

    public static List<String> fetchGameAchievements(int rawgId) {
        List<String> achievementNames = new ArrayList<>();

        String url = API_BASE_URL + "games/" + rawgId + "/achievements?key=" + API_KEY + "&page_size=" + MAX_ACHIEVEMENTS_LIMIT;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode rootNode = MAPPER.readTree(response.body());
                JsonNode resultsNode = rootNode.path("results");

                if (resultsNode.isArray()) {
                    for (JsonNode achievementNode : resultsNode) {
                        String name = achievementNode.path("name").asText();
                        if (!name.isEmpty()) {
                            achievementNames.add(name);
                        }
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return achievementNames;
    }

    public static int fetchTotalAchievementCount(int rawg_id){
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

                int achievementCount = rootNode.path("achievements_count").asInt();

                if (achievementCount == 0) {
                    return 0;
                }

                return Math.min(achievementCount, MAX_ACHIEVEMENTS_LIMIT);

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;  //Fallback value
    }

    public static GameInfo fetchGameDetails(int rawgId) {
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

                double rating = rootNode.path("rating").asDouble();
                Double gameRating = (rating > 0.0) ? rating : null;

                //Gets release date as "yyyy-MM-dd"
                String releaseDate = rootNode.path("released").asText(null);

                String coverImageUrl = rootNode.path("background_image").asText(null);

                return new GameInfo(gameRating, releaseDate, coverImageUrl);
            }

        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new GameInfo(null, null, null);
    }

}