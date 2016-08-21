package com.pensum.pensumapplication.api_clients;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by violetaria on 8/20/16.
 */
public class ZipCodeApiClient {
    private static final String BASE_URL = "https://www.zipcodeapi.com/rest/";
    private static final String API_KEY = "GDGlvapEs1MBxs1ivfjMD359GQ0s49xKNe6ipvXRwJt4cumIKFunJzxpqxRuHpfu";

    private static AsyncHttpClient client = new AsyncHttpClient();

    // https://www.zipcodeapi.com/rest/APIKEY/info.json/33060/degrees
    public static void getLocationForZip(String zipCode, AsyncHttpResponseHandler responseHandler) {
        String url = "/info.json/" + zipCode + "/degrees";
        client.get(getAbsoluteUrl(url), null, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + API_KEY + relativeUrl;
    }
}
