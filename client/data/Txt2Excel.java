package client.data;


import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 */
public class Txt2Excel {


    public void txt2excel1() {
        File file = new File("D:\\学习\\Slime's fighting\\account.txt");// 将读取的txt文件
        File file2 = new File("D:\\学习\\Slime's fighting\\src\\client\\data\\data.xls");// 将生成的excel表格
        if (file.exists() && file.isFile()) {

            InputStreamReader read = null;
            BufferedReader input = null;
            WritableWorkbook wbook = null;
            WritableSheet sheet;

            try {
                read = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                input = new BufferedReader(read);
                Scanner scanner = new Scanner(read);
                wbook = Workbook.createWorkbook(file2);// 根据路径生成excel文件
                sheet = wbook.createSheet("first", 0);// 新标签页

                try {
                    Label sName = new Label(0, 0, "");// 如下皆为列名
                    sheet.addCell(sName);
                    Label account = new Label(0, 0, "用户名");// 如下皆为列名
                    sheet.addCell(account);
                    Label pass = new Label(1, 0, "密码");
                    sheet.addCell(pass);

                } catch (RowsExceededException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }

                int m = 1;// excel行数
                int n = 0;// excel列数
                int count = 0;
                Label t;

                while ((scanner.hasNext())) {
                    String word = scanner.next();
                    t = new Label(n, m, word.trim());
                    sheet.addCell(t);
                    n++;
                    count++;
                    if (count == 2) {
                        n = 0;// 回到列头部
                        m++;// 向下移动一行
                        count = 0;
                    }

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            } finally {
                try {
                    wbook.write();
                    wbook.close();
                    input.close();
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("over!");

        } else {
            System.out.println("file is not exists or not a file");

        }
    }


    public void txt2excel2() {
        File file = new File("D:\\学习\\Slime's fighting\\src\\client\\data\\data.txt");// 将读取的txt文件
        File file2 = new File("D:\\学习\\Slime's fighting\\src\\client\\data\\game_data.xls");// 将生成的excel表格

        if (file.exists() && file.isFile()) {

            InputStreamReader read = null;
            BufferedReader input = null;
            WritableWorkbook wbook = null;
            WritableSheet sheet;

            try {
                read = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                input = new BufferedReader(read);
                Scanner scanner = new Scanner(read);
                wbook = Workbook.createWorkbook(file2);// 根据路径生成excel文件
                sheet = wbook.createSheet("first", 0);// 新标签页

                try {
                    Label sName = new Label(0, 0, "");// 如下皆为列名
                    sheet.addCell(sName);
                    Label account = new Label(0, 0, "用户名");// 如下皆为列名
                    sheet.addCell(account);
                    Label team = new Label(1, 0, "队伍（0绿1蓝）");
                    sheet.addCell(team);
                    Label blood = new Label(2, 0, "剩余血量");
                    sheet.addCell(blood);
                    Label time = new Label(3, 0, "在线时长");
                    sheet.addCell(time);

                } catch (RowsExceededException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }

                int m = 1;// excel行数
                int n = 0;// excel列数
                int count = 0;
                Label t;
                scanner.next();
                scanner.next();
                scanner.next();
                scanner.next();
                while ((scanner.hasNext())) {
                    String word = scanner.next();
                    t = new Label(n, m, word.trim());
                    sheet.addCell(t);
                    n++;
                    count++;
                    if (count == 4) {
                        n = 0;// 回到列头部
                        m++;// 向下移动一行
                        count = 0;
                    }

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            } finally {
                try {
                    wbook.write();
                    wbook.close();
                    input.close();
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("over!");

        } else {
            System.out.println("file is not exists or not a file");

        }
    }
}


