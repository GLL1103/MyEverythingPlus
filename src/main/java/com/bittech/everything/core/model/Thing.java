package com.bittech.everything.core.model;

import lombok.Data;

/**
 * 文件属性索引之后的记录Thing表示
 */
@Data   //getter,setter,toString生成完成
public class Thing {

    /**
     * 文件名称（保留名称）
     * File D:/a/b/hello.txt    hello.txt
     */
    private String name;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件深度
     */
    private Integer depath;

    /**
     * 文件类型
     */
    private FileType fileType;

}
