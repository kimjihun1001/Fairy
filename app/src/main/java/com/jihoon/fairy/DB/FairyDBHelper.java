package com.jihoon.fairy.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jihoon.fairy.Const.Const;

import java.time.LocalDateTime;

public class FairyDBHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;
    public static final String DBFILE_CONTACT = "fairyDB.db";

    public FairyDBHelper(Context context) {
        super(context, DBFILE_CONTACT, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Const.SQL_CREATE_TBL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    private void load_values(FairyDBHelper fairyDBHelper) {

        SQLiteDatabase db = fairyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(Const.SQL_SELECT_TBL_EMOTIONS, null);

        if (cursor.moveToFirst()) {
            // COL_NO 값 가져오기
            String RegistrationDate = cursor.getString(1);
            String RegistrationTime = cursor.getString(2);
            Double happinessDegree = cursor.getDouble(3);
            Double neutralDegree = cursor.getDouble(4);
            Double sadnessDegree = cursor.getDouble(5);
        }
    }
}
