package com.jihoon.fairy.Model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ModelEmotions {

    // RegistrationDate (측정날짜)
    private LocalDate RegistrationDate;
    public LocalDate getRegistrationDate() {
        return this.RegistrationDate;
    }
    public void setRegistrationDate(LocalDate RegistrationDate) {
        this.RegistrationDate = RegistrationDate;
    }

    // RegistrationTime (측정시간)
    private LocalTime RegistrationTime;
    public LocalTime getRegistrationTime() {
        return this.RegistrationTime;
    }
    public void setRegistrationTime(LocalTime RegistrationTime) {
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

    //sadnessDegree (슬픔)
    private double sadnessDegree;
    public Double getSadnessDegree() {
        return this.sadnessDegree;
    }
    public void setSadnessDegree(Double sadnessDegree) {
        this.sadnessDegree = sadnessDegree;
    }

    //neutralDegree (무표정)
    private double neutralDegree;
    public Double getNeutralDegree() {
        return this.neutralDegree;
    }
    public void setNeutralDegree(Double neutralDegree) {
        this.neutralDegree = neutralDegree;
    }
}
