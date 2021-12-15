package client.protocol;

import client.bean.Direction;
import client.bean.Slimeball;
import client.client.SlimeClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class SlimeballNewMsg implements Msg {
    private final int msgType = Msg.SLIMEBALL_NEW_MSG;
    private SlimeClient sc;
    private Slimeball sb;

    public SlimeballNewMsg(SlimeClient sc) {
        this.sc = sc;
    }

    public SlimeballNewMsg(Slimeball sb) {
        this.sb = sb;
    }

    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(100);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(sb.getSlimeId());
            dos.writeInt(sb.getId());
            dos.writeInt(sb.getX());
            dos.writeInt(sb.getY());
            dos.writeInt(sb.getDir().ordinal());
            dos.writeBoolean(sb.isTeam());
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
            if (tankId == sc.getMySlime().getId()) {
                return;
            }
            int id = dis.readInt();
            int x = dis.readInt();
            int y = dis.readInt();
            Direction dir = Direction.values()[dis.readInt()];
            boolean good = dis.readBoolean();

            Slimeball sb = new Slimeball(tankId, x, y, good, dir, sc);
            sb.setId(id);
            sc.getSlimeballs().add(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
