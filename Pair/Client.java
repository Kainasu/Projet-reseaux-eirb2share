package Pair;

import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Client implements PairInterface {
    private ClientConfig config;
    private String fileLocation;
    protected Communication communication;
    private ArrayList<FileUtil> leechingFiles = new ArrayList<>();
    private ArrayList<FileUtil> seedingFiles = new ArrayList<>();

    public Client(String configFilename, Communication communication, boolean firstTime) {
        config = new ClientConfig(configFilename);
        if (firstTime) {
            try {
                fileLocation = config.getFileLocation();
                String[] fileNames = new File(fileLocation).list();
                if (fileNames != null) {
                    Scanner sc = new Scanner(System.in);
                    boolean entering = true;
                    do {
                        System.out.print("Choose your files from /Pair/files directory (F to finish): ");
                        String fileName = sc.nextLine();
                        if (fileName.toUpperCase().equals("F"))
                            entering = false;
                        else {
                            seedingFiles.add(new FileUtil(fileLocation + "/" + fileName));
                        }
                    } while (entering);

                    //for (String f : fileNames) {
                    //    seedingFiles.add(new FileUtil(fileLocation + "/" + f));
                    //}
                } else {
                    throw new FileNotFoundException("The path " + fileLocation + "is invalid!");
                }
            } catch (NoSuchAlgorithmException | IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        this.communication = communication;
    }
    
    public Client(Client anotherClient, Communication communication) {
        //System.out.println("ancien Client : " + anotherClient.seedingFiles.toString());        
        this.config = anotherClient.config;
        this.fileLocation = anotherClient.fileLocation;
        this.leechingFiles = anotherClient.leechingFiles;
        this.seedingFiles = anotherClient.seedingFiles;
        this.communication = communication;
        //System.out.println("newClient : " + seedingFiles.toString());        
    }


    public Client(String configFilename, boolean firstTime) {
        config = new ClientConfig(configFilename);
        try {
            this.communication = new NetworkCommunication(config.getTrackerIP(), config.getTrackerPort());
            if (firstTime) {
                fileLocation = config.getFileLocation();
                String[] fileNames = new File(fileLocation).list();
                if (fileNames != null) {
                    Scanner sc = new Scanner(System.in);
                    boolean entering = true;
                    do {
                        System.out.print("Choose your files from /Pair/files directory (F to finish): ");
                        String fileName = sc.nextLine();
                        if (fileName.toUpperCase().equals("F")) {
                            entering = false;
                        } else {
                            seedingFiles.add(new FileUtil(fileLocation + "/" + fileName));
                        }
                    } while (entering);

                    //for (String f : fileNames) {
                    //    seedingFiles.add(new FileUtil(fileLocation + "/" + f));
                    //}
                } else {
                    throw new FileNotFoundException("The path " + fileLocation + "is invalid!");
                }
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    

    public void look(String str) throws IOException {
        this.communication.send("look ", str);
    }

    /**
     * Finds key to given file name according to local file list
     *
     * @param fileName name of file for which the key is to be found
     * @return key of file according to local file list
     * @throws FileNotFoundException if file  was not registered in local database yet
     */
    private String findKey(String fileName) throws FileNotFoundException {
        String key = null;
        for (FileUtil f : Stream.concat(seedingFiles.stream(), leechingFiles.stream()).toList()) {
            if (fileName.equals(f.file.getName())) {
                key = f.getKey();
            }
        }
        if (key == null) {
            throw new FileNotFoundException("The file with the name \"" + fileName + "\" could not be found locally");
        }
        return key;
    }

    private String BitSetToString(BitSet bs) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bs.length(); i++) {
            if (bs.get(i))
                s.append('1');
            else
                s.append('0');
        }
        return s.toString();
    }


    public void getFile(String str) throws IOException {
        //String key = str.split(" ", 2)[1];
        this.communication.send("getfile ", str);
    }

    public void interested(String str) throws IOException {
        //String fileName = str.split(" ", 2)[1];
        this.communication.send("> interested ", str);
    }

    //> getpieces 8905e92afeb80fc7722ec89eb0bf0966 [3 5 7 8 9]
    public void getPieces(String str, int max) throws IOException {
        String[] cmd = str.split(" ", 4);
        FileUtil file;

        //System.out.println("request : " + str);
        //System.out.println("cmd " + cmd.toString());
        //
        //System.out.println("cmd[1] : " + cmd[1]);
        //System.out.println("cmd[2] : " + cmd[2]);
        //System.out.println("cmd[3] : " + cmd[3]);
        if ((file = this.findFileByKey(cmd[2])) == null)
            throw new FileNotFoundException("The file with key: " + cmd[2] + " could not be found.");

        //System.out.println("Je suis la");
        //TODO throw exception
        if (cmd[3].length() == 0 || !(cmd[3].charAt(0) == '[' && cmd[3].charAt(cmd[3].length() - 1) == ']')) {
            System.err.println("You need to insert your pieces inside brackets => [x y z]");
            return;
        }

        //System.out.println("strPieces");
        String[] strPieces = cmd[3].substring(1, cmd[3].length() - 1).split(" ");
        //System.out.println("piecesIndexes");
        int[] pieceIndexes = Arrays.stream(strPieces)
                .mapToInt(Integer::parseInt)
                .toArray();
        //System.out.println("before send");
        //TODO check buffer map for presence of chunk        
        communication.send(this.compilePieces(file, pieceIndexes));
        
    }

    private FileUtil findFileByKey(String key) {
        return Stream.concat(seedingFiles.stream(), leechingFiles.stream())
                .filter(f -> f.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public void have(String str) throws IOException {
        String[] cmd = str.split(" ", 3);
        String key = cmd[2];
        //String key = findKey(fileName);
        String bufferMap = "";        
        for (FileUtil f : seedingFiles) {            
            if (Objects.equals(key, f.getKey())) {                              
                bufferMap = f.BitSetToString(f.getBufferMap());                
            }
        }        
        this.communication.send("> have ", key, " " , bufferMap);
    }

    public void update() throws IOException {
        StringBuilder out = new StringBuilder("update [");
        for (FileUtil f : seedingFiles) {
            out.append(f.getKey()).append(" ");
        }
        this.communication.send(out.toString().stripTrailing(), "] leech [" + constructLeeching());
    }

    private List<Piece> compilePieces(FileUtil file, int[] pieceIndexes) {
        //System.out.println("Entre dans compilePieces");
        return Arrays.stream(pieceIndexes)
                .mapToObj(file::getPiece)
                .collect(Collectors.toList());
    }

    protected String constructSeeding() {
        StringBuilder seeding = new StringBuilder();
        for (FileUtil f : seedingFiles) {
            seeding.append(f.getAnnouncement()).append(" ");
        }
        return seeding.toString().stripTrailing();
    }

    protected String constructLeeching() {
        StringBuilder leeching = new StringBuilder();
        for (FileUtil f : leechingFiles) {
            leeching.append(f.getKey()).append(" ");
        }
        return leeching.toString().stripTrailing();
    }

    protected ClientConfig getConfig() {
        return config;
    }

    public void receive() throws Exception {
        //System.out.println("Rentre dans receive()");
        StringBuilder command = new StringBuilder();
        int spaces = 0;
        int i = 0;
        //System.out.println("AVANT FUNC RECEIVE COM");
        Response r = this.communication.receive();
        //System.out.println("\nAPRES FUNC RECEIVE COM");
        List<Byte> bytes = r.bytes();
        
        StringBuilder answer = new StringBuilder();

        //This loop is to print the server response in the client terminal
        while (i < bytes.size()) {
            answer.append((char) (byte) bytes.get(i));
            i++;
        }
        i = 0;
        
        while (i < bytes.size()) {
            if (spaces > 0)
                command.append((char) (byte) bytes.get(i)); //Better way
            if (bytes.get(i) == ' ')
                spaces++;
            if (spaces >= 2) {
                i++;
                break;
            }
            i++;
        }
        i = 0;
        List<Byte> args = bytes.subList(i, bytes.size());        
        String key;
        try {
            key = Utils.bytesToString(args.subList(0, args.indexOf((byte) ' ')));
        } catch (IllegalArgumentException e) {
            key = "";
        }
        
        //TODO : add all other commands that we don't need to show        
        
        if (!(command.toString().strip().equals("interested")) && !(command.toString().strip().equals("getpieces")))
            System.out.println(answer.toString());
        
            
        //System.out.println(" answer : " + answer.toString());
        switch (command.toString().strip()) {            
            case "interested" -> this.have(answer.toString());                
            case "getpieces" -> {
                //System.out.println("Entre dans getpieces");
                getPieces(answer.toString().strip(), 10);                
                this.receivePieces(key,args);
                //System.out.println("Sort de getpieces");
                //this.communication.send(bytes);

            }
            case "have" -> this.receiveBufferMap(key, args, r.ipAddress(), r.port());            
            //case "data" -> this.receivePieces(key,args);
        }
    }

    public void receivePieces(String key, List<Byte> arguments) throws Exception {        
        FileUtil file = findFileByKey(key);

        List<Byte> pieces = arguments.subList(arguments.indexOf((byte) '[') + 1, arguments.indexOf((byte) ']'));
        List<List<Byte>> piecesList = new ArrayList<>();
        piecesList.add(new ArrayList<>());
        int i = 0;
        for (byte b : pieces) {
            if (b != ' ') {
                piecesList.get(i).add(b);
            } else {
                piecesList.add(new ArrayList<>());
                i++;
            }
        }

        for (List<Byte> l : piecesList) {
            file.receivePiece(l);
        }
    }

    public void receiveBufferMap(String key, List<Byte> arguments, InetAddress address, int port) throws NoSuchAlgorithmException, IOException {        
        FileUtil file = findFileByKey(key);
        if (file == null) {
            System.out.print("Enter filename to save : ");
            Scanner sc = new Scanner(System.in);
            String filename = sc.nextLine();
            file = new FileUtil(filename);
            //sc.close();
        }
        Seeder seeder = new Seeder(address, port);    
        file.updateSeeder(seeder, Utils.listToBitSet(arguments));
    }

    public void stopCommunication() throws IOException {
        this.communication.stopCommunication();
    }
}
