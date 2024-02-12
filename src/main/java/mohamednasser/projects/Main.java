package mohamednasser.projects;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {

        InetSocketAddress address = new InetSocketAddress(8080);
        HttpServer server = HttpServer.createInstance(address);
        server.run();
    }

}