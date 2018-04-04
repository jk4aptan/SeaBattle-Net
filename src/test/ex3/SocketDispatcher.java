package test.ex3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketDispatcher implements Runnable {
    private final Socket socket;

    SocketDispatcher(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String html = "<html><head><title>Hi</title></head><body><h1>Hello Java!</h1></body></html>";
        String header = "HTTP/1.1 200 OK\n" +
                "Content-language: ru\n" +
                "Content-Type: text/html; charset=utf-8\n" +
                "Content-Length: " + html.length() + "\n" +
                "Connection: close\n\n";

        String result = header + html;
        try {
            if (outputStream != null) {
                outputStream.write(result.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
