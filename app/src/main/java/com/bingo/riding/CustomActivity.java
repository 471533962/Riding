package com.bingo.riding;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bingo.riding.adapter.ViewPagerAdapter;
import com.bingo.riding.fragment.LoginFragment;
import com.bingo.riding.fragment.RegisterFragment;
import com.mingle.widget.ShapeLoadingDialog;

/**
 * Created by bingo on 15/10/8.
 */
public class CustomActivity extends AppCompatActivity {
    private ViewPager pagerContainer;
    private TabLayout pagerTabLayout;

    private ShapeLoadingDialog shapeLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        shapeLoadingDialog = new ShapeLoadingDialog(this);
        shapeLoadingDialog.setCanceledOnTouchOutside(false);

        //Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.parseColor("#FF5722"));
        }

        initView();
    }

    private void initView(){
        pagerContainer = (ViewPager)findViewById(R.id.pagerContainer);
        pagerTabLayout = (TabLayout)findViewById(R.id.pagerTabLayout);
        pagerTabLayout.setTabTextColors(Color.parseColor("#9E9E9E"),Color.parseColor("#FFFFFF"));

        setupViewPager(pagerContainer);
        pagerTabLayout.setupWithViewPager(pagerContainer);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new LoginFragment(), "登录");
        adapter.addFrag(new RegisterFragment(), "注册");
        viewPager.setAdapter(adapter);
    }

    public void showLoadingDialog(String loadingText){
        if (shapeLoadingDialog != null){
            shapeLoadingDialog.setLoadingText(loadingText);
            shapeLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog(){
        if (shapeLoadingDialog != null){
            shapeLoadingDialog.dismiss();
        }
    }
}
