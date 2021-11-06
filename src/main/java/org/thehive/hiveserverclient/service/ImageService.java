package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.model.Image;
import org.thehive.hiveserverclient.service.result.Result;
import org.thehive.hiveserverclient.service.status.ImageStatus;

import java.util.function.Consumer;

public interface ImageService {

    void take(String username, Consumer<? super Result<ImageStatus, ? extends Image>> consumer);

}
