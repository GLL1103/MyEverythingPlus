package com.bittech.everything.cmd;

import com.bittech.everything.core.MyEverythingPlusManager;
import com.bittech.everything.core.model.Condition;

import java.util.Scanner;

public class MyEverythingPlusCmdApp {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        //欢迎
        welcome();

        //统一调度器
        MyEverythingPlusManager manager = MyEverythingPlusManager.getInstance();

        //交互式
        interactive(manager);
    }

    private static void interactive(MyEverythingPlusManager manager) {
        while(true) {
            System.out.print("MyEverythingPlus >>");
            String input = scanner.nextLine();
            //优先处理search
            if(input.startsWith("search")) {
                //search name [file_type]
                String[] values = input.split(" ");
                if(values.length >= 2) {
                    //开头包含search但不是search
                    if(!values[0].equals("search")) {
                        help();
                        continue;
                    }

                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);

                    //除文件名外还有其他搜索条件
                    if(values.length >= 3) {
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    search(manager,condition);
                    continue;
                }
                else {
                    help();
                    continue;
                }
            }

            switch(input) {
                case "help":
                    help();
                    break;
                case "quit":
                    quit();
                    break;
                case "index":
                    index(manager);
                    break;
                default:
                    help();
            }
        }
    }

    private static void search(MyEverythingPlusManager manager,Condition condition ) {
        System.out.println("检索功能");
        //统一调度器中的search
        //name fileType limit orderByAsc
        manager.search(condition);
    }

    private static void index(MyEverythingPlusManager manager) {
        //统一调度器中的index
        new Thread(new Runnable() {
            @Override
            public void run() {
                manager.buildIndex();
            }
        }).start();
    }

    private static void quit() {
        System.out.println("谢谢使用！");
        System.exit(0);
    }

    private static void welcome() {
        System.out.println("欢迎使用，my_everything_plus");
    }

    private static void help() {
        System.out.println("命令列表:");
        System.out.println("退出:quit");
        System.out.println("帮助:help");
        System.out.println("索引:index");
        System.out.println("搜索:search <name> [<file-Type> img | doc | bin | archive | other]");
    }

}
