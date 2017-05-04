package de.thb.ue.android.data.VOs;

/**
 * Created by Sebastian Müller on 26.01.2017.
 */

public class ProcessedResponse<T> {
    private int responseCode;
    private T   responseBody;
    private String httpMessage;

    public ProcessedResponse(int responseCode, T responseBody) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }

    public ProcessedResponse(int responseCode, T responseBody, String httpMessage) {
        this.responseCode = responseCode;
        this.httpMessage = httpMessage;
        this.responseBody = responseBody;
    }

    public int getCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public T getBody() {
        return responseBody;
    }

    public void setResponseBody(T responseBody) {
        this.responseBody = responseBody;
    }

    public String getHttpMessage() {
        return httpMessage;
    }

    public void setHttpMessage(String httpMessage) {
        this.httpMessage = httpMessage;
    }
}
