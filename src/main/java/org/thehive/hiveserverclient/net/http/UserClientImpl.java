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
import org.thehive.hiveserverclient.model.User;

import java.util.concurrent.ExecutorService;

@Slf4j
public class UserClientImpl extends AppHttpClient implements UserClient {

    public UserClientImpl(String url, ObjectMapper objectMapper, CloseableHttpClient httpClient, ExecutorService executorService) {
        super(url, objectMapper, httpClient, executorService);
    }

    @Override
    public void get(RequestCallback<? super User> callback, Header... headers) {
        var req = RequestUtils.getRequestOf(url, headers);
        log.debug("#get");
        executeRequest(req, callback);
    }

    @Override
    public void get(int id, RequestCallback<? super User> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlPath(url, id);
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        log.debug("#get id: {}", id);
        executeRequest(req, callback);
    }

    @Override
    public void save(User user, RequestCallback<? super User> callback, Header... headers) {
        String userStr;
        try {
            userStr = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        var req = RequestUtils.postRequestOf(url, userStr, headers);
        log.debug("#save user: {}", user);
        executeRequest(req, callback);
    }

    @Override
    public void update(int id, User user, RequestCallback<? super User> callback, Header... headers) {
        String userStr;
        try {
            userStr = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        var reqUrl = RequestUtils.concatUrlPath(url, id);
        var req = RequestUtils.putRequestOf(reqUrl, userStr, headers);
        log.debug("#update id: {} user: {}", id, user);
        executeRequest(req, callback);
    }

    private void executeRequest(@NonNull HttpRequestBase req, @NonNull RequestCallback<? super User> callback) {
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
                        var user = objectMapper.readValue(responseBody, User.class);
                        log.debug("Executing callback onRequest, user: {}", user);
                        callback.onRequest(user);
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
