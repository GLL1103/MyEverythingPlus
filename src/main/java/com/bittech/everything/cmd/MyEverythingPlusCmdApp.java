package com.bittech.everything.cmd;

import com.bittech.everything.config.MyEverythingPlusConfig;
import com.bittech.everything.core.MyEverythingPlusManager;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;

import java.util.List;
import java.util.Scanner;

public class MyEverythingPlusCmdApp {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        //处理参数     用户可配置
        parseParams(args);

        //欢迎
        welcome();

        //统一调度器
        MyEverythingPlusManager manager = MyEverythingPlusManager.getInstance();

        //启动清理线程
        manager.startBackgroundClearThread();

        //交互式
        interactive(manager);
    }

    private static void parseParams(String[] args) {
        MyEverythingPlusConfig config = MyEverythingPlusConfig.getInstance();

        /**
         * 处理参数
         * 如果用户指定的参数格式不对，使用默认值
         */
        for(String param : args) {
            String maxReturnParam = "--maxReturn=";
            if(param.startsWith(maxReturnParam)) {
                //--maxReturn=values
                int index = param.indexOf("=");
                String maxReturnStr = param.substring(index+1);
                try{
                    int maxReturn = Integer.parseInt(maxReturnStr);
                    config.setMaxReturn(maxReturn);
                }catch (NumberFormatException e){
                    //格式错误，不做处理，使用默认值
                }
            }

            String depthOrderByAscParam = "--depthOrderByAsc=";
            if(param.startsWith(depthOrderByAscParam)) {
                //--depthOrderByAsc=values
                int index = param.indexOf("=");
                String depthOrderByAsc = param.substring(index+1);
                //当传入字符串为空时，返回false
                config.setDepthOrderByAsc(Boolean.parseBoolean(depthOrderByAsc));
            }

            String includePathParam = "--includePath=";
            if(param.startsWith(includePathParam)) {
                //--includePath=A;B
                int index = param.indexOf("=");
                String includePath = param.substring(index+1);
                String[] includePaths = includePath.split(";");

                //如果为空，表示没有输入搜索路径，不清除默认搜索路径
                if(0 < includePaths.length) {
                    config.getIncludePath().clear();
                }
                for(String p : includePaths) {
                    config.getIncludePath().add(p);
                }
            }

            String excludePathParam = "--excludePath";
            if(param.startsWith(excludePathParam)) {
                //--excludePath=A;B
                int index = param.indexOf("=");
                String excludePath = param.substring(index+1);
                String[] excludePaths = excludePath.split(";");

                //如果为空，表示没有输入排除路径，不清除默认排除路径
                if(0 < excludePaths.length) {
                    config.getExcludePath().clear();
                }
                for(String p : excludePaths) {
                    config.getExcludePath().add(p);
                }
            }
        }
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
        //统一调度器中的search
        //name fileType limit orderByAsc
        condition.setLimit(MyEverythingPlusConfig.getInstance().getMaxReturn());
        condition.setOrderByAsc(MyEverythingPlusConfig.getInstance().getDepthOrderByAsc());
        List<Thing> things = manager.search(condition);
        for(Thing thing : things) {
            System.out.println(thing.getPath());
        }
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
