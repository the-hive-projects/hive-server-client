package org.thehive.hiveserverclient.net.http;

import org.apache.http.Header;
import org.thehive.hiveserverclient.model.Submission;

public interface SubmissionClient {

    void getByUserId(int userId, RequestCallback<? super Submission[]> callback, Header... headers);

    void getBySessionId(int sessionId, RequestCallback<? super Submission[]> callback, Header... headers);

    void save(Submission submission, RequestCallback<? super Submission> callback, Header... headers);

}
