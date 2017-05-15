package com.taotao.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

/**
 * Created by HuHaifan on 2017/4/26.
 */
public class HttpClientTest {

    @Test
    public void doget() throws Exception{
        //创建一个httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建一个GET对象
        HttpGet get = new HttpGet("https://www.zhihu.com");
        //执行请求
        CloseableHttpResponse response = httpClient.execute(get);
        //取响应结果
        int code = response.getStatusLine().getStatusCode();
        System.out.println(code);
        HttpEntity entity = response.getEntity();
        String string = EntityUtils.toString(entity,"utf-8");
        //关闭httpclient
        response.close();
        httpClient.close();
    }

    public void doPost() throws Exception{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建一个post对象

    }
}
