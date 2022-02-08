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

    // angerDegree
    private double angerDegree;
    public Double getAngerDegree() {
        return this.angerDegree;
    }
    public void setAngerDegree(Double angerDegree) {
        this.angerDegree = angerDegree;
    }

    //contemptDegree
    private double contemptDegree;
    public Double getContemptDegree() {
        return this.contemptDegree;
    }
    public void setContemptDegree(Double contemptDegree) {
        this.contemptDegree = contemptDegree;
    }

    //disgustDegree
    private double disgustDegree;
    public Double getDisgustDegree() {
        return this.disgustDegree;
    }
    public void setDisgustDegree(Double disgustDegree) {
        this.disgustDegree = disgustDegree;
    }

    //fearDegree
    private double fearDegree;
    public Double getFearDegree() {
        return this.fearDegree;
    }
    public void setFearDegree(Double fearDegree) {
        this.fearDegree = fearDegree;
    }

    //happinessDegree
    private double happinessDegree;
    public Double getHappinessDegree() {
        return this.happinessDegree;
    }
    public void setHappinessDegree(Double happinessDegree) {
        this.happinessDegree = happinessDegree;
    }

    //neutralDegree
    private double neutralDegree;
    public Double getNeutralDegree() {
        return this.neutralDegree;
    }
    public void setNeutralDegree(Double neutralDegree) {
        this.neutralDegree = neutralDegree;
    }

    //sadnessDegree
    private double sadnessDegree;
    public Double getSadnessDegree() {
        return this.sadnessDegree;
    }
    public void setSadnessDegree(Double sadnessDegree) {
        this.sadnessDegree = sadnessDegree;
    }

    //surpriseDegree
    private double surpriseDegree;
    public Double getSurpriseDegree() {
        return this.surpriseDegree;
    }
    public void setSurpriseDegree(Double surpriseDegree) {
        this.surpriseDegree = surpriseDegree;
    }
}
