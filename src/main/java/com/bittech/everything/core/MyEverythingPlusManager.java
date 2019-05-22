package com.bittech.everything.core;

import com.bittech.everything.config.MyEverythingPlusConfig;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.FileScan;
import com.bittech.everything.core.index.impl.FileScanImpl;
import com.bittech.everything.core.interceptor.impl.FileIndexInterceptor;
import com.bittech.everything.core.interceptor.impl.FilePrintInterceptor;
import com.bittech.everything.core.interceptor.impl.ThingClearInterceptor;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;
import com.bittech.everything.core.search.FileSearch;
import com.bittech.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MyEverythingPlusManager {

    //全局统一调度器
    private static volatile MyEverythingPlusManager manager;
    private FileSearch fileSearch;
    private FileScan fileScan;
    private ExecutorService executorService;

    //清理删除的文件
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;

    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);


    private MyEverythingPlusManager() {
        this.initComponent();
    }

    private void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.GetDataSource();

        //检查数据库
        checkDatabase();
        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        this.fileScan = new FileScanImpl();
        //为了检查程序效果
        //this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));

        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Clear");
        //将清理线程设置为守护线程
        this.backgroundClearThread.setDaemon(true);
    }

    private void checkDatabase() {
        String fileName = MyEverythingPlusConfig.getInstance().getH2IndexPath() + ".mv.db";
        File dbFile = new File(fileName);
        //初始化数据库
        if (dbFile.exists() && dbFile.isDirectory()) {
            throw new RuntimeException("The following path has the same folder as the database name, database creation failed!!\n"
                    + MyEverythingPlusConfig.getInstance().getH2IndexPath() + ".mv.db\n"
                    + "Please delete this folder and restart the program!");
        } else if (!dbFile.exists()) {
            DataSourceFactory.initDatabase();
        }
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
        //stream  流式处理  JDK8
        return this.fileSearch.search(condition).stream().filter(thing -> {
            String path = thing.getPath();
            File f = new File(path);
            boolean flag = f.exists();
            if(!flag) {
                //做删除
                //模拟生产者消费者模型（将待删除的文件加入队列中，指定某一线程去删除）
                thingClearInterceptor.apply(thing);
            }
            return flag;
        }).collect(Collectors.toList());
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

    //启动清理线程
    public void startBackgroundClearThread() {
        if(this.backgroundClearThreadStatus.compareAndSet(false,true)) {
            this.backgroundClearThread.start();
        }
        else {
            System.out.println("this thread is running ...");
        }
    }
}
