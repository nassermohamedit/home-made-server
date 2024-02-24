package mohamednasser.projects.http;

public enum HttpStatusCode {

    // Client Errors
    CLIENT_ERROR_400(400, "Bad Request"),
    CLIENT_ERROR_401(401, "Method Not Allowed"),
    CLIENT_ERROR_404(414, "Not Found"),
    CLIENT_ERROR_406(406, "Not Acceptable"),
    CLIENT_ERROR_414(414, "URI Too Long"),

    // Server Errors
    SERVER_ERROR_500(500, "Internal Server Error"),
    SERVER_ERROR_501(501, "Not Implemented"),
    SERVER_ERROR_505(505, "HTTP Version Not Supported");

    public final int code;

    public final String message;

    HttpStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
