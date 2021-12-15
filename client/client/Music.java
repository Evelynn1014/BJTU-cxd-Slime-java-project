package client.client;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class Music {
    private static Clip bgm;//背景乐
    private static AudioInputStream ais;

    public static void play(String name) {
        try {
            bgm = AudioSystem.getClip();
            InputStream is = Music.class.getClassLoader().getResourceAsStream("client/music/" + name + ".wav");
            //getclassLoader得到当前类的加载器.getResourceAsStream加载资源，只能加载wav的音乐格式
            if (is != null) {
                ais = AudioSystem.getAudioInputStream(is);//获取输入流
            }
            bgm.open(ais);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        bgm.start();//开始播放
        bgm.loop(Clip.LOOP_CONTINUOUSLY);//循环播放
    }

    public static void stop() {
        if (ais != null)
            bgm.close();
    }

    public static void play1(String name) {
        try {
            bgm = AudioSystem.getClip();
            InputStream is = Music.class.getClassLoader().getResourceAsStream("client/music/" + name + ".wav");
            //getclassLoader得到当前类的加载器.getResourceAsStream加载资源，只能加载wav的音乐格式
            if (is != null) {
                ais = AudioSystem.getAudioInputStream(is);//获取输入流
            }
            bgm.open(ais);
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        bgm.start();//开始播放

    }
}
