package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Image;
import org.thehive.hiveserverclient.net.http.ImageClient;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.service.status.ImageStatus;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageClient imageClient;

    @Override
    public void take(@NonNull String username, @NonNull Consumer<? super Result<ImageStatus, ? extends Image>> consumer) {
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        imageClient.get(username, new RequestCallback<>() {
            @Override
            public void onRequest(Image entity) {
                var result = Result.of(ImageStatus.TAKEN, entity);
                consumer.accept(result);
            }

            @Override
            public void onError(Error e) {
                var result = Result.<ImageStatus, Image>of(ImageStatus.ERROR, e.getMessage());
                consumer.accept(result);
            }

            @Override
            public void onFail(Throwable t) {
                var result = Result.<ImageStatus, Image>of(ImageStatus.FAIL, t);
                consumer.accept(result);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

}
