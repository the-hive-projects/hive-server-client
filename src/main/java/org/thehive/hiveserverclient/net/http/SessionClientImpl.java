package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Session;

import java.util.concurrent.ExecutorService;

@Slf4j
public class SessionClientImpl implements SessionClient {

    private final String url;
    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;
    private final ExecutorService executorService;

    public SessionClientImpl(@NonNull String url, @NonNull ObjectMapper objectMapper,
                             @NonNull CloseableHttpClient httpClient, @NonNull ExecutorService executorService) {
        this.url = url;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.executorService = executorService;
    }

    @Override
    public void get(String id, RequestCallback<? super Session> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlVariables(url, id);
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        log.info("#get id: {}", id);
        executeRequest(req, callback);
    }

    @Override
    public void save(Session session, RequestCallback<? super Session> callback, Header... headers) {
        String userStr;
        try {
            userStr = objectMapper.writeValueAsString(callback);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        var req = RequestUtils.postRequestOf(url, userStr, headers);
        log.info("#save session: {}", session);
        executeRequest(req, callback);
    }

    private void executeRequest(@NonNull HttpRequestBase req, @NonNull RequestCallback<? super Session> callback) {
        executorService.execute(() -> {
            log.debug("Request is being sent, path: {}, method: {}", req.getURI().getPath(), req.getMethod());
            boolean executeCallbackFail = true;
            try {
                try (var response = httpClient.execute(req)) {
                    var statusCode = response.getStatusLine().getStatusCode();
                    var responseBody = EntityUtils.toString(response.getEntity());
                    log.debug("Request has been received, path: {}, method: {}, statusCode: {}, body: {}", req.getURI().getPath(), req.getMethod(), statusCode, responseBody);
                    executeCallbackFail = false;
                    if (statusCode / 100 == 2) {
                        var session = objectMapper.readValue(responseBody, Session.class);
                        log.debug("Executing callback onRequest, session: {}", session);
                        callback.onRequest(session);
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
                    e.printStackTrace();
            }
        });
    }

}
