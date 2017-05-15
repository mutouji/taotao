package com.taotao.rest.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.HashSet;

/**
 * Created by HuHaifan on 2017/4/27.
 */
public class JedisTest {

    @Test
    public void testJedisSingle(){
        //创建一个jedis对象
        Jedis jedis = new Jedis("119.29.217.159", 6379);
        //调用jedis对象的方法，方法名称和redis的命令一直
        jedis.set("key1", "jedis test");
        String string = jedis.get("key1");
        System.out.printf(string);
        //关闭jedis
        jedis.close();
    }

    /**
     * 使用连接池
     */
    @Test
    public void testJedisPool(){
        //创建jedis连接池
        JedisPool pool = new JedisPool("119.29.217.159", 6379);
        //从连接池中获得jedis对象
        Jedis jedis = pool.getResource();
        String string = jedis.get("key1");
        System.out.printf(string);
        //关闭jedis
        jedis.close();
    }

    /**
     * 集群版测试
     */
    @Test
    public void testJedisCluster(){
        HashSet<HostAndPort> nodes = new HashSet<>();
        nodes.add(new HostAndPort("119.29.217.159", 7001));
        nodes.add(new HostAndPort("119.29.217.159", 7002));
        nodes.add(new HostAndPort("119.29.217.159", 7003));
        nodes.add(new HostAndPort("119.29.217.159", 7004));
        nodes.add(new HostAndPort("119.29.217.159", 7005));
        nodes.add(new HostAndPort("119.29.217.159", 7006));
        JedisCluster cluster = new JedisCluster(nodes,60,1000);

        cluster.set("key1", "1000");
        String string = cluster.get("key1");
        System.out.println(string);

        cluster.close();
    }

    @Test
    public void testSpringJedisSingle() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/spring-*.xml");
        JedisPool pool = (JedisPool) applicationContext.getBean("redisClient");
        Jedis jedis = pool.getResource();
        String string = jedis.get("key1");
        System.out.println(string);
        jedis.close();
        pool.close();
    }

    @Test
    public void testSpringJedisCluster() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/spring-*.xml");
        JedisCluster jedisCluster =  (JedisCluster) applicationContext.getBean("redisClient");
        String string = jedisCluster.get("key1");
        System.out.println(string);
        jedisCluster.close();
    }


}
