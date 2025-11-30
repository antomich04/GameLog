package org.gamelog.repository;

import org.gamelog.model.BacklogItem;
import org.gamelog.utils.RawgClient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
                    int totalAchievements = RawgClient.fetchTotalAchievements(rawg_id);

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
                        int totalAchievements = RawgClient.fetchTotalAchievements(rawg_id);
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
                        int totalAchievements = RawgClient.fetchTotalAchievements(rawg_id);
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

}
