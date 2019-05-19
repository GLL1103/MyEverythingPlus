package com.bittech.everything.core.common;


import com.bittech.everything.core.model.FileType;
import com.bittech.everything.core.model.Thing;

import java.io.File;

//辅助工具类，将File对象转换成Thing对象
public final class FileConvertThing {

    private FileConvertThing(){}

    public static Thing convert(File file) {
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepath(computeFileDepath(file));
        thing.setFileType(computeFileType(file));

        return thing;
    }

    //获取文件深度
    private static int computeFileDepath(File file) {
        int dept = 0;
        String[] message = file.getAbsolutePath().split("\\\\");
        dept = message.length -1;

        return dept;
    }

    //获取文件类型
    private static FileType computeFileType(File file) {
        //目录
        if(file.isDirectory()) {
            return FileType.OTHER;
        }

        //文件
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");

        if(index != -1 && index<fileName.length()-1){
            String extend = fileName.substring(index+1);
            return FileType.lookUp(extend);
        }
        else {
            return FileType.OTHER;
        }
    }
}
