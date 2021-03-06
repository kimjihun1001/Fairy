package com.jihoon.fairy;
// Ver 1.0

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.jihoon.fairy.Adapter.AdvertisementListViewAdapter;
import com.jihoon.fairy.Adapter.HistoryRecyclerViewAdapter;
import com.jihoon.fairy.Adapter.PhotoHistoryListViewAdapter;
import com.jihoon.fairy.Const.Const;
import com.jihoon.fairy.Control.ExampleDataMaker;
import com.jihoon.fairy.DB.FairyDBHelper;
import com.jihoon.fairy.DB.FairyDBManager;
import com.jihoon.fairy.Model.Advertisement;
import com.jihoon.fairy.Control.AdviceListMaker;
import com.jihoon.fairy.Model.ModelEmotions;
import com.jihoon.fairy.Model.ModelUserData;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView_result;
    TextView textView_userName;
    TextView textView_userAge;

    Disposable backgroundtask;

    FairyDBManager fairyDBManager;
    FairyDBHelper fairyDBHelper;

    private final String apiEndpoint = "https://koreacentral.api.cognitive.microsoft.com/face/v1.0";
    private final String subscriptionKey = "3c9f199990c54486bd55515df1226852";

    private FaceServiceClient faceServiceClient;

    private Bitmap bmRotated;

    private double happy;
    private double sad;
    private double neutral;

    ListView history_ListView;
    PhotoHistoryListViewAdapter history_Adapter;

    //??????
    AdvertisementListViewAdapter advertisementListViewAdapter;
    ArrayList<Advertisement> advertisements;
    ListView listView;

    // ????????? ?????????
    LineChart chart;

    // Firebase - Google Analytics
    FirebaseAnalytics analytics;

    //?????? ??????
    int count;
    private ArrayList<Integer> emotionOfsad;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ?????? ????????? ??????
        Const.internalStorage = this.getFilesDir();
        // AssetsManager
        Const.assetManager = getResources().getAssets();
        // ????????? ?????? ?????????
        Const.currentUserData = new ModelUserData();

        imageView = findViewById(R.id.imageView);
        textView_result = findViewById(R.id.textView_result);
        textView_userName = findViewById(R.id.textView_userName);
        textView_userAge = findViewById(R.id.textView_userAge);

        advertisementListViewAdapter = new AdvertisementListViewAdapter();

        // DBHelper ?????????
        init_tables();

        // DB ????????? - ????????? ?????? ?????????
        fairyDBManager = new FairyDBManager();

        // DB ???????????? + App??? Const.currentUserDate??? ????????? ??????
        fairyDBManager.load_userData(fairyDBHelper);
        System.out.println("????????? ?????? ????????????");

        // DB ???????????? + App??? Const List??? ????????? ??????
        fairyDBManager.load_values(fairyDBHelper);

        // ?????? ?????? ??????????????? ?????? ???????????? - ?????? ????????? ?????? ??????
//        // ?????? ?????? ?????? ????????? ??????????????? ????????????
//        for (ModelEmotions modelEmotions : Const.List_ModelEmotions) {
//            if (modelEmotions.getImageName().equals("2022-02-05T10:30:30")) {
//                Const.isInitialDataAdded = true;
//            }
//        }
//        // ?????? ?????? ????????? ????????? + DB??? ?????? + App??? Const List??? ????????? ??????
//        ExampleDataMaker exampleDataMaker = new ExampleDataMaker();
//        if (Const.isInitialDataAdded == false) {
//            for (ModelEmotions modelEmotions1 : exampleDataMaker.MakeExampleData()) {
//                fairyDBManager.save_values(fairyDBHelper, modelEmotions1);
//            }
//        }

        //Azure Face API ??????
        faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);

        // ?????? ????????? ??? ?????? ?????? ????????? ????????? ?????????
        // ??? ?????? ?????? ?????? FrameLayout ?????? ????????? XML?????? visible??? ??????????????????.
        // ????????? ??????????????? ??? ???????????? ??? ????????? ??????. ?????? ??????
        // ????????? ?????? ???, ??????, ?????? ????????? ??? ?????? ??????.
        // TabItem tabItem_home = (TabItem) findViewById(R.id.tabItem_home);
        // tabItem_home.setSelected(true);

        // ??? ?????? ????????? ????????? ????????????
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                // ????????? ?????? ?????? ????????? ???????????? ?????????
                ChangeView(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // ?????? ??? ??????????????? ????????? ????????????
        history_Adapter = new PhotoHistoryListViewAdapter();
        history_ListView = (ListView) findViewById(R.id.listView_historyPhoto);
        history_ListView.setAdapter(history_Adapter);

    }

    // ????????? ?????????
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawChart() {
        chart = (LineChart) findViewById(R.id.chart);
        List<Entry> happyEntries = new ArrayList<Entry>();
        List<Entry> sadEntries = new ArrayList<Entry>();
        emotionOfsad = new ArrayList<Integer>();

        // ???????????? ???????????? ?????? ?????? map??? ?????????
        Map<LocalDate, List<ModelEmotions>> map = new HashMap<>();
        for (ModelEmotions modelEmotions : Const.List_ModelEmotions) {
            LocalDate localDate = modelEmotions.getRegistrationDateTime().toLocalDate();

            if (map.containsKey(localDate)) {
                // ?????? ????????? key??? ???????????? value??? modelemotions ????????????
                map.get(localDate).add(modelEmotions);
            } else {
                List<ModelEmotions> List_emotionsOfDay = new ArrayList<>();
                List_emotionsOfDay.add(modelEmotions);
                map.put(localDate, List_emotionsOfDay);
            }
        }

        // map??? ?????? ???????????? ???????????? ??????
        // map??? key???(LocalDate)??? ?????? ?????????????????? ????????????
        ArrayList<LocalDate> mapKey = new ArrayList<LocalDate>(map.keySet());
        mapKey.sort(Comparator.naturalOrder());
        // ValueFormatter?????? ??? ????????? ????????? - float -> String
        // X?????? label??? ????????? ?????????
        List<String> List_localDateStr = new ArrayList<>();

        // x??? ??????
        float valueOfX = 0;
        for (LocalDate localDate : mapKey) {
            System.out.println(localDate.toString());
            // map??? key?????? X??? label??? ????????????
            String localDateStr = String.valueOf(localDate);
            // ?????? ?????? ??? ?????? ????????????
            List_localDateStr.add(localDateStr.substring(5, 10));

            // ?????? ?????? ????????? ?????? ??????
            float sumOfHappy = 0;
            float sumOfSad = 0;

            for (ModelEmotions modelEmotions : map.get(localDate)) {
                float happy = modelEmotions.getHappinessDegree().floatValue();
                float sad = modelEmotions.getSadnessDegree().floatValue();
                sumOfHappy += happy;
                sumOfSad += sad;
            }

            float averageOfHappy;
            float averageOfSad;
            float sizeOfList = map.get(localDate).size();
            averageOfHappy = sumOfHappy / sizeOfList;
            averageOfSad = sumOfSad / sizeOfList;

            // 100 ????????? ????????? ?????????????????? ????????? - ???: 0.123456 -> 1234.56 -> 1235 -> 12.35
            float valueOfY_happy = Math.round(averageOfHappy * 10000)/100;
            float valueOfY_sad = Math.round(averageOfSad * 10000)/100;

            //???????????? ?????? ?????? ??????
            emotionOfsad.add((int) valueOfY_sad);

            // ????????? ??????
            happyEntries.add(new Entry(valueOfX, valueOfY_happy));
            sadEntries.add(new Entry(valueOfX, valueOfY_sad));
            valueOfX += 1;
        }

        // Chart Style
        // chart.setBackgroundColor(Color.rgb(254, 247, 235));
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setDragXEnabled(true);
        chart.setDragYEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(true);

        // Legend??? ?????? ????????????
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);

        XAxis xAxis;
        xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        // ????????? ??????
        xAxis.setSpaceMax(1f);
        xAxis.setSpaceMin(1f);
        // ?????? ????????? ????????? ????????? ??????
        xAxis.setValueFormatter(new IndexAxisValueFormatter(List_localDateStr));
        // ??? ????????? ?????? ?????? : 2??? ?????? 2????????? ????????? ??????
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        YAxis yAxis;
        yAxis = chart.getAxisLeft();
        chart.getAxisRight().setEnabled(false);
        xAxis.setTextSize(10);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisMinimum(0f);
        // yAxis.setSpaceMax(0.3f);
        // yAxis.setSpaceMin(0.3f);
        // Y??? ????????? ?????? % ?????????
        yAxis.setValueFormatter(new PercentFormatter());

        // ????????? ????????? ??????
        LineDataSet dataSet = new LineDataSet(happyEntries, "??????");
        dataSet.setColor(Color.rgb(251, 99, 118));
        dataSet.setValueTextColor(Color.rgb(251, 99, 118));
        dataSet.setValueTextSize(10);
        dataSet.setLineWidth(2);
        dataSet.setCircleColor(Color.BLACK);
        // ??? ?????? ??????
        // dataSet.setCircleRadius(15);
        // ????????? ??? ?????? % ?????????
        dataSet.setValueFormatter(new PercentFormatter());

        LineDataSet dataSet2 = new LineDataSet(sadEntries, "??????");
        dataSet2.setColor(Color.rgb(82, 122, 184));
        dataSet2.setValueTextColor(Color.rgb(82, 122, 184));
        dataSet2.setValueTextSize(10);
        dataSet2.setLineWidth(2);
        dataSet2.setCircleColor(Color.BLACK);
        // ????????? ??? ?????? % ?????????
        dataSet2.setValueFormatter(new PercentFormatter());

        LineData lineData = new LineData(dataSet, dataSet2);
        chart.setData(lineData);

        // ?????? x?????? ???????????? ????????? ????????????
        chart.setVisibleXRange(5, 5);
        // ?????? ????????? ????????? ???????????? ????????? ????????????
        chart.moveViewToX(dataSet.getEntryCount());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showAdvice(){
        int size;
        int num = 0;
        String advice;
        count = 0;

        AdviceListMaker adviceListMaker = new AdviceListMaker();
        TextView textView = findViewById(R.id.textView_advice);

        if(emotionOfsad.size() > 14){

            size = emotionOfsad.size() - 14 ;
            num += size;
        }

        for (int i = emotionOfsad.size() - 1; i >= num; i-- ){

           if(emotionOfsad.get(i) > 10){
               count++;
           }
        }

        if (count <= 5){
            advice = adviceListMaker.GoodAdvice();
        }

        else if (count < 10){
            advice = adviceListMaker.NormalAdvice();
        }

        else{
            advice = adviceListMaker.SadAdvice();
        }

        textView.setText(advice);
    }

    // ??? ?????? ???, ?????? ?????? ????????????
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ChangeView(int index) {
        LinearLayout layout_home = findViewById(R.id.layout_home);
        ScrollView scrollView_history = findViewById(R.id.scrollView_history);
        LinearLayout layout_photoHistory = findViewById(R.id.layout_photoHistory);
        LinearLayout layout_setting = findViewById(R.id.layout_setting);
        switch (index) {
            case 0:
                layout_home.setVisibility(View.VISIBLE);
                scrollView_history.setVisibility(View.INVISIBLE);
                layout_photoHistory.setVisibility(View.INVISIBLE);
                layout_setting.setVisibility(View.INVISIBLE);

                // Google Analytics
                analytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle(); // logEvent()?????? ??????
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "??? ??? ??????");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Home Tab");
                analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
//                Toast.makeText(MainActivity.this, String.valueOf(bundle), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                // ????????? ????????? - onCreate()?????? ????????? ??????.
                drawChart();
                // ????????? ????????????
                chart.invalidate();

                // ?????? ????????????
                showAdvice();
                // ?????? ????????????
                showAdvertiesment();

                layout_home.setVisibility(View.INVISIBLE);
                scrollView_history.setVisibility(View.VISIBLE);
                layout_photoHistory.setVisibility(View.INVISIBLE);
                layout_setting.setVisibility(View.INVISIBLE);

                // Google Analytics
                analytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle2 = new Bundle(); // logEvent()?????? ??????
                bundle2.putString(FirebaseAnalytics.Param.SCREEN_NAME, "?????? ??? ??????");
                bundle2.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "History Tab");
                analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle2);
                break;
            case 2:
                // ???????????? ?????????
                if (Const.List_ModelEmotions.size() == 0 || Const.List_ModelEmotions == null) {
                    Toast.makeText(this, "????????? ????????????.\n??? ????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }

                // ?????????????????? ????????? ??????
                ArrayList<ModelEmotions> Sort_Date_List_ModelEmotions = new ArrayList<ModelEmotions>();
                FairyDBManager fairyDBManager = new FairyDBManager();
                fairyDBManager.load_sort_values(fairyDBHelper, Sort_Date_List_ModelEmotions);

                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < Sort_Date_List_ModelEmotions.size(); i++) {
                    String tempString;
                    tempString = Sort_Date_List_ModelEmotions.get(i).getRegistrationDateTime().format(DateTimeFormatter.ofPattern("yyyy???\nMM???\ndd???"));

                    if (!list.contains(tempString)) {
                        list.add(tempString);
                    }
                }

                RecyclerView recyclerView = findViewById(R.id.recyclerView_historyDate);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(list);
                recyclerView.setAdapter(adapter);

                layout_home.setVisibility(View.INVISIBLE);
                scrollView_history.setVisibility(View.INVISIBLE);
                layout_photoHistory.setVisibility(View.VISIBLE);
                layout_setting.setVisibility(View.INVISIBLE);

                // Google Analytics
                analytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle3 = new Bundle(); // logEvent()?????? ??????
                bundle3.putString(FirebaseAnalytics.Param.SCREEN_NAME, "?????? ??? ??????");
                bundle3.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Photo Tab");
                analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle3);
                break;
            case 3:
                textView_userName.setText(Const.currentUserData.getUserName());
                if (Const.currentUserData.getUserAge() == 0) {
                    textView_userAge.setText("????????? ??????????????????");
                }
                else {
                    textView_userAge.setText(String.valueOf(Const.currentUserData.getUserAge()));
                }

                layout_home.setVisibility(View.INVISIBLE);
                scrollView_history.setVisibility(View.INVISIBLE);
                layout_photoHistory.setVisibility(View.INVISIBLE);
                layout_setting.setVisibility(View.VISIBLE);

                // Google Analytics
                analytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle4 = new Bundle(); // logEvent()?????? ??????
                bundle4.putString(FirebaseAnalytics.Param.SCREEN_NAME, "?????? ??? ??????");
                bundle4.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Setting Tab");
                analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle4);
                break;
        }
    }

    // ????????? ?????? ????????? ????????? ????????? ?????? ????????? ??????
    public void Click_button_upload(View view) {
        // Toast.makeText(this, "????????? ?????? ??????", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    // ??????????????? ???????????? ???????????? ????????? ?????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // ?????????
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();

                // ???????????? ?????? ?????? ??????.
                String realPath = createCopyAndReturnRealPath(this, fileUri);

                //?????? ?????? ??????
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(realPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                try {
                    Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(resolver, fileUri);

                    //????????? ???????????????
                    bmRotated = rotateBitmap(imgBitmap, orientation);
                    int a = bmRotated.getHeight();

                    if (a > 1000)
                    {
                        bmRotated = bmRotated.createScaledBitmap(bmRotated, bmRotated.getWidth() / 4, bmRotated.getHeight() / 4, true);
                    }

                    imageView.setImageBitmap(bmRotated);    // ????????? ????????? ??????????????? ??????
                    // Toast.makeText(getApplicationContext(), "?????? ???????????? ??????", Toast.LENGTH_SHORT).show();
                    textView_result.setText("????????? ????????????????????????. ?????? ????????? ???????????????.");
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "?????? ???????????? ??????", Toast.LENGTH_SHORT).show();
                }

                detectandFrame(bmRotated);
            }
        }
    }

    public String SaveImage(Bitmap imgBitmap, String imgName) {   // ????????? ????????? ?????? ???????????? ??????
        File tempFile = new File(Const.internalStorage, imgName);    // ?????? ????????? ?????? ??????
        try {
            tempFile.createNewFile();   // ???????????? ??? ????????? ????????????
            FileOutputStream out = new FileOutputStream(tempFile);  // ????????? ??? ??? ?????? ???????????? ????????????
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);   // compress ????????? ????????? ???????????? ???????????? ????????????
            // ?????? ????????? ???????????????, quality??? ????????? ???????????? ?????? ?????? ????????? ????????? ?????? ??????.
            // System.out.println("?????? ???????????? ????????? ?????? ??????: " + imgBitmap.getRowBytes());
            out.close();    // ????????? ????????????
//            Toast.makeText(getApplicationContext(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
        }
        return Const.internalStorage + "/" + imgName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Click_button_result(View view) {

        if(bmRotated != null) {
        // ?????? ??????
        ModelEmotions currentModelEmotions = new ModelEmotions();

        // ?????? ??????, ?????? ??????
        LocalDateTime localDateTime = LocalDateTime.now();

        // ????????? ?????? ??????, ?????? ??????
        currentModelEmotions.setRegistrationDateTime(localDateTime);

        // ????????? ?????? ??????
        currentModelEmotions.setImageName(localDateTime.toString());

        // ????????? ?????? ?????? ?????? ??? ??????
        currentModelEmotions.setHappinessDegree(happy);
        currentModelEmotions.setSadnessDegree(sad);
        currentModelEmotions.setNeutralDegree(neutral);

        //????????? ?????? ??????????????? (?????? ?????? ????????? ??????)
        System.out.println("?????? ?????? ??????: " + bmRotated.getRowBytes());
        bmRotated = bmRotated.createScaledBitmap(bmRotated, bmRotated.getWidth() / 4, bmRotated.getHeight() / 4, true);
        System.out.println("?????? ?????? ??????: " + bmRotated.getRowBytes());

        // ?????? ??????(?????? ?????????)??? ??????
        // ????????? ?????? ??????????????? ????????? ????????? ?????? ??????
        String imgPath = SaveImage(bmRotated, currentModelEmotions.getImageName());
        currentModelEmotions.setImagePath(imgPath);

        // DB ??????
        FairyDBManager fairyDBManager = new FairyDBManager();
        fairyDBManager.save_values(fairyDBHelper, currentModelEmotions);

        // ????????? ????????????
        ShowResult(currentModelEmotions);
        }
        else {
            Toast.makeText(getApplicationContext(),"????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
        }


    }

    private void ShowResult(ModelEmotions modelEmotions) {

        textView_result.setText("??????: " + Const.ConvertDoubleToPercentage(modelEmotions.getHappinessDegree()) + "??????: " + Const.ConvertDoubleToPercentage(modelEmotions.getSadnessDegree()) + "?????????: " + Const.ConvertDoubleToPercentage(modelEmotions.getNeutralDegree()));

        // Google Analytics
        analytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle(); // logEvent()?????? ??????
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "?????? ?????? ??????");
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Click");
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
    }

    private void init_tables() {
        fairyDBHelper = new FairyDBHelper(this);
    }

    //?????? ?????? ????????? ???????????? ??????
    public static String createCopyAndReturnRealPath(@NonNull Context context, @NonNull Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null)
            return null;

        //?????? ????????? ??????
        String filePath = context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis();

        File file = new File(filePath);
        try {
            //??????????????? ?????? uri ??? ?????? ???????????? ????????? ???????????? ?????????
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;

            //????????? ???????????? ?????? ??????????????? file????????? ???????????? ?????? ??????
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();
        } catch (IOException ignore) {
            return null;
        }
        return file.getAbsolutePath();
    }

    //?????? ??????
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        //????????? ????????? ?????? ????????? ?????? ????????? ???
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    void BackgroundTask(InputStream... inputStreams) {
//onPreExecute
        ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.show();
        pd.setMessage("????????? ????????? ?????????.");

        backgroundtask = Observable.fromCallable(() -> {
//doInBackground

            FaceServiceClient.FaceAttributeType[] faceAttr = new FaceServiceClient.FaceAttributeType[]{
                    FaceServiceClient.FaceAttributeType.Emotion
            };

            try {
                Face[] result = faceServiceClient.detect(inputStreams[0],
                        true,           // returnFaceId
                        false,         // returnFaceLandmarks
                        faceAttr);        // returnFaceAttributes

                if (result == null) {
                }

                return result;
            } catch (Exception e) {
                return null;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext((ObservableSource<? extends Face[]>) throwable -> Observable.just(100,200,300))
                .subscribe(
                (result) -> {
//onPostExecute
                    if(result == null)
                    {
                        backgroundtask.dispose();
                    }

                    else{
                        pd.dismiss();

                        //Java?????? JSON ??????
                        Gson gson = new Gson();
                        String data = gson.toJson(result);

                        getResult(data);
                        backgroundtask.dispose();
                    }
                });
    }

    private void detectandFrame(final Bitmap mBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //bitmap ????????? ?????? ????????? ??????
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        final ByteArrayInputStream inputStream = new ByteArrayInputStream((outputStream.toByteArray()));

        // Rxjava ??????
        BackgroundTask(inputStream);
    }

    private void getResult(String data) {
        Gson gson = new Gson();
        Face[] face = gson.fromJson(data, Face[].class);

        TreeMap<Double, String> treeMap = new TreeMap<>();
        treeMap.put(face[0].faceAttributes.emotion.happiness, "Happiness");
        treeMap.put(face[0].faceAttributes.emotion.anger, "Anger");
        treeMap.put(face[0].faceAttributes.emotion.disgust, "Disgust");
        treeMap.put(face[0].faceAttributes.emotion.sadness, "Sadness");
        treeMap.put(face[0].faceAttributes.emotion.neutral, "Neutral");
        treeMap.put(face[0].faceAttributes.emotion.surprise, "Surprise");
        treeMap.put(face[0].faceAttributes.emotion.fear, "Fear");


        ArrayList<Double> arrayList = new ArrayList<>();
        TreeMap<Integer, String> rank = new TreeMap<>();

        int counter = 0;
        for (Map.Entry<Double, String> entry : treeMap.entrySet()) {
            String key = entry.getValue();
            Double value = entry.getKey();
            rank.put(counter, key);
            counter++;
            arrayList.add(value);
        }
        happy = 0;
        sad = 0;
        neutral = 0;

        for (int i = 1; i <= counter; i++) {
            if (rank.get(rank.size() - i) == "Happiness") {
                happy = arrayList.get(rank.size() - i);
            } else if (rank.get(rank.size() - i) == "Sadness") {
                sad = arrayList.get(rank.size() - i);
            } else if (rank.get(rank.size() - i) == "Neutral") {
                neutral = arrayList.get(rank.size() - i);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Click_button_history(View view) {
        // findViewById ???????????? ????????? ?????? ???????????? ??????. view ??????????????? ????????? ?????? ???????????? ????????? ?????? ????????????
        Button button = (Button) view;

        // button??? text??? String??? ???????????? (String)?????? casting??????.
        String folderName = (String) button.getText();
//        Toast.makeText(this, folderName, Toast.LENGTH_SHORT).show();

        // ??????????????? ?????? ?????????
        history_Adapter.clearItem();

        for (ModelEmotions modelEmotions : Const.List_ModelEmotions) {
            String fileName = modelEmotions.getRegistrationDateTime().format(DateTimeFormatter.ofPattern("yyyy???\nMM???\ndd???"));

            // String??? Value ????????? ?????? == ????????? equals ???????????? ???.
            if (folderName.equals(fileName)) {
                history_Adapter.addItem(modelEmotions);
            }
        }

        history_Adapter.notifyDataSetChanged();     // ?????? ????????? ????????????????????? ???

    }

    // "????????? ?????? ?????? ??????" ?????? ????????? ?????????
    public void Click_correctUserData(View view) {
        ImageButton imageButton = (ImageButton) view;

        AlertDialog.Builder alert_correctUserData = new AlertDialog.Builder(this);

        String alertTitle;
        String alertMessage;

        if (imageButton.getId() == R.id.imageButton_userName) {
            alertTitle = "????????? ??????";
            alertMessage = "????????? ??????????????????";
        }
        else if (imageButton.getId() == R.id.imageButton_userAge) {
            alertTitle = "????????? ??????";
            alertMessage = "????????? ??????????????????";
        }
        else {
            alertTitle = "";
            alertMessage = "";
        }

        // Alert??? ??????, ????????? ????????????
        alert_correctUserData.setTitle(alertTitle);
        alert_correctUserData.setMessage(alertMessage);

        // ?????????????????? ????????? ???????????? ?????? ??????
        EditText input = new EditText(this);
        alert_correctUserData.setView(input);

        alert_correctUserData.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (imageButton.getId() == R.id.imageButton_userName) {
                    String userName = input.getText().toString();
                    textView_userName.setText(userName);
                    // DB ????????????
                    Const.currentUserData.setUserName(userName);
                    fairyDBManager.save_userData(fairyDBHelper, Const.currentUserData);
                }
                else if (imageButton.getId() == R.id.imageButton_userAge) {
                    try {
                        int userAge = Integer.parseInt(input.getText().toString());
                        textView_userAge.setText(String.valueOf(userAge));
                        // DB ????????????
                        Const.currentUserData.setUserAge(userAge);
                        fairyDBManager.save_userData(fairyDBHelper, Const.currentUserData);
                    } catch (Exception e) {
                        textView_userAge.setText("????????? ??????????????????.");
                    }
                }
            }
        });

        alert_correctUserData.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // ?????????
            }
        });

        alert_correctUserData.show();

    }

    private void showAdvertiesment(){
        advertisementListViewAdapter = new AdvertisementListViewAdapter();
        listView = (ListView) findViewById(R.id.listView_advertisement);
        listView.setAdapter(advertisementListViewAdapter);

        advertisementListViewAdapter.addItem(new Advertisement());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Google Analytics
                analytics = FirebaseAnalytics.getInstance(MainActivity.this);
                Bundle bundle = new Bundle(); // logEvent()?????? ??????
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, view.getTag().toString());
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Link");
                analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(view.getTag().toString()));
                startActivity(intent);
            }
        });

    }

}