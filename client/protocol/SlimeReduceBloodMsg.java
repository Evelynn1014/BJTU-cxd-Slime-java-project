package client.protocol;

import client.bean.Adhesion;
import client.bean.Slime;
import client.bean.Slimeball;
import client.client.SlimeClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class SlimeReduceBloodMsg implements Msg {
    private final int msgType = Msg.SLIME_REDUCE_BLOOD_MSG;
    private int slimeId;
    private Slimeball sb;
    private SlimeClient sc;

    public SlimeReduceBloodMsg(int slimeId, Slimeball sb) {
        this.slimeId = slimeId;
        this.sb = sb;
    }

    public SlimeReduceBloodMsg(SlimeClient sc) {
        this.sc = sc;
    }

    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(50);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(slimeId);//发送扣血史莱姆的id
            dos.writeInt(sb.getX() - 20);
            dos.writeInt(sb.getY() - 20);
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
            int id = dis.readInt();
            if (id == sc.getMySlime().getId()) {
                return;
            }
            int bX = dis.readInt();
            int bY = dis.readInt();
            this.sc.getAdhesions().add(new Adhesion(bX, bY, this.sc));
            for (Slime s : sc.getSlimes()) {
                if (s.getId() == id) {
                    s.setBlood(s.getBlood() - 5);//找到扣血的史莱姆, 减少5滴血
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
