package org.thehive.hiveserverclient.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.thehive.hiveserverclient.Authentication;
import org.thehive.hiveserverclient.model.Error;
import org.thehive.hiveserverclient.model.Submission;
import org.thehive.hiveserverclient.net.http.RequestCallback;
import org.thehive.hiveserverclient.net.http.SubmissionClient;
import org.thehive.hiveserverclient.util.HeaderUtils;

import java.util.function.Consumer;

@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionClient submissionClient;

    public SubmissionServiceImpl(@NonNull SubmissionClient submissionClient) {
        this.submissionClient = submissionClient;
    }

    @Override
    public void takeAll(@NonNull Consumer<? super AppResponse<? extends Submission[]>> consumer) {
        log.info("#takeAll");
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        submissionClient.getAllSubmissions(new RequestCallback<>() {
            @Override
            public void onResponse(Submission[] responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#take, status: {}", response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                AppResponse<Submission[]> response;
                if (error.getStatus() == 404)
                    response = AppResponse.of(ResponseStatus.ERROR_UNAVAILABLE, error.getMessage());
                else
                    response = AppResponse.of(error.getMessage());
                log.info("#take status: {}", response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<Submission[]>of(t);
                log.info("#takeByUser, status: {}", response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

    @Override
    public void takeAllBySession(int sessionId, @NonNull Consumer<? super AppResponse<? extends Submission[]>> consumer) {
        log.info("#takeBySession sessionId: {}", sessionId);
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        submissionClient.getAllBySessionId(sessionId, new RequestCallback<>() {
            @Override
            public void onResponse(Submission[] responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#takeBySession sessionId: {}, status: {}", sessionId, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                AppResponse<Submission[]> response;
                if (error.getStatus() == 404)
                    response = AppResponse.of(ResponseStatus.ERROR_UNAVAILABLE, error.getMessage());
                else
                    response = AppResponse.of(error.getMessage());
                log.info("#takeBySession sessionId: {}, status: {}", sessionId, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<Submission[]>of(t);
                log.info("#takeBySession sessionId: {}, status: {}", sessionId, response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

    @Override
    public void takeThis(Consumer<? super AppResponse<? extends Submission>> consumer) {
        log.info("#takeThis");
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        submissionClient.getThisSubmission(new RequestCallback<>() {
            @Override
            public void onResponse(Submission responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#takeThis status: {}", response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                AppResponse<Submission> response;
                if (error.getStatus() == 404)
                    response = AppResponse.of(ResponseStatus.ERROR_UNAVAILABLE, error.getMessage());
                else
                    response = AppResponse.of(error.getMessage());
                log.info("#takeThis status: {}", response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<Submission>of(t);
                log.info("#takeThis status: {}", response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

    @Override
    public void submit(@NonNull Submission submission, @NonNull Consumer<? super AppResponse<? extends Submission>> consumer) {
        log.info("#submit submission: {}", submission);
        if (!Authentication.INSTANCE.isAuthenticated())
            throw new IllegalStateException("Authentication instance has not been authenticated");
        submissionClient.save(submission, new RequestCallback<>() {
            @Override
            public void onResponse(Submission responseBody) {
                var response = AppResponse.of(responseBody);
                log.info("#submit submission: {}, status: {}", submission, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onError(Error error) {
                AppResponse<Submission> response;
                if (error.getStatus() == 404)
                    response = AppResponse.of(ResponseStatus.ERROR_UNAVAILABLE, error.getMessage());
                else
                    response = AppResponse.of(error.getMessage());
                log.info("#submit submission: {}, status: {}", submission, response.status().name());
                consumer.accept(response);
            }

            @Override
            public void onFail(Throwable t) {
                var response = AppResponse.<Submission>of(t);
                log.info("#submit submission: {}, status: {}", submission, response.status().name());
                consumer.accept(response);
            }
        }, HeaderUtils.httpBasicAuthenticationHeader(Authentication.INSTANCE.getToken()));
    }

}
