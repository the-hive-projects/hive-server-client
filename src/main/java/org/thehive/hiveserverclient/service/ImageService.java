package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.model.Image;

import java.util.function.Consumer;

public interface ImageService {

    void take(String username, Consumer<? super AppResponse<? extends Image>> consumer);

}
