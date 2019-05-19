package com.bittech.everything.core.interceptor.impl;

import com.bittech.everything.core.common.FileConvertThing;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.impl.FileScanImpl;
import com.bittech.everything.core.interceptor.FileInterceptor;
import com.bittech.everything.core.model.Thing;

import java.io.File;

public class FileIndexInterceptor implements FileInterceptor {

    private final FileIndexDao fileIndexDao;
    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileConvertThing.convert(file);
        System.out.println("Thing -> " + thing);
        fileIndexDao.insert(thing);
    }



}
