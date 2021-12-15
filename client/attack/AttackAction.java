package client.attack;

import client.bean.Slime;
import client.bean.Slimeball;
import client.client.Music;
import client.protocol.SlimeballNewMsg;


public class AttackAction implements ThrowSlimeballAction {
    static int sb = 0;

    public void throwSlimeballAction(Slime slime) {

        if (!slime.isAlive()) {
            return;
        }
        int x = slime.getX() + 15 - 5;//确定粘液的坐标, 这里应该用子弹的常量计算, 待修正
        int y = slime.getY() + 15 - 5;
        Slimeball s = new Slimeball(slime.getId(), x, y, slime.isTeam(), slime.getPtDir(), slime.getSc());//产生一颗子弹
        sb++;
        slime.getSc().getSlimeballs().add(s);

        Music.play1("5");
        SlimeballNewMsg msg = new SlimeballNewMsg(s);
        slime.getSc().getNc().send(msg);
    }

    public static int getSb() {
        return sb;
    }
}
