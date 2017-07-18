/*
 * Copyright 2016 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.axlecho.tabgallery.spider;

import android.content.Context;
import android.support.annotation.Nullable;

import com.axlecho.tabgallery.ImageTabInfo;
import com.hippo.beerbelly.SimpleDiskCache;
import com.hippo.streampipe.InputStreamPipe;
import com.hippo.streampipe.OutputStreamPipe;
import com.hippo.yorozuya.MathUtils;

import java.io.File;

public final class SpiderDen {

    @Nullable
    private volatile int mMode = SpiderQueen.MODE_READ;
    private final long mGid;

    @Nullable
    private static SimpleDiskCache sCache;

    public static void initialize(Context context) {
        sCache = new SimpleDiskCache(new File(context.getCacheDir(), "image"),
                MathUtils.clamp(160, 40, 640) * 1024 * 1024);
    }


    public SpiderDen(ImageTabInfo galleryInfo) {
        mGid = galleryInfo.gid;
    }

    public void setMode(@SpiderQueen.Mode int mode) {
        mMode = mode;
    }


    public boolean isReady() {
        switch (mMode) {
            case SpiderQueen.MODE_READ:
                return sCache != null;
            default:
                return false;
        }
    }


    private boolean containInCache(int index) {
        if (sCache == null) {
            return false;
        }

        String key = getImageKey(mGid, index);
        return sCache.contain(key);
    }

    public boolean contain(int index) {
        if (mMode == SpiderQueen.MODE_READ) {
            return containInCache(index);
        } else {
            return false;
        }
    }

    private boolean removeFromCache(int index) {
        if (sCache == null) {
            return false;
        }

        String key = getImageKey(mGid, index);
        return sCache.remove(key);
    }

    public boolean remove(int index) {
        boolean result = removeFromCache(index);
        return result;
    }

    public static String getImageKey(long gid, int index) {
        return "image:" + gid + ":" + index;
    }

    @Nullable
    private OutputStreamPipe openCacheOutputStreamPipe(int index) {
        if (sCache == null) {
            return null;
        }

        String key = getImageKey(mGid, index);
        return sCache.getOutputStreamPipe(key);
    }


    @Nullable
    public OutputStreamPipe openOutputStreamPipe(int index, @Nullable String extension) {
        if (mMode == SpiderQueen.MODE_READ) {
            return openCacheOutputStreamPipe(index);
        } else {
            return null;
        }
    }


    @Nullable
    private InputStreamPipe openCacheInputStreamPipe(int index) {
        if (sCache == null) {
            return null;
        }

        String key = getImageKey(mGid, index);
        return sCache.getInputStreamPipe(key);
    }


    @Nullable
    public InputStreamPipe openInputStreamPipe(int index) {
        if (mMode == SpiderQueen.MODE_READ) {
            return openCacheInputStreamPipe(index);
        } else {
            return null;
        }
    }
}
