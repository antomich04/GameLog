package org.gamelog.model;

public class AchievementStatus {
    private final String name;
    private final boolean achieved;
    private final int gid; //Used to link back to the game if needed

    public AchievementStatus(String name, boolean achieved, int gid) {
        this.name = name;
        this.achieved = achieved;
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public boolean isAchieved() {
        return achieved;
    }

    public int getGid() {
        return gid;
    }
}