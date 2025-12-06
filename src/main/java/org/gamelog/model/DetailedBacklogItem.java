package org.gamelog.model;

public class DetailedBacklogItem extends BacklogItem {

    //Fields fetched later
    private Double rating;
    private String releaseDate;
    private String coverImageUrl;

    public DetailedBacklogItem(int backlog_id, int gid, int rawg_id, String gameName, String platform, int progress, int totalAchievements) {
        super(backlog_id, gid, rawg_id, gameName, platform, progress, totalAchievements);
        this.rating = 0.0;
        this.releaseDate = null;
        this.coverImageUrl = null;
    }

    public Double getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
}
