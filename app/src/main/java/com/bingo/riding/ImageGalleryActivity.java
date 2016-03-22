package com.bingo.riding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.FilePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

public class ImageGalleryActivity extends AppCompatActivity {

    private Intent intent;

    private GalleryViewPager mViewPager;
    private BasePagerAdapter pagerAdapter;

    private ArrayList<String> photosList;
    private ArrayList<String> photoDeleteList = new ArrayList<>();
    private Boolean photoManager;
    private int startPosition;
    private Boolean isFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_image_gallery);
        mViewPager = (GalleryViewPager)findViewById(R.id.galleryViewPager);
        mViewPager.setOffscreenPageLimit(3);

        intent = getIntent();
        if (intent != null){
            photosList = intent.getStringArrayListExtra("photos");
            photoManager = intent.getBooleanExtra("photoManager", false);
            startPosition = intent.getIntExtra("startPosition", 0);
            isFile = intent.getBooleanExtra("isFile", true);
        }

        if (photosList == null || photosList.size() == 0){
            this.finish();
            return;
        }else{
            photosList.remove(PublishActivity.ADDNEWIMAGE);
        }

        if (isFile == true){
            pagerAdapter = new FilePagerAdapter(getApplicationContext(), photosList);
        }else{
            pagerAdapter = new UrlPagerAdapter(getApplicationContext(), photosList);
        }

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(startPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (photoManager == true){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_image_gallery_menu, menu);
            return true;
        }else{
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.activity_image_gallery_delete:
                int position = mViewPager.getCurrentItem();
                photoDeleteList.add(photosList.get(position));
                photosList.remove(position);

                if (photosList.size() == 0){
                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putStringArrayListExtra("deletePhotoList", photoDeleteList);
                    //设置返回数据
                    setResult(RESULT_OK, intent);
                    //关闭Activity
                    ImageGalleryActivity.this.finish();
                    return true;
                }


                if (photosList.size() >= position){
                    mViewPager.setCurrentItem(position);
                }else{
                    mViewPager.setCurrentItem(0);
                }

                pagerAdapter.notifyDataSetChanged();
                break;
            case android.R.id.home:
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putStringArrayListExtra("deletePhotoList", photoDeleteList);
                //设置返回数据
                setResult(RESULT_OK, intent);
                //关闭Activity
                finish();
                break;
        }
        return true;
    }
}
