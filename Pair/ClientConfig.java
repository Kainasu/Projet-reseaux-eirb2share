package Pair;

import java.io.*;

public class ClientConfig {
    private int trackerPort;
    private String trackerIP;
    private int nbConnexion;
    private int messageSize;
    private int updatePeriod;
    private String fileLocation;
    private int peerPort;

    public ClientConfig(String configFilename) {
        try (BufferedReader br = new BufferedReader(new FileReader(configFilename))) {
            String str = "";
            while ((str = br.readLine()) != null) {
                String[] infos = str.split("=");
                if (infos[0].startsWith("#"))
                    continue;
                switch (infos[0].strip()) {
                    case "tracker-address" -> trackerIP = infos[1].strip();
                    case "tracker-port" -> trackerPort = Integer.parseInt(infos[1]);
                    case "connexion-limit" -> nbConnexion = Integer.parseInt(infos[1]);
                    case "message-size" -> messageSize = Integer.parseInt(infos[1]);
                    case "update-period" -> updatePeriod = Integer.parseInt(infos[1]);
                    case "file-location" -> fileLocation = infos[1].strip();
                    case "peer-port" -> peerPort = Integer.parseInt(infos[1]);
                    default -> System.out.println("Option " + infos[0] + " was ignored");
                }
            }
        } catch (IOException e) {
            System.err.println("Client configuration failed");
        }
    }


    public int getTrackerPort() {
        return trackerPort;
    }

    public String getTrackerIP() {
        return trackerIP;
    }

    public int getNbConnexion() {
        return nbConnexion;
    }

    public int getMessageSize() {
        return messageSize;
    }

    public int getUpdatePeriod() {
        return updatePeriod;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public int getPeerPort(){
        return peerPort;
    }
}
