package com.bittech.everything.core.index;

import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.impl.FileScanImpl;
import com.bittech.everything.core.interceptor.FileInterceptor;
import com.bittech.everything.core.interceptor.impl.FileIndexInterceptor;
import com.bittech.everything.core.interceptor.impl.FilePrintInterceptor;
import com.bittech.everything.core.model.Thing;

public interface FileScan {

    //遍历path
    void index(String path);

    //遍历的拦截器
    void interceptor(FileInterceptor interceptor);

    public static void main(String[] args) {
        FileScan scan = new FileScanImpl();
        FileInterceptor printInterceptor = new FilePrintInterceptor();

        scan.interceptor(printInterceptor);

        FileInterceptor fileIndexInterceptor = new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.GetDataSource()));
        scan.interceptor(fileIndexInterceptor);

        scan.index("F://Everything");
    }
}
