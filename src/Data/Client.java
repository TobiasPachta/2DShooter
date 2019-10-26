package Data;

import Main.Alerter;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public Socket host;
    private int port = 1111;
    private String ip = "127.0.0.1";

    public Client() {
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

    public void connectToHost(String ip, int port) throws IOException {
        host = new Socket(ip, port);
    }

    public String readMessage() throws IOException {
        return ConnectionIO.readMessage(host);
    }

    public void writeMessage(String message) throws IOException {
        ConnectionIO.writeMessage(host, message);
    }
}
