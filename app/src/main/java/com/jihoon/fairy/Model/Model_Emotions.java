package com.jihoon.fairy.Model;

import java.time.LocalDateTime;

public class Model_Emotions {

    // RegistrationTime
    private LocalDateTime RegistrationTime;
    public LocalDateTime getRegistrationTime() {
        return this.RegistrationTime;
    }
    public void setRegistrationTime(LocalDateTime RegistrationTime) {
        this.RegistrationTime = RegistrationTime;
    }

    //happinessDegree (기쁨)
    private double happinessDegree;
    public Double getHappinessDegree() {
        return this.happinessDegree;
    }
    public void setHappinessDegree(Double happinessDegree) {
        this.happinessDegree = happinessDegree;
    }

    //neutralDegree (무표정)
    private double neutralDegree;
    public Double getNeutralDegree() {
        return this.neutralDegree;
    }
    public void setNeutralDegree(Double neutralDegree) {
        this.neutralDegree = neutralDegree;
    }

    //sadnessDegree (슬픔)
    private double sadnessDegree;
    public Double getSadnessDegree() {
        return this.sadnessDegree;
    }
    public void setSadnessDegree(Double sadnessDegree) {
        this.sadnessDegree = sadnessDegree;
    }
}
