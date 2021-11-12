package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.User;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

    protected final String url;
    protected final CloseableHttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final ExecutorService executorService;

    @Override
    public void get(RequestCallback<? super User> callback, Header... headers) {
        var req = RequestUtils.getRequestOf(url, headers);
        executeRequest(req, callback);
    }

    @Override
    public void get(int id, RequestCallback<? super User> callback, Header... headers) {
        if (id < 1)
            throw new IllegalArgumentException("Id must be positive value");
        var reqUrl = RequestUtils.concatUrlVariables(url, id);
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        executeRequest(req, callback);
    }

    @SneakyThrows
    @Override
    public void save(User user, RequestCallback<? super User> callback, Header... headers) {
        var userStr = objectMapper.writeValueAsString(user);
        var req = RequestUtils.postRequestOf(url, userStr, headers);
        executeRequest(req, callback);
    }

    @SneakyThrows
    @Override
    public void update(int id, User user, RequestCallback<? super User> callback, Header... headers) {
        if (id < 1)
            throw new IllegalArgumentException("Id must be positive value");
        var reqUrl = RequestUtils.concatUrlVariables(url, id);
        var userStr = objectMapper.writeValueAsString(user);
        var req = RequestUtils.putRequestOf(reqUrl, userStr, headers);
        executeRequest(req, callback);
    }

    private void executeRequest(@NonNull HttpRequestBase request, @NonNull RequestCallback<? super User> callback) {
        executorService.execute(() -> {
            var executeFail = true;
            try {
                try (var response = httpClient.execute(request)) {
                    var responseBody = EntityUtils.toString(response.getEntity());
                    executeFail = false;
                    if (response.getStatusLine().getStatusCode() / 100 == 2)
                        callback.onRequest(objectMapper.readValue(responseBody, User.class));
                    else
                        callback.onError(objectMapper.readValue(responseBody, Error.class));
                }
            } catch (JacksonException e) {
                e.printStackTrace();
            } catch (IOException e) {
                if (executeFail)
                    callback.onFail(e);
                else
                    e.printStackTrace();
            }
        });
    }

}
