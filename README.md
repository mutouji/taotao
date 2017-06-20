# 分布式架构的电商网站

## 技术栈
* Spring&SpringMVC
* Mybatis&Mybatis-generator
* Redis集群
* Solr集群&Zookeeper集群
* Nginx
* Jackson&Jsonp
* httpclient
## 系统模块
1. manager
 
    网站的后台管理模块。使用了mybatis的逆向工程插件、分页插件。
1. common

    工具包，封装了一些常用的工具和POJO类
1. order
    
    订单模块,Redis存储订单相关信息
1. portal
    
    门户层，主页面展示
1. rest
    
    服务提供层，提供接口供其它模块调用
1. search

    提供搜索服务，基于Solr集群实现，使用IK分词器进行中文分词
1. sso
    
    单点登录系统，session数据存放在redis中,可自由设置有效时间
## 技术要点
1. Maven的工程聚合,工程分Module开发，父工程负责聚合依赖
```
<modules>
        <module>taotaocommon</module>
        <module>taotaomanager</module>
        <module>taotaorest</module>
        <module>taotaoportal</module>
        <module>taotaosearch</module>
        <module>taotaosso</module>
        <module>taotaoorder</module>
</modules>
```
1. 减少表的管理查询，使用冗余解决表的关联问题

![](http://upload-images.jianshu.io/upload_images/3245878-f077c65bf04d3035.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)