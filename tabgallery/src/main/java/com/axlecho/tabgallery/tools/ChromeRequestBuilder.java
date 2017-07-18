package com.axlecho.tabgallery.tools;

import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Request;

public class ChromeRequestBuilder extends Request.Builder {

    private static final String CHROME_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.103 Safari/537.36";

    public ChromeRequestBuilder(String url) throws MalformedURLException {
        url(new URL(url));
        addHeader("User-Agent", CHROME_USER_AGENT);
    }
}
