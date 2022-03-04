package com.jihoon.fairy.Const;

import com.jihoon.fairy.Model.ModelEmotions;

import java.io.File;
import java.util.ArrayList;

public class Const {

    public static ArrayList<ModelEmotions> List_ModelEmotions = new ArrayList<ModelEmotions>();

    private Const() {

    }

    public static File InternalStorage;

    // Double 형태의 수치를 "00.00%" 형태의 String으로 변환하기 위한 메쏘드
    public static String ConvertDoubleToPercentage(Double num) {
        num = num * 100;
        String result = String.format("%.2f", num);
        result += "%";
        return result;
    }
}
