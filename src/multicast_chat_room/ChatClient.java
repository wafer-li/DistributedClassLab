package multicast_chat_room;

import java.awt.event.ActionEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This is the Client class
 * Please put some info here.
 *
 * @author Wafer Li
 * @since 16/11/16 23:07
 */
public class ChatClient {

    private JTextField ipField;
    private JTextField nameField;
    private JTextField portField;
    private JButton    connect;
    private JButton    disconnect;
    private JTextField inputField;
    private JPanel     window;
    private JTextArea  chatArea;


    private Socket           socket    = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread client    = null;


    public ChatClient() {

        initListener();
    }


    public void initListener() {

        connect.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String serverName = ipField.getText();
                int    serverPort = Integer.parseInt(portField.getText());
                String nickname   = nameField.getText();

                try {
                    socket = new Socket(serverName, serverPort);
                    open();
                    send("$name:" + nickname);

                }
                catch (UnknownHostException uhe) {
                    display("Host unknown: " + uhe.getMessage());
                }
                catch (IOException ioe) {
                    display("Unexpected exception: " + ioe.getMessage());
                }

                connect.setEnabled(false);
                disconnect.setEnabled(true);

                inputField.setEnabled(true);

                ipField.setEnabled(false);
                portField.setEnabled(false);
                nameField.setEnabled(false);
            }
        });

        disconnect.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                send(".bye");
                close();

                disconnect.setEnabled(false);
                inputField.setEnabled(false);

                connect.setEnabled(true);
                ipField.setEnabled(true);
                portField.setEnabled(true);
                nameField.setEnabled(true);
            }
        });

        inputField.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                send();
            }
        });
    }


    private void send(String msg) {

        try {
            streamOut.writeUTF(msg);
            streamOut.flush();
            inputField.setText("");
        }
        catch (IOException ioe) {
            display("Sending error: " + ioe.getMessage());
            close();
        }
    }


    private void send() {

        try {
            streamOut.writeUTF(inputField.getText());
            streamOut.flush();
            inputField.setText("");
        }
        catch (IOException ioe) {
            display("Sending error: " + ioe.getMessage());
            close();
        }
    }


    public void close() {

        try {

            if (streamOut != null) {
                streamOut.close();
            }

            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException ioe) {
            display("Error closing ...");
        }

        client.close();
        client.stop();
    }


    public void display(String msg) {

        chatArea.append(msg);
        chatArea.append("\n");
    }


    public void open() {

        try {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client = new ChatClientThread(this, socket);
        }
        catch (IOException ioe) {
            display("Error opening output stream: " + ioe);
        }
    }


    public void handle(String msg) {

        display(msg);
        System.out.println(msg);
    }


    public static void main(String[] args) {

        JFrame frame = new JFrame("ChatClient");
        frame.setContentPane(new ChatClient().window);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
