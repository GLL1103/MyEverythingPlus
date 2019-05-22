package com.bittech.everything.config;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Getter@ToString
public final class MyEverythingPlusConfig {
    private static volatile MyEverythingPlusConfig config;
    //建立索引的目录
    private Set<String> includePath = new HashSet<>();
    //排除索引文件的目录
    private Set<String> excludePath = new HashSet<>();

    //检索最大的返回值数量
    @Setter
    private Integer maxReturn = 30;
    //深度排序的规则，默认是升序
    @Setter
    private Boolean depthOrderByAsc = true;

    private MyEverythingPlusConfig() { }

    private void initDefaultPathsConfig() {
        //获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();

        //遍历的目录
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        iterable.forEach(path -> config.includePath.add(path.toString()));


        //排除的目录
        /**
         * Windows: C:\Windows  C:\Program Files  C:\Program Files (x86)  C:\ProgramData
         * Linux:  /tmp  /etc  /root
         * unix
         * 这里只处理Windows和Linux两种操作系统
         */
        String osName = System.getProperty("os.name");
        if(osName.startsWith("Windows")) {
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\Program Files");
            config.getExcludePath().add("C:\\Program Files (x86)");
            config.getExcludePath().add("C:\\ProgramData");
        }
        else {
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/root");
        }
    }


    /**
     * H2数据库文件路径
     * @return
     */
//    private String h2IndexPath = System.getProperty("user.dir")+ FileSystems.getDefault()+"MyEverythingPlus";
    private String h2IndexPath =System.getProperty("user.dir") + File.separator+"MyEverythingPlus";

    public static MyEverythingPlusConfig getInstance() {
        if(config == null) {
            synchronized (MyEverythingPlusConfig.class) {
                if(config == null) {
                    config = new MyEverythingPlusConfig();
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }

}
