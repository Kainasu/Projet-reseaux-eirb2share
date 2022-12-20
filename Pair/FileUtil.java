package Pair;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class FileUtil {
    /**
     * File size in bytes
     */
    private long fileSize;
    /**
     * Piece size in bytes
     */
    private int pieceSize;
    private final int nbPieces;
    private String key;
    private BitSet bufferMap;
    private boolean leeching;
    public File file;
    public Map<Seeder, BitSet> seeders;

    public FileUtil(String fileName, int pieceSize, boolean leeching) throws IOException, NoSuchAlgorithmException {
        file = new File(fileName);
        if (file.createNewFile() && !leeching) System.out.println("Something is wrong");        
        this.fileSize = this.file.length();
        this.pieceSize = pieceSize;
        this.leeching = leeching;
        this.nbPieces = (int) Math.ceil((double) this.fileSize / pieceSize);
        this.key = Md5Hash.getMD5Checksum(this.file);
        this.bufferMap = new BitSet(nbPieces);        
        if (fileSize != 0) //file present locally during instantiation
            this.bufferMap.set(0, nbPieces);
        //System.out.println(bufferMap);
        this.seeders = new HashMap<>();
    }

    public FileUtil(String fileName) throws IOException, NoSuchAlgorithmException {
        this(fileName, 1024, true);
    }

    public String getFileName() {
        return this.file.getName();
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getKey() {
        return key;
    }

    public int getNbPieces() {
        return nbPieces;
    }

    public int getPieceSize() {
        return pieceSize;
    }

    public BitSet getBufferMap() {
        return bufferMap;
    }

    /**
     * Returns a string containing the bytes of a given index
     *
     * @param index index of piece to be returned
     * @return String containing piece of file in binary representation
     * @throws IndexOutOfBoundsException if the specified index is negative or greater than {@code NbPieces}
     */
    public Piece getPiece(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= nbPieces) {
            throw new IndexOutOfBoundsException("Index " + index + " out of Bounds for NbPieces = " + nbPieces);
        }
        try (RandomAccessFile raf = new RandomAccessFile(this.file, "r")) {
            raf.seek((long) index * pieceSize);
            if (index >= nbPieces - 1) {
                //TODO
            }
            byte[] bytes = new byte[pieceSize];
            raf.readFully(bytes);
            return new Piece(index, this.key, bytes);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void receivePiece(List<Byte> piece) throws Exception { //TODO check piece length
        int debug = 0;
        System.out.println("debug : " + (debug++));
        int pieceIndex = Integer.parseInt(String.valueOf((char) (byte) piece.get(0)));
        System.out.println("debug : " + (debug++));
        if (this.bufferMap.get(pieceIndex))
            throw new Exception("BitAlreadySet");
        System.out.println("debug : " + (debug++));
        
        
        List<Byte> bytes = piece.subList(2, piece.size());
        System.out.println("debug : " + (debug++));
        assert (bytes.size() % 8 == 0);
        System.out.println("debug : " + (debug++));
        try (RandomAccessFile raf = new RandomAccessFile(this.file, "rwd")) { //TODO check if mode d is necessary
            raf.seek((long) pieceIndex * this.pieceSize * 8); //why * 8?
            System.out.println("debug : " + (debug++));
            for (byte b : bytes) {
                System.out.println("debug : " + (debug++));
                raf.writeByte(b);
            }
        
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("debug : " + (debug++));
        this.bufferMap.set(pieceIndex);
    }

    public void updateSeeder(Seeder seeder, BitSet bufferMap) {        
        this.seeders.put(seeder, bufferMap);
    }

    public boolean isLeeching() {
        return leeching;
    }

    public String getAnnouncement() {
        return this.getFileName() + " " + this.fileSize + " " + this.getPieceSize() + " " + this.key;
    }

    public String BitSetToString(BitSet bs) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bs.length(); i++) {
            char c;
            if (bs.get(i))
                c = '1';
            else
                c = '0';
            s.append(c);
        }
        return s.toString();
    }


    public void StringToBitSet(BitSet bs, String str) {
        int i = 0;
        for (char c : str.toCharArray()) {
            switch (c) {
                case '0' -> i++;
                case '1' -> {
                    bs.set(i);
                    i++;
                }
            }
        }
    }
}
