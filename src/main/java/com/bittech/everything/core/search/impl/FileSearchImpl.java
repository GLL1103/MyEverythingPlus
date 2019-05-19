package com.bittech.everything.core.search.impl;

import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;
import com.bittech.everything.core.search.FileSearch;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class FileSearchImpl implements FileSearch {

    /**
     * 被final修饰的变量不能被修改
     * 三种初始化方法
     * 直接赋值  构造方法中赋值  代码块中赋值
     */

    private final FileIndexDao fileIndexDao;
    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition) {
        //数据库的处理逻辑
        return fileIndexDao.search(condition);
    }
}
