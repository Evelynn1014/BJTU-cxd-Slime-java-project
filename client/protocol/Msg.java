package client.protocol;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Msg {
    int SLIME_NEW_MSG = 1;
    int SLIME_MOVE_MSG = 2;
    int SLIMEBALL_NEW_MSG = 3;
    int SLIME_DEAD_MSG = 4;
    int SLIMEBALL_DEAD_MSG = 5;
    int SLIME_EXIST_MSG = 6;
    int SLIME_REDUCE_BLOOD_MSG = 7;

    void send(DatagramSocket ds, String IP, int UDP_Port);

    void parse(DataInputStream dis);
}