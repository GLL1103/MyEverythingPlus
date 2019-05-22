package com.bittech.everything.core.dao;


import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;

import java.util.List;

//业务层访问数据库的CRUD
public interface FileIndexDao {
    //插入
    void insert(Thing thing);
    //查找
    List<Thing> search(Condition condition);
    //删除
    void delete(Thing thing);
}
