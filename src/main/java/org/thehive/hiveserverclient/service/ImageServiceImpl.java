package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Image;
import org.thehive.hiveserverclient.net.http.ImageClient;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageClient imageClient;

    public ImageServiceImpl(@NonNull ImageClient imageClient) {
        this.imageClient = imageClient;
    }

    @Override
    public void take(@NonNull String username, @NonNull Consumer<? super Result<? extends Image>> consumer) {
        log.info("#take username: {}", username);
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        imageClient.get(username, new RequestCallback<>() {
            @Override
            public void onRequest(Image entity) {
                var result = Result.of(entity);
                log.info("#take username: {}, status: {}", username, result.status().name());
                consumer.accept(result);
            }

            @Override
            public void onError(Error error) {
                var result = Result.<Image>of(error.getMessage());
                log.info("#take username: {}, status: {}", username, result.status().name());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<Image>of(t);
                log.info("#take username: {}, status: {}", username, result.status().name());
                consumer.accept(result);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

}
