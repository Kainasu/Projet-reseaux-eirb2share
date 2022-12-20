package test;


import Pair.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;



public class ClientTest {

    private Client client;
    private ArrayList<FileUtil> file;
    private Server server;

    @Before
    public void setUp() throws InterruptedException, IOException, NoSuchAlgorithmException {
        //Thread serverThread = startServer();
        file = new ArrayList<>();
        file.add(new FileUtil("./test/TestFiles/test2Mo.txt"));
        client = new Client("./test/testConfig.ini", new NetworkCommunication("192.168.145.97", 8080), true);
    }

    private Thread startServer() throws InterruptedException {
        Thread serverThread = new Thread(server = new Server());
        serverThread.start();
        Thread.sleep(1000);
        return serverThread;
    }

    @Test
    public void testInstantiation() {
        //assertEquals("/127.0.0.1", client.getSocket().getInetAddress().toString());
        //assertEquals(8080, client.getSocket().getPort());
    }

    @Test
    //TODO finish test case
    public void testGetPieces() throws IOException, InterruptedException {
        client.getPieces("< getpieces " + file.get(0).getKey() + " [1 2]", 5);
        Thread.sleep(100);
        //System.out.println("Data out: " + client.getSocket().getOutputStream().);
        //assertEquals("> data [" + file.get(0).getPiece(0) + file.get(0).getPiece(2) + "]", server.lastMessage);
    }

    @Test
    public void testSendPieces() throws IOException, NoSuchAlgorithmException {
        FileUtil file = new FileUtil("./test/TestFiles/test2Mo.txt");
        //client.seedingFiles.add(file);
        client.getPieces("< getpieces " + file.getKey() + " [0]", 0);
    }
}
