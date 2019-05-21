package com.bittech.everything.core.index;

import com.bittech.everything.core.interceptor.FileInterceptor;

public interface FileScan {

    //遍历path
    void index(String path);

    //遍历的拦截器
    void interceptor(FileInterceptor interceptor);

}
