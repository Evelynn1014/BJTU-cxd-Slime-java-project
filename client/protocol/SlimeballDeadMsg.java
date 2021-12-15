package client.protocol;

import client.bean.Adhesion;
import client.bean.Slimeball;
import client.client.SlimeClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class SlimeballDeadMsg implements Msg {
    private final int msgType = Msg.SLIMEBALL_DEAD_MSG;
    private SlimeClient sc;
    private int slimeId;
    private int id;

    public SlimeballDeadMsg(int tankId, int id) {
        this.slimeId = tankId;
        this.id = id;
    }

    public SlimeballDeadMsg(SlimeClient sc) {
        this.sc = sc;
    }

    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(slimeId);
            dos.writeInt(id);
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
            int id = dis.readInt();
            for (Slimeball sb : sc.getSlimeballs()) {
                if (tankId == sc.getMySlime().getId() && id == sb.getId()) {
                    sb.setAlive(false);
                    sc.getAdhesions().add(new Adhesion(sb.getX() - 20, sb.getY() - 20, sc));
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
