package com.bittech.everything.core.interceptor.impl;

import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.interceptor.ThingInterceptor;
import com.bittech.everything.core.model.Thing;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingClearInterceptor implements ThingInterceptor,Runnable{

    //相当于消费者队列 （删除队列）
    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    private final FileIndexDao fileIndexDao;

    public ThingClearInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }


    @Override
    public void apply(Thing thing) {
        this.queue.add(thing);
    }

    @Override
    public void run() {
        while(true) {
            Thing thing = this.queue.poll();
            if(null != thing) {
                fileIndexDao.delete(thing);
            }
            //优化点 -> 批量删除
            //List<Thing> list = new ArrayList<>();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
