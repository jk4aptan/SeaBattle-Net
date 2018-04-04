package test.ex3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8082);

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new SocketDispatcher(socket)).start();
        }
    }
}
