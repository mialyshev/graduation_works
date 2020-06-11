package org.suai.blamer.issuetracker;

public class ItemAssignee {
    private String sourceName;
    private boolean isAuthor;
    private boolean isDublicate;
    private Integer number;

    public ItemAssignee(String name, boolean author) {
        sourceName = name;
        isAuthor = author;
        isDublicate = false;
    }

    public ItemAssignee(boolean dublicate, int num) {
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
