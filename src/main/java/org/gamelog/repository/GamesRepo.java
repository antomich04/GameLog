package org.gamelog.repository;

import org.gamelog.model.AchievementStatus;
import org.gamelog.model.BacklogItem;
import org.gamelog.model.DetailedBacklogItem;
import org.gamelog.model.GameInfo;
import org.gamelog.utils.RawgClient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GamesRepo {

    public static List<BacklogItem> fetchUserBacklog(String username){
        List<BacklogItem> backlog = new ArrayList<>();

        String fetchQuery = "SELECT * FROM get_user_backlog(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchQuery)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int backlog_id = rs.getInt(1);
                    int gid = rs.getInt(2);
                    int rawg_id = rs.getInt(3);
                    String gameName = rs.getString(4);
                    String platform = rs.getString(5);
                    int progress = rs.getInt(6);
                    int totalAchievements = RawgClient.fetchTotalAchievementCount(rawg_id);

                    backlog.add(new BacklogItem(backlog_id, gid, rawg_id, gameName, platform, progress, totalAchievements));
                }
            }

            return backlog;
        } catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>(); //Returns empty list on DB failure
        }
    }

    public static boolean addBacklog(String username, int rawgId, String gameName, String platform) {

        String insertQuery = "SELECT add_user_backlog_item(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, username);
            stmt.setInt(2, rawgId);
            stmt.setString(3, gameName);
            stmt.setString(4, platform);

            stmt.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeBacklogItem(int backlogId) {

        String deleteQuery = "SELECT remove_user_backlog_item(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setInt(1, backlogId);
            stmt.execute();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isDuplicateBacklogItem(String username, int rawgId, String platform) {

        String checkQuery = "SELECT is_duplicate_backlog_item(?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkQuery)) {

            stmt.setString(1, username);
            stmt.setInt(2, rawgId);
            stmt.setString(3, platform);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<BacklogItem> fetchLatestBacklogItems(String username) {
        List<BacklogItem> latestItems = new ArrayList<>();
        ExecutorService apiExecutor = Executors.newFixedThreadPool(2); //Only need a small pool for 2 items
        List<Future<BacklogItem>> futures = new ArrayList<>();

        String fetchQuery =
                "SELECT * FROM get_latest_backlog_items(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchQuery)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int backlog_id = rs.getInt(1);
                    int gid = rs.getInt(2);
                    int rawg_id = rs.getInt(3);
                    String gameName = rs.getString(4);
                    String platform = rs.getString(5);
                    int progress = rs.getInt(6);

                    Future<BacklogItem> future = apiExecutor.submit(() -> {
                        int totalAchievements = RawgClient.fetchTotalAchievementCount(rawg_id);
                        return new BacklogItem(backlog_id, gid, rawg_id, gameName, platform, progress, totalAchievements);
                    });
                    futures.add(future);
                }
            }

            //Waits for all concurrent API calls to finish
            for (Future<BacklogItem> future : futures) {
                try {
                    latestItems.add(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return latestItems;

        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            apiExecutor.shutdown();
        }
    }

    public static boolean toggleFavorite(String username, int gid, String platform){
        String toggleQuery = "SELECT toggle_favorite_game(?, ?, ?)";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(toggleQuery)){

            stmt.setString(1, username);
            stmt.setInt(2, gid);
            stmt.setString(3, platform);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isFavorite(String username, int gid, String platform){
        String favoriteQuery = "SELECT is_favorite_game(?, ?, ?)";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(favoriteQuery)){

            stmt.setString(1, username);
            stmt.setInt(2, gid);
            stmt.setString(3, platform);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }

        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public static List<BacklogItem> fetchUserFavorites(String username){
        List<BacklogItem> favorites = new ArrayList<>();

        ExecutorService apiExecutor = Executors.newFixedThreadPool(10);
        List<Future<BacklogItem>> futures = new ArrayList<>();

        String fetchQuery = "SELECT * FROM get_user_favorites(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchQuery)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int backlog_id = rs.getInt(1);
                    int gid = rs.getInt(2);
                    int rawg_id = rs.getInt(3);
                    String gameName = rs.getString(4);
                    String platform = rs.getString(5);
                    int progress = rs.getInt(6);

                    Future<BacklogItem> future = apiExecutor.submit(() -> {
                        int totalAchievements = RawgClient.fetchTotalAchievementCount(rawg_id);
                        return new BacklogItem(backlog_id, gid, rawg_id, gameName, platform, progress, totalAchievements);
                    });
                    futures.add(future);
                }
            }

            for (Future<BacklogItem> future : futures) {
                try {
                    favorites.add(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return favorites;
        } catch(SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            apiExecutor.shutdown();
        }
    }

    public static boolean updateGameInfo(int gid, GameInfo details) {
        if (details == null) {
            return false;
        }

        String updateQuery = "SELECT update_game_info(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setInt(1, gid);

            if (details.getRating() != null) {
                stmt.setDouble(2, details.getRating());
            } else {
                stmt.setNull(2, java.sql.Types.FLOAT);
            }

            stmt.setString(3, details.getReleaseDate());
            stmt.setString(4, details.getCoverImageUrl());


            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static DetailedBacklogItem fetchDetailedBacklogItem(int backlogId) {
        String fetchQuery = "SELECT * FROM get_backlog_item_details(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchQuery)) {

            stmt.setInt(1, backlogId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int backlog_id = rs.getInt("backlog_id");
                    int gid = rs.getInt("gid");
                    int rawg_id = rs.getInt("rawg_id");
                    String gameName = rs.getString("game_name");
                    String platform = rs.getString("platform");
                    int progress = rs.getInt("progress");
                    int totalAchievements = RawgClient.fetchTotalAchievementCount(rawg_id);

                    DetailedBacklogItem item = new DetailedBacklogItem(backlog_id, gid, rawg_id, gameName, platform, progress, totalAchievements);

                    Object ratingObj = rs.getObject("rating");
                    item.setRating(ratingObj != null ? rs.getDouble("rating") : null);

                    item.setReleaseDate(rs.getString("release_date"));
                    item.setCoverImageUrl(rs.getString("cover_image"));

                    return item;
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<AchievementStatus> fetchAchievementStatus(String username, int gid, int rawgId) {
        //Fetches the limited list of achievement names from RAWG
        List<String> apiAchievements = RawgClient.fetchGameAchievements(rawgId);

        List<AchievementStatus> achievementStatuses = new ArrayList<>();

        String fetchUserAchievementsQuery =
                "SELECT * FROM get_user_achievements_by_username(?, ?)";

        //Map for quick look-up
        Map<String, Boolean> dbStatus = new HashMap<>();

        //Populates existing status from DB
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchUserAchievementsQuery)) {

            stmt.setString(1, username);
            stmt.setInt(2, gid);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dbStatus.put(rs.getString("achievement_name"), rs.getBoolean("achieved"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean dbNeedsInsertion = dbStatus.isEmpty();

        for (String name : apiAchievements) {
            if (dbStatus.containsKey(name)) {
                achievementStatuses.add(new AchievementStatus(name, dbStatus.get(name), gid));
            } else {
                achievementStatuses.add(new AchievementStatus(name, false, gid));
            }
        }

        if (dbNeedsInsertion && !apiAchievements.isEmpty()) {
            insertNewAchievements(username, gid, apiAchievements);
        }

        return achievementStatuses;
    }

    //Inserts a batch of new unachieved achievements into the user_game_achievements table.
    private static void insertNewAchievements(String username, int gid, List<String> achievementNames) {
        String insertQuery = "SELECT insert_achievements_by_username(?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            conn.setAutoCommit(false); //Starts transaction for batch insert

            for (String name : achievementNames) {
                stmt.setString(1, username);
                stmt.setInt(2, gid);
                stmt.setString(3, name);
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit(); //Commits the batch
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateAchievementStatus(String username, int gid, String achievementName, boolean newStatus) {
        String updateQuery = "SELECT update_achievement_status_by_username(?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, username);
            stmt.setInt(2, gid);
            stmt.setString(3, achievementName);
            stmt.setBoolean(4, newStatus);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateBacklogProgress(String username, int gid, int newAchievedCount) {
        String updateQuery = "SELECT update_backlog_progress_by_username(?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, username);
            stmt.setInt(2, gid);
            stmt.setInt(3, newAchievedCount);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getBoolean(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getAchievedCount(String username, int gid) {
        String countQuery = "SELECT get_achieved_count_by_username(?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(countQuery)) {

            stmt.setString(1, username);
            stmt.setInt(2, gid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
