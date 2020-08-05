import events.HeartbeatEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {


  public static void main(String[] arg) {

    try {
      try (Socket clientSocket = new Socket("localhost", 4004)) {

        try (ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
          HeartbeatEvent heartbeatEvent = new HeartbeatEvent(1);
          oos.writeObject(heartbeatEvent);
          String serverWord = in.readLine();
          System.out.println(serverWord);
        }

      } finally {
        System.out.println("Client was closed...");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


  }

}
