package mohamednasser.projects.webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private final SocketAddress address;
    private final Path webroot;

    private final ServerSocket socket;

    private boolean started = false;

    private boolean closed = false;

    private final ExecutorService executor;

    private HttpServer(SocketAddress address, Path webRoot) throws IOException {
        this.address = address;
        this.webroot = webRoot;
        this.socket = new ServerSocket();
        this.socket.bind(address);
        this.executor = Executors.newCachedThreadPool(Thread.ofVirtual().factory());
    }

    public static HttpServer createInstance(SocketAddress address) throws IOException {
        return new HttpServer(address, Paths.get("."));
    }

    public static HttpServer createInstance(Path webroot) throws IOException {
        return new HttpServer(new InetSocketAddress(0), webroot);
    }

    public static HttpServer createInstance(SocketAddress address, Path webroot) throws IOException {
        return new HttpServer(new InetSocketAddress(0), webroot);
    }

    public static HttpServer createInstance() throws IOException {
        return new HttpServer(new InetSocketAddress(0), Paths.get("."));
    }

    public void run() throws IOException {
        if (started) throw new IllegalArgumentException();
        started = true;
        closed = false;
        LOGGER.info("Server Started");
        while (!closed) {
            Socket clientSocket = this.socket.accept();
            LOGGER.info("Connection accepted: " + clientSocket.getInetAddress());
            executor.submit(new ClientHandler(clientSocket));
        }
    }


    public void close() throws IOException {
        closed = true;
        socket.close();
        executor.shutdown();
    }
}
