package me.ryguy.ctfbot.util

import org.jsoup.Jsoup
import java.net.CookieHandler
import java.net.CookieManager
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object WebUtil {
    fun getWebResource(string: String): String {
        val cookieManager = CookieManager()
        CookieHandler.setDefault(cookieManager)

        val con = URL(string).openConnection() as HttpsURLConnection
        con.apply {
            requestMethod = "GET"
            addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36")
        }.run {
            return this.inputStream.reader(Charsets.UTF_8).buffered()
                    .readText()
        }
    }
}

fun URL.parseHtml(): org.jsoup.nodes.Document? {
    return Jsoup.parse(this.GET())
}

fun URL.GET(): String {
    return WebUtil.getWebResource(this.toString())
}