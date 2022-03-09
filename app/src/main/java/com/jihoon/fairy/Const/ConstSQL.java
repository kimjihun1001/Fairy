package com.jihoon.fairy.Const;

public class ConstSQL {
    private ConstSQL() {};

    // 사진, 감정 데이터
    public static final String TBL_EMOTIONS = "EMOTIONS_T";
    public static final String COL_NO = "NO";
    public static final String COL_DATETIME = "REGISTRATION_DATETIME";
    public static final String COL_HAPPINESS = "HAPPINESS_DEGREE";
    public static final String COL_SADNESS = "SADNESS_DEGREE";
    public static final String COL_NEUTRAL = "NEUTRAL_DEGREE";
    public static final String COL_IMAGEPATH = "IMAGEPATH";
    public static final String COL_IMAGENAME = "IMAGENAME";


    // CREATE TABLE IF NOT EXISTS EMOTIONS_T
    public static final String SQL_CREATE_TBL_EMOTIONS = "CREATE TABLE IF NOT EXISTS " + TBL_EMOTIONS + " " +
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


    // 사용자 정보 데이터
    public static final String TBL_USERDATA = "USERDATA_T";
    public static final String COL_USERNAME = "USERNAME";
    public static final String COL_USERAGE = "USERAGE";

    // CREATE TABLE IF NOT EXISTS USERDATA_T
    public static final String SQL_CREATE_TBL_USERDATA = "CREATE TABLE IF NOT EXISTS " + TBL_USERDATA + " " +
            "(" +
            COL_NO +        " INTEGER PRIMARY KEY AUTOINCREMENT" + ", " +
            COL_USERNAME +  " TEXT NOT NULL"                              + ", " +
            COL_USERAGE +   " INT NOT NULL"                               + ")";

    // DB INSERT OR UPDATE
    // INSERT INTO USERDATA_T(USERNAME, USERAGE) VALUES
    public static final String SQL_INSERT_TBL_USERDATA = "INSERT INTO " + TBL_USERDATA + "("
            + COL_USERNAME + ", " + COL_USERAGE + ") VALUES ";

    // UPDATE 테이블명 SET 컬럼명 1 = 값1, 컬럼명2 = 값2, ... WHERE 조건식;
    public static final String SQL_UPDATE_TBL_USERDATA_FRONT = "UPDATE " + TBL_USERDATA + " SET ";
    public static final String SQL_UPDATE_TBL_USERDATA_BACK = " WHERE " + COL_NO + "=1";

    // DB 불러오기
    public static final String SQL_SELECT_TBL_USERDATA = "SELECT * FROM " + TBL_USERDATA;

}
