package client.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Login {
    static String account;
    private PrintWriter out;
    static Socket s;
    JFrame log = new JFrame();
    JLabel acc = new JLabel();
    JLabel pass = new JLabel();
    JButton register = new JButton();
    JButton confirm = new JButton();
    JButton reset = new JButton();
    JButton cancel = new JButton();
    JTextField jtfa = new JTextField();
    JPasswordField jtfp = new JPasswordField();
    JPanel center = new JPanel();
    JPanel east = new JPanel();


    public void showLog() {

        JPanel jp1 = new JPanel();
        JPanel jp2 = new JPanel();
        acc.setText("Account");
        pass.setText("Password");
        jtfa.setColumns(10);
        jtfp.setColumns(10);
        jp1.add(acc, BoxLayout.X_AXIS);
        jp1.add(jtfa, BoxLayout.X_AXIS);
        jp2.add(pass, BoxLayout.X_AXIS);
        jp2.add(jtfp, BoxLayout.X_AXIS);
        center.add(jp1);
        center.add(jp2);
        register.setAlignmentX(Box.CENTER_ALIGNMENT);
        confirm.setAlignmentX(Box.CENTER_ALIGNMENT);
        reset.setAlignmentX(Box.CENTER_ALIGNMENT);
        cancel.setAlignmentX(Box.CENTER_ALIGNMENT);
        register.setText("register");
        confirm.setText("confirm");
        reset.setText("reset");
        cancel.setText("cancel");
        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));

        east.add(confirm);
        east.add(reset);
        east.add(cancel);
        east.add(register);
        confirm.addActionListener(new Listener());
        reset.addActionListener(new Listener());
        cancel.addActionListener(new Listener());
        register.addActionListener(new Listener());
        log.setTitle("log in");
        log.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        log.setSize(420, 170);
        log.add(center, BorderLayout.CENTER);
        log.add(east, BorderLayout.EAST);
        log.setVisible(true);
    }

    class Listener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("confirm")) {
                try {

                    out = new PrintWriter(s.getOutputStream(), true);
                    out.println("confirm");
                    out.println(jtfa.getText());
                    out.println(String.copyValueOf(jtfp.getPassword()));
                    setAccount(jtfa.getText());
                    BufferedReader bufin = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String mess = bufin.readLine();

                    JOptionPane.showMessageDialog(null, mess, mess, JOptionPane.INFORMATION_MESSAGE);

                    if (mess.equals("successfully log in")) {

                        log.setVisible(false);

                        jtfa.setEnabled(false);
                        jtfp.setText("");
                        SlimeClient sc = new SlimeClient();

                        sc.launchFrame();
                    } else if (mess.equals("password error")) {
                        JOptionPane.showMessageDialog(null, mess, mess, JOptionPane.ERROR_MESSAGE);
                    } else if (mess.equals("error account")) {
                        JOptionPane.showMessageDialog(null, mess, mess, JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            } else if (e.getActionCommand().equals("reset")) {
                jtfa.setText("");
                jtfp.setText("");
            } else if (e.getActionCommand().equals("cancel")) {
                System.exit(0);

            } else if (e.getActionCommand().equals("register")) {


                if (jtfa.getText().equals("") || String.copyValueOf(jtfp.getPassword()).equals("")) {
                    JOptionPane.showMessageDialog(null, "please enter the blank", "please enter the blank", JOptionPane.ERROR_MESSAGE);

                } else {
                    try {
                        out = new PrintWriter(s.getOutputStream(), true);
                        out.println("register");
                        out.println(jtfa.getText());
                        out.println(String.copyValueOf(jtfp.getPassword()));
                        JOptionPane.showMessageDialog(null, "successfully register", "successfully register ", JOptionPane.INFORMATION_MESSAGE);

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }

            } else ;
        }

    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        Login.account = account;
    }

    public static void main(String[] args) {
        try {
            s = new Socket("127.0.0.1", 12040);
            Login log = new Login();
            log.showLog();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


