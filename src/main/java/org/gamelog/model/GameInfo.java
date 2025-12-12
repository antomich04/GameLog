package org.gamelog.model;

public class GameInfo {
    private final Double rating;
    private final String releaseDate;
    private final String coverImageUrl;

    public GameInfo(Double rating, String releaseDate, String coverImageUrl) {
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.coverImageUrl = coverImageUrl;
    }

    public Double getRating() { return rating; }
    public String getReleaseDate() { return releaseDate; }
    public String getCoverImageUrl() { return coverImageUrl; }
}
