package com.axlecho.tabgallery.provider;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hippo.glgallery.GalleryProvider;
import com.hippo.unifile.UniFile;

public abstract class GalleryProvider2 extends GalleryProvider {

    // With dot
    public static final String[] SUPPORT_IMAGE_EXTENSIONS = {
            ".jpg", // Joint Photographic Experts Group
            ".jpeg",
            ".png", // Portable Network Graphics
            ".gif", // Graphics Interchange Format
    };

    public int getStartPage() {
        return 0;
    }

    public void putStartPage(int page) {
    }

    /**
     * @return without extension
     */
    @NonNull
    public abstract String getImageFilename(int index);

    public abstract boolean save(int index, @NonNull UniFile file);

    /**
     * @param filename without extension
     */
    @Nullable
    public abstract UniFile save(int index, @NonNull UniFile dir, @NonNull String filename);
}