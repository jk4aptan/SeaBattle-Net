package test.ex4;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class ChatHandler extends Thread {
    private static List<ChatHandler> handlers = Collections.synchronizedList(new ArrayList<>()); // Потокобезопасная - работает с ней только кто-то один

    private final Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    ChatHandler(Socket socket) {
        this.socket = socket;
        try {
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        handlers.add(this);
        try {
            while (true) {
                String message = dataInputStream.readUTF();
                brodcast(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            handlers.remove(this);
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void brodcast(String message) {
        synchronized (handlers) {
            Iterator<ChatHandler> iterator = handlers.iterator();
            while (iterator.hasNext()) {
                ChatHandler chatHandler = iterator.next();
                try {
                    synchronized (chatHandler.dataOutputStream) {
                        chatHandler.dataOutputStream.writeUTF(message);
                    }
                    chatHandler.dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
