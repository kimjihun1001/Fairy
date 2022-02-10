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

        String RegistrationDateTime = modelEmotions.getRegistrationDateTime().toString();
        Double happinessDegree = modelEmotions.getHappinessDegree();
        Double sadnessDegree = modelEmotions.getSadnessDegree();
        Double neutralDegree = modelEmotions.getNeutralDegree();
        String imagePath = modelEmotions.getImagePath();
        String imageName = modelEmotions.getImageName();

        String sqlInsert = Const.SQL_INSERT_TBL_EMOTIONS +
                " (" +
                "'" + RegistrationDateTime + "', " +
                "" + happinessDegree + ", " +
                "" + sadnessDegree + ", " +
                "" + neutralDegree + ", " +
                "'" + imagePath + "', " +
                "'" + imageName + "', " +
                ")" ;

        db.execSQL(sqlInsert) ;
    }

    // 데이터 조회(SELECT)
    public void load_values(FairyDBHelper fairyDBHelper, ModelEmotions modelEmotions) {

        SQLiteDatabase db = fairyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(Const.SQL_SELECT_TBL_EMOTIONS, null);

        String registrationDateTime;
        Double happinessDegree;
        Double sadnessDegree;
        Double neutralDegree;
        String imagePath;
        String imageName;

        if (cursor.moveToFirst()) {
            registrationDateTime = cursor.getString(1);
            happinessDegree = cursor.getDouble(2);
            sadnessDegree = cursor.getDouble(3);
            neutralDegree = cursor.getDouble(4);
            imagePath = cursor.getString(5);
            imageName = cursor.getString(6);
        }

        // modelEmotions.setRegistrationDateTime(registrationDateTime);
        // modelEmotions.setHappinessDegree(happinessDegree);

    }
}
