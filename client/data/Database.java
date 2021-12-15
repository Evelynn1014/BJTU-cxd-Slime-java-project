package client.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {

    public static void main(String[] args) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:sqlite:test.db", "root", "1234567890");
        stmt = conn.createStatement();
        stmt.close();


    }

    public void add_data(String name, boolean t, int b, int sb, long time) throws Exception {
        Connection conn = null;
        Statement stmt = null;

        System.out.println("Opened database successfully");
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:sqlite:test.db", "root", "1234567890");
        conn.setAutoCommit(false);
        stmt = conn.createStatement();
        if (!t) {
            stmt.executeUpdate("INSERT INTO GAMEDATA(ACCOUNT,TEAM,BLOOD,THROW,TIME )" + "VALUES('" + name + "'," + 1 + "," + b + "," + sb + "," + time + ");");
        } else {
            stmt.executeUpdate("INSERT INTO GAMEDATA(ACCOUNT,TEAM,BLOOD,THROW,TIME )" + "VALUES('" + name + "'," + 0 + "," + b + "," + sb + "," + time + ");");//green
        }
        conn.commit();
        conn.close();
        stmt.close();

    }

    public void clsTable() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:sqlite:test.db", "root", "1234567890");
        stmt = conn.createStatement();
        stmt.executeUpdate("DROP TABLE  GAMEDATA");
        newTable();
        stmt.close();
    }


    public void newTable() throws Exception {

        Connection conn = null;
        Statement stmt = null;
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:sqlite:test.db", "root", "1234567890");
        stmt = conn.createStatement();


        stmt.executeUpdate(" CREATE TABLE GAMEDATA\n" +
                "(\n" +
                "      account string    null,\n" +
                "    team   tinyint(1) null,\n" +
                "    blood  int        null,\n" +
                "    throw  int        null,\n" +
                "      time   long       null\n" +
                "\n" +
                ");\n");

        // stmt.executeUpdate("INSERT INTO GAMEDATA(TEAM,MOVE,THROW)"+"VALUES(0,23,34);");
        stmt.close();
    }
}

