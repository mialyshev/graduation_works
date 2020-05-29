package org.suai.blamer.issuetracker;

public class Pair {
    private String sourceName;
    private boolean isAuthor;
    private boolean isDublicate;
    private Integer number;

    public Pair(String name, boolean author) {
        sourceName = name;
        isAuthor = author;
        isDublicate = false;
    }

    public Pair(boolean dublicate, int num) {
        isDublicate = true;
        number = num;
    }

    public String getSourceName() {
        return sourceName;
    }

    public boolean getisAuthor() {
        return isAuthor;
    }

    public boolean isDublicate() {
        return isDublicate;
    }

    public Integer getNumber() {
        return number;
    }
}
