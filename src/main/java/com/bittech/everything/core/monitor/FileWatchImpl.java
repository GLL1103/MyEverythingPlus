package com.bittech.everything.core.monitor;

import com.bittech.everything.core.common.FileConvertThing;
import com.bittech.everything.core.common.HandlePath;
import com.bittech.everything.core.dao.FileIndexDao;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;


public class FileWatchImpl implements FileWatch,FileAlterationListener{

    private FileIndexDao fileIndexDao;
    private FileAlterationMonitor monitor;

    public FileWatchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.monitor = new FileAlterationMonitor(10);
    }


    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
        //添加监听
       // fileAlterationObserver.addListener(this);
    }

    @Override
    public void onDirectoryCreate(File file) {

    }

    @Override
    public void onDirectoryChange(File file) {

    }

    @Override
    public void onDirectoryDelete(File file) {

    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("加入新文件");
        //有文件的创建，将新文件加入数据库
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("文件修改");
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println("删除文件");
        //有文件的删除，将文件从数据库中删除
        this.fileIndexDao.delete(FileConvertThing.convert(file));
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
        //移除文件
        //fileAlterationObserver.removeListener(this);
    }

    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(HandlePath handlePath) {
        //监控的是includePath集合
        for(String path : handlePath.getIncludePath()) {
                    FileAlterationObserver observer = new FileAlterationObserver(path,pathname -> {
                        String currentPath = pathname.getAbsolutePath();
                        for(String excludePath : handlePath.getExcludePath()) {
                            if(excludePath.startsWith(currentPath)) {
                                return false;
                            }
                        }
                        return true;
                    });
                    observer.addListener(this);
                    this.monitor.addObserver(observer);
        }
    }

    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
