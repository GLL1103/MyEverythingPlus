package com.bittech.everything.core.dao.impl;

import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.FileType;
import com.bittech.everything.core.model.Thing;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileIndexDaoImpl implements FileIndexDao {

    private final DataSource dataSource;
    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // 1.获取数据库连接
            connection = dataSource.getConnection();
            // 2.准备SQL语句
            String sql = "insert into file_index(name,path,depath,file_type) values (?,?,?,?)";
            // 3.准备命令
            statement = connection.prepareStatement(sql);
            // 4.设置参数 1 2 3 4
            statement.setString(1,thing.getName());
            statement.setString(2,thing.getPath());
            statement.setInt(3,thing.getDepath());
            statement.setString(4,thing.getFileType().name());
            // 5.执行命令
            statement.executeUpdate();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally {
            releaseResource(null,statement,connection);
        }
    }

    @Override
    public List<Thing> search(Condition condition) {

        List<Thing> things = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // 1.获取数据库连接
            connection = dataSource.getConnection();
            // 2.准备SQL语句
            // name     : like
            // fileType : =
            // limit    : limit offset
            // orderbyAsc : order by
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select name,path,depath,file_type from file_index");
            //name匹配： 前模糊 后模糊 前后模糊
            sqlBuilder.append(" where ").append(" name like '%").append(condition.getName()).append("%' ");
            if(condition.getFileType() != null) {
                sqlBuilder.append(" and file_type = '").append(condition.getFileType().toUpperCase()).append("' ");
            }
            //limit   order
            if(condition.getOrderByAsc() != null) {
                sqlBuilder.append(" order by depath ").append(condition.getOrderByAsc() ? "asc" : "desc" );
            }
            if(condition.getLimit() != null) {
                sqlBuilder.append(" limit ").append(condition.getLimit()).append(" offset 0 ");
            }


            //打印SQL 语句
            System.out.println(sqlBuilder);
            // 3.准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            // 4.设置参数 1 2 3 4

            // 5.执行命令
            resultSet = statement.executeQuery();
            // 6.处理结果
            while(resultSet.next()) {
                // 数据库中的行记录  -- >  java中的对象Thing
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepath(resultSet.getInt("depath"));
                String fileType = resultSet.getString("file_type");
                thing.setFileType(FileType.lookupByName(fileType));
                things.add(thing);
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        finally {
            releaseResource(resultSet,statement,connection);
        }
        return things;
    }


    //解决内部代码大量重复问题：  重构
    private void releaseResource(ResultSet resultSet,PreparedStatement statement,Connection connection) {
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null) {
            try {
                connection.close();
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(DataSourceFactory.GetDataSource());

        Thing thing = new Thing();
        thing.setName("简历2.ppt");
        thing.setPath("D:\\简历2.ppt");
        thing.setDepath(1);
        thing.setFileType(FileType.DOC);

        fileIndexDao.insert(thing);
        Condition condition = new Condition();
        condition.setName("简历");
        condition.setLimit(1);
        condition.setOrderByAsc(true);
        List<Thing> things = fileIndexDao.search(condition);

        for(Thing t : things) {
            System.out.println(t);
        }
    }
}
