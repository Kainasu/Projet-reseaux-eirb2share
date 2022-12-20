package Pair;

import java.io.IOException;
import java.util.logging.*;
import java.net.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger("MyLog");
        FileHandler fh;

        try {
            // This block configure the logger with handler and formatter  
            LogManager.getLogManager().reset();
            fh = new FileHandler("log/client.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ClientTracker c = new ClientTracker("./Pair/config.ini"); // For Tracker

            Client c2 = null; // For Peer
            Communication peerCom = null;

            ServerSocket peerSocket = new ServerSocket(0);            
            Thread peerServer = new Thread(new ClientServer(peerSocket, c)); // For answering peer request
            peerServer.start();

            c.announcePresence(peerSocket.getLocalPort());
            c.receive();
            logger.info("New Client with a configuration");
            boolean open = true;
            while (open) {
                System.out.print("< ");
                String[] s = c.bReader.readLine().strip().split(" ", 2);
                String cmd = s[0];
                String str = "";
                if (s.length > 1)
                    str = s[1];

                switch (cmd) {
                    case "look" -> {c.look(str); logger.info("Client : look"); c.receive();}
                    case "getfile" -> {c.getFile(str); logger.info("Client : getfile"); c.receive();}
                    case "interested" -> {
                        if (c2 == null) {System.out.println("Please connect to a peer first"); break;}
                        c2.interested(str); logger.info("Client : interested"); c2.receive();} //TODO Read answer
                    case "getpieces" -> {
                        if (c2 == null) {
                            System.out.println("Please connect to a peer first");
                            break;
                        }                        
                        c2.communication.send("> getpieces ", str); logger.info("Client : getpieces"); c2.receive();}
                    case "have" -> {
                        if (c2 == null) {System.out.println("Please connect to a peer first"); break;}
                        c2.have(str);
                        logger.info("Client : have");
                    }                    
                    case "connect" -> {
                        String[] peerToContact = str.split(":", 2);
                        String ip = peerToContact[0];
                        int port = Integer.parseInt(peerToContact[1]);                        
                        peerCom = new NetworkCommunication(ip, port);
                        c2 = new Client(c, peerCom);
                        logger.info("Client : connected to peer");
                        c2.receive();                        
                    }
                    case "EXIT" -> {
                        c.stopCommunication();
                        open = false;
                        logger.info("Client : Exit");
                    }
                    default -> {
                        logger.warning("Client : Command not found");
                        System.out.println("command not found");
                        System.out.println("possible commands: ");
                        System.out.println("- look [criterium1 criterium2 ...]");
                        System.out.println("- getfile <key>");
                        System.out.println("- interested <key>");
                        System.out.println("- getpieces <key> [$index1 $index2 ...]");
                        System.out.println("- have <fileName> ");
                        System.out.println("- connect <ip:port>");
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
