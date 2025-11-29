package org.gamelog.model;

public class SearchResult {

    private final int rawgId;
    private final String name;
    private final String releaseDate;

    public SearchResult(int rawgId, String name, String releaseDate) {
        this.rawgId = rawgId;
        this.name = name;
        this.releaseDate = releaseDate;
    }

    public int getRawgId() {
        return rawgId;
    }

    public String getName() {
        return name;
    }

    //Used for displaying the result in a ComboBox or ListView
    @Override
    public String toString() {
        //Formats the output for display in the UI
        String year = (releaseDate != null && releaseDate.length() >= 4) ? releaseDate.substring(0, 4) : "N/A";
        return name + " (" + year + ")";
    }
}