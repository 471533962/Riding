package com.bingo.riding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int REQUEST_NIKENAME = 0;
    public static final int REQUEST_EMAIL = 1;
    public static final int REQUEST_MESSAGE = 2;
    public static final int PHOTO_REQUEST_GALLERY = 3;
    public static final int PHOTO_REQUEST_CUT = 4;

    private RelativeLayout personalImage;
    private RelativeLayout personalNikeName;
    private RelativeLayout personalEmail;
    private RelativeLayout personalSex;
    private RelativeLayout personalMessage;

    private ImageView personalImage_imageView;
    private TextView personalNikeName_textView;
    private TextView personalEmail_textView;
    private TextView personalSex_textView;
    private TextView personalMessage_textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        initView();
    }

    private void initView(){

        personalEmail = (RelativeLayout) findViewById(R.id.personalEmail);
        personalImage = (RelativeLayout) findViewById(R.id.personalImage);
        personalNikeName = (RelativeLayout) findViewById(R.id.personalNikeName);
        personalSex = (RelativeLayout) findViewById(R.id.personalSex);
        personalMessage = (RelativeLayout) findViewById(R.id.personalMessage);

        personalImage_imageView = (ImageView) findViewById(R.id.personalImage_imageView);
        personalEmail_textView = (TextView) findViewById(R.id.personalEmail_textView);
        personalMessage_textView = (TextView) findViewById(R.id.personalMessage_textView);
        personalNikeName_textView = (TextView) findViewById(R.id.personalNikeName_textView);
        personalSex_textView = (TextView) findViewById(R.id.personalSex_textView);

        initData();
        initViewListener();
    }

    private void initData(){
        AVUser avUser = AVUser.getCurrentUser();

        personalEmail_textView.setText(avUser.getEmail());
        personalSex_textView.setText(avUser.getBoolean("isMale") == true ? "男" : "女");
        personalNikeName_textView.setText(avUser.getString("nikeName"));
        personalMessage_textView.setText(avUser.getString("message"));

        AVFile userPhoto = avUser.getAVFile("userPhoto");
        if (userPhoto != null) {
            Glide.with(getApplicationContext())
                    .load(userPhoto.getUrl())
                    .signature(new StringSignature(userPhoto.getUrl()))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.loaderror)
                    .centerCrop()
                    .into(personalImage_imageView);
        }else{
            personalImage_imageView.setImageResource(R.drawable.default_photo);
        }
    }

    private void initViewListener(){
        personalEmail.setOnClickListener(this);
        personalImage.setOnClickListener(this);
        personalMessage.setOnClickListener(this);
        personalNikeName.setOnClickListener(this);
        personalSex.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.personalEmail:
                Intent intentEmail = new Intent(PersonalInfoActivity.this, ModifyActivity.class);
                intentEmail.putExtra("limit", 0);//代表没有限制
                intentEmail.putExtra("request", REQUEST_EMAIL);
                startActivityForResult(intentEmail, REQUEST_EMAIL);
                break;
            case R.id.personalImage:
                Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                getAlbum.setType("image/*");
                startActivityForResult(getAlbum, PHOTO_REQUEST_GALLERY);
                break;
            case R.id.personalNikeName:
                Intent intentNikeName = new Intent(PersonalInfoActivity.this, ModifyActivity.class);
                intentNikeName.putExtra("limit", 0);//代表没有限制
                intentNikeName.putExtra("request", REQUEST_NIKENAME);
                startActivityForResult(intentNikeName, REQUEST_NIKENAME);
                break;
            case R.id.personalSex:
                break;
            case R.id.personalMessage:
                Intent intentMessage = new Intent(PersonalInfoActivity.this, ModifyActivity.class);
                intentMessage.putExtra("limit", 20);
                intentMessage.putExtra("request", REQUEST_MESSAGE);
                startActivityForResult(intentMessage, REQUEST_MESSAGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            AVUser avUser = AVUser.getCurrentUser();
            switch (requestCode){
                case REQUEST_EMAIL:
                    String email = data.getStringExtra("result_content");
                    avUser.setEmail(email);
                    avUser.setFetchWhenSave(true);
                    avUser.saveInBackground();

                    personalEmail_textView.setText(email);
                    break;
                case REQUEST_MESSAGE:
                    String message = data.getStringExtra("result_content");
                    avUser.put("message", message);
                    avUser.setFetchWhenSave(true);
                    avUser.saveInBackground();

                    personalMessage_textView.setText(message);
                    break;
                case REQUEST_NIKENAME:
                    String nikeName = data.getStringExtra("result_content");
                    avUser.put("nikeName", nikeName);
                    avUser.setFetchWhenSave(true);
                    avUser.saveInBackground();

                    personalNikeName_textView.setText(nikeName);
                    break;
                case PHOTO_REQUEST_GALLERY:
                    // 做非空判断，当我们觉得不满意想重新剪裁的时候便不会报异常，下同
                    if (data != null) {
                        startPhotoZoom(data.getData());
                    } else {
                        System.out.println("================");
                    }
                    break;
                case PHOTO_REQUEST_CUT:// 返回的结果
                    if (data != null){
                        Bitmap bitmap = data.getParcelableExtra("data");
                        File file = saveMyBitmap(bitmap);

                        try {
                            final AVFile avFile = AVFile.withFile(file.getName(), file);
                            avFile.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null){
                                        AVUser user = AVUser.getCurrentUser();
                                        user.put("userPhoto", avFile);
                                        user.setFetchWhenSave(true);
                                        user.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null){
                                                    Toast.makeText(PersonalInfoActivity.this, "上传头像成功", Toast.LENGTH_SHORT).show();
                                                    PersonalInfoActivity.this.recreate();
                                                }else{
                                                    Toast.makeText(PersonalInfoActivity.this, "上传头像失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(PersonalInfoActivity.this, "上传头像失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        System.out.println("22================");
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    //从Uri中获取Bitmap格式的图片
    private static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap;
        try {
                bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        return bitmap;
    }

    public File saveMyBitmap(Bitmap mBitmap)  {
        File f = new File( Environment.getExternalStorageDirectory() + File.separator
                + "Riding" + File.separator + System.currentTimeMillis() + ".png");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }
}
