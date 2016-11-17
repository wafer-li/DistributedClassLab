package multicast_chat_room;

import java.awt.event.ActionEvent;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatServer implements Runnable {

    private List<ChatServerThread> clients     = new ArrayList<>(50);
    private ServerSocket           server      = null;
    private Thread                 thread      = null;
    private int                    clientCount = 0;


    private JPanel     window;
    private JTextField portField;
    private JButton    openServer;
    private JTextArea  msgArea;
    private JButton    stopServer;


    private ChatServer() {

        initListener();
    }


    private void initListener() {

        openServer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int port = Integer.parseInt(portField.getText());
                open(port);

                openServer.setEnabled(false);
                portField.setEnabled(false);
            }
        });

        stopServer.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                close();

                openServer.setEnabled(true);
                portField.setEnabled(true);
                stopServer.setEnabled(false);
            }
        });
    }


    public void run() {

        while (thread != null) {
            try {
                display("Waiting for a client ...");
                addThread(server.accept());
            }
            catch (IOException ioe) {
                display("Server accept error: " + ioe);
                stop();
            }
        }
    }


    private void start() {

        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }


    private void stop() {

        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }


    private void open(int port) {

        if (server == null) {
            try {
                display("Binding to port " + port + ", please wait  ...");
                server = new ServerSocket(port);

                display("Server started: " + server);
                start();
            }
            catch (IOException ioe) {
                display("Can not bind to port " + port + ": " + ioe.getMessage());
            }
        }
    }


    private void close() {

        if (server != null) {

            display("Server closed.");

            try {
                stop();
                server.close();
                server = null;
                removeAll();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private int findClient(int ID) {

        for (int i = 0; i < clientCount; i++) {

            if (clients.get(i).getID() == ID) {
                return i;
            }
        }
        return -1;
    }


    synchronized void handle(int ID, String input) {

        ChatServerThread currentClient = clients.get(findClient(ID));

        if (input.equals(".bye")) {

            for (ChatServerThread client : clients) {
                client.send(currentClient.getNickName() + " quit!");
            }

            remove(ID);
        }
        else if (input.contains("$name:")) {
            currentClient.setNickName(input.substring(6));

            for (ChatServerThread client : clients) {
                client.send(currentClient.getNickName() + " come!");
            }
        }
        else {
            for (ChatServerThread client : clients) {
                client.send(clients.get(findClient(ID)).getNickName() + ": " + input);
            }
        }
    }


    public void display(String msg) {

        msgArea.append(msg);
        msgArea.append("\n");
    }


    synchronized void remove(int ID) {

        int pos = findClient(ID);

        if (pos >= 0) {

            ChatServerThread toTerminate = clients.get(pos);


            display("Removing client thread " + ID + " at " + pos);


            if (pos < clientCount - 1) {
                clients.remove(pos);
            }

            clientCount--;

            try {
                toTerminate.close();
            }
            catch (IOException ioe) {
                display("Error closing thread: " + ioe);
            }

            toTerminate.stop();
        }
    }


    private void removeAll() {

        clients.forEach(Thread::stop);
        clients.clear();
    }


    private void addThread(Socket socket) {

        if (clientCount < 50) {

            display("Client accepted: " + socket);
            clients.add(new ChatServerThread(this, socket));

            try {
                clients.get(clientCount).open();
                clients.get(clientCount).start();
                clientCount++;
            }
            catch (IOException ioe) {
                display("Error opening thread: " + ioe);
            }
        }
        else {
            display("Client refused: maximum " + 50 + " reached.");
        }
    }


    public static void main(String[] args) {

        JFrame frame = new JFrame("ChatServer");
        frame.setContentPane(new ChatServer().window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}