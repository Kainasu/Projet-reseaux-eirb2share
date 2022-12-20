package Pair;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public interface Communication {

    void send(String... strings) throws IOException;

    void send(List<Piece> pieces)throws IOException;

    Response receive() throws IOException;

    void stopCommunication() throws IOException;

    Socket getSocket();

}
