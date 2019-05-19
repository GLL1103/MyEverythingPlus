package com.bittech.everything.config;


import lombok.Getter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Getter
public final class MyEverythingPlusConfig {
    private static volatile MyEverythingPlusConfig config;
    //建立索引的目录
    private Set<String> includePath = new HashSet<>();
    //排除索引文件的目录
    private Set<String> excludePath = new HashSet<>();

    private MyEverythingPlusConfig() {}

    public static MyEverythingPlusConfig getInstance() {
        if(config == null) {
            synchronized (MyEverythingPlusConfig.class) {
                if(config == null) {
                    config = new MyEverythingPlusConfig();
                    //获取文件系统
                    FileSystem fileSystem = FileSystems.getDefault();

                    //遍历的目录
                    Iterable<Path> iterable = fileSystem.getRootDirectories();
                    iterable.forEach(path -> config.getIncludePath().add(path.toString()));

                    //排除的目录
                    /**
                     * Windows: C:\Windows  C:\Program Files  C:\Program Files (x86)  C:\ProgramData
                     * Linux:  /tmp  /etc  /root
                     * unix
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
            }
        }
        return config;
    }

}
