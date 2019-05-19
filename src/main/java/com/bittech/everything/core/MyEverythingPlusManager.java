package com.bittech.everything.core;

import com.bittech.everything.config.MyEverythingPlusConfig;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.FileScan;
import com.bittech.everything.core.index.impl.FileScanImpl;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;
import com.bittech.everything.core.search.FileSearch;
import com.bittech.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class MyEverythingPlusManager {

    //全局统一调度器
    private static volatile MyEverythingPlusManager manager;
    private FileSearch fileSearch;
    private FileScan fileScan;
    private ExecutorService executorService;


    private MyEverythingPlusManager() {
        this.initComponent();
    }

    private void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.GetDataSource();
        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan = new FileScanImpl();
    }


    public static MyEverythingPlusManager getInstance() {
        if(manager == null) {
            synchronized (MyEverythingPlusManager.class) {
                if(manager == null) {
                    manager = new MyEverythingPlusManager();
                }
            }
        }
        return manager;
    }


    //检索
    public List<Thing> search(Condition condition) {
        //NOTICE 扩展点
        return this.fileSearch.search(condition);
    }

    //索引
    public void buildIndex() {
        Set<String> directories = MyEverythingPlusConfig.getInstance().getIncludePath();
        if(this.executorService == null) {
            //固定大小线程池,大小为目录个数
            //ThreadFactory   给线程起名字
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                private final AtomicInteger threadId = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                    return thread;
                }
            });
        }

        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());

        System.out.println("Build index start ...");
        for(String path : directories) {
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    MyEverythingPlusManager.this.fileScan.index(path);
                    //当前任务完成，值-1
                    countDownLatch.countDown();
                }
            });
        }

        //阻塞，直到任务完成，值为0
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Buidl index complete ...");
    }

}
