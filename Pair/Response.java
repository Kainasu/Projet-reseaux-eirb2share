package Pair;

import java.net.InetAddress;
import java.util.List;

public record Response(InetAddress ipAddress, int port, List<Byte> bytes) {
}
