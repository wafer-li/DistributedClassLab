package multicast_chat_room;

import java.net.*;
import java.io.*;

public class ChatServerThread extends Thread {

    private ChatServer       server    = null;
    private Socket           socket    = null;
    private int              ID        = -1;
    private DataInputStream  streamIn  = null;
    private DataOutputStream streamOut = null;

    private String nickName;


    public ChatServerThread(ChatServer server, Socket socket) {

        super();
        this.server = server;
        this.socket = socket;
        ID = socket.getPort();
    }


    public void send(String msg) {

        try {
            streamOut.writeUTF(msg);
            streamOut.flush();
        }
        catch (IOException ioe) {
            server.display(ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
            stop();
        }
    }


    public int getID() {

        return ID;
    }


    public void run() {

        server.display("Server Thread " + ID + " running.");

        while (true) {
            try {
                server.handle(ID, streamIn.readUTF());
            }
            catch (IOException ioe) {
                server.display(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
            }
        }
    }


    public void open() throws IOException {

        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        streamOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }


    public void close() throws IOException {

        if (socket != null) {
            socket.close();
        }

        if (streamIn != null) {
            streamIn.close();
        }

        if (streamOut != null) {
            streamOut.close();
        }
    }


    public String getNickName() {

        return nickName;
    }


    public void setNickName(String nickName) {

        this.nickName = nickName;
    }
}
