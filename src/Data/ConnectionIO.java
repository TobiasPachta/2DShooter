package Data;

import java.io.*;
import java.net.Socket;

class ConnectionIO {
    static String readMessage(Socket socket) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        char[] buffer = new char[255];
        if (bufferedReader.ready()) {
            int charCount = bufferedReader.read(buffer, 0, 255);
            return new String(buffer, 0, charCount);
        }
        return "";
    }

    static void writeMessage(Socket socket, String message) throws IOException {
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        printWriter.print(message);
        printWriter.flush();
    }
}
