package mohamednasser.projects.http;

public abstract class HttpMessage {

    public static final String VERSION = "HTTP/1.1";

    protected String version;

    public static boolean isSupported(String version) {
        return version.equals(VERSION) || version.equals("HTTP/1.0");
    }

   public String getVersion() {
        return version;
   }

}
