package Pair;
import java.net.*;
import java.io.*;


class ClientServer implements Runnable{
    private ServerSocket serversocket;
    private Client c;

    public ClientServer(ServerSocket serverSocket, Client c){        
        this.serversocket = serverSocket;
        this.c = c;
    }

    @Override
    public void run(){
        while(!serversocket.isClosed()){
            try {
                Socket socket = serversocket.accept();
                System.out.print("\rA new peer is connected\n< ");
                ClientHandler clientHandler = new ClientHandler(c, new NetworkCommunication(socket));
                Thread thread = new Thread(clientHandler);
                thread.start();   
            }catch (IOException e){}    
        }                    
    }    
}
