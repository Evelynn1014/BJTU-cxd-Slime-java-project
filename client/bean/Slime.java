package client.bean;

import client.attack.AttackAction;
import client.attack.ThrowSlimeball;
import client.attack.ThrowSlimeballAction;
import client.client.SlimeClient;
import client.data.Database;
import client.event.SlimeAdhesionEvent;
import client.event.SlimeAdhesionListener;
import client.protocol.SlimeDeadMsg;
import client.protocol.SlimeMoveMsg;
import client.protocol.SlimeReduceBloodMsg;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import static client.bean.Slimeball.countSb;

public class Slime implements SlimeAdhesionListener, ThrowSlimeball {
    private int id;

    public static final int XSPEED = 13;
    public static final int YSPEED = 13;
    public int cntj = 0;
    Database d = new Database();
    private final String name;
    private boolean team;
    private int x, y;
    private boolean alive = true;
    private SlimeClient sc;
    private boolean bL, bU, bR, bD;
    private Direction dir = Direction.STOP;
    private Direction ptDir = Direction.D;
    private int blood = 100;
    private final BloodBar bb = new BloodBar();
    private final ThrowSlimeballAction throwSlimeballAction = new AttackAction();//可以开火

    private static final Toolkit tk = Toolkit.getDefaultToolkit();
    private static final Image[] imgs;
    private static final Map<String, Image> map = new HashMap<>();

    static {
        imgs = new Image[]{//加载两方阵营的图片
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/tD.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/tL.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/tLD.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/tLU.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/tR.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/tRD.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/tRU.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/tU.png")),

                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/eD.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/eL.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/eLD.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/eLU.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/eR.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/eRD.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/eRU.png")),
                tk.getImage(Slime.class.getClassLoader().getResource("client/images/slime/eU.png")),
        };
        map.put("tD", imgs[0]);
        map.put("tL", imgs[1]);
        map.put("tLD", imgs[2]);
        map.put("tLU", imgs[3]);
        map.put("tR", imgs[4]);
        map.put("tRD", imgs[5]);
        map.put("tRU", imgs[6]);
        map.put("tU", imgs[7]);
        map.put("eD", imgs[8]);
        map.put("eL", imgs[9]);
        map.put("eLD", imgs[10]);
        map.put("eLU", imgs[11]);
        map.put("eR", imgs[12]);
        map.put("eRD", imgs[13]);
        map.put("eRU", imgs[14]);
        map.put("eU", imgs[15]);
    }

    public static final int WIDTH = imgs[0].getWidth(null);
    public static final int HEIGHT = imgs[0].getHeight(null);

    public Slime(int x, int y, boolean team, String name) {
        this.x = x;
        this.y = y;
        this.team = team;
        this.name = name;
    }

    public Slime(String name, int x, int y, boolean team, Direction dir, SlimeClient sc) {
        this(x, y, team, name);
        this.dir = dir;
        this.sc = sc;
    }

    public void draw(Graphics g) {
        if (!alive) {
            if (!team) {
                sc.getSlimes().remove(this);


            }
            return;
        }
        g.drawString("HP         :" + getSc().getMySlime().blood, 10, 880);//状态栏
        g.drawString("atk        : 5", 300, 880);//状态栏
        switch (ptDir) {
            case L:
                g.drawImage(team ? map.get("tL") : map.get("eL"), x, y, null);
                break;
            case LU:
                g.drawImage(team ? map.get("tLU") : map.get("eLU"), x, y, null);
                break;
            case U:
                g.drawImage(team ? map.get("tU") : map.get("eU"), x, y, null);
                break;
            case RU:
                g.drawImage(team ? map.get("tRU") : map.get("eRU"), x, y, null);
                break;
            case R:
                g.drawImage(team ? map.get("tR") : map.get("eR"), x, y, null);
                break;
            case RD:
                g.drawImage(team ? map.get("tRD") : map.get("eRD"), x, y, null);
                break;
            case D:
                g.drawImage(team ? map.get("tD") : map.get("eD"), x, y, null);
                break;
            case LD:
                g.drawImage(team ? map.get("tLD") : map.get("eLD"), x, y, null);
                break;
        }
        g.drawString(name, x, y - 20);
        bb.draw(g);//画出血条
        move();
    }

    private void move() {
        switch (dir) {
            case L:
                x -= XSPEED;
                break;
            case LU:
                x -= XSPEED / 1.414;
                y -= YSPEED / 1.414;
                break;
            case U:
                y -= YSPEED;
                break;
            case RU:
                x += XSPEED / 1.414;
                y -= YSPEED / 1.414;
                break;
            case R:
                x += XSPEED;
                break;
            case RD:
                x += XSPEED / 1.414;
                y += YSPEED / 1.414;
                break;
            case D:
                y += YSPEED;
                break;
            case LD:
                x -= XSPEED / 1.414;
                y += YSPEED / 1.414;
                break;
            case STOP:
                break;
        }

        if (dir != Direction.STOP) {
            ptDir = dir;
        }

        if (x < 0) x = 0;
        if (y < 30) y = 30;
        if (x + WIDTH >= SlimeClient.GAME_WIDTH) x = SlimeClient.GAME_WIDTH - WIDTH;
        if (y + HEIGHT > SlimeClient.GAME_HEIGHT) y = SlimeClient.GAME_HEIGHT - HEIGHT;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_A:
                bL = true;
                break;
            case KeyEvent.VK_W:
                bU = true;
                break;
            case KeyEvent.VK_D:
                bR = true;
                break;
            case KeyEvent.VK_S:
                bD = true;
                break;
        }
        locateDirection();
    }

    private void locateDirection() {
        Direction oldDir = this.dir;
        if (bL && !bU && !bR && !bD) dir = Direction.L;
        else if (bL && bU && !bR && !bD) dir = Direction.LU;
        else if (!bL && bU && !bR && !bD) dir = Direction.U;
        else if (!bL && bU && bR && !bD) dir = Direction.RU;
        else if (!bL && !bU && bR && !bD) dir = Direction.R;
        else if (!bL && !bU && bR && bD) dir = Direction.RD;
        else if (!bL && !bU && !bR && bD) dir = Direction.D;
        else if (bL && !bU && !bR && bD) dir = Direction.LD;
        else if (!bL && !bU && !bR && !bD) dir = Direction.STOP;

        if (dir != oldDir) {
            SlimeMoveMsg msg = new SlimeMoveMsg(id, x, y, dir, ptDir);
            sc.getNc().send(msg);
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_J://监听到J键按下则开火
                throwSlimeball();
                cntj++;
                break;
            case KeyEvent.VK_A:
                bL = false;
                break;
            case KeyEvent.VK_W:
                bU = false;
                break;
            case KeyEvent.VK_D:
                bR = false;
                break;
            case KeyEvent.VK_S:
                bD = false;
                break;
        }
        locateDirection();
    }

    public void throwSlimeball() {
        throwSlimeballAction.throwSlimeballAction(this);
    }

    public void actionToSlimeAdhesionEvent(SlimeAdhesionEvent slimeAdhesionEvent) {
        this.sc.getAdhesions().add(new Adhesion(slimeAdhesionEvent.getRoot().getX() - 20,
                slimeAdhesionEvent.getRoot().getY() - 20, this.sc));//自身产生一个爆炸
        if (this.blood == 5) {//每次扣5滴血, 如果只剩下5滴了, 那么就标记为死亡.
            this.alive = false;
            try {
                long elapsedTime = System.currentTimeMillis() - this.getSc().getStartTime();
                long elapsedSeconds = elapsedTime / 1000;
                d.add_data(this.getName(), this.isTeam(), 0, countSb, elapsedSeconds);

            } catch (Exception e) {
                e.printStackTrace();
            }
            SlimeDeadMsg msg = new SlimeDeadMsg(this.id);//向其他客户端转发自己死亡的消息
            this.sc.getNc().send(msg);
            this.sc.getNc().sendClientDisconnectMsg();//和服务器断开连接

            this.sc.gameOver();
            return;
        }
        this.blood -= 5;//血量减少5并通知其他客户端本坦克血量减少5.
        SlimeReduceBloodMsg msg = new SlimeReduceBloodMsg(this.id, slimeAdhesionEvent.getRoot());
        this.sc.getNc().send(msg);
    }


    private class BloodBar {
        public void draw(Graphics g) {
            Color c = g.getColor();
            g.setColor(Color.BLACK);
            g.drawRect(x, y - 15, 30, 8);
            int w = (30 * blood) / 100;
            g.setColor(Color.RED);
            g.fillRect(x, y - 15, w, 8);
            g.drawString("HP : " + blood + "/100", x, y + 5);
            g.setColor(c);

        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, imgs[0].getWidth(null), imgs[0].getHeight(null));
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setPtDir(Direction ptDir) {
        this.ptDir = ptDir;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public Direction getDir() {
        return dir;
    }

    public Direction getPtDir() {
        return ptDir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public void setTeam(boolean team) {
        this.team = team;
    }

    public boolean isTeam() {
        return team;
    }

    public int getBlood() {
        return blood;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SlimeClient getSc() {
        return sc;
    }

    public String getName() {
        return name;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }
}
