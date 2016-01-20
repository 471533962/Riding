package com.bingo.riding.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.avos.avoscloud.LogUtil;
import com.bingo.riding.R;

/**
 * Created by bingo on 15/10/12.
 */
public class RidingFragment extends Fragment implements LocationSource, AMapLocationListener{

    private Bundle savedInstanceState;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mapLocationClient;
    private AMapLocationClientOption aMapLocationClientOption;

    private MapView mapView;
    private AMap aMap;
    private RelativeLayout search_layout;
    private Button search_place_btn;
    private EditText search_edit_text;

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
            case R.id.riding_guide:
                if (item.isChecked()){
                    item.setIcon(R.drawable.ic_near_me_white_24dp);
                    item.setChecked(false);
                    search_layout.setVisibility(View.INVISIBLE);
                }else{
                    item.setIcon(R.drawable.ic_near_me_grey_500_24dp);
                    item.setChecked(true);
                    search_layout.setVisibility(View.VISIBLE);

                }
                return true;
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

            mapLocationClient.setLocationOption(aMapLocationClientOption);
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
        LogUtil.avlog.e("OnLocationChanged: latitude: " + aMapLocation.getLatitude() + " longitude:" + aMapLocation.getLongitude());
        if (mListener != null && aMapLocation != null) {
            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
        }
    }

    private void initView(View view){
        mapView = (MapView) view.findViewById(R.id.bmapView);
        mapView.onCreate(savedInstanceState);

        aMap = mapView.getMap();
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);

        search_layout = (RelativeLayout) view.findViewById(R.id.search_layout);
        search_place_btn = (Button) view.findViewById(R.id.search_place_btn);
        search_edit_text = (EditText) view.findViewById(R.id.search_edit_text);


        mapLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        aMapLocationClientOption = new AMapLocationClientOption();
        // 设置定位模式为GPS
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
        //设置是否返回地址信息（默认返回地址信息）
        aMapLocationClientOption.setNeedAddress(false);
        //设置是否只定位一次,默认为false
        aMapLocationClientOption.setOnceLocation(false);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        aMapLocationClientOption.setMockEnable(true);
        //设置定位间隔,单位毫秒,默认为2000ms
        aMapLocationClientOption.setInterval(10 * 1000);
        // 设置定位监听
        mapLocationClient.setLocationOption(aMapLocationClientOption);
        mapLocationClient.setLocationListener(this);
        mapLocationClient.startLocation();
    }
}
