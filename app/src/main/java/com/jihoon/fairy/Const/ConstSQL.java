package com.jihoon.fairy.Const;

public class ConstSQL {
    private ConstSQL() {};

    public static final String TBL_EMOTIONS = "EMOTIONS_T";
    public static final String COL_NO = "NO";
    public static final String COL_DATETIME = "REGISTRATION_DATETIME";
    public static final String COL_HAPPINESS = "HAPPINESS_DEGREE";
    public static final String COL_SADNESS = "SADNESS_DEGREE";
    public static final String COL_NEUTRAL = "NEUTRAL_DEGREE";
    public static final String COL_IMAGEPATH = "IMAGEPATH";
    public static final String COL_IMAGENAME = "IMAGENAME";


    // CREATE TABLE IF NOT EXISTS EMOTIONS_T (TITLE TEXT NOT NULL)
    public static final String SQL_CREATE_TBL = "CREATE TABLE IF NOT EXISTS " + TBL_EMOTIONS + " " +
            "(" +
            COL_NO +        " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
            COL_DATETIME +  " TEXT"                              + ", " +
            COL_HAPPINESS + " DOUBLE"                            + ", " +
            COL_SADNESS +   " DOUBLE"                            + ", " +
            COL_NEUTRAL +   " DOUBLE"                            + ", " +
            COL_IMAGEPATH + " DOUBLE"                            + ", " +
            COL_IMAGENAME + " DOUBLE"                            + ")";

    // DB 저장
    public static final String SQL_INSERT_TBL_EMOTIONS = "INSERT OR REPLACE INTO " + TBL_EMOTIONS + " " +
            "(" +
            COL_DATETIME + ", " + COL_HAPPINESS + ", " + COL_SADNESS + ", " + COL_NEUTRAL + ", " + COL_IMAGEPATH + ", " + COL_IMAGENAME + ") VALUES ";

    // DB 불러오기
    public static final String SQL_SELECT_TBL_EMOTIONS = "SELECT * FROM " + TBL_EMOTIONS;

    // DB 날짜 역순으로 불러오기
    public static final String SQL_SELECT_TBL_EMOTIONS_SORT_DATE = "SELECT * FROM " + TBL_EMOTIONS + " ORDER BY " + COL_DATETIME + " DESC";
}
