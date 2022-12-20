package Pair;

import java.io.*;

public class ClientTracker extends Client{
    BufferedReader bReader;
    
    public ClientTracker(String configFilename, Communication c) throws IOException{
            super(configFilename, c, true);
            bReader = new BufferedReader(new InputStreamReader(System.in));
            /*
            try{
                //this.announcePresence();
            } catch (IOException e){
                throw new RuntimeException(e.getMessage());
            }
            */
    }

    public ClientTracker(String configFilename) throws IOException{
        super(configFilename, true);
        bReader = new BufferedReader(new InputStreamReader(System.in));
        /*
        try{
            //this.announcePresence();
        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
        */
    }
    
    
    /**
    * Compiles and sends announce message to tracker server
    *
    * @throws IOException
    */
    public void announcePresence(int port) throws IOException {
        communication.send("announce listen ",
                String.valueOf(port),
                " seed [",
                constructSeeding(),
                "] leech [",
                constructLeeching(),
                "]");
    }

    public Communication getCommunication(){
        return communication;
    }

}