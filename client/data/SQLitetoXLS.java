package client.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class SQLitetoXLS {


    public void sql2excel() {

        Connection c;
        Statement stmt;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM GAMEDATA;");
            File file1 = new File("D:\\学习\\Slime's fighting\\src\\client\\data\\data.txt");// 将读入的txt文件

            FileOutputStream fw = new FileOutputStream(file1);
            OutputStreamWriter writer = new OutputStreamWriter(fw, StandardCharsets.UTF_8);
            writer.append("用户名\t队伍\t血量\t在线时长\n");


            while (rs.next()) {
                String name = rs.getString("account");
                int team = rs.getInt("team");
                int blood = rs.getInt("blood");
                long time = rs.getLong("time");
                writer.append(name + "\t  " + team + "\t   " + blood + "  \t" + time + "\n");
                writer.flush();
            }
            rs.close();
            stmt.close();
            c.close();

            Txt2Excel t = new Txt2Excel();
            t.txt2excel2();
            writer.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Operation done successfully");
    }
}