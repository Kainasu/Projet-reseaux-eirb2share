package test;

import Pair.Utils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class UtilsTest {

    @Test
    public void testListToBitSet() {
        List<Byte> bytes = new ArrayList<>();
        bytes.add((byte) -1);
        BitSet bs = new BitSet();
        bs.set(0,8);
        assertEquals(bs, Utils.listToBitSet(bytes));
        bytes.add((byte) 0);
        assertEquals(bs, Utils.listToBitSet(bytes));
        bs.set(16, 24);
        bytes.add((byte) -1);
        assertEquals(bs, Utils.listToBitSet(bytes));
    }

    @Test
    public void testBytesToString() {
        List<Byte> list = new ArrayList<>();
        byte zero = 48;
        byte one = 49;
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0)
                list.add(zero);
            else
                list.add(one);
        }
        assertEquals("01010101", Utils.bytesToString(list));
    }
}
