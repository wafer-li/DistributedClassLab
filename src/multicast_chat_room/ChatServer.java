package multicast_chat_room;

import java.net.*;
import java.io.*;

public class ChatServer implements Runnable {

    private ChatServerThread clients[]   = new ChatServerThread[50];
    private ServerSocket     server      = null;
    private Thread           thread      = null;
    private int              clientCount = 0;


    private ChatServer(int port) {

        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        } catch (IOException ioe) {
            System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }


    public void run() {

        while (thread != null) {
            try {
                System.out.println("Waiting for a client ...");
                addThread(server.accept());
            } catch (IOException ioe) {
                System.out.println("Server accept error: " + ioe);
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


    private int findClient(int ID) {

        for (int i = 0; i < clientCount; i++)
            if (clients[i].getID() == ID)
                return i;
        return -1;
    }


    synchronized void handle(int ID, String input) {

        if (input.equals(".bye")) {

            for (ChatServerThread client : clients) {
                client.send(clients[findClient(ID)].getNickName() + "quit");
            }

            remove(ID);
        }
        else if (input.contains("$name:")) {
            clients[findClient(ID)].setNickName(input.substring(6));
        }
        else {
            for (ChatServerThread client : clients) {
                client.send(clients[findClient(ID)].getNickName() + ": " + input);
            }
        }
    }


    synchronized void remove(int ID) {

        int pos = findClient(ID);

        if (pos >= 0) {

            ChatServerThread toTerminate = clients[pos];

            System.out.println("Removing client thread " + ID + " at " + pos);

            if (pos < clientCount - 1) {
                System.arraycopy(clients, pos + 1, clients, pos + 1 - 1, clientCount - (pos + 1));
            }

            clientCount--;

            try {
                toTerminate.close();
            } catch (IOException ioe) {
                System.out.println("Error closing thread: " + ioe);
            }

            toTerminate.stop();
        }
    }


    private void addThread(Socket socket) {

        if (clientCount < clients.length) {
            System.out.println("Client accepted: " + socket);
            clients[clientCount] = new ChatServerThread(this, socket);
            try {
                clients[clientCount].open();
                clients[clientCount].start();
                clientCount++;
            } catch (IOException ioe) {
                System.out.println("Error opening thread: " + ioe);
            }
        }
        else
            System.out.println("Client refused: maximum " + clients.length + " reached.");
    }


    public static void main(String args[]) {

        if (args.length != 1) {
            System.out.println("Usage: java ChatServer port");
        }
        else {
            new ChatServer(Integer.parseInt(args[0]));
        }
    }
}