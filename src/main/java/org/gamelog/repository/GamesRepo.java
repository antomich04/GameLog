package org.gamelog.repository;

import org.gamelog.model.BacklogItem;
import org.gamelog.utils.RawgClient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
}
