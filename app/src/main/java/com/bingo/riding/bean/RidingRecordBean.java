package com.bingo.riding.bean;

import com.avos.avoscloud.AVFile;

/**
 * Author: CodingBingo.
 * Email: codingbingo@gmail.com
 * Blog: codingbingo.github.io
 * Github: https://github.com/CodingBingo
 * Created at 16/4/13
 */
public class RidingRecordBean {

    private double averageSpeed;
    private long ridingTime;
    private AVFile screenShot;
    private double ridingDistance;

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public double getRidingDistance() {
        return ridingDistance;
    }

    public void setRidingDistance(double ridingDistance) {
        this.ridingDistance = ridingDistance;
    }

    public long getRidingTime() {
        return ridingTime;
    }

    public void setRidingTime(long ridingTime) {
        this.ridingTime = ridingTime;
    }

    public AVFile getScreenShot() {
        return screenShot;
    }

    public void setScreenShot(AVFile screenShot) {
        this.screenShot = screenShot;
    }
}
