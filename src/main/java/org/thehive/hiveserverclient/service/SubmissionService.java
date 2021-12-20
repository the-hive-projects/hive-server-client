package org.thehive.hiveserverclient.service;

import org.thehive.hiveserverclient.model.Submission;

import java.util.function.Consumer;

public interface SubmissionService {

    void takeAll(Consumer<? super AppResponse<? extends Submission[]>> consumer);

    void takeAllBySession(int sessionId, Consumer<? super AppResponse<? extends Submission[]>> consumer);

    void takeThis(Consumer<? super AppResponse<? extends Submission>> consumer);

    void submit(Submission submission, Consumer<? super AppResponse<? extends Submission>> consumer);

}
