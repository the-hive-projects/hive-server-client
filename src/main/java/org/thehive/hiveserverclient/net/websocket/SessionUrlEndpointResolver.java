package org.thehive.hiveserverclient.net.websocket;

import org.thehive.hiveserverclient.payload.Payload;

public interface SessionUrlEndpointResolver {

    String resolveSubscriptionUrlEndpoint(String id);

    void setDestinationUrlEndpoint(Class<? extends Payload> payloadType, String destinationUrlEndpoint);

    boolean containsDestinationUrlEndpoint(Class<? extends Payload> payloadType);

    String resolveDestinationUrlEndpoint(Class<? extends Payload> payloadType, String id);

}
