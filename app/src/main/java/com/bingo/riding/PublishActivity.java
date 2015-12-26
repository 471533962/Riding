package com.bingo.riding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bingo.riding.adapter.MessagePhotoAdapter;
import com.bingo.riding.service.PublishMessageService;
import com.bingo.riding.utils.DataTools;
import com.bingo.riding.utils.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class PublishActivity extends AppCompatActivity {
    public static final String ADDNEWIMAGE = R.drawable.zx + "";

    private ArrayList<String> photoList = new ArrayList<>();

    private static final int REQUEST_IMAGE = 2;
    private static final int REQUEST_IMAGE_DELETE = 3;

    private Toolbar toolbar;

    private GridView photoGridView;
    private EditText publish_message_content;
    private MessagePhotoAdapter messagePhotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        //Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.parseColor("#3F51B5"));
        }

        initView();
    }

    private void initView(){
        /* setup toolbar */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("发表状态");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photoGridView = (GridView) findViewById(R.id.photo_select);
        publish_message_content = (EditText) findViewById(R.id.publish_message_content);

        initData();
    }

    private void initData(){
        photoList.add(ADDNEWIMAGE);
        messagePhotoAdapter = new MessagePhotoAdapter(PublishActivity.this, photoList);
        photoGridView.setAdapter(messagePhotoAdapter);

        initListener();
    }

    private void initListener(){
        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (photoList.get(position).equals(ADDNEWIMAGE)){
                    Intent intent = new Intent(PublishActivity.this, MultiImageSelectorActivity.class);
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 10 - photoList.size());
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                    startActivityForResult(intent, REQUEST_IMAGE);
                }else {
                    Intent intent = new Intent(PublishActivity.this, ImageGalleryActivity.class);
                    intent.putStringArrayListExtra("photos", photoList);
                    intent.putExtra("photoManager", true);
                    intent.putExtra("startPosition", position);
                    intent.putExtra("isFile", true);
                    startActivityForResult(intent, REQUEST_IMAGE_DELETE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                // Get the result list of select image paths
                List<String> photoPathList = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // display pic in view
                //先移除添加图片的那张图
                photoList.remove(ADDNEWIMAGE);
                for (String path : photoPathList){
                    if (photoList.contains(path) == false){
                        photoList.add(path);
                    }
                }
                //添加添加图片的那张图
                photoList.add(ADDNEWIMAGE);
                //显示出来
                messagePhotoAdapter.notifyDataSetChanged();
            }
        }else if (requestCode == REQUEST_IMAGE_DELETE){
            if (resultCode == RESULT_OK){
                photoList.remove(ADDNEWIMAGE);
                ArrayList<String> deletePhotos = data.getStringArrayListExtra("deletePhotoList");
                for (String path : deletePhotos){
                    if (photoList.contains(path) == true){
                        photoList.remove(path);
                    }
                }
                photoList.add(ADDNEWIMAGE);
                //显示出来
                messagePhotoAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_publish_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.activity_publish_publish:
                //关闭软键盘
                Utils.closeSoftInput(this, publish_message_content);

                final String content = publish_message_content.getText().toString();
                if (content.length() == 0){
                    Snackbar.make(toolbar.getRootView(), "请填写要发表的内容", Snackbar.LENGTH_LONG).show();
                    break;
                }
                photoList.remove(ADDNEWIMAGE);
                Intent intent = new Intent(PublishActivity.this, PublishMessageService.class);
                intent.putStringArrayListExtra("photoList", photoList);
                intent.putExtra("content", content);

                PublishActivity.this.startService(intent);
                Utils.finish(this);
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
