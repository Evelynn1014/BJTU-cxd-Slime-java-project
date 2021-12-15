package server;

import client.data.Database;
import client.data.PieChart;
import client.data.SQLitetoXLS;
import client.data.Txt2Excel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

public class SlimeServer extends Frame {
    public static int ID = 100;//id号的初始序列
    public static final int TCP_PORT = 55555;//TCP端口号
    public static final int UDP_PORT = 55556;//转发客户端数据的UDP端口号
    public static final int TANK_DEAD_UDP_PORT = 55557;//接收客户端坦克死亡的端口号
    private final List<Client> clients = new ArrayList<>();//客户端集合
    private Image offScreenImage = null;//服务器画布
    private static final int SERVER_HEIGHT = 900;
    private static final int SERVER_WIDTH = 200;

    public static void main(String[] args) {
        SlimeServer ts = new SlimeServer();
        ts.launchFrame();
        ts.start();
    }

    public void start() {
        new Thread(new UDPThread()).start();
        new Thread(new SlimeDeadUDPThread()).start();
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(TCP_PORT);//在TCP欢迎套接字上监听客户端连接
            System.out.println("SlimeServer has started...");
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Socket s = null;
            try {
                s = ss.accept();//给客户但分配专属TCP套接字
                System.out.println("A client has connected...");
                DataInputStream dis = new DataInputStream(s.getInputStream());
                int UDP_PORT = dis.readInt();//记录客户端UDP端口
                Client client = new Client(s.getInetAddress().getHostAddress(), UDP_PORT, ID);//创建Client对象
                clients.add(client);//添加进客户端容器

                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeInt(ID++);//向客户端分配id号
                dos.writeInt(SlimeServer.UDP_PORT);//告诉客户端自己的UDP端口号
                dos.writeInt(SlimeServer.TANK_DEAD_UDP_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (s != null) s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class UDPThread implements Runnable {

        byte[] buf = new byte[1024];

        @Override
        public void run() {
            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket(UDP_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            while (null != ds) {
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                try {
                    ds.receive(dp);
                    for (Client c : clients) {
                        dp.setSocketAddress(new InetSocketAddress(c.IP, c.UDP_PORT));
                        ds.send(dp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 监听坦克死亡的UDP线程
     */
    private class SlimeDeadUDPThread implements Runnable {
        byte[] buf = new byte[300];

        @Override
        public void run() {
            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket(TANK_DEAD_UDP_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            while (null != ds) {
                DatagramPacket dp = new DatagramPacket(buf, buf.length);
                ByteArrayInputStream bais = null;
                DataInputStream dis = null;
                try {
                    ds.receive(dp);
                    bais = new ByteArrayInputStream(buf, 0, dp.getLength());
                    dis = new DataInputStream(bais);
                    int deadTankUDPPort = dis.readInt();
                    for (int i = 0; i < clients.size(); i++) {
                        Client c = clients.get(i);
                        if (c.UDP_PORT == deadTankUDPPort) {
                            clients.remove(c);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (null != dis) {
                        try {
                            dis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != bais) {
                        try {
                            bais.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public class Client {
        String IP;
        int UDP_PORT;
        int id;

        public Client(String ipAddr, int UDP_PORT, int id) {
            this.IP = ipAddr;
            this.UDP_PORT = UDP_PORT;
            this.id = id;
        }
    }

    public void paint(Graphics g) {
        g.drawString("SlimeClient :", 10, 80);
        int y = 100;
        for (int i = 0; i < clients.size(); i++) {//显示出每个客户端的信息
            Client c = clients.get(i);
            g.drawString("id : " + c.id + " - IP : " + c.IP, 10, y);
            y += 30;
        }
    }

    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(SERVER_WIDTH, SERVER_HEIGHT);
        }
        Graphics gOffScreen = offScreenImage.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.white);
        gOffScreen.fillRect(0, 0, SERVER_WIDTH, SERVER_HEIGHT);
        gOffScreen.setColor(c);
        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    MenuBar menubar = new MenuBar();
    JButton emp = new JButton("Empty the database");

    public void launchFrame() {
        this.setLocation(200, 100);
        this.setSize(SERVER_WIDTH, SERVER_HEIGHT);
        this.setTitle("SlimeServer");
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit(0);
            }
        });
        this.setResizable(false);
        this.setBackground(Color.white);
        this.setVisible(true);
        new Thread(new PaintThread()).start();
        this.setMenuBar(menubar);
        Menu data = new Menu("data");
        Database d = new Database();
        MenuItem empty_database = new MenuItem("empty the database");
        MenuItem export_database = new MenuItem("export database");
        data.add(empty_database);
        empty_database.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    d.clsTable();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        data.add(export_database);
        export_database.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Txt2Excel t = new Txt2Excel();
                t.txt2excel1();
                SQLitetoXLS s = new SQLitetoXLS();
                s.sql2excel();
                PieChart p = new PieChart();

                JFrame frame = new JFrame("Java数据统计图");
                frame.add(p.getChartPanel());
                frame.setBounds(50, 50, 800, 600);
                frame.setVisible(true);
            }
        });
        menubar.add(data);
        Menu help = new Menu("help");
        Menu exit = new Menu("exit");
        menubar.add(help);
        MenuItem help1 = new MenuItem("help");
        help.add(help1);
        help1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "In this server, you can export the game data, and you also can empty the database", "help", JOptionPane.INFORMATION_MESSAGE);

            }
        });
        menubar.add(exit);

        MenuItem exit1 = new MenuItem("exit");
        exit.add(exit1);
        exit1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit(0);
            }
        });
    }

    class PaintThread implements Runnable {
        public void run() {
            while (true) {
                repaint();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
