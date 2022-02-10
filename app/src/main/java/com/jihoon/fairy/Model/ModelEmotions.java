package com.jihoon.fairy.Model;

import android.media.MediaCodecInfo;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ModelEmotions {

    // RegistrationDate (측정날짜 및 시간)
    private LocalDateTime RegistrationDateTime;
    public LocalDateTime getRegistrationDateTime() {
        return this.RegistrationDateTime;
    }
    public void setRegistrationDateTime(LocalDateTime RegistrationDateTime) {
        this.RegistrationDateTime = RegistrationDateTime;
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

    //imagePath (이미지 경로)
    private String imagePath;
    public String getImagePath() { return this.imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    //imageName (이미지 이름)
    private String imageName;
    public String getImageName() { return this.imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
}
