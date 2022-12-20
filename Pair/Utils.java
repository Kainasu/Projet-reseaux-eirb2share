package Pair;

import java.util.BitSet;
import java.util.List;

public class Utils {

    public static BitSet listToBitSet(List<Byte> list) {
        BitSet bitSet = new BitSet();

        for (int i = 0; i < list.size(); i++) {
            String s = byteToBitString(list.get(i));
            for (int j = 0; j < 8; j++) {
                if (s.charAt(j) == '1')
                    bitSet.set(i*8 + j);
            }
        }
        return bitSet;
    }

    public static String byteToBitString(byte b) {
        return String.format("%32s", Integer.toBinaryString(b))
                .replace(' ', '0')
                .substring(24);
    }

    public static String bytesToString(List<Byte> bytes) {
        return bytes.stream()
                .collect(StringBuilder::new,
                        (a,b) -> a.append((char) (byte) b),
                        StringBuilder::append)
                .toString();
    }
}
