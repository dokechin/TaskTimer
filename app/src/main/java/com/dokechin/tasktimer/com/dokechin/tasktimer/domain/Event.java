package com.dokechin.tasktimer.com.dokechin.tasktimer.domain;

/**
 * Created by dokechin on 2016/07/22.
 */
public class Event {
    private Long startTimeMills;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getStartTimeMills() {
        return startTimeMills;
    }

    public void setStartTimeMills(Long startTimeMills) {
        this.startTimeMills = startTimeMills;
    }
}
