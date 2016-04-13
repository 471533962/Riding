package com.bingo.riding.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bingo.riding.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bingo on 15/10/12.
 */
public class RidingFragment extends Fragment implements LocationSource, AMapLocationListener, AMap.OnMapScreenShotListener {

    private boolean isRiding = false;
    private float distance = 0;
    private long time = 0;

    private Bundle savedInstanceState;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption aMapLocationClientOption;
    private AMapLocation lastLocation;
    private Polyline mPolyline;

    private MapView mapView;
    private AMap aMap;
    private LinearLayout riding_infor_layout;
    private TextView riding_distance;
    private TextView riding_time;
    private TextView average_speed;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (isRiding) {
                        updateView();
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
                case 0:
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//添加菜单不调用该方法是没有用的
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_riding_fragment, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.riding_start:
                if (riding_infor_layout.getVisibility() == View.VISIBLE){
                    aMap.getMapScreenShot(RidingFragment.this);


                    //保存用户骑行数据

                    aMapLocationClientOption.setInterval(10 * 1000);
                    mapLocationClient.setLocationOption(aMapLocationClientOption);
                    mapLocationClient.startLocation();

                } else {
                    riding_infor_layout.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(1);
                    isRiding = true;

                    distance = 0;
                    time = 0;
                    if (mPolyline != null){
                        mPolyline.remove();
                    }
                    //调整时间间隔
                    aMapLocationClientOption.setInterval(1 * 1000);
                    mapLocationClient.setLocationOption(aMapLocationClientOption);
                    mapLocationClient.startLocation();

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_riding, container, false);

        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mapLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mapLocationClient.onDestroy();
            mapLocationClient = null;
            aMapLocationClientOption = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (null == mapLocationClient){
            mapLocationClient = new AMapLocationClient(getActivity().getApplicationContext());

            if (aMapLocationClientOption == null){
                initLocationOption();
            }
            mapLocationClient.setLocationOption(aMapLocationClientOption);
            //设置定位监听
            mapLocationClient.setLocationListener(this);
            mapLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (null != mapLocationClient){
            mapLocationClient.stopLocation();
            mapLocationClient.onDestroy();
        }
        mapLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0){
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点

                if (aMapLocation.getAccuracy() < 20){

                    if (lastLocation == null){
                        lastLocation = aMapLocation;
                    } else {
                        LatLng endLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        LatLng startLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                        mPolyline = aMap.addPolyline(
                                new PolylineOptions()
                                        .add(endLatLng).geodesic(true).color(Color.BLUE)
                        );

                        distance += AMapUtils.calculateLineDistance(startLatLng, endLatLng);
                    }

                }
            }else{
                String errText = "定位失败," + aMapLocation.getErrorCode()+ ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String filePath = Environment.getExternalStorageDirectory() + File.separator + "Riding" + File.separator +
                sdf.format(new Date()) + ".png";
        try {
            // 保存在SD卡根目录下，图片为png格式。
            FileOutputStream fos = new FileOutputStream(filePath);
            boolean b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            try {
                fos.flush();

                File file = new File(filePath);
                if (file.exists()){
                    final AVObject ridingRoute = new AVObject("ridingRoute");
                    ridingRoute.put("belongUser", AVUser.getCurrentUser());
                    ridingRoute.put("ridingTime", time);
                    ridingRoute.put("ridingDistance", distance);
                    ridingRoute.put("averageSpeed", distance / time * 3.6);

                    final AVFile screenShot = AVFile.withAbsoluteLocalPath(file.getName(), file.getAbsolutePath());
                    screenShot.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null){
                                ridingRoute.put("screenShot", screenShot);
                            }

                            ridingRoute.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null){
                                        Toast.makeText(getActivity(), "保存到服务器成功", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getActivity(), "保存到服务器失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        riding_infor_layout.setVisibility(View.GONE);
        mHandler.sendEmptyMessage(0);
        isRiding = false;
    }

    private void initView(View view){
        mapView = (MapView) view.findViewById(R.id.bmapView);
        mapView.onCreate(savedInstanceState);

        riding_infor_layout = (LinearLayout) view.findViewById(R.id.riding_infor_layout);
        riding_distance = (TextView) view.findViewById(R.id.riding_distance);
        riding_time = (TextView) view.findViewById(R.id.riding_time);
        average_speed = (TextView) view.findViewById(R.id.average_speed);

        if (aMap == null){
            aMap = mapView.getMap();
            setUpAMap();
        }
        mapLocationClient = new AMapLocationClient(getActivity().getApplicationContext());

        // 设置定位监听
        mapLocationClient.setLocationListener(this);
        if (aMapLocationClientOption == null){
            initLocationOption();
        }
        mapLocationClient.setLocationOption(aMapLocationClientOption);
        mapLocationClient.startLocation();
    }

    public void initLocationOption(){
        aMapLocationClientOption = new AMapLocationClientOption();
        // 设置定位模式为GPS
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        //设置是否返回地址信息（默认返回地址信息）
        aMapLocationClientOption.setNeedAddress(false);
        //设置是否只定位一次,默认为false
        aMapLocationClientOption.setOnceLocation(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        aMapLocationClientOption.setMockEnable(true);
        //设置为高精度定位模式
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        aMapLocationClientOption.setInterval(10 * 1000);
    }

    public void setUpAMap(){
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);
        aMap.setMapType(AMap.LOCATION_TYPE_LOCATE);
    }

    private void updateView(){
        time += 1;
        int hour = (int) ((time / 60) / 60) % 60;
        int minute = (int) (time / 60) % 60;
        int second = (int) (time % 60);

        String out_time = hour < 10 ? "0" + hour : "" + hour;
        out_time += ":";
        out_time += minute < 10 ? "0" + minute : "" + minute;
        out_time += ":";
        out_time += second < 10 ? "0" + second : "" + second;

        riding_time.setText(out_time);

        DecimalFormat fnum = new DecimalFormat("##0.00");

        riding_distance.setText( fnum.format(distance / 1000) + "");
        double speed_val = distance / time * 3.6;
        average_speed.setText( fnum.format(speed_val) + "");
    }
}
