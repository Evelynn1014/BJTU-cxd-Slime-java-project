package client.bean;

import client.client.SlimeClient;
import client.event.SlimeAdhesionEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Slimeball {
    static int countSb = 0;
    //确定史莱姆粘液速度
    public static final int XSPEED = 30;
    public static final int YSPEED = 30;
    private static int ID = 10;//便于确认粘液的id

    //粘液的各种属性
    private int id;
    private SlimeClient sc;
    private int slimeId;
    private int x, y;
    private Direction dir = Direction.D;
    private boolean alive = true;
    private boolean team;//只有两种阵营

    private static final Toolkit tk = Toolkit.getDefaultToolkit();
    private static final Image[] imgs;

    private static final Map<String, Image> map = new HashMap<>();

    static {
        imgs = new Image[]{
                tk.getImage(Slimeball.class.getClassLoader().getResource("client/images/slimeball/a.png")),
                tk.getImage(Slimeball.class.getClassLoader().getResource("client/images/slimeball/b.png"))
        };

        map.put("a", imgs[0]);
        map.put("b", imgs[1]);
    }

    //确认子弹尺寸
    public static final int WIDTH = imgs[0].getWidth(null);
    public static final int HEIGHT = imgs[0].getHeight(null);

    //构造器，用多态的思想
    public Slimeball(int slimeId, int x, int y, boolean team, Direction dir) {
        this.slimeId = slimeId;
        this.x = x;
        this.y = y;
        this.team = team;
        this.dir = dir;
        this.id = ID++;
    }

    public Slimeball(int slimeId, int x, int y, boolean team, Direction dir, SlimeClient sc) {
        this(slimeId, x, y, team, dir);
        this.sc = sc;
    }

    public void draw(Graphics g) {
        if (!alive) {
            sc.getSlimeballs().remove(this);
            return;
        }
        g.drawImage(team ? map.get("a") : map.get("b"), x, y, null);
        move();
    }


    //子弹的移动就是快速闪烁坐标
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

        if (x < 0 || y < 0 || x >= SlimeClient.GAME_WIDTH || y >= SlimeClient.GAME_HEIGHT) {
            alive = false;
        }
    }

    //确定粘液方块
    public Rectangle getRect() {
        return new Rectangle(x, y, imgs[0].getWidth(null), imgs[0].getHeight(null));
    }

    //相交就等价于发生碰撞
    public boolean contactSlime(Slime s) {

        if (this.alive && s.isAlive() && this.team != s.isTeam() && this.getRect().intersects(s.getRect())) {
            this.alive = false;
            countSb++;//子弹消失
            s.actionToSlimeAdhesionEvent(new SlimeAdhesionEvent(this));
            //受到攻击
            return true;
        }
        return false;

    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isTeam() {
        return team;
    }

    public Direction getDir() {
        return dir;
    }


    public int getId() {
        return id;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public int getSlimeId() {
        return slimeId;
    }

    public void setSlimeId(int slimeId) {
        this.slimeId = slimeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    static public int getCountSb() {
        return countSb;
    }

    public void setTeam(boolean team) {
        this.team = team;
    }

}
