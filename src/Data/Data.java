package Data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Data {
    private String fileName = "Players.txt";

    public void writeNewPlayer(String outputLine) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
        writer.append('\n');
        writer.append(outputLine);
        writer.close();
    }

    public void writeNewFile(String changedContent) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(changedContent);
        writer.close();
    }

    public String readWholeFile() {
        String content = "";

        if (!(new File(fileName)).exists())
            return content;

        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return content;
    }
}
