package Data;

import Main.Alerter;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Host {
    public Socket client;
    private ServerSocket host;
    private int port = 10270;
    private String ip = "127.0.0.1";
    private MyConnectionThread connectionThread;

    public Host() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ip = inetAddress.getHostAddress();
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
    }

    public String getMyIP() {
        return ip;
    }

    public String getMyPort() {
        return Integer.toString(port);
    }

    public void setMyPort(int port) {
        this.port = port;
    }

    public void createHost() {
        try {
            host = new ServerSocket(port);
            connectionThread = new MyConnectionThread(host, this);
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
    }

    public String readMessage() throws IOException {
        return ConnectionIO.readMessage(client);
    }

    public void writeMessage(String message) throws IOException {
        ConnectionIO.writeMessage(client, message);
    }

    public void close() {
        try {
            if (!host.isClosed())
                host.close();
        } catch (IOException ioExc) {
            Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
    }
}
