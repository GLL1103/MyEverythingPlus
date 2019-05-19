package com.bittech.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLOutput;


public class DataSourceFactory {

    //数据源  单例
    private static volatile DruidDataSource dataSource;
    //构造方法私有化
    private DataSourceFactory(){ }

    public static DataSource GetDataSource() {
        if(dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if(dataSource == null) {
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC   driver class
                    dataSource.setDriverClassName("org.h2.Driver");
                    //URL ， userName，password
                    //采用的是H2嵌入式数据库，数据库以本地文件的方式存储，只需提供URL接口
                    //获取当前工程路径
                    String workDir = System.getProperty("user.dir");

                    //JDBC规范中关于H2  jdbc:h2:filepath -> 存储到本地文件
                    //JDBC规范中关于H2  jdbc:h2://ip:port/databaseName  -> 存储到服务器
                    dataSource.setUrl("jdbc:h2:"+workDir+ File.separator+"my_everything_plus");
                }
            }
        }
        return dataSource;
    }

    //数据库的初始化
    public static void initDatabase() {
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.GetDataSource();
        //2.获取SQL语句
        //不采取读取绝对路径文件
        //采取读取classPath路径下的文件
        //try-with-resources
        try(InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("my_everything_plus.sql");) {
            if(in == null) {
                throw new RuntimeException("Not read init database script please check it");
            }

            StringBuilder sqlBuilder = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {
                String line = null;
                while((line = reader.readLine()) != null) {
                    if(!line.startsWith("--")) {
                        sqlBuilder.append(line);
                    }
                }
            }
            //3.获取数据库连接和名称执行SQL
            String sql = sqlBuilder.toString();

            //jdbc
            //3.1 获取数据库的链接
            Connection connection = dataSource.getConnection();
            //3.2 创建命令
            PreparedStatement statement = connection.prepareStatement(sql);
            //3.3 执行SQL语句
            statement.execute();
            connection.close();
            statement.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

    }

}



