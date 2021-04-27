package com.example.kickboardguard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
// 공공 데이터 부분
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
// 공공 데이터 부분
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity implements net.daum.mf.map.api.MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, LocationListener {

    private static final String LOG_TAG = "MainActivity";
    String html = "http://apis.data.go.kr/B552468/acdntFreqocZone/getAcdntFreqocZone";
    private MapView mMapView;
    ListView listview = null;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    private Home home;
    private Settings settings;
    private Helmet helmet;
    private Sensor sensor;

    private LocationManager locationManager;
    private Location mLastlocation = null;
    private double speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        // 프레그먼트 설정 ##########################################################################

        home = new Home();
        settings = new Settings();
        helmet = new Helmet();
        sensor = new Sensor();
        // 프레그먼트 설정 ##########################################################################


        // 메뉴 설정 ################################################################################
        final String[] items = {"Home", "Setting", "sensor", "BLUE", "BLACK"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        listview = (ListView) findViewById(R.id.drawer_menulist);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                switch (position) {
                    case 0: // Home
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, home).commit();
                        break;
                    case 1: // Setting
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, settings).commit();
                        break;
                    case 2: // sensor
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, sensor).commit();
                        break;
                    case 3: // Helmet
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, helmet).commit();
                        break;
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
        // 메뉴 설정 ################################################################################




        // 카카오 맵 설정 ############################################################################
        mMapView = (net.daum.mf.map.api.MapView) findViewById(R.id.map_view);
        //mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setCurrentLocationEventListener(this);
        //mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        } else {

            checkRunTimePermission();
        }
        // 카카오 맵 설정 ############################################################################





        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String formatDate = sdf.format(new Date(lastKnownLocation.getTime()));
        }
        // GPS 사용 가능 여부 확인
        boolean isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);



        // 공공 데이터 ##############################################################################
        StrictMode.enableDefaults();


        boolean inresultCode = false, inresultMsg = false, intotalCount = false, innumOfRows = false, inpageNo = false;
        boolean infreqocZoneVer = false, infreqocZoneId = false, infreqocZoneNm = false, insignguCode = false, insignguNm=false;
        boolean inacdntCo = false, incenterX= false , incenterY = false , inzoneRds = false;

        String resultCode = null, resultMsg = null, totalCount = null, numOfRows = null, pageNo = null, freqocZoneVer=null, freqocZoneId=null, freqocZoneNm=null;
        String signguCode = null, signguNm = null, acdntCo = null, centerX = null, centerY = null, zoneRds = null;


        try{
            URL url = new URL("http://apis.data.go.kr/B552468/acdntFreqocZone/getAcdntFreqocZone?" +
                    "serviceKey=sY6y0bVXhsk6jkopIZpTWSZAAXLGLYJB1Tg1O%2B0f%2BcqvmV2Pe9P1Yx7Ne3JolOMxBbHcjEba%2BsXRABa4ZUUtyQ%3D%3D" +       //서비스키
                    "&numOfRows=100" +           //한 페이지 결과 수
                    "&pageNo=1" +               //페이지 번호
                    "&signguCode=46110" +       //시군구코드
                    "&datatype=XML"             //데이터 유형
            ); //검색 URL부분

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            System.out.println("파싱시작합니다.");

            while (parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                        if(parser.getName().equals("resultCode")){ //title 만나면 내용을 받을수 있게 하자
                            inresultCode = true;
                        }
                        if(parser.getName().equals("resultMsg")){ //address 만나면 내용을 받을수 있게 하자
                            inresultMsg = true;
                        }
                        if(parser.getName().equals("totalCount")){ //mapx 만나면 내용을 받을수 있게 하자
                            intotalCount = true;
                        }
                        if(parser.getName().equals("numOfRows")){ //mapy 만나면 내용을 받을수 있게 하자
                            innumOfRows = true;
                        }
                        if(parser.getName().equals("pageNo")){ //mapy 만나면 내용을 받을수 있게 하자
                            inpageNo = true;
                        }
                        if(parser.getName().equals("freqocZoneVer")){ //mapy 만나면 내용을 받을수 있게 하자
                            infreqocZoneVer = true;
                        }
                        if(parser.getName().equals("freqocZoneId")){ //mapy 만나면 내용을 받을수 있게 하자
                            infreqocZoneId = true;
                        }
                        if(parser.getName().equals("freqocZoneNm")){ //mapy 만나면 내용을 받을수 있게 하자
                            infreqocZoneNm = true;
                        }
                        if(parser.getName().equals("signguCode")){ //mapy 만나면 내용을 받을수 있게 하자
                            insignguCode = true;
                        }
                        if(parser.getName().equals("signguNm")){ //mapy 만나면 내용을 받을수 있게 하자
                            insignguNm = true;
                        }
                        if(parser.getName().equals("acdntCo")){ //mapy 만나면 내용을 받을수 있게 하자
                            inacdntCo = true;
                        }
                        if(parser.getName().equals("centerX")) { //mapy 만나면 내용을 받을수 있게 하자
                            incenterX = true;
                        }
                        if(parser.getName().equals("centerY")) { //mapy 만나면 내용을 받을수 있게 하자
                            incenterY = true;
                        }
                        if(parser.getName().equals("zoneRds")) { //mapy 만나면 내용을 받을수 있게 하자
                            inzoneRds = true;
                        }
                        if(parser.getName().equals("resultMsg")){ //message 태그를 만나면 에러 출력
                            Log.i("에러 :", resultMsg+ "에러");
                            //여기에 에러코드에 따라 다른 메세지를 출력하도록 할 수 있다.
                        }
                        break;

                    case XmlPullParser.TEXT://parser가 내용에 접근했을때
                        if(intotalCount){ //isTitle이 true일 때 태그의 내용을 저장.
                            totalCount = parser.getText();
                            intotalCount = false;
                        }
                        if(innumOfRows){ //isAddress이 true일 때 태그의 내용을 저장.
                            numOfRows = parser.getText();
                            innumOfRows = false;
                        }
                        if(inpageNo){ //isMapx이 true일 때 태그의 내용을 저장.
                            pageNo = parser.getText();
                            inpageNo = false;
                        }
                        if(infreqocZoneVer){ //isMapy이 true일 때 태그의 내용을 저장.
                            freqocZoneVer = parser.getText();
                            infreqocZoneVer = false;
                        }
                        if(infreqocZoneId){ //isMapy이 true일 때 태그의 내용을 저장.
                            freqocZoneId = parser.getText();
                            infreqocZoneId = false;
                        }
                        if(infreqocZoneNm){ //isMapy이 true일 때 태그의 내용을 저장.
                            freqocZoneNm = parser.getText();
                            infreqocZoneNm = false;
                        }
                        if(insignguCode){ //isMapy이 true일 때 태그의 내용을 저장.
                            signguCode = parser.getText();
                            insignguCode = false;
                        }
                        if(insignguNm){ //isMapy이 true일 때 태그의 내용을 저장.
                            signguNm = parser.getText();
                            insignguNm = false;
                        }
                        if(inacdntCo){ //isMapy이 true일 때 태그의 내용을 저장.
                            acdntCo = parser.getText();
                            inacdntCo = false;
                        }
                        if(incenterX){ //isMapy이 true일 때 태그의 내용을 저장.
                            centerX = parser.getText();
                            incenterX = false;
                        }
                        if(incenterY){ //isMapy이 true일 때 태그의 내용을 저장.
                            centerY = parser.getText();
                            incenterY = false;
                        }
                        if(inzoneRds){ //isMapy이 true일 때 태그의 내용을 저장.
                            zoneRds = parser.getText();
                            inzoneRds = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("item")){
                            Log.d("data","총건수 : "+ totalCount +"\n 한 페이지 결과수: "+ numOfRows +"\n 페이지  번호 : " + pageNo
                                    +"\n 다발구역버전 : " + freqocZoneVer +  "\n 다발구역아이디 : " + freqocZoneId+ "\n 다발구역명 : " + freqocZoneNm
                                    +"\n 시군구코드 : " +signguCode + "\n 시군구명 : " + signguNm + "\n 사고건수 : " +acdntCo
                                    +"\n 중심X : " +centerX +"\n 중심Y : " +centerY+"\n"+"\n 구역반경 : " +zoneRds+"\n");


                            MapCircle circle = new MapCircle(
                                    MapPoint.mapPointWithGeoCoord(Double.parseDouble(centerY), Double.parseDouble(centerX)), // center
                                    Integer.parseInt(zoneRds), // radius
                                    Color.argb(128, 255, 0, 0), // strokeColor
                                    Color.argb(128, 0, 255, 0) // fillColor
                            );
                            //circle.setTag(Integer.parseInt(freqocZoneId));
                            mMapView.addCircle(circle);

                        }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch(Exception e){
            Log.i("에러","에러발생");
        }
        // 공공 데이터 ##############################################################################





    }

    // 속도 설정 #################################################################################
    @Override
    public void onLocationChanged(Location location) {
        // 속도 설정 #################################################################################
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        double deltaTime = 0;
        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //  getSpeed() 함수를 이용하여 속도를 계산
        if (lastKnownLocation != null) {
            sdf = new SimpleDateFormat("HH:mm:ss");
            String formatDate = sdf.format(new Date(lastKnownLocation.getTime()));
            Log.d("Time",formatDate);  //Time
        }
        double getSpeed = Double.parseDouble(String.format("%.3f", lastKnownLocation.getSpeed()));
        Log.d("Get Speed" , String.valueOf(getSpeed));  //Get Speed
        String formatDate = sdf.format(new Date(lastKnownLocation.getTime()));
        Log.d("Time",formatDate);  //Time

        // 위치 변경이 두번째로 변경된 경우 계산에 의해 속도 계산
        if(mLastlocation != null) {
            //시간 간격
            deltaTime = (lastKnownLocation.getTime() - mLastlocation.getTime()) / 1000.0;
            Log.d("Time difference" ,deltaTime + " sec");  // Time Difference
            Log.d("// Time Difference", mLastlocation.distanceTo(lastKnownLocation) + " m");  // Time Difference
            // 속도 계산
            speed = mLastlocation.distanceTo(lastKnownLocation) / deltaTime;

            String formatLastDate = sdf.format(new Date(mLastlocation.getTime()));
            Log.d("Last Time",formatLastDate);

            double calSpeed = Double.parseDouble(String.format("%.3f", speed));
            double kmhcalSpeed=3.6*calSpeed;
            if(kmhcalSpeed>25){
                Toast.makeText(this.getApplicationContext(),"위험 25km/h 초과했습니다",
                        Toast.LENGTH_SHORT).show();
                MediaPlayer player=MediaPlayer.create(this,R.raw.beep);
                player.start();


            }
            Log.d("Cal Speed", String.valueOf(kmhcalSpeed));
        }
        // 현재위치를 지난 위치로 변경
        mLastlocation = lastKnownLocation;

        // 속도 설정 #################################################################################
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // 위치정보 업데이트
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, this);
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //권한 체크
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // 위치정보 업데이트
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 위치정보 가져오기 제거
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

    }


    public void data() throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552468/acdntFreqocZone"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=서비스키"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + URLEncoder.encode("-", "UTF-8")); /*공공데이터포털에서 받은 인증키*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("XML", "UTF-8")); /*결과형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("signguCode","UTF-8") + "=" + URLEncoder.encode("11110", "UTF-8")); /*검색을 원하는 시군구 코드 *시군구 코드 참조*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        Log.d("data test","************************************");
        Log.d("data test", sb.toString());



    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.setCurrentLocationTrackingMode(net.daum.mf.map.api.MapView.CurrentLocationTrackingMode.TrackingModeOff);
        mMapView.setShowCurrentLocationMarker(false);
    }

    @Override
    public void onCurrentLocationUpdate(net.daum.mf.map.api.MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));

    }


    @Override
    public void onCurrentLocationDeviceHeadingUpdate(net.daum.mf.map.api.MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(net.daum.mf.map.api.MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(net.daum.mf.map.api.MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }




    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {
                Log.d("@@@", "start");
                //위치 값을 가져올 수 있음
                mMapView.setCurrentLocationTrackingMode(net.daum.mf.map.api.MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
            mMapView.setCurrentLocationTrackingMode(net.daum.mf.map.api.MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }



    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


    }

}

