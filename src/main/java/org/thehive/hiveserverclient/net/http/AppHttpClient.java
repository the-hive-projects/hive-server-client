package org.thehive.hiveserverclient.net.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.concurrent.ExecutorService;

public abstract class AppHttpClient {

    protected final String url;
    protected final ObjectMapper objectMapper;
    protected final CloseableHttpClient httpClient;
    protected final ExecutorService executorService;

    protected AppHttpClient(@NonNull String url, @NonNull ObjectMapper objectMapper,
                            @NonNull CloseableHttpClient httpClient, @NonNull ExecutorService executorService) {
        this.url = url;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.executorService = executorService;
    }

}
