package test.ex2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8082);

        Socket socket = serverSocket.accept();

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        String html = "<html><head><title>Hi</title></head><body><h1>Hello Java!</h1></body></html>";
        String header = "HTTP/1.1 200 OK\n" +
                "Content-language: ru\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "Content-Length: " + html.length() + "\n" +
                "Connection: close\n\n";

        String result = header + html;
        outputStream.write(result.getBytes());

        outputStream.close();
        socket.close();
    }
}
