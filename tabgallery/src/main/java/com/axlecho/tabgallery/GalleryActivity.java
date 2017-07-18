package com.axlecho.tabgallery;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.axlecho.tabgallery.provider.DirGalleryProvider;
import com.axlecho.tabgallery.provider.TabGalleryProvider;
import com.axlecho.tabgallery.provider.GalleryProvider2;
import com.axlecho.tabgallery.provider.ZipGalleryProvider;
import com.axlecho.tabgallery.spider.SpiderDen;
import com.hippo.glgallery.GalleryProvider;
import com.hippo.glgallery.GalleryView;
import com.hippo.glgallery.SimpleAdapter;
import com.hippo.glview.view.GLRootView;
import com.hippo.unifile.UniFile;
import com.hippo.yorozuya.ConcurrentPool;
import com.hippo.yorozuya.ResourcesUtils;
import com.hippo.yorozuya.SimpleHandler;

import java.io.File;

public class GalleryActivity extends AppCompatActivity implements GalleryView.Listener, View.OnClickListener {

    public static final String ACTION_DIR = "dir";
    public static final String ACTION_ZIP = "zip";
    public static final String ACTION_EH = "eh";

    public static final String KEY_FILENAME = "filename";
    public static final String KEY_GALLERY_INFO = "gallery_info";

    @Nullable
    private GLRootView mGLRootView;
    @Nullable
    private GalleryView mGalleryView;
    @Nullable
    private GalleryProvider2 mGalleryProvider;
    @Nullable
    private GalleryAdapter mGalleryAdapter;

    private final ConcurrentPool<NotifyTask> mNotifyTaskPool = new ConcurrentPool<>(3);

    private String mAction;
    private String mFilename;
    private Uri mUri;
    private int mPage;
    private ImageTabInfo mGalleryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        keepBrightness(this);
        onInit();
        mGalleryProvider.start();
        SpiderDen.initialize(this);
        mGLRootView = (GLRootView) findViewById(R.id.gl_root_view);
        mGalleryAdapter = new GalleryAdapter(mGLRootView, mGalleryProvider);
        Resources resources = getResources();
        int primaryColor = ResourcesUtils.getAttrColor(this, R.attr.colorPrimary);

        mGalleryView = new GalleryView.Builder(this, mGalleryAdapter)
                .setListener(this)
                .setLayoutMode(GalleryView.LAYOUT_TOP_TO_BOTTOM)
                .setScaleMode(GalleryView.SCALE_FIT)
                .setStartPosition(GalleryView.START_POSITION_TOP_RIGHT)
                .setStartPage(0)
                .setBackgroundColor(resources.getColor(R.color.background))
                .setEdgeColor(primaryColor & 0xffffff | 0x33000000)
                .setPagerInterval(resources.getDimensionPixelOffset(R.dimen.gallery_pager_interval))
                .setScrollInterval(resources.getDimensionPixelOffset(R.dimen.gallery_scroll_interval))
                .setPageMinHeight(resources.getDimensionPixelOffset(R.dimen.gallery_page_min_height))
                .setPageInfoInterval(resources.getDimensionPixelOffset(R.dimen.gallery_page_info_interval))
                .setProgressColor(primaryColor)
                .setProgressSize(resources.getDimensionPixelOffset(R.dimen.gallery_progress_size))
                .setPageTextColor(resources.getColor(R.color.secondary_text_default_dark))
                .setPageTextSize(resources.getDimensionPixelOffset(R.dimen.gallery_page_text_size))
                .setPageTextTypeface(Typeface.DEFAULT)
                .setErrorTextColor(resources.getColor(R.color.red_500))
                .setErrorTextSize(resources.getDimensionPixelOffset(R.dimen.gallery_error_text_size))
                .setDefaultErrorString(resources.getString(R.string.error_unknown))
                .setEmptyString(resources.getString(R.string.error_empty))
                .build();

        mGLRootView.setContentPane(mGalleryView);
        mGalleryProvider.setListener(mGalleryAdapter);
        mGalleryProvider.setGLRoot(mGLRootView);


        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_rotate).setOnClickListener(this);
    }

    private void onInit() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mAction = intent.getAction();
        mFilename = intent.getStringExtra(KEY_FILENAME);
        mUri = intent.getData();
        mGalleryInfo = intent.getParcelableExtra(KEY_GALLERY_INFO);
        buildProvider();
    }

    private void buildProvider() {
        if (mGalleryProvider != null) {
            return;
        }

        if (ACTION_DIR.equals(mAction)) {
            if (mFilename != null) {
                mGalleryProvider = new DirGalleryProvider(UniFile.fromFile(new File(mFilename)));
            }
        } else if (ACTION_ZIP.equals(mAction)) {
            if (mFilename != null) {
                mGalleryProvider = new ZipGalleryProvider(new File(mFilename));
            }
        } else if (ACTION_EH.equals(mAction)) {
            if (mGalleryInfo != null) {
                mGalleryProvider = new TabGalleryProvider(this, mGalleryInfo);
            }
        } else if (Intent.ACTION_VIEW.equals(mAction)) {
            if (mUri != null) {
                // Only support zip now
                mGalleryProvider = new ZipGalleryProvider(new File(mUri.getPath()));
            }
        }
    }

    @Override
    public void onUpdateCurrentIndex(int index) {

    }

    @Override
    public void onTapSliderArea() {

    }

    @Override
    public void onTapMenuArea() {

    }

    @Override
    public void onLongPressPage(int index) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            this.finish();
        } else if (v.getId() == R.id.btn_rotate) {
            if (this.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    private void keepBrightness(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private class GalleryAdapter extends SimpleAdapter {

        public GalleryAdapter(@NonNull GLRootView glRootView, @NonNull GalleryProvider provider) {
            super(glRootView, provider);
        }

        @Override
        public void onDataChanged() {
            super.onDataChanged();

            if (mGalleryProvider != null) {
                int size = mGalleryProvider.size();
                NotifyTask task = mNotifyTaskPool.pop();
                if (task == null) {
                    task = new NotifyTask();
                }
                task.setData(NotifyTask.KEY_SIZE, size);
                SimpleHandler.getInstance().post(task);
            }
        }
    }

    private class NotifyTask implements Runnable {

        public static final int KEY_LAYOUT_MODE = 0;
        public static final int KEY_SIZE = 1;
        public static final int KEY_CURRENT_INDEX = 2;
        public static final int KEY_TAP_SLIDER_AREA = 3;
        public static final int KEY_TAP_MENU_AREA = 4;
        public static final int KEY_LONG_PRESS_PAGE = 5;

        private int mKey;
        private int mValue;

        public void setData(int key, int value) {
            mKey = key;
            mValue = value;
        }

        private void onTapMenuArea() {

        }

        private void onTapSliderArea() {

        }

        private void onLongPressPage(final int index) {
        }

        @Override
        public void run() {
            switch (mKey) {
                case KEY_CURRENT_INDEX:

                case KEY_TAP_MENU_AREA:
                    onTapMenuArea();
                    break;
                case KEY_TAP_SLIDER_AREA:
                    onTapSliderArea();
                    break;
                case KEY_LONG_PRESS_PAGE:
                    onLongPressPage(mValue);
                    break;
            }
            mNotifyTaskPool.push(this);
        }
    }
}
