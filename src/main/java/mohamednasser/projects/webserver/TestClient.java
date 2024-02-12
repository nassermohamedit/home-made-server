package mohamednasser.projects.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestClient {

    public static void main(String[] args) throws IOException {

        int n = 7;
        List<OutputStream> outs = new ArrayList<>();

        for (int i=0; i<n; i++) {
            Socket socket = new Socket();
            SocketAddress address = new InetSocketAddress(8080);
            socket.connect(address);
            outs.add(socket.getOutputStream());
        }


        String input = "";
        Scanner sc = new Scanner(System.in);

        while (input != "stop") {
            input = sc.nextLine() + "\n";
            for (int i=0; i<n; i++) {
                String message = "client " + i + " says: " + input + "\n";
                outs.get(i).write(message.getBytes());
            }
        }
    }
}
