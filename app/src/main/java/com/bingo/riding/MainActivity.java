package com.bingo.riding;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.bingo.riding.fragment.MeFragment;
import com.bingo.riding.fragment.RidingFragment;
import com.bingo.riding.fragment.SquareFragment;
import com.bingo.riding.utils.AVImClientManager;
import com.bingo.riding.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;

    private SquareFragment squareFragment;
    private MeFragment meFragment;
    private RidingFragment ridingFragment;
    private Fragment currentFragment;


    private TextView userName;
    private TextView userEmail;
    private ImageView userPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (AVUser.getCurrentUser() == null){
            Utils.startActivity(MainActivity.this, CustomActivity.class);
            Utils.finish(this);
            return;
        }

       initView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_index) {
            // Handle the camera action
            fragmentManager
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(squareFragment)
                    .commit();
            currentFragment = squareFragment;
        } else if (id == R.id.nav_me) {
            fragmentManager
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(meFragment)
                    .commit();
            currentFragment = meFragment;
        } else if (id == R.id.nav_riding){
            fragmentManager
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(ridingFragment)
                    .commit();
            currentFragment = ridingFragment;
        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_feedback) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navigationView.setNavigationItemSelectedListener(this);

        userEmail = (TextView) header.findViewById(R.id.userEmail);
        userName = (TextView) header.findViewById(R.id.userName);
        userPhoto = (ImageView) header.findViewById(R.id.userPhoto);

        userEmail.setText(AVUser.getCurrentUser().getEmail() + "");
        userName.setText(AVUser.getCurrentUser().getString("nikeName"));
        AVFile userPhotoFile = AVUser.getCurrentUser().getAVFile("userPhoto");
        if (userPhotoFile != null){
            Glide.with(this.getApplicationContext())
                    .load(userPhotoFile.getUrl())
                    .placeholder(R.drawable.default_photo)
                    .signature(new StringSignature(userPhotoFile.getUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(userPhoto);
        } else {
            userPhoto.setImageResource(R.drawable.default_photo);
        }

        initFragment();
    }

    private void initFragment(){
        squareFragment = new SquareFragment();
        ridingFragment = new RidingFragment();
        meFragment = new MeFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.activity_main_framelayout, squareFragment, "squareFragment")
                .add(R.id.activity_main_framelayout, ridingFragment, "squareFragment")
                .add(R.id.activity_main_framelayout, meFragment, "squareFragment")
                .hide(meFragment)
                .hide(ridingFragment)
                .show(squareFragment)
                .commit();

        currentFragment = squareFragment;
    }
}
