package client.protocol;

import client.bean.Direction;
import client.bean.Slime;
import client.client.SlimeClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class SlimeNewMsg implements Msg {
    private final int msgType = Msg.SLIME_NEW_MSG;
    private Slime slime;
    private SlimeClient sc;

    public SlimeNewMsg(Slime slime) {
        this.slime = slime;
    }

    public SlimeNewMsg(SlimeClient sc) {
        this.sc = sc;
    }

    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(88);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(slime.getId());
            dos.writeInt(slime.getX());
            dos.writeInt(slime.getY());
            dos.writeInt(slime.getDir().ordinal());
            dos.writeBoolean(slime.isTeam());
            dos.writeUTF(slime.getName());

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
            if (id == this.sc.getMySlime().getId()) {
                return;
            }
            int x = dis.readInt();
            int y = dis.readInt();
            Direction dir = Direction.values()[dis.readInt()];
            boolean good = dis.readBoolean();
            String tankName = dis.readUTF();
            Slime newSlime = new Slime(tankName, x, y, good, dir, sc);
            newSlime.setId(id);
            sc.getSlimes().add(newSlime);

            SlimeExistMsg msg = new SlimeExistMsg(sc.getMySlime());
            sc.getNc().send(msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
