package com.jihoon.fairy.DB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jihoon.fairy.Const.Const;

public class FairyDBManager {

    public void load_values(FairyDBHelper fairyDBHelper) {

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
