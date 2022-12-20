package Pair;

import java.io.*;
import java.net.*;

public class Server implements Runnable{
    static final int port = 8080;

    public static void main(String[] args) {
        Server server = new Server();
        while (true) {
            server.startServer();
        }
    }

    @Override
    public void run() {
        Server server = new Server();
        while (true) {
            server.startServer();
        }
    }

    private void startServer() {
        try (ServerSocket ss = new ServerSocket(port);
             Socket s = ss.accept();
             BufferedReader bf = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter pr = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true)) {

            String str = "";
            while (!str.equals("END")) {
                str = bf.readLine();
                System.out.println("Server in: " + str);
                pr.println("> ok");
                System.out.println("Server out: ok");
            }

            System.out.println("Connection was closed by client");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
