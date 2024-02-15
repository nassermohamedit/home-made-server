package mohamednasser.projects.http;

public class HttpException extends Exception {

    HttpStatusCode httpStatusCode;

    public HttpException(HttpStatusCode httpStatusCode) {
        super(httpStatusCode.message);
        this.httpStatusCode = httpStatusCode;
    }
}
