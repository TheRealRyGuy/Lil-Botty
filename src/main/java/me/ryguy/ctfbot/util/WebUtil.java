package me.ryguy.ctfbot.util;

import org.apache.commons.lang3.Validate;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URL;
import java.nio.charset.Charset;

public class WebUtil {
    public static String getJSONApi(String string) throws Exception{
        Validate.notNull(string);

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        StringBuilder ret = new StringBuilder();
        URL url = new URL(string);
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");

        InputStream is = con.getInputStream();

        BufferedReader r = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        int cp;
        while ((cp = r.read()) != -1) ret.append((char) cp);
        return ret.toString();
    }
}
