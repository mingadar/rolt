package dev.mikita.rolt.rest.handler;

/**
 * Contains information about an error and can be send to client as JSON to let them know what went wrong.
 */
public class ErrorInfo {
    private String message;
    private String requestUri;

    /**
     * Instantiates a new Error info.
     */
    public ErrorInfo() {}

    /**
     * Instantiates a new Error info.
     *
     * @param message    the message
     * @param requestUri the request uri
     */
    public ErrorInfo(String message, String requestUri) {
        this.message = message;
        this.requestUri = requestUri;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets request uri.
     *
     * @return the request uri
     */
    public String getRequestUri() {
        return requestUri;
    }

    /**
     * Sets request uri.
     *
     * @param requestUri the request uri
     */
    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    @Override
    public String toString() {
        return "ErrorInfo{" + requestUri + ", message = " + message + "}";
    }
}
