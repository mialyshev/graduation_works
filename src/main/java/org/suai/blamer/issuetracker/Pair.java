package org.suai.blamer.issuetracker;

public class Pair {
    private String sourceName;
    private boolean isAuthor;

    public Pair(String first, boolean second) {
        sourceName = first;
        isAuthor = second;
    }

    public String getSourceName() {
        return sourceName;
    }

    public boolean getisAuthor() {
        return isAuthor;
    }
}
