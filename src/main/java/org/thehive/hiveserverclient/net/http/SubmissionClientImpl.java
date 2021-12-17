package org.thehive.hiveserverclient.net.http;

import org.apache.http.Header;
import org.thehive.hiveserverclient.model.Submission;

public class SubmissionClientImpl implements SubmissionClient {


    @Override
    public void getBySessionId(int sessionId, RequestCallback<? super Submission> callback, Header... headers) {

    }

    @Override
    public void getByUserId(int sessionId, RequestCallback<? super Submission> callback, Header... headers) {

    }

    @Override
    public void save(Submission submission, RequestCallback<? super Submission> callback, Header... headers) {

    }

}
