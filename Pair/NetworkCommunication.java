package Pair;

import java.util.*;
import java.net.*;
import java.io.*;


public class NetworkCommunication implements Communication {
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public NetworkCommunication(Socket socket){
        try{
            this.socket = socket;
            this.dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            this.dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        //System.out.println(this.socket.getLocalPort());
    }

    public NetworkCommunication(String ipAddress, int port) throws IOException {
        this(new Socket(ipAddress, port));
    }

    @Override
    public void send(String... strings) throws IOException {
        StringBuilder toSend = new StringBuilder("");
        boolean addEnd = false;
        for (String s : strings) {
            //System.out.println(s);
            if (s.strip().equals("> interested") || s.strip().equals("> data") || s.strip().equals("> have") || s.strip().equals("> getpieces")){
                addEnd = true;
            }

            toSend.append(s);
        }
        if (toSend.length() == 0) {
            dataOut.write('\n');
            return;
        }

        //System.out.println("DEBUG :" + toSend.toString());
        //toSend.setCharAt(toSend.length() - 1, '\n');        
        dataOut.writeBytes(toSend.toString());
        if (addEnd) {
            //System.out.println("Met le slash n");
            dataOut.writeBytes("\n");
        }
        dataOut.flush();
    }

    /**
     * Receives commands and data and processes it
     *
     * @return
     * @throws IOException
     */
    @Override
    public Response receive() throws IOException {
        int i;
        //System.out.print("DEBUG RECEIVE COM: ");
        List<Byte> byteList = new ArrayList<>();
        //System.out.println("dataIn : " + dataIn.read());
        while ((i = dataIn.read()) != '\n') {
            byteList.add((byte) i);
            //System.out.print((char) (byte) i);
        }
        //System.out.println("DEBUG RECEIVE OUT");
        return new Response(this.socket.getInetAddress(), this.socket.getPort(), byteList);
    }

    public void stopCommunication() throws IOException {
        this.dataOut.close();
        this.dataIn.close();
        this.socket.close();
    }

    @Override
    public void send(List<Piece> pieces) throws IOException {
        //System.out.println("rentre dans send");
        dataOut.writeBytes("> data " + pieces.get(0).getFileKey() + " [");
        //System.out.println("rentre dans send 2 ");
        for (Piece p : pieces) {
            //System.out.println("rentre dans send i");
            //System.out.println(p.getData());
            dataOut.writeBytes(p.getIndex() + ":");
            dataOut.write(p.getData());
            dataOut.writeBytes(" ");
        }
        //System.out.println("rentre dans send 4");
        dataOut.writeBytes("]\n");
        dataOut.flush();
    }

    public Socket getSocket() {
        return socket;
    }
}