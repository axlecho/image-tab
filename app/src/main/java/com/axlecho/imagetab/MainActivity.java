package com.axlecho.imagetab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.axlecho.tabgallery.GalleryActivity;
import com.axlecho.tabgallery.ImageTabInfo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_test) {
            startImageTab();
        }
    }

    private void startImageTab() {
        ImageTabInfo info = new ImageTabInfo();
        info.gid = 1231;
        info.imgs = new String[]{"http://jts-attachment.oss-cn-hangzhou.aliyuncs.com/data/attachment/forum/201705/24/112640fra9fjc8acltlawm.gif@!tab_thumb",
                "http://jts-attachment.oss-cn-hangzhou.aliyuncs.com/data/attachment/forum/201705/24/112641zx7jw8oaakzx2f22.gif@!tab_thumb",
                "http://jts-attachment.oss-cn-hangzhou.aliyuncs.com/data/attachment/forum/201705/24/112642tsnnmwvkdmdzsjmn.gif@!tab_thumb"};

        Intent intent = new Intent();
        intent.setClass(this, GalleryActivity.class);
        intent.setAction("eh");
        intent.putExtra(GalleryActivity.KEY_GALLERY_INFO, info);
        startActivity(intent);
    }
}
