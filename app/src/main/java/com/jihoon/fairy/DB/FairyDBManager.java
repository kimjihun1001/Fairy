package com.jihoon.fairy.DB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.CheckBox;
import android.widget.EditText;

import com.jihoon.fairy.Const.Const;
import com.jihoon.fairy.Model.ModelEmotions;
import com.jihoon.fairy.R;

public class FairyDBManager {

    // 데이터 저장(INSERT INTO)
    public void save_values(FairyDBHelper fairyDBHelper, ModelEmotions modelEmotions) {
        SQLiteDatabase db = fairyDBHelper.getWritableDatabase() ;

        String RegistrationDate = modelEmotions.getRegistrationDate().toString();
        String RegistrationTime = modelEmotions.getRegistrationTime().toString();
        Double happinessDegree = modelEmotions.getHappinessDegree();
        Double sadnessDegree = modelEmotions.getSadnessDegree();
        Double neutralDegree = modelEmotions.getNeutralDegree();

        String sqlInsert = Const.SQL_INSERT_TBL_EMOTIONS +
                " (" +
                "'" + RegistrationDate + "', " +
                "'" + RegistrationTime + "', " +
                "" + happinessDegree + ", " +
                "" + sadnessDegree + ", " +
                "" + neutralDegree + ", " +
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
            Double sadnessDegree = cursor.getDouble(4);
            Double neutralDegree = cursor.getDouble(5);
        }
    }
}
