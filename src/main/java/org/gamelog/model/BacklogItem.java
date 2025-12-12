package org.gamelog.model;

public class BacklogItem {
    private final int backlog_id;
    private final int gid;
    private final int rawg_id;
    private final String gameName;
    private String platform;
    private int progress;
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

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTotalAchievements() {
        return totalAchievements;
    }

    public int getBacklogId() {
        return backlog_id;
    }

    public int getGid() { return gid; }

    public int getRawgId() { return rawg_id; }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getSortableProgressScore() {
        if (totalAchievements <= 0) {   //Returns -1 so that the games are correctly put when sorting by progress status
            return -1;
        }
        return (int) Math.round(((double) progress / totalAchievements) * 100);
    }
}