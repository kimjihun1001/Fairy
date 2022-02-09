package com.jihoon.fairy.Const;

public class Const {
    private Const() {};

    public static final String TBL_EMOTIONS = "EMOTIONS_T";
    public static final String COL_NO = "NO";
    public static final String COL_DATE = "REGISTRATION_DATE";
    public static final String COL_TIME = "REGISTRATION_TIME";
    public static final String COL_HAPPINESS = "HAPPINESS_DEGREE";
    public static final String COL_NEUTRAL = "NEUTRAL_DEGREE";
    public static final String COL_SADNESS = "SADNESS_DEGREE";

    // CREATE TABLE IF NOT EXISTS EMOTIONS_T (TITLE TEXT NOT NULL)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_EMOTIONS + " " +
            "(" +
            COL_NO +        " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
            COL_DATE +      " TEXT"                              + ", " +
            COL_TIME +      " TEXT"                              + ", " +
            COL_HAPPINESS + " DOUBLE"                            + ", " +
            COL_NEUTRAL +   " DOUBLE"                            + ", " +
            COL_SADNESS +   " DOUBLE"                            + ")";

    // DB 저장
    public static final String SQL_INSERT_TBL_EMOTIONS = "INSERT OR REPLACE INTO " + TBL_EMOTIONS + " " +
            "(" +
            COL_DATE + ", " + COL_TIME + ", " + COL_HAPPINESS + ", " + COL_NEUTRAL + ", " + COL_SADNESS + ") VALUES ";

    // DB 불러오기
    public static final String SQL_SELECT_TBL_EMOTIONS = "SELECT * FROM " + TBL_EMOTIONS;
}
