package mohamednasser.projects.http;

public  enum HttpMethod {

    GET, HEAD;

    public static final int MAX_LENGTH;

    static {
        int max = 0;
        int l;
        for (HttpMethod method: HttpMethod.values()) {
            if ((l = method.name().length()) > max)
                max = l;
        }
        MAX_LENGTH = max;
    }
}

