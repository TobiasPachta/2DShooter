package Data;

import Main.Alerter;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.ServerSocket;

class MyConnectionThread implements Runnable {
    private Thread t;
    private ServerSocket host;
    private Host connectionHoster;

    MyConnectionThread(ServerSocket host, Host connectionHoster) {
        t = new Thread(this);
        this.host = host;
        this.connectionHoster = connectionHoster;
        t.start();
    }

    public void run() {
        try {
            connectionHoster.client = host.accept();
        } catch (IOException ioExc) {
            //Alerter.Alert(Alert.AlertType.ERROR, "IO Error", "Something went wrong" + ioExc.getMessage());
        }
    }
}