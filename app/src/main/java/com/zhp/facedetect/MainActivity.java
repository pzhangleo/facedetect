package com.zhp.facedetect;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.being.base.http.callback.ICallback;
import com.being.base.http.retrofit.RetrofitManager;
import com.being.base.log.NHLog;
import com.being.base.utils.AndroidUtils;
import com.being.base.utils.CacheFileUtils;
import com.being.base.utils.ImageUtils;
import com.zhp.facedetect.net.Apis;
import com.zhp.facedetect.net.Config;
import com.zhp.facedetect.net.HttpApiBase;
import com.zhp.facedetect.net.request.FaceIdentifyRequest;
import com.zhp.facedetect.net.response.FaceidentifyResponse;
import com.zhp.facedetect.net.response.model.IdentifyItem;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import retrofit2.Retrofit;

@RuntimePermissions
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.name_tv)
    TextView mNameTv;
    @BindView(R.id.mobile_tv)
    TextView mMobileTv;
    @BindView(R.id.take_pic_btn)
    Button mTakePicBtn;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    private File mSelFile;

    private Apis mApis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpApiBase.init(getApplication());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTakePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelFile = CacheFileUtils.generateTempPictureFilePath(MainActivity.this);
                AndroidUtils.openCamera(MainActivity.this, mSelFile, 0);
            }
        });
        mApis = RetrofitManager.get().create(Apis.class);
        MainActivityPermissionsDispatcher.getPermissionWithCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    protected void getPermission() {

    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    protected void permissionDenied() {
        Toast.makeText(this, "请授权相关权限", Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add) {
            startActivity(new Intent(this, AddPersonActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
                    FaceIdentifyRequest request = new FaceIdentifyRequest();
                    request.image = imageStr;
                    RequestBody body = RequestBody.create(MediaType.parse("Content-Type, text/json"), request.toString());
                    mApis.faceIdentify(body).enqueue(new ICallback<FaceidentifyResponse>() {
                        @Override
                        public void onSuccess(FaceidentifyResponse baseData) {
                            NHLog.d("response %s", baseData.toString());
                            if (baseData.candidates == null || baseData.candidates.size() == 0) {
                                Toast.makeText(MainActivity.this, "没有找到匹配的人", Toast.LENGTH_LONG).show();
                            } else {
                                IdentifyItem identifyItem = baseData.candidates.get(0);
                                mNameTv.setText(identifyItem.tag);
                            }
                        }

                        @Override
                        public boolean onFail(int statusCode, @Nullable FaceidentifyResponse failDate, @Nullable Throwable error) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
