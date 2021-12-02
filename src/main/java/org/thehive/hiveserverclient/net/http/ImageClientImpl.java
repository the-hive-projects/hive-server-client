package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Image;

import java.util.concurrent.ExecutorService;

@Slf4j
public class ImageClientImpl implements ImageClient {

    private final String url;
    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;
    private final ExecutorService executorService;

    public ImageClientImpl(@NonNull String url, @NonNull ObjectMapper objectMapper,
                           @NonNull CloseableHttpClient httpClient, @NonNull ExecutorService executorService) {
        this.url = url;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.executorService = executorService;
    }

    @Override
    public void get(String username, RequestCallback<? super Image> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlVariables(url, username);
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        log.debug("#get username: {}", username);
        executorService.execute(() -> {
            log.debug("Request is being sent, path: {}, method: {}", req.getURI().getPath(), req.getMethod());
            var executeCallbackFail = true;
            try {
                try (var response = httpClient.execute(req)) {
                    var statusCode = response.getStatusLine().getStatusCode();
                    var responseBody = EntityUtils.toString(response.getEntity());
                    log.debug("Request has been received, path: {}, method: {}, statusCode: {}, body: {}", req.getURI().getPath(), req.getMethod(), statusCode, responseBody);
                    executeCallbackFail = false;
                    if (statusCode / 100 == 2) {
                        var image = objectMapper.readValue(responseBody, Image.class);
                        log.debug("Executing callback onRequest, image: {}", image);
                        callback.onRequest(image);
                    } else {
                        var error = objectMapper.readValue(responseBody, Error.class);
                        log.debug("Executing callback onError, error: {}", error);
                        callback.onError(error);
                    }
                }
            } catch (Exception e) {
                if (executeCallbackFail) {
                    log.debug("Executing callback onFail, exception: {}", e.getClass().getName());
                    callback.onFail(e);
                } else
                    log.warn("Error while http request", e);
            }
        });

    }

}
