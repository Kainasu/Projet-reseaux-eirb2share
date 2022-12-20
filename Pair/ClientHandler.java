package Pair;

import java.io.*;

public class ClientHandler extends Client implements Runnable{
    
    public ClientHandler(Client c, NetworkCommunication nc) {    
        super(c, nc);                
    }

    @Override
    public void run(){
        
        try {    
            this.communication.send("\r> Succesfully connected\n");            
            while(communication.getSocket().isConnected()){                
                receive(); //A changer car n'est pas bloquant
            }
            
        }
        catch (IOException e){}         
        catch (Exception e){}                                
    }
}
