package com.dokechin.tasktimer.com.dokechin.tasktimer.domain;

/**
 * Created by dokechin on 2016/07/22.
 */
public class Counter {
    private int sec = 0;
    public Counter(int second) {
        this.sec = second;
    }
    public String clockFormat(){

        int hour = this.sec / 3600;
        int min = (this.sec  - hour * 3600) / 60;
        int sec = (this.sec  - hour * 3600 - min * 60);

        return String.format("%1$02d:%2$02d:%3$02d", hour, min , sec);
    }
}
