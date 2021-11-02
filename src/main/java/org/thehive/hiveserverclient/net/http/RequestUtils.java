package org.thehive.hiveserverclient.net.http;

import lombok.NonNull;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;

public class RequestUtils {

    public static HttpGet getRequestOf(@NonNull String url, @NonNull Header... headers) {
        var req = new HttpGet(url);
        for (var header : headers)
            req.addHeader(header);
        return req;
    }

    public static HttpPost postRequestOf(@NonNull String url, @NonNull Header... headers) {
        var req = new HttpPost(url);
        for (var header : headers)
            req.addHeader(header);
        return req;
    }

    public static HttpPost postRequestOf(@NonNull String url, @NonNull String bodyJson, @NonNull Header... headers) {
        var req = postRequestOf(url, headers);
        var entityBody = new BasicHttpEntity();
        entityBody.setContent(new ByteArrayInputStream(bodyJson.getBytes()));
        entityBody.setContentLength(bodyJson.length());
        entityBody.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        req.setEntity(entityBody);
        return req;
    }

    public static HttpPut putRequestOf(@NonNull String url, @NonNull Header... headers) {
        var req = new HttpPut(url);
        for (var header : headers)
            req.addHeader(header);
        return req;
    }

    public static HttpPut putRequestOf(@NonNull String url, @NonNull String bodyJson, @NonNull Header... headers) {
        var req = putRequestOf(url, headers);
        var entityBody = new BasicHttpEntity();
        entityBody.setContent(new ByteArrayInputStream(bodyJson.getBytes()));
        entityBody.setContentLength(bodyJson.length());
        entityBody.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        req.setEntity(entityBody);
        return req;
    }

    public static String concatUrlVariables(@NonNull String baseUrl, Object... vars) {
        var result = baseUrl;
        for (var var : vars)
            result = result.concat("/").concat(var.toString());
        return result;
    }

}
