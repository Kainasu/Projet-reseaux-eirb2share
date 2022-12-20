package test;

import Pair.FileUtil;
//import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FileUtilTest {

    final ArrayList<FileUtil> files = new ArrayList<>();

    @Before
    public void setUp() throws IOException, NoSuchAlgorithmException {
        files.add(new FileUtil("./test/TestFiles/test10Mibit.txt"));
        files.add(new FileUtil("./test/TestFiles/test2Mo.txt"));
        files.add(new FileUtil("./test/TestFiles/Eirb2Share_A_P2P_File_sharing_system.pdf"));
        files.add(new FileUtil("./Pair/files/file1.txt", 1024, false));
        files.add(new FileUtil("./Pair/files/file2.txt", 1024, false));
        files.add(new FileUtil("./test/TestFiles/download.txt", 1, true));
    }
    
    private String readBinaryTestFile(String filename, int pieceSize, int index) {
        StringBuilder s = new StringBuilder();
        try (RandomAccessFile raf = new RandomAccessFile(filename, "r")) {
            raf.seek((long) pieceSize * index * 8);
            for (int i = pieceSize * 8; i < pieceSize * 8 * 2; i++) {
                s.append((char) raf.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.toString();
    }

    @Test
    public void testInstantiationEven() {
        assertEquals(10485760, files.get(0).file.length());
        assertEquals(10240, files.get(0).getNbPieces());
        assertEquals(2097152, files.get(1).file.length());
        assertEquals(2048, files.get(1).getNbPieces());
    }

    @Test
    public void testInstantiationUneven() {
        assertEquals(434453, files.get(2).file.length());
        assertEquals(425, files.get(2).getNbPieces());
    }

    @Test
    public void testGetPiece() {

    }

    @Test
    public void testWritePieceToFile() throws Exception {
        //files.get(5).receivePiece("1:00000010");
        //files.get(5).receivePiece("0:00000001");
    }

    //@After
    public void after() {
        //new File("./test/TestFiles/download.txt").delete();
    }
}
