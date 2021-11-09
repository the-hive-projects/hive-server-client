package org.thehive.hiveserverclient.net.websocket.header;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;


@RequiredArgsConstructor
public class StompHeadersProxy extends StompHeaders {

    private final StompHeaders stompHeaders;

    @Override
    @Nullable
    public MimeType getContentType() {
        return stompHeaders.getContentType();
    }

    @Override
    public void setContentType(MimeType mimeType) {
        stompHeaders.setContentType(mimeType);
    }

    @Override
    public long getContentLength() {
        return stompHeaders.getContentLength();
    }

    @Override
    public void setContentLength(long contentLength) {
        stompHeaders.setContentLength(contentLength);
    }

    @Override
    @Nullable
    public String getReceipt() {
        return stompHeaders.getReceipt();
    }

    @Override
    public void setReceipt(String receipt) {
        stompHeaders.setReceipt(receipt);
    }

    @Override
    @Nullable
    public String getHost() {
        return stompHeaders.getHost();
    }

    @Override
    public void setHost(String host) {
        stompHeaders.setHost(host);
    }

    @Override
    @Nullable
    public String[] getAcceptVersion() {
        return stompHeaders.getAcceptVersion();
    }

    @Override
    public void setAcceptVersion(String... acceptVersions) {
        stompHeaders.setAcceptVersion(acceptVersions);
    }

    @Override
    @Nullable
    public String getLogin() {
        return stompHeaders.getLogin();
    }

    @Override
    public void setLogin(String login) {
        stompHeaders.setLogin(login);
    }

    @Override
    @Nullable
    public String getPasscode() {
        return stompHeaders.getPasscode();
    }

    @Override
    public void setPasscode(String passcode) {
        stompHeaders.setPasscode(passcode);
    }

    @Override
    @Nullable
    public long[] getHeartbeat() {
        return stompHeaders.getHeartbeat();
    }

    @Override
    public void setHeartbeat(long[] heartbeat) {
        stompHeaders.setHeartbeat(heartbeat);
    }

    @Override
    public boolean isHeartbeatEnabled() {
        return stompHeaders.isHeartbeatEnabled();
    }

    @Override
    @Nullable
    public String getSession() {
        return stompHeaders.getSession();
    }

    @Override
    public void setSession(String session) {
        stompHeaders.setSession(session);
    }

    @Override
    @Nullable
    public String getServer() {
        return stompHeaders.getServer();
    }

    @Override
    public void setServer(String server) {
        stompHeaders.setServer(server);
    }

    @Override
    @Nullable
    public String getDestination() {
        return stompHeaders.getDestination();
    }

    @Override
    public void setDestination(String destination) {
        stompHeaders.setDestination(destination);
    }

    @Override
    @Nullable
    public String getId() {
        return stompHeaders.getId();
    }

    @Override
    public void setId(String id) {
        stompHeaders.setId(id);
    }

    @Override
    @Nullable
    public String getAck() {
        return stompHeaders.getAck();
    }

    @Override
    public void setAck(String ack) {
        stompHeaders.setAck(ack);
    }

    @Override
    @Nullable
    public String getSubscription() {
        return stompHeaders.getSubscription();
    }

    @Override
    public void setSubscription(String subscription) {
        stompHeaders.setSubscription(subscription);
    }

    @Override
    @Nullable
    public String getMessageId() {
        return stompHeaders.getMessageId();
    }

    @Override
    public void setMessageId(String messageId) {
        stompHeaders.setMessageId(messageId);
    }

    @Override
    @Nullable
    public String getReceiptId() {
        return stompHeaders.getReceiptId();
    }

    @Override
    public void setReceiptId(String receiptId) {
        stompHeaders.setReceiptId(receiptId);
    }

    @Override
    @Nullable
    public String getFirst(String headerName) {
        return stompHeaders.getFirst(headerName);
    }

    @Override
    public void add(String headerName, String headerValue) {
        stompHeaders.add(headerName, headerValue);
    }

    @Override
    public void addAll(String headerName, List<? extends String> headerValues) {
        stompHeaders.addAll(headerName, headerValues);
    }

    @Override
    public void addAll(MultiValueMap<String, String> values) {
        stompHeaders.addAll(values);
    }

    @Override
    public void set(String headerName, String headerValue) {
        stompHeaders.set(headerName, headerValue);
    }

    @Override
    public void setAll(Map<String, String> values) {
        stompHeaders.setAll(values);
    }

    @Override
    public Map<String, String> toSingleValueMap() {
        return stompHeaders.toSingleValueMap();
    }

    @Override
    public int size() {
        return stompHeaders.size();
    }

    @Override
    public boolean isEmpty() {
        return stompHeaders.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return stompHeaders.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return stompHeaders.containsValue(value);
    }

    @Override
    public List<String> get(Object key) {
        return stompHeaders.get(key);
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return stompHeaders.put(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        return stompHeaders.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> map) {
        stompHeaders.putAll(map);
    }

    @Override
    public void clear() {
        stompHeaders.clear();
    }

    @Override
    public Set<String> keySet() {
        return stompHeaders.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return stompHeaders.values();
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        return stompHeaders.entrySet();
    }

    @Override
    public boolean equals(Object other) {
        return stompHeaders.equals(other);
    }

    @Override
    public int hashCode() {
        return stompHeaders.hashCode();
    }

    @Override
    public String toString() {
        return stompHeaders.toString();
    }

    @Override
    public void addIfAbsent(String key, String value) {
        stompHeaders.addIfAbsent(key, value);
    }

    @Override
    public List<String> getOrDefault(Object key, List<String> defaultValue) {
        return stompHeaders.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super List<String>> action) {
        stompHeaders.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super List<String>, ? extends List<String>> function) {
        stompHeaders.replaceAll(function);
    }

    @Override
    public List<String> putIfAbsent(String key, List<String> value) {
        return stompHeaders.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return stompHeaders.remove(key, value);
    }

    @Override
    public boolean replace(String key, List<String> oldValue, List<String> newValue) {
        return stompHeaders.replace(key, oldValue, newValue);
    }

    @Override
    public List<String> replace(String key, List<String> value) {
        return stompHeaders.replace(key, value);
    }

    @Override
    public List<String> computeIfAbsent(String key, Function<? super String, ? extends List<String>> mappingFunction) {
        return stompHeaders.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public List<String> computeIfPresent(String key, BiFunction<? super String, ? super List<String>, ? extends List<String>> remappingFunction) {
        return stompHeaders.computeIfPresent(key, remappingFunction);
    }

    @Override
    public List<String> compute(String key, BiFunction<? super String, ? super List<String>, ? extends List<String>> remappingFunction) {
        return stompHeaders.compute(key, remappingFunction);
    }

    @Override
    public List<String> merge(String key, List<String> value, BiFunction<? super List<String>, ? super List<String>, ? extends List<String>> remappingFunction) {
        return stompHeaders.merge(key, value, remappingFunction);
    }

}
