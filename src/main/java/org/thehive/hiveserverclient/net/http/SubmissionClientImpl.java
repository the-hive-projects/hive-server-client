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
import org.thehive.hiveserverclient.model.Submission;

import java.util.concurrent.ExecutorService;

@Slf4j
public class SubmissionClientImpl extends AppHttpClient implements SubmissionClient {

    public SubmissionClientImpl(String url, ObjectMapper objectMapper, CloseableHttpClient httpClient, ExecutorService executorService) {
        super(url, objectMapper, httpClient, executorService);
    }

    @Override
    public void getAllSubmissions(RequestCallback<? super Submission[]> callback, Header... headers) {
        var req = RequestUtils.getRequestOf(url, headers);
        log.debug("#getAllSubmissions uri: {}", req.getURI());
        executeGetRequest(req, callback);
    }

    @Override
    public void getAllBySessionId(int sessionId, RequestCallback<? super Submission[]> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlPath(url, sessionId);
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        log.debug("#getAllBySessionId uri: {}", req.getURI());
        executeGetRequest(req, callback);
    }

    @Override
    public void getThisSubmission(RequestCallback<? super Submission> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlPath(url, "this");
        var req = RequestUtils.getRequestOf(reqUrl, headers);
        log.debug("#getThisSubmission uri: {}", req.getURI());
        executorService.execute(() -> {
            log.debug("Request is being sent, path: {}, method: {}", req.getURI().getPath(), req.getMethod());
            var executeCallbackFail = true;
            try {
                try (var response = httpClient.execute(req)) {
                    var statusCode = response.getStatusLine().getStatusCode();
                    if (response.getEntity().getContentLength() == 0L) {
                        log.debug("Response has been received, path: {}, method: {}, statusCode: {}, body: {}", req.getURI().getPath(), req.getMethod(), statusCode, "[EMPTY]");
                        callback.onResponse(Submission.EMPTY);
                    } else {
                        var responseBody = EntityUtils.toString(response.getEntity());
                        log.debug("Response has been received, path: {}, method: {}, statusCode: {}, body: {}", req.getURI().getPath(), req.getMethod(), statusCode, responseBody);
                        executeCallbackFail = false;
                        if (statusCode / 100 == 2) {
                            var submission = objectMapper.readValue(responseBody, Submission.class);
                            log.debug("Executing callback onResponse, submission: {}", submission);
                            callback.onResponse(submission);
                        } else {
                            var error = objectMapper.readValue(responseBody, Error.class);
                            log.debug("Executing callback onError, error: {}", error);
                            callback.onError(error);
                        }
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

    @Override
    public void save(Submission submission, RequestCallback<? super Submission> callback, Header... headers) {
        var reqUrl = RequestUtils.concatUrlPath(url);
        String submissionStr;
        try {
            submissionStr = objectMapper.writeValueAsString(submission);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
        var req = RequestUtils.postRequestOf(reqUrl, submissionStr, headers);
        log.debug("#save uri: {}, body: {}", req.getURI(), submissionStr);
        executeSaveRequest(req, callback);
    }

    private void executeGetRequest(@NonNull HttpRequestBase req, @NonNull RequestCallback<? super Submission[]> callback) {
        executorService.execute(() -> {
            log.debug("Request is being sent, path: {}, method: {}", req.getURI().getPath(), req.getMethod());
            var executeCallbackFail = true;
            try {
                try (var response = httpClient.execute(req)) {
                    var statusCode = response.getStatusLine().getStatusCode();
                    var responseBody = EntityUtils.toString(response.getEntity());
                    log.debug("Response has been received, path: {}, method: {}, statusCode: {}, body: {}", req.getURI().getPath(), req.getMethod(), statusCode, responseBody);
                    executeCallbackFail = false;
                    if (statusCode / 100 == 2) {
                        var submissions = objectMapper.readValue(responseBody, Submission[].class);
                        log.debug("Executing callback onResponse, submissionsLength: {}", submissions.length);
                        callback.onResponse(submissions);
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

    private void executeSaveRequest(@NonNull HttpRequestBase req, @NonNull RequestCallback<? super Submission> callback) {
        executorService.execute(() -> {
            log.debug("Request is being sent, path: {}, method: {}", req.getURI().getPath(), req.getMethod());
            var executeCallbackFail = true;
            try {
                try (var response = httpClient.execute(req)) {
                    var statusCode = response.getStatusLine().getStatusCode();
                    var responseBody = EntityUtils.toString(response.getEntity());
                    log.debug("Response has been received, path: {}, method: {}, statusCode: {}, body: {}", req.getURI().getPath(), req.getMethod(), statusCode, responseBody);
                    executeCallbackFail = false;
                    if (statusCode / 100 == 2) {
                        var submission = objectMapper.readValue(responseBody, Submission.class);
                        log.debug("Executing callback onResponse, submission: {}", submission);
                        callback.onResponse(submission);
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
