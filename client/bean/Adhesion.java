package client.bean;

import client.client.SlimeClient;

import java.awt.*;

public class Adhesion {
    private final int x;
    private final int y;
    private boolean alive = true;//爆炸的生命
    private final SlimeClient sc;
    private int step = 0;
    private static boolean init = false;//在正式画出爆炸之前先在其他地方画出一次爆炸, 确保爆炸的图片加入到内存中
    private static final Toolkit tk = Toolkit.getDefaultToolkit();
    private static final Image[] images = {
            tk.getImage(Adhesion.class.getClassLoader().getResource("client/images/adhesion/adhesion1.png")),
            tk.getImage(Adhesion.class.getClassLoader().getResource("client/images/adhesion/adhesion2.png")),
            tk.getImage(Adhesion.class.getClassLoader().getResource("client/images/adhesion/adhesion3.png")),
            tk.getImage(Adhesion.class.getClassLoader().getResource("client/images/adhesion/adhesion4.png")),
            tk.getImage(Adhesion.class.getClassLoader().getResource("client/images/adhesion/adhesion5.png")),
            tk.getImage(Adhesion.class.getClassLoader().getResource("client/images/adhesion/adhesion6.png")),
    };

    public Adhesion(int x, int y, SlimeClient sc) {
        this.x = x;
        this.y = y;
        this.sc = sc;
    }

    public void draw(Graphics g) {
        if (!init) {//先在其他地方画一次爆炸
            for (int i = 0; i < images.length; i++) {
                g.drawImage(images[i], -100, -100, null);
            }
            init = true;
        }
        if (!alive) {//爆炸炸完了就从容器移除
            sc.getAdhesions().remove(this);
            return;
        }
        if (step == images.length) {//把爆炸数组中的图片都画一次
            alive = false;
            step = 0;
            return;
        }
        g.drawImage(images[step++], x, y, null);
    }
}







