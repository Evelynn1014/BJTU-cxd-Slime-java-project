package client.protocol;

import client.bean.Slime;
import client.client.SlimeClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class SlimeDeadMsg implements Msg {
    private final int msgType = Msg.SLIME_DEAD_MSG;
    private int slimeId;
    private SlimeClient sc;

    public SlimeDeadMsg(int slimeId) {
        this.slimeId = slimeId;
    }

    public SlimeDeadMsg(SlimeClient sc) {
        this.sc = sc;
    }

    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(slimeId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buf = baos.toByteArray();
        try {
            DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(IP, UDP_Port));
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse(DataInputStream dis) {
        try {
            int tankId = dis.readInt();
            if (tankId == this.sc.getMySlime().getId()) {
                return;
            }
            for (Slime t : sc.getSlimes()) {
                if (t.getId() == tankId) {
                    t.setAlive(false);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
