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

public class SlimeMoveMsg implements Msg {
    private final int msgType = Msg.SLIME_MOVE_MSG;
    private int id;
    private int x, y;
    private Direction dir;
    private Direction ptDir;
    private SlimeClient sc;

    public SlimeMoveMsg(int id, int x, int y, Direction dir, Direction ptDir) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.ptDir = ptDir;
    }

    public SlimeMoveMsg(SlimeClient sc) {
        this.sc = sc;
    }

    public void send(DatagramSocket ds, String IP, int UDP_Port) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(30);//指定大小, 免得字节数组扩容占用时间
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(msgType);
            dos.writeInt(id);
            dos.writeInt(dir.ordinal());
            dos.writeInt(ptDir.ordinal());
            dos.writeInt(x);
            dos.writeInt(y);
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
            Direction dir = Direction.values()[dis.readInt()];
            Direction ptDir = Direction.values()[dis.readInt()];
            int x = dis.readInt();
            int y = dis.readInt();
            for (Slime t : sc.getSlimes()) {
                if (t.getId() == id) {
                    t.setDir(dir);
                    t.setPtDir(ptDir);
                    t.setX(x);
                    t.setY(y);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
