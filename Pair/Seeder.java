package Pair;

import java.net.InetAddress;
import java.util.Objects;

public record Seeder(InetAddress address, int port) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seeder seeder = (Seeder) o;
        return port == seeder.port && address.equals(seeder.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}
