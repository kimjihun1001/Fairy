package com.jihoon.fairy.Const;

import android.content.res.AssetManager;

import com.jihoon.fairy.Model.ModelEmotions;
import com.jihoon.fairy.Model.ModelUserData;

import java.io.File;
import java.util.ArrayList;

public class Const {

    public static ArrayList<ModelEmotions> List_ModelEmotions = new ArrayList<ModelEmotions>();

    private Const() {

    }

    public static File internalStorage;
    public static AssetManager assetManager;
    public static boolean isInitialDataAdded = false;   // 초기 데이터 한 번만 추가하도록 하려고 만든 변수
    public static ModelUserData currentUserData;

    // Double 형태의 수치를 "00.00%" 형태의 String으로 변환하기 위한 메쏘드
    public static String ConvertDoubleToPercentage(Double num) {
        num = num * 100;
        String result = String.format("%.2f", num);
        result += "%";
        return result;
    }
}
