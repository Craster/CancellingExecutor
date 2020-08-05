import server.Server;

import java.util.Scanner;

public class Main {


  public static void main(String[] args) {

    Server server = new Server();
    server.start();

    System.out.println("Started");

    System.out.println("Press any key to continue . . .");
    Scanner scan = new Scanner(System.in);
    scan.nextLine();
    server.stop();

  }

}
