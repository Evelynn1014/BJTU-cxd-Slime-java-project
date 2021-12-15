package client.client;

import client.bean.Adhesion;
import client.bean.Direction;
import client.bean.Slime;
import client.bean.Slimeball;
import client.data.Database;
import client.protocol.SlimeballDeadMsg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import static client.attack.AttackAction.getSb;
import static client.client.Login.account;

public class SlimeClient extends Frame {
    //游戏背景的大小
    public static final int GAME_WIDTH = 1500;
    public static final int GAME_HEIGHT = 900;
    private Image offScreenImage = null;
    private Image beach;
    //本客户端的史莱姆

    private Slime mySlime;
    private NetClient nc = new NetClient(this);
    //针对各种异常情况弹出对话框
    private ConDialog dialog = new ConDialog();
    private GameOverDialog gameOverDialog = new GameOverDialog();
    private UDPPortWrongDialog udpPortWrongDialog = new UDPPortWrongDialog();
    private ServerNotStartDialog serverNotStartDialog = new ServerNotStartDialog();
    static long startTime = System.currentTimeMillis();
    private java.util.List<Slimeball> slimeballs = new ArrayList<>();//粘液球的储存数组
    private java.util.List<Adhesion> adhesions = new ArrayList<>();//黏着数量的储存数组
    private List<Slime> slimes = new ArrayList<>();//史莱姆的储存集合
    //输出状态栏
    Database d = new Database();

    public void paint(Graphics g) {
        g.setFont(new Font("", Font.BOLD, 16));
        g.drawString("slimeballs :" + slimeballs.size(), 10, 790);
        //    g.drawString("adhesions  :" + adhesions.size(), 10, 820);
        //    g.drawString("enemy      :" + slimes.size(), 10, 850);
        //  g.drawString("HP         :" + String.valueOf(mySlime.getBlood()),10, 880) ;


        for (int i = 0; i < slimeballs.size(); i++) {
            Slimeball s = slimeballs.get(i);
            if (s.contactSlime(mySlime)) {

                SlimeballDeadMsg sbdmsg = new SlimeballDeadMsg(s.getSlimeId(), s.getId());
                nc.send(sbdmsg);
                //  nc.sendClientDisconnectMsg();
                //   gameOverDialog.setVisible(true);
            }
            s.draw(g);
        }
        for (int i = 0; i < adhesions.size(); i++) {
            Adhesion a = adhesions.get(i);
            a.draw(g);
        }
        for (int i = 0; i < slimes.size(); i++) {
            Slime t = slimes.get(i);
            t.draw(g);
        }
        if (null != mySlime) {
            mySlime.draw(g);
        }
    }

    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(1500, 900);
            Toolkit tk = Toolkit.getDefaultToolkit();
            beach = tk.getImage("src/client/images/other/beach2.png");
        }
        Graphics gOffScreen = offScreenImage.getGraphics();
        gOffScreen.drawImage(beach, 0, 0, GAME_WIDTH, GAME_HEIGHT, null);
        paint(gOffScreen);

        g.drawImage(offScreenImage, 0, 0, null);


    }

    public void launchFrame() {
        this.setLocation(100, 30);
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setTitle("SlimeClient");
        this.setMenuBar(menubar);
        Menu help = new Menu("help");
        MenuItem help1 = new MenuItem("help message");
        menubar.add(help);
        help.add(help1);
        help1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "In this game, you will control slime to throw the slimeball to make your enemies adhesion.", "help", JOptionPane.INFORMATION_MESSAGE);

            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                nc.sendClientDisconnectMsg();//关闭窗口前要向服务器发出注销消息.

                long elapsedTime = System.currentTimeMillis() - getMySlime().getSc().getStartTime();
                long elapsedSeconds = elapsedTime / 1000;
                try {
                    d.add_data(getMySlime().getName(), getMySlime().isTeam(), getMySlime().getBlood(), getSb(), elapsedSeconds);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                System.exit(0);
            }
        });
        this.setResizable(false);
        this.setBackground(Color.white);


        this.addKeyListener(new KeyMonitor());

        this.setVisible(true);

        new Thread(new PaintThread()).start();

        dialog.setVisible(true);
    }

    public static void main(String[] args) {


        SlimeClient sc = new SlimeClient();

        sc.launchFrame();
    }

    //线程

    class PaintThread implements Runnable {
        public void run() {
            while (true) {
                repaint();
                try {

                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class KeyMonitor extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            mySlime.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            mySlime.keyPressed(e);
        }
    }

    MenuBar menubar = new MenuBar();

    //制作游戏前链接服务器的菜单
    class ConDialog extends Dialog {

        Button b = new Button("connect to server");
        TextField tfIP = new TextField("127.0.0.1", 15);//服务器的IP地址
        TextField tfSlimeName = new TextField("", 8);
        TextArea ta = new TextArea("Use WASD to move \n J to throw your slimeball to hurt enemies\nHave a good time!", 3, 15);

        public ConDialog() {
            super(SlimeClient.this, true);
            ta.setEditable(false);
            this.setLayout(new FlowLayout());
            this.add(new Label("server IP:"));
            this.add(tfIP);
            this.add(new Label("slime name:"));
            tfSlimeName.setText(account);
            this.add(tfSlimeName);

            //tfSlimeName.setEditable(false);

            this.add(b);
            this.add(ta);
            this.setLocation(150, 100);
            this.pack();

            Music.play("0");

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                    System.exit(0);

                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    String IP = tfIP.getText().trim();
                    String slimeName = tfSlimeName.getText().trim();
                    mySlime = new Slime(slimeName, 50 + (int) (Math.random() * (GAME_WIDTH - 300)),
                            50 + (int) (Math.random() * (GAME_HEIGHT - 100)), true, Direction.STOP, SlimeClient.this);
                    nc.connect(IP);
                    setVisible(false);

                    Music.stop();
                    long startTime = System.currentTimeMillis();
                    setStartTime(startTime);
                    Music.play("3");
                }
            });
        }
    }

    //史莱姆阵亡后的对话框
    class GameOverDialog extends Dialog {
        Button b = new Button("exit");

        public GameOverDialog() {
            super(SlimeClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("Game Over~"));
            this.add(b);

            // Database.add_data(,0,getSb())

            //  Music.play("1");
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }

    //UDP端口分配失败提示
    class UDPPortWrongDialog extends Dialog {
        Button b = new Button("ok");

        public UDPPortWrongDialog() {
            super(SlimeClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("error!, please try again"));
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }

    //连接服务器失败
    class ServerNotStartDialog extends Dialog {
        Button b = new Button("ok");

        public ServerNotStartDialog() {
            super(SlimeClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("The server has not been opened yet..."));
            this.add(b);
            this.setLocation(500, 400);
            this.pack();
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
    }

    public void gameOver() {
        this.gameOverDialog.setVisible(true);
    }

    public List<Adhesion> getAdhesions() {
        return adhesions;
    }

    public List<Slime> getSlimes() {
        return slimes;
    }

    public Slime getMySlime() {
        return mySlime;
    }

    public void setNc(NetClient nc) {
        this.nc = nc;
    }

    public void setMySlime(Slime mySlime) {
        this.mySlime = mySlime;
    }

    public void setAdhesions(List<Adhesion> adhesions) {
        this.adhesions = adhesions;
    }

    public void setSlimeballs(List<Slimeball> slimeballs) {
        this.slimeballs = slimeballs;
    }

    public void setSlimes(List<Slime> slimes) {
        this.slimes = slimes;
    }

    public UDPPortWrongDialog getUdpPortWrongDialog() {
        return udpPortWrongDialog;
    }

    public GameOverDialog getGameOverDialog() {
        return gameOverDialog;
    }

    public ServerNotStartDialog getServerNotStartDialog() {
        return serverNotStartDialog;
    }

    public void setServerNotStartDialog(ServerNotStartDialog serverNotStartDialog) {
        this.serverNotStartDialog = serverNotStartDialog;
    }

    public void setGameOverDialog(GameOverDialog gameOverDialog) {
        this.gameOverDialog = gameOverDialog;
    }

    public void setUdpPortWrongDialog(UDPPortWrongDialog udpPortWrongDialog) {
        this.udpPortWrongDialog = udpPortWrongDialog;
    }

    public ConDialog getDialog() {
        return dialog;
    }

    public NetClient getNc() {
        return nc;
    }

    public void setOffScreenImage(Image offScreenImage) {
        this.offScreenImage = offScreenImage;
    }

    public void setDialog(ConDialog dialog) {
        this.dialog = dialog;
    }

    public static void setStartTime(long startTime) {
        SlimeClient.startTime = startTime;
    }

    public List<Slimeball> getSlimeballs() {
        return slimeballs;
    }

    public long getStartTime() {
        return startTime;
    }
}
