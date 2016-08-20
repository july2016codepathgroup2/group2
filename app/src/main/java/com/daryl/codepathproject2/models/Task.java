package com.daryl.codepathproject2.models;

public class Task {

    String objectId;
    long budget;
    User author;
    String title;
    String description;
    String status;
    String type;

    public Task() {
        //TODO figure out how to initialize task from parse
    }

    public String getObjectId() {
        return objectId;
    }

    public long getBudget() {
        return budget;
    }

    public User getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }
}
