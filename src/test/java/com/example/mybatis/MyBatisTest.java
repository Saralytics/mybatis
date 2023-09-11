package com.example.mybatis;
import com.example.mybatis.entity.Goods;
import com.example.mybatis.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.apache.ibatis.io.Resources;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyBatisTest {
    @Test
    public void testSqlSessionFactory() throws IOException {
        // read the xml config file in classpath, using java io
        Reader reader = Resources.getResourceAsReader("config.xml");

        // parse the xml file and initialize a SqlSessionFactory object
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        System.out.println("Load successful");

        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();
//            Connection conn = sqlSession.getConnection();
//            System.out.println(conn);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(sqlSession != null){
                // either release the connection back into the pool
                // or actually close the connection depending on type = "POOLED" or "UNPOOLED"
                 sqlSession.close();
            }
        }
    }

    @Test
    public void testMyBatisUtils(){
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
             Connection connection = sqlSession.getConnection();
            System.out.println(connection);
            //        sqlSes
            //        sion.insert(); etc..
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectAll(){
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtils.openSession();
            List<Goods> list = sqlSession.selectList("goods.selectAll");
            for(Goods i:list){
                System.out.println(i.getTitle());
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testSelectId(){
        SqlSession sqlSession = null;
        try{
            sqlSession = MyBatisUtils.openSession();
            Goods goods = sqlSession.selectOne("goods.selectById",1603);
            System.out.println(goods.getTitle());
        } catch(Exception e)
        {
            e.printStackTrace();
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }

    }
    @Test
    public void testSelectPriceRange(){
        SqlSession sqlSession = null;
        try{
            sqlSession = MyBatisUtils.openSession();
            Map param = new HashMap<>();
            param.put("min", 100);
            param.put("max", 200);
            param.put("limit", 50);
            List<Goods> list = sqlSession.selectList("selectByPricerange",param);
            for(Goods i:list){
                System.out.println(i.getTitle());
            }

        } catch(Exception e)
        {
            e.printStackTrace();
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }

    @Test
    public void testInsert(){
        SqlSession sqlSession = null;

        try{
            sqlSession = MyBatisUtils.openSession();
            Goods newproduct = new Goods();
            newproduct.setTitle("Test Product");
            newproduct.setSubtitle("test subtitle");
            newproduct.setOriginalCost(200f);
            newproduct.setCurrentPrice(100f);
            newproduct.setDiscount(0.5f);
            newproduct.setIsFreeDelivery(1);
            newproduct.setCategoryID(43);

            // insert method has a return value which is the number of rows impacted
            int num = sqlSession.insert("goods.insertProduct",newproduct);
            System.out.println("Rows are inserted, number is " + num);
            sqlSession.commit(); // auto commit is off
            System.out.println(newproduct.getGoodsID());
        } catch(Exception e){
            if(sqlSession!=null){
                sqlSession.rollback(); // explicitly rollback in case of failure
            }
            throw e;
        } finally {
            MyBatisUtils.closeSession(sqlSession);
        }
    }
}
