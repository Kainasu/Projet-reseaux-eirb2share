package Pair;

public class Piece {

    private final int index;
    private final String fileKey;
    private final byte[] data;

    public Piece(int index, String fileKey, byte[] data) {
        this.index = index;
        this.fileKey = fileKey;
        this.data = data;
    }

    public int getIndex() {
        return index;
    }

    public String getFileKey() {
        return fileKey;
    }

    public byte[] getData() {
        return data;
    }
}
