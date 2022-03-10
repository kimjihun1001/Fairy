package com.jihoon.fairy;
// Ver 0.4

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

    //광고
    AdvertisementListViewAdapter advertisementListViewAdapter;
    ArrayList<Advertisement> advertisements;
    ListView listView;

    // 그래프 그리기
    LineChart chart;

    //감정 모음
    int count;
    private ArrayList<Integer> emotionOfsad;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 내부 저장소 경로
        Const.internalStorage = this.getFilesDir();
        // AssetsManager
        Const.assetManager = getResources().getAssets();
        // 사용자 정보 초기화
        Const.currentUserData = new ModelUserData();

        imageView = findViewById(R.id.imageView);
        textView_result = findViewById(R.id.textView_result);
        textView_userName = findViewById(R.id.textView_userName);
        textView_userAge = findViewById(R.id.textView_userAge);

        advertisementListViewAdapter = new AdvertisementListViewAdapter();

        // DBHelper 초기화
        init_tables();

        // DB 매니저 - 싱글톤 변경 필요함
        fairyDBManager = new FairyDBManager();

        // DB 불러오기 + App의 Const.currentUserDate에 데이터 저장
        fairyDBManager.load_userData(fairyDBHelper);
        System.out.println("사용자 정보 불러오기");

        // DB 불러오기 + App의 Const List에 데이터 저장
        fairyDBManager.load_values(fairyDBHelper);

        // 이미 초기 예시 데이터 추가했는지 확인하기
        for (ModelEmotions modelEmotions : Const.List_ModelEmotions) {
            if (modelEmotions.getImageName().equals("2022-02-05T10:30:30")) {
                Const.isInitialDataAdded = true;
            }
        }
        // 초기 예시 데이터 만들기 + DB에 추가 + App의 Const List에 데이터 저장
        ExampleDataMaker exampleDataMaker = new ExampleDataMaker();
        if (Const.isInitialDataAdded == false) {
            for (ModelEmotions modelEmotions1 : exampleDataMaker.MakeExampleData()) {
                fairyDBManager.save_values(fairyDBHelper, modelEmotions1);
            }
        }

        //Azure Face API 사용
        faceServiceClient = new FaceServiceRestClient(apiEndpoint, subscriptionKey);

        // 앱이 켜지면 홈 화면 탭이 선택된 상태로 만들기
        // 홈 화면 탭에 대한 FrameLayout 내의 화면은 XML에서 visible로 설정되어있음.
        // 왜인지 모르겠지만 이 코드로는 탭 선택이 안됨. 오류 발생
        // 그래서 그냥 홈, 기록, 설정 순서로 탭 순서 변경.
        // TabItem tabItem_home = (TabItem) findViewById(R.id.tabItem_home);
        // tabItem_home.setSelected(true);

        // 탭 선택 이벤트 핸들러 설정하기
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                // 선택한 탭에 대한 화면을 표시하는 메쏘드
                ChangeView(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // 기록 탭 리스트뷰와 어뎁터 연결하기
        history_Adapter = new PhotoHistoryListViewAdapter();
        history_ListView = (ListView) findViewById(R.id.listView_historyPhoto);
        history_ListView.setAdapter(history_Adapter);

        AddAdvertiesment();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(view.getTag().toString()));
                startActivity(intent);
            }
        });
    }

    // 그래프 그리기
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void drawChart() {
        chart = (LineChart) findViewById(R.id.chart);

        List<Entry> happyEntries = new ArrayList<Entry>();
        List<Entry> sadEntries = new ArrayList<Entry>();
        emotionOfsad = new ArrayList<Integer>();

        // 날짜별로 데이터를 묶기 위해 map을 만들기
        Map<LocalDate, List<ModelEmotions>> map = new HashMap<>();
        for (ModelEmotions modelEmotions : Const.List_ModelEmotions) {
            LocalDate localDate = modelEmotions.getRegistrationDateTime().toLocalDate();

            if (map.containsKey(localDate)) {
                // 해당 날짜를 key로 검색해서 value에 modelemotions 추가하기
                map.get(localDate).add(modelEmotions);
            } else {
                List<ModelEmotions> List_emotionsOfDay = new ArrayList<>();
                List_emotionsOfDay.add(modelEmotions);
                map.put(localDate, List_emotionsOfDay);
            }
        }

        // map에 있는 데이터를 그래프로 넣기
        // map을 key값(LocalDate)에 대해 내림차순으로 정렬하기
        ArrayList<LocalDate> mapKey = new ArrayList<LocalDate>(map.keySet());
        mapKey.sort(Comparator.naturalOrder());
        // ValueFormatter만들 때 사용할 리스트 - float -> String
        // X축의 label로 사용할 리스트
        List<String> List_localDateStr = new ArrayList<>();

        // x축 간격
        float valueOfX = 0;
        for (LocalDate localDate : mapKey) {
            System.out.println(localDate.toString());
            // map의 key값을 X축 label로 사용하기
            String localDateStr = String.valueOf(localDate);
            // 년도 빼고 월 일만 표시하기
            List_localDateStr.add(localDateStr.substring(5, 10));

            // 같은 날의 감정을 평균 내기
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





            // 100 곱하고 소수점 둘째자리에서 반올림 - 예: 0.123456 -> 1234.56 -> 1235 -> 12.35
            float valueOfY_happy = Math.round(averageOfHappy * 10000)/100;
            float valueOfY_sad = Math.round(averageOfSad * 10000)/100;

            //리스트에 슬픔 감정 추가
            emotionOfsad.add((int) valueOfY_sad);

            // 데이터 넣기
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

        // Legend에 대해 설정하기
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
        // 레이블 간격
        xAxis.setSpaceMax(1f);
        xAxis.setSpaceMin(1f);
        // 축을 숫자가 아니라 날짜로 표시
        xAxis.setValueFormatter(new IndexAxisValueFormatter(List_localDateStr));
        // TODO : 이게 데이터 수가 많아지면 오류가 생기네 자꾸...
        // 축 레이블 표시 간격 : 2로 하면 2칸마다 레이블 표시
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
        // Y축 레이블 뒤에 % 붙여줌
        yAxis.setValueFormatter(new PercentFormatter());

        // 차트에 데이터 연결
        LineDataSet dataSet = new LineDataSet(happyEntries, "기쁨");
        dataSet.setColor(Color.rgb(251, 99, 118));
        dataSet.setValueTextColor(Color.rgb(251, 99, 118));
        dataSet.setValueTextSize(10);
        dataSet.setLineWidth(2);
        dataSet.setCircleColor(Color.BLACK);
        // 원 둘레 굵기
        // dataSet.setCircleRadius(15);
        // 데이터 값 뒤에 % 붙여줌
        dataSet.setValueFormatter(new PercentFormatter());

        LineDataSet dataSet2 = new LineDataSet(sadEntries, "슬픔");
        dataSet2.setColor(Color.rgb(82, 122, 184));
        dataSet2.setValueTextColor(Color.rgb(82, 122, 184));
        dataSet2.setValueTextSize(10);
        dataSet2.setLineWidth(2);
        dataSet2.setCircleColor(Color.BLACK);
        // 데이터 값 뒤에 % 붙여줌
        dataSet2.setValueFormatter(new PercentFormatter());

        LineData lineData = new LineData(dataSet, dataSet2);
        chart.setData(lineData);
        chart.invalidate(); // refresh -> 안됨 ...

        // 최대 x좌표 기준으로 몇개를 보여줄지
        chart.setVisibleXRange(5, 5);
        // 가장 최근에 추가한 데이터의 위치로 이동처리
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

    // 탭 선택 시, 표시 화면 변경하기
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
                break;
            case 1:
                // TODO : 그래프 새로고침
                drawChart();
                showAdvice();

                layout_home.setVisibility(View.INVISIBLE);
                scrollView_history.setVisibility(View.VISIBLE);
                layout_photoHistory.setVisibility(View.INVISIBLE);
                layout_setting.setVisibility(View.INVISIBLE);
                break;
            case 2:
                // 리사이클러뷰 아이템 생성
                ArrayList<ModelEmotions> Sort_Date_List_ModelEmotions = new ArrayList<ModelEmotions>();
                FairyDBManager fairyDBManager = new FairyDBManager();
                fairyDBManager.load_sort_values(fairyDBHelper, Sort_Date_List_ModelEmotions);

                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < Sort_Date_List_ModelEmotions.size(); i++) {
                    String tempString;
                    tempString = Sort_Date_List_ModelEmotions.get(i).getRegistrationDateTime().format(DateTimeFormatter.ofPattern("yyyy년\nMM월\ndd일"));

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
                break;
            case 3:
                textView_userName.setText(Const.currentUserData.getUserName());
                if (Const.currentUserData.getUserAge() == 0) {
                    textView_userAge.setText("나이를 입력해주세요");
                }
                else {
                    textView_userAge.setText(String.valueOf(Const.currentUserData.getUserAge()));
                }

                layout_home.setVisibility(View.INVISIBLE);
                scrollView_history.setVisibility(View.INVISIBLE);
                layout_photoHistory.setVisibility(View.INVISIBLE);
                layout_setting.setVisibility(View.VISIBLE);
                break;
        }
    }

    // 이미지 선택 누르면 실행됨 이미지 고를 갤러리 오픈
    public void Click_button_upload(View view) {
        Toast.makeText(this, "업로드 버튼 클릭", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    // 갤러리에서 이미지를 선택하면 요청이 전송됨
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 갤러리
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();

                // 사진파일 실제 주소 얻기.
                String realPath = createCopyAndReturnRealPath(this, fileUri);

                //회전 여부 알기
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

                    //이미지 회전시키기
                    bmRotated = rotateBitmap(imgBitmap, orientation);
                    int a = bmRotated.getHeight();

                    if (a > 1000)
                    {
                        bmRotated = bmRotated.createScaledBitmap(bmRotated, bmRotated.getWidth() / 4, bmRotated.getHeight() / 4, true);
                    }

                    imageView.setImageBitmap(bmRotated);    // 선택한 이미지 이미지뷰에 표시
                    // Toast.makeText(getApplicationContext(), "사진 불러오기 성공", Toast.LENGTH_SHORT).show();
                    textView_result.setText("사진이 업로드되었습니다. 결과 확인을 눌러주세요.");
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "사진 불러오기 실패", Toast.LENGTH_SHORT).show();
                }

                detectandFrame(bmRotated);
            }
        }
    }

    public String SaveImage(Bitmap imgBitmap, String imgName) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(Const.internalStorage, imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            // 아래 코드로 확인해보니, quality를 낮춰서 저장하는 것은 파일 크기에 영향을 주지 않음.
            // System.out.println("축소 파일에서 퀄리티 낮춘 크기: " + imgBitmap.getRowBytes());
            out.close();    // 스트림 닫아주기
//            Toast.makeText(getApplicationContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
        }
        return Const.internalStorage + "/" + imgName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Click_button_result(View view) {

        // 객체 생성
        ModelEmotions currentModelEmotions = new ModelEmotions();

        // 현재 날짜, 시간 측정
        LocalDateTime localDateTime = LocalDateTime.now();

        // 객체에 생성 날짜, 시간 부여
        currentModelEmotions.setRegistrationDateTime(localDateTime);

        // 객체에 이름 부여
        currentModelEmotions.setImageName(localDateTime.toString());

        // 객체에 감정 분석 결과 값 부여
        currentModelEmotions.setHappinessDegree(happy);
        currentModelEmotions.setSadnessDegree(sad);
        currentModelEmotions.setNeutralDegree(neutral);

        //이미지 화질 다운시키기 (로딩 속도 증가를 위한)
        System.out.println("원본 파일 크기: " + bmRotated.getRowBytes());
        bmRotated = bmRotated.createScaledBitmap(bmRotated, bmRotated.getWidth() / 4, bmRotated.getHeight() / 4, true);
        System.out.println("축소 파일 크기: " + bmRotated.getRowBytes());

        // 사진 로컬(내부 저장소)에 저장
        // 이미지 경로 반환받아서 객체에 이미지 경로 저장
        String imgPath = SaveImage(bmRotated, currentModelEmotions.getImageName());
        currentModelEmotions.setImagePath(imgPath);

        // DB 저장
        FairyDBManager fairyDBManager = new FairyDBManager();
        fairyDBManager.save_values(fairyDBHelper, currentModelEmotions);

        // 화면에 표시하기
        ShowResult(currentModelEmotions);
    }

    private void ShowResult(ModelEmotions modelEmotions) {

        textView_result.setText("기쁨: " + Const.ConvertDoubleToPercentage(modelEmotions.getHappinessDegree()) + "슬픔: " + Const.ConvertDoubleToPercentage(modelEmotions.getSadnessDegree()) + "무표정: " + Const.ConvertDoubleToPercentage(modelEmotions.getNeutralDegree()));

    }

    private void init_tables() {
        fairyDBHelper = new FairyDBHelper(this);
    }

    //파일 절대 경로를 알아오는 코드
    public static String createCopyAndReturnRealPath(@NonNull Context context, @NonNull Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null)
            return null;

        //파일 경로를 만듬
        String filePath = context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis();

        File file = new File(filePath);
        try {
            //매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러옴
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;

            //이미지 데이터를 다시 내보내면서 file객체에 만들었던 경로 이용
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

    //파일 회전
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        //돌아간 정도를 얻고 그만큼 다시 돌리는 코
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
        pd.setMessage("잠시만 기다려 주세요.");

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

                        //Java에서 JSON 사용
                        Gson gson = new Gson();
                        String data = gson.toJson(result);

                        getResult(data);
                        backgroundtask.dispose();
                    }
                });
    }

    private void detectandFrame(final Bitmap mBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //bitmap 크기를 압축 시키는 코드
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        final ByteArrayInputStream inputStream = new ByteArrayInputStream((outputStream.toByteArray()));

        // Rxjava 코드
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
        // findViewById 사용해서 고정된 애를 받아오면 안됨. view 파라미터를 통해서 클릭 이벤트가 발생한 애를 받아오기
        Button button = (Button) view;

        // button의 text가 String이 아니라서 (String)으로 casting해줌.
        String folderName = (String) button.getText();
//        Toast.makeText(this, folderName, Toast.LENGTH_SHORT).show();

        // 리스트뷰의 목록 초기화
        history_Adapter.clearItem();

        for (ModelEmotions modelEmotions : Const.List_ModelEmotions) {
            String fileName = modelEmotions.getRegistrationDateTime().format(DateTimeFormatter.ofPattern("yyyy년\nMM월\ndd일"));

            // String의 Value 비교할 때는 == 아니라 equals 사용해야 함.
            if (folderName.equals(fileName)) {
                history_Adapter.addItem(modelEmotions);
            }
        }

        history_Adapter.notifyDataSetChanged();     // 변화 생기면 업데이트되도록 함

    }

    // "사용자 정보 수정 버튼" 클릭 이벤트 핸들러
    public void Click_correctUserData(View view) {
        ImageButton imageButton = (ImageButton) view;

        AlertDialog.Builder alert_correctUserData = new AlertDialog.Builder(this);

        String alertTitle;
        String alertMessage;

        if (imageButton.getId() == R.id.imageButton_userName) {
            alertTitle = "사용자 이름";
            alertMessage = "이름을 입력해주세요";
        }
        else if (imageButton.getId() == R.id.imageButton_userAge) {
            alertTitle = "사용자 나이";
            alertMessage = "나이를 입력해주세요";
        }
        else {
            alertTitle = "";
            alertMessage = "";
        }

        // Alert의 제목, 메시지 설정하기
        alert_correctUserData.setTitle(alertTitle);
        alert_correctUserData.setMessage(alertMessage);

        // 사용자로부터 텍스트 입력받기 위한 박스
        EditText input = new EditText(this);
        alert_correctUserData.setView(input);

        alert_correctUserData.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (imageButton.getId() == R.id.imageButton_userName) {
                    String userName = input.getText().toString();
                    textView_userName.setText(userName);
                    // DB 업데이트
                    Const.currentUserData.setUserName(userName);
                    fairyDBManager.save_userData(fairyDBHelper, Const.currentUserData);
                }
                else if (imageButton.getId() == R.id.imageButton_userAge) {
                    try {
                        int userAge = Integer.parseInt(input.getText().toString());
                        textView_userAge.setText(String.valueOf(userAge));
                        // DB 업데이트
                        Const.currentUserData.setUserAge(userAge);
                        fairyDBManager.save_userData(fairyDBHelper, Const.currentUserData);
                    } catch (Exception e) {
                        textView_userAge.setText("숫자만 입력해주세요.");
                    }
                }
            }
        });

        alert_correctUserData.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 취소됨
            }
        });

        alert_correctUserData.show();

    }

    private void AddAdvertiesment(){
        advertisementListViewAdapter = new AdvertisementListViewAdapter();
        listView = (ListView) findViewById(R.id.listView_advertisement);

        setData();

        listView.setAdapter(advertisementListViewAdapter);
    }

    private void setData(){
        Advertisement advertisement = new Advertisement();

        advertisementListViewAdapter.addItem(advertisement);
    }
}