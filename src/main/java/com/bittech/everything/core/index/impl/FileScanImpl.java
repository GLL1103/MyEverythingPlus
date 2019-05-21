package com.bittech.everything.core.index.impl;


import com.bittech.everything.config.MyEverythingPlusConfig;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.FileScan;
import com.bittech.everything.core.interceptor.FileInterceptor;
import com.bittech.everything.core.interceptor.impl.FileIndexInterceptor;
import com.bittech.everything.core.interceptor.impl.FilePrintInterceptor;
import com.bittech.everything.core.model.Thing;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileScanImpl implements FileScan {

    //DAO
    private MyEverythingPlusConfig config = MyEverythingPlusConfig.getInstance();
    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path) {
        File file = new File(path);

        //文件F
        if(file.isFile()) {
            if(config.getExcludePath().contains(file.getParent())) {
                return ;
            }
        }
        //目录
        else {
            File[] files = file.listFiles();
            if(files != null) {
                for(File f :files) {
                    index(f.getAbsolutePath());
                }
            }
        }
        //File Directory
        for(FileInterceptor interceptor:this.interceptors) {
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }

}
