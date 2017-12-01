package com.zhp.facedetect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.being.base.http.callback.ICallback;
import com.being.base.http.retrofit.RetrofitManager;
import com.being.base.log.NHLog;
import com.being.base.utils.AndroidUtils;
import com.being.base.utils.CacheFileUtils;
import com.being.base.utils.ImageUtils;
import com.zhp.facedetect.net.Apis;
import com.zhp.facedetect.net.request.FaceIdentifyRequest;
import com.zhp.facedetect.net.request.NewPersonRequest;
import com.zhp.facedetect.net.response.FaceidentifyResponse;
import com.zhp.facedetect.net.response.NewPersonResponse;
import com.zhp.facedetect.net.response.model.IdentifyItem;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AddPersonActivity extends AppCompatActivity {

    @BindView(R.id.name_tv)
    EditText mNameTv;
    @BindView(R.id.camera_btn)
    Button mCameraBtn;
    @BindView(R.id.save_btn)
    Button mSaveBtn;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.phone_tv)
    EditText mPhoneTv;
    @BindView(R.id.imageView)
    ImageView mImageView;
    private File mSelFile;
    private Apis mApis;
    private String mName;
    private String mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        ButterKnife.bind(this);
        mApis = RetrofitManager.get().create(Apis.class);
        mCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    mSelFile = CacheFileUtils.generateTempPictureFilePath(AddPersonActivity.this);
                    AndroidUtils.openCamera(AddPersonActivity.this, mSelFile, 0);
                }
            }
        });
    }

    protected boolean check() {
        mName = mNameTv.getText().toString();
        mId = mPhoneTv.getText().toString();
        if (TextUtils.isEmpty(mName)) {
            mNameTv.setError("请输入姓名");
            return false;
        }
        if (TextUtils.isEmpty(mId)) {
            mPhoneTv.setError("请输入手机号");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_CANCELED == resultCode)
            return;
        if (requestCode == 0) {
            if (mSelFile.exists()) {
                mProgressBar.setVisibility(View.VISIBLE);
                File file = ImageUtils.scaleImageFile(this, mSelFile, CacheFileUtils.generateTempPictureFilePath(this), 320, 320);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                mImageView.setImageBitmap(bitmap);
                try {
                    String imageStr = ImageUtils.bitmapToBase64(bitmap);
                    NewPersonRequest request = new NewPersonRequest(mPhoneTv.getText().toString(), imageStr);
                    request.person_name = mNameTv.getText().toString();
                    request.tag = mNameTv.getText().toString();
                    request.image = imageStr;
                    RequestBody body = RequestBody.create(MediaType.parse("Content-Type, text/json"), request.toString());
                    mApis.newperson(body).enqueue(new ICallback<NewPersonResponse>() {
                        @Override
                        public void onSuccess(NewPersonResponse baseData) {
                            NHLog.d("response %s", baseData.toString());
                            if (baseData.isSuc()) {
                                Toast.makeText(getApplicationContext(), "人员添加成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), baseData.errormsg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public boolean onFail(int statusCode, @Nullable NewPersonResponse failDate, @Nullable Throwable error) {
                            Toast.makeText(getApplicationContext(), "请求错误", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public void onFinish() {
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }, getLifecycle());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
