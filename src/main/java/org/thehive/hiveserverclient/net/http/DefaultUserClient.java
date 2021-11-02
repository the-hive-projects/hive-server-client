package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.thehive.hiveserverclient.model.User;

import java.util.concurrent.ThreadPoolExecutor;

public class DefaultUserClient implements UserClient {

    protected final String url;
    protected final ThreadPoolExecutor executor;
    protected final ObjectMapper objectMapper;
    protected final CloseableHttpClient httpClient;

    public DefaultUserClient(String url, ThreadPoolExecutor executor, ObjectMapper objectMapper, CloseableHttpClient httpClient) {
        this.url = url;
        this.executor = executor;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    @Override
    public void get(RequestCallback<User> callback, Header... headers) {
        var req = RequestUtils.getRequestOf(url, headers);
        executeRequest(req, callback);
    }

    @Override
    public void get(int id, RequestCallback<User> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlVariables(url, id);
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        executeRequest(req, callback);
    }

    @SneakyThrows
    @Override
    public void save(User user, RequestCallback<User> callback, Header... headers) {
        var userStr = objectMapper.writeValueAsString(user);
        var req = RequestUtils.postRequestOf(url, userStr, headers);
        executeRequest(req, callback);
    }

    @SneakyThrows
    @Override
    public void update(int id, User user, RequestCallback<User> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlVariables(url, id);
        var userStr = objectMapper.writeValueAsString(user);
        var req = RequestUtils.putRequestOf(reqUrl, userStr, headers);
        executeRequest(req, callback);
    }

    private void executeRequest(@NonNull HttpRequestBase request, @NonNull RequestCallback<? super User> callback) {
        executor.execute(() -> {
            try {
                try (var response = httpClient.execute(request)) {
                    var responseBody = EntityUtils.toString(response.getEntity());
                    if (response.getStatusLine().getStatusCode() % 100 == 2)
                        callback.onRequest(objectMapper.readValue(responseBody, User.class));
                    else
                        callback.onError(objectMapper.readValue(responseBody, Error.class));
                }
            } catch (Throwable t) {
                callback.onFail(t);
            }
        });
    }

}
