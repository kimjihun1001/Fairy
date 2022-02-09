package com.jihoon.fairy.DB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jihoon.fairy.Const.Const;
import com.jihoon.fairy.R;

public class FairyDBManager {

    // 데이터 저장(INSERT INTO)
    public void save_values(FairyDBHelper fairyDBHelper, String Date, String Time,
                            Double happiness, Double neutral, Double sadness) {
        SQLiteDatabase db = fairyDBHelper.getWritableDatabase() ;

        String RegistrationDate = Date;
        String RegistrationTime = Time;
        Double happinessDegree = happiness;
        Double neutralDegree = neutral;
        Double sadnessDegree = sadness;

        String sqlInsert = Const.SQL_INSERT_TBL_EMOTIONS +
                " (" +
                "'" + RegistrationDate + "', " +
                "'" + RegistrationTime + "', " +
                "" + happinessDegree + ", " +
                "" + neutralDegree + ", " +
                "" + sadnessDegree + ", " +
                ")" ;

        db.execSQL(sqlInsert) ;
    }

    // 데이터 조회(SELECT)
    public void load_values(FairyDBHelper fairyDBHelper) {

        SQLiteDatabase db = fairyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(Const.SQL_SELECT_TBL_EMOTIONS, null);

        if (cursor.moveToFirst()) {
            String RegistrationDate = cursor.getString(1);
            String RegistrationTime = cursor.getString(2);
            Double happinessDegree = cursor.getDouble(3);
            Double neutralDegree = cursor.getDouble(4);
            Double sadnessDegree = cursor.getDouble(5);
        }
    }
}
