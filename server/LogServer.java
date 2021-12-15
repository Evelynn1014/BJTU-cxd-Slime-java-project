package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class LogServer extends Thread {

    private static final int SERVER_PORT = 12040;
    ArrayList<Socket> sockets = new ArrayList<Socket>(50);
    private ServerSocket serverSocket;
    private Socket socket = null;

    public LogServer(Socket socket) throws IOException {
        this.socket = socket;
    }

    int i = 0;

    public void run() {


        try {

            System.out.println("connect!");
            PrintWriter out = null;

            out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader bufin = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            int p = 0;
            String temp = null;
            int j = 0;
            while (true) {
                if (j == 0) {
                    temp = bufin.readLine();
                } else {
                    temp = "end";
                }

                if (temp.equals("confirm")) {
                    String name = bufin.readLine();
                    String pass = bufin.readLine();
                    String message = null;
                    FileReader fr1 = new FileReader(new File("account.txt"));
                    //  FileWriter fw1 = new FileWriter(new File(""));
                    Scanner scanner = new Scanner(fr1);
                    while (scanner.hasNext()) {


                        if (name.equals(scanner.next())) {
                            p++;
                            if (pass.equals(scanner.next())) {
                                message = "successfully log in";
                                j = 1;
                            } else {
                                message = "password error";
                            }
                        } else {
                            scanner.next();
                        }
                    }
                    if (p == 0) message = "error account";
                    System.out.println(name + " " + message);

                    out.println(message);
                } else if (temp.equals("end")) {
                    socket.close();
                } else if (temp.equals("register")) {
                    String name = bufin.readLine();
                    String pass = bufin.readLine();
                    String message = null;
                    BufferedWriter bfr = null;
                    bfr = new BufferedWriter(new FileWriter("account.txt", true));
                    bfr.append("\n" + name + "\t" + pass);
                    bfr.close();
                }

            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Socket socket = new Socket();
        try {
            // 创建服务端socket
            ServerSocket serverSocket = new ServerSocket(12040);
            LogServer ls = new LogServer(socket);
            // 创建客户端socket

            //    File file = new File("account.txt");
            //   FileReader fr = new FileReader(file);
            int acc = 0;
            //   BufferedReader bfr = new BufferedReader(fr);
            //use scanner to read
            //   Scanner s = new Scanner(fr);


            //   fr.close();

            //     FileReader fr1 = new FileReader(new File("account.txt"));
            //    FileWriter fw1 = new FileWriter(new File("temp.txt"));
            //   String str;
            //    Scanner scanner = new Scanner(fr1);

            //    while (scanner.hasNext()) {
            //       str = scanner.nextLine();
            //       fw1.write(str + "\n");
            //  }

            //   fr1.close();
            //   fw1.close();

            while (true) {

                socket = serverSocket.accept();
                LogServer thread = new LogServer(socket);
                thread.start();

                InetAddress address = socket.getInetAddress();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}


