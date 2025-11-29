package org.gamelog.model;

public class BacklogItem {
    private final int backlog_id;
    private final int gid;
    private final int rawg_id;
    private final String gameName;
    private final String platform;
    private final int progress;
    private final int totalAchievements;

    public BacklogItem(int backlog_id, int gid, int rawg_id, String gameName, String platform, int progress, int totalAchievements) {
        this.backlog_id = backlog_id;
        this.gid = gid;
        this.rawg_id = rawg_id;
        this.gameName = gameName;
        this.platform = platform;
        this.progress = progress;
        this.totalAchievements = totalAchievements;
    }

    public String getGameName() {
        return gameName;
    }

    public String getPlatform() {
        return platform;
    }

    public int getProgress() {
        return progress;
    }

    public int getTotalAchievements() {
        return totalAchievements;
    }

    public int getBacklogId() { // NEW GETTER
        return backlog_id;
    }

    public int getRawg_id() { return rawg_id; }
}
