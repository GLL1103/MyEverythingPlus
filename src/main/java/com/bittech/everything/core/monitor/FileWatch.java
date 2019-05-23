package com.bittech.everything.core.monitor;

import com.bittech.everything.core.common.HandlePath;

public interface FileWatch {

    //启动监听
    void start();

    //监听的目录
    void monitor(HandlePath handlePath);

    //关闭监听
    void stop();
}
