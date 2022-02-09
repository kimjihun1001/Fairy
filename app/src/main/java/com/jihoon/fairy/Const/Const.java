package com.jihoon.fairy.Const;

public class Const {
    private Const() {};

    public static final String TBL_EMOTIONS = "EMOTIONS_T";
    public static final String COL_NO = "NO";
    public static final String COL_TIME = "REGISTRATION_TIME";
    public static final String COL_ANGER = "ANGER_DEGREE";
    public static final String COL_CONTEMPT = "CONTEMPT_DEGREE";
    public static final String COL_DISGUST = "DISGUST_DEGREE";
    public static final String COL_FEAR = "FEAR_DEGREE";
    public static final String COL_HAPPINESS = "HAPPINESS_DEGREE";
    public static final String COL_NEUTRAL = "NEUTRAL_DEGREE";
    public static final String COL_SADNESS = "SADNESS_DEGREE";
    public static final String COL_SURPRISE = "SURPRISE_DEGREE";

    // CREATE TABLE IF NOT EXISTS EMOTIONS_T (TITLE TEXT NOT NULL)
    public static final String SQL_CREATE_TBL_EMOTIONS = String.format("CREATE TABLE IF NOT EXISTS %s " +
                    "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s DATETIME, %s DOUBLE, %s DOUBLE, %s DOUBLE, %s DOUBLE, " +
                    "%s DOUBLE, %s DOUBLE, %s DOUBLE, %s DOUBLE",
            TBL_EMOTIONS, COL_NO, COL_TIME, COL_ANGER, COL_CONTEMPT, COL_DISGUST,
            COL_FEAR, COL_HAPPINESS, COL_NEUTRAL, COL_SADNESS, COL_SURPRISE);

    // DB 저장
    public static final String SQL_INSERT_TBL_EMOTIONS = String.format("INSERT OR REPLACE INTO %s " +
                    "(%s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES",
            TBL_EMOTIONS, COL_TIME, COL_ANGER, COL_CONTEMPT, COL_DISGUST,
            COL_FEAR, COL_HAPPINESS, COL_NEUTRAL, COL_SADNESS, COL_SURPRISE);

    // DB 불러오기
    public static final String SQL_SELECT_TBL_EMOTIONS = String.format("SELECT * FROM %s ",TBL_EMOTIONS);
}
