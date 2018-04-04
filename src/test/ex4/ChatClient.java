package test.ex4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class ChatClient extends JFrame implements Runnable {
    private final Socket socket;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;
    private JTextArea outTextArea;
    private JTextField inTextArea;


    private ChatClient(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        super("client");
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;

        setSize(400, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        outTextArea = new JTextArea();
        inTextArea = new JTextField();

        add(outTextArea);
        add(inTextArea, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);

                try {
                    dataOutputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        inTextArea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dataOutputStream.writeUTF(inTextArea.getText());
                    dataOutputStream.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                inTextArea.setText("");
            }
        });

        setVisible(true);
        inTextArea.requestFocus();

        new Thread(this).start();
    }


    @Override
    public void run() {
        try {
            while (true) { //todo create flag
                String line = dataInputStream.readUTF();
                outTextArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inTextArea.setVisible(false);
            validate();
        }
    }


    public static void main(String[] args) {
        String site = "localhost";
        String port = "8082";

        Socket socket = null;
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        try {
            socket = new Socket(site, Integer.parseInt(port));
            dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            new ChatClient(socket, dataInputStream, dataOutputStream);

        } catch (IOException e) {
            e.printStackTrace();

            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
