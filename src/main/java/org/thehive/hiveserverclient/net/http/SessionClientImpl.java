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
public class SessionClientImpl extends AppHttpClient implements SessionClient {

    public SessionClientImpl(String url, ObjectMapper objectMapper, CloseableHttpClient httpClient, ExecutorService executorService) {
        super(url, objectMapper, httpClient, executorService);
    }

    @Override
    public void getAllSessions(RequestCallback<? super Session[]> callback, Header... headers) {
        var req = RequestUtils.getRequestOf(url, headers);
        log.debug("#getAllSessions uri: {}", req.getURI());
        executorService.execute(() -> {
            log.debug("Request is being sent, path: {}, method: {}", req.getURI().getPath(), req.getMethod());
            boolean executeCallbackFail = true;
            try {
                try (var response = httpClient.execute(req)) {
                    var statusCode = response.getStatusLine().getStatusCode();
                    var responseBody = EntityUtils.toString(response.getEntity());
                    log.debug("Response has been received, path: {}, method: {}, statusCode: {}, body: {}", req.getURI().getPath(), req.getMethod(), statusCode, responseBody);
                    executeCallbackFail = false;
                    if (statusCode / 100 == 2) {
                        var sessions = objectMapper.readValue(responseBody, Session[].class);
                        log.debug("Executing callback onResponse, sessionsLength: {}", sessions.length);
                        callback.onResponse(sessions);
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

    @Override
    public void getLiveSession(String liveId, RequestCallback<? super Session> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlPath(url, liveId);
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        log.debug("#getLiveSession uri: {}", req.getURI());
        executeRequest(req, callback);
    }

    @Override
    public void save(Session session, RequestCallback<? super Session> callback, Header... headers) {
        String sessionStr;
        try {
            sessionStr = objectMapper.writeValueAsString(session);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        var req = RequestUtils.postRequestOf(url, sessionStr, headers);
        log.debug("#save uri: {}, body: {}", req.getURI(), sessionStr);
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
                    log.debug("Response has been received, path: {}, method: {}, statusCode: {}, body: {}", req.getURI().getPath(), req.getMethod(), statusCode, responseBody);
                    executeCallbackFail = false;
                    if (statusCode / 100 == 2) {
                        var session = objectMapper.readValue(responseBody, Session.class);
                        log.debug("Executing callback onResponse, session: {}", session);
                        callback.onResponse(session);
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
