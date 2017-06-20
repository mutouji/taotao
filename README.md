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
1. 逆向工程生成Mapper和Pojo减少代码量，需要多表查询的时候再使用手写mapper的形式
```
<plugin>
                    <groupId>org.mybatis.generator</groupId>
                    <artifactId>mybatis-generator-maven-plugin</artifactId>
                    <version>1.3.2</version>
                    <configuration>
                        <!--配置文件的位置-->      <configurationFile>src/main/resources/generatorConfig.xml</configurationFile>
                        <verbose>true</verbose>
                        <overwrite>true</overwrite>
                    </configuration>
                    <dependencies>
                        <dependency>
                            <groupId>org.mybatis.generator</groupId>
                            <artifactId>mybatis-generator-core</artifactId>
                            <version>1.3.2</version>
                        </dependency>
                    </dependencies>
</plugin>
```
1. Spring和SpringMVC父子容器关系

![](http://upload-images.jianshu.io/upload_images/3245878-642cfde5d4f7d631.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
1. 创建EasyUI异步tree的数据结构,实现商品的异步加载
```
public class EUTreeNode {
    private long id;
    private String text;
    private String state;
    }
```
1. 使用Sftp实现图片上传,nginx实现http服务
```
// 更改服务器目录
            if (null != remotePath && remotePath.trim() != "") {
                sftp.cd(remotePath);
            }
            // 发送文件
            sftp.put(input, remoteFilename);
            success = true;
```
1. 富文本编辑器的使用
```
var contentAddEditor ;
	$(function(){
		contentAddEditor = TT.createEditor("#contentAddForm [name=content]");
		TT.initOnePicUpload();
		$("#contentAddForm [name=categoryId]").val($("#contentCategoryTree").tree("getSelected").id);
	});
```
1. 商品规格使用模板的方式存储,不需要进行多表查询

![](http://upload-images.jianshu.io/upload_images/3245878-e3f2966c3508670c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
1. 根据规格模块生成HTML,传递给页面展示
```
 sb.append("<table cellpadding=\"0\" cellspacing=\"1\" width=\"100%\" border=\"0\" class=\"Ptable\">\n");
                sb.append("    <tbody>\n");
                for(Map m1:jsonList) {
                    sb.append("        <tr>\n");
                    sb.append("            <th class=\"tdTitle\" colspan=\"2\">"+m1.get("group")+"</th>\n");
                    sb.append("        </tr>\n");
                    List<Map> list2 = (List<Map>) m1.get("params");
                    for(Map m2:list2) {
                        sb.append("        <tr>\n");
                        sb.append("            <td class=\"tdTitle\">"+m2.get("k")+"</td>\n");
                        sb.append("            <td>"+m2.get("v")+"</td>\n");
                        sb.append("        </tr>\n");
                    }
                }
                sb.append("    </tbody>\n");
                sb.append("</table>");
```
1. 通过jsonp跨域请求json数据
```
function() {
    	//使用jsonp来实现跨域请求
        $.getJSONP(this.URL_Serv, category.getDataService);
    	//直接使用ajax请求json数据
    	/*$.getJSON(this.URL_Serv, function(json){
    		category.getDataService(json);
    	});*/
    }
```
1. 通过httpclient请求首页内容，有利于SEO优化
```
String result = HttpClientUtil.doGet(REST_BASE_URL + REST_INDEX_AD_URL);
        //把字符串转TaotaoResult
            TaotaoResult taotaoResult = TaotaoResult.formatToList(result, TbContent.class);
            //取内容列表
            List<TbContent> list = (List<TbContent>) taotaoResult.getData();
            List<Map> resultlist = new ArrayList<>();
```
1. 通过redis集群缓存业务
```
 //从缓存中取内容
        try {
            String result = jedisClient.hget(INDEX_CONTENT_REDIS_KEY, contentCid + "");
            if (!StringUtils.isBlank(result)) {
                //把字符串转list
                List<TbContent> resultList = JsonUtils.jsonToList(result, TbContent.class);
                return resultList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
```
```
//向缓存中添加内容
        try {
            String cacheString = JsonUtils.objectToJson(list);
            jedisClient.hset(INDEX_CONTENT_REDIS_KEY, contentCid+"", cacheString);
        } catch (Exception e) {
            e.printStackTrace();
        }
```
1. 通过solr集群实现搜索功能，并使用solrJ调用
```
//返回值对象
		SearchResult result = new SearchResult();
		//根据查询条件查询索引库
		QueryResponse queryResponse = solrServer.query(query);
		//取查询结果
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		//取查询结果总数量
		result.setRecordCount(solrDocumentList.getNumFound());
		//商品列表
		List<Item> itemList = new ArrayList<>();
		//取高亮显示
		Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
		//取商品列表
		for (SolrDocument solrDocument : solrDocumentList) {
			//创建一商品对象
			Item item = new Item();
			item.setId((String) solrDocument.get("id"));
			//取高亮显示的结果
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			String title = "";
			if (list != null && list.size()>0) {
				title = list.get(0);
			} else {
				title = (String) solrDocument.get("item_title");
			}
			item.setTitle(title);
			item.setImage((String) solrDocument.get("item_image"));
			item.setPrice((long) solrDocument.get("item_price"));
			item.setSell_point((String) solrDocument.get("item_sell_point"));
			item.setCategory_name((String) solrDocument.get("item_category_name"));
			//添加的商品列表
			itemList.add(item);
		}
		result.setItemList(itemList);
		return result;
```
1. 单点登录系统实现，将session存在redis中，注册接口支持jsonp调用

![](http://upload-images.jianshu.io/upload_images/3245878-35e5c9b3b9aa3f12.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
1. 登录逻辑实现
```
if (list == null || list.size() == 0) {
            return TaotaoResult.build(400, "用户名或密码错误");
        }
        TbUser tbUser = list.get(0);
        //比对密码
        if (!DigestUtils.md5DigestAsHex(password.getBytes()).equals(tbUser.getPassword())) {
            return TaotaoResult.build(400, "用户名或密码错误");
        }
        //生成token
        String token = UUID.randomUUID().toString();
        //保存用户之前删掉密码
        tbUser.setPassword(null);
        //写入redis
        jedisClient.set(REDIS_USER_SESSION_KEY + ":" + token, JsonUtils.objectToJson(tbUser));
        //设置session的过期时间
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);

        //添加写cookie的逻辑,关闭浏览器就失效
        CookieUtils.setCookie(request,response,"TT_TOKEN",token);
```
1. 拦截器实现用户登录
```
/在Handler执行之前处理
        //1判断用户是否登录
        //从cookie中取token
        String token = CookieUtils.getCookieValue(httpServletRequest, "TT_TOKEN");
        //根据token换取用户信息,调用sso系统接口
        TbUser user = userService.getUserByToken(token);
        //取不到用户信息
        if (null == user) {
            //跳转到登录页面,把用户请求的url作为重定向参数传递给登录页面
            httpServletResponse.sendRedirect(userService.SSO_BASE_URL + userService.SSO_PAGE_LOGIN
                    + "?redirect=" +httpServletRequest.getRequestURL());
            //返回false
            return false;
        }
        //取到用户信息，放行
        //把用户信息放入request
        httpServletRequest.setAttribute("user", user);
        //返回值决定handler是否执行。true:执行，flase:不执行
```
1. 购物车实现，通过cookie存储
```
//取商品信息
        CartItem cartItem = null;
        //取购物车商品列表
        List<CartItem> itemList = getCartItemList(request);
        //判断购物车商品列表中是否存在此商品
        for (CartItem cItem : itemList) {
            //如果存在此商品
            if (cItem.getId() == itemId) {
                //增加商品数量
                cItem.setNum(cItem.getNum() + num);
                cartItem = cItem;
                break;
            }
        }
        if (cartItem == null) {
            cartItem = new CartItem();
            //根据商品id查询商品基本信息。
            String json = HttpClientUtil.doGet(REST_BASE_URL + ITEM_INFO_URL + itemId);
            //把json转换成java对象
            TaotaoResult taotaoResult = TaotaoResult.formatToPojo(json, TbItem.class);
            if (taotaoResult.getStatus() == 200) {
                TbItem item = (TbItem) taotaoResult.getData();
                cartItem.setId(item.getId());
                cartItem.setTitle(item.getTitle());
                cartItem.setImage(item.getImage() == null ? "":item.getImage().split(",")[0]);
                cartItem.setNum(num);
                cartItem.setPrice(item.getPrice());
            }
            //添加到购物车列表
            itemList.add(cartItem);
        }
        //把购物车列表写入cookie
        CookieUtils.setCookie(request, response, "TT_CART", JsonUtils.objectToJson(itemList), true);
```
1. 通过redis的incr命令生成订单号
```
long orderId = jedisClient.incr(ORDER_GEN_KEY);
        //补全pojo的属性
        order.setOrderId(orderId + "");
        //状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭
        order.setStatus(1);
        Date date = new Date();
        order.setCreateTime(date);
        order.setUpdateTime(date);
        //0：未评价 1：已评价
        order.setBuyerRate(0);
        //向订单表插入数据
        orderMapper.insert(order);
        //插入订单明细
        for (TbOrderItem tbOrderItem : itemList) {
            //补全订单明细
            //取订单明细id
            long orderDetailId = jedisClient.incr(ORDER_DETAIL_GEN_KEY);
            tbOrderItem.setId(orderDetailId + "");
            tbOrderItem.setOrderId(orderId + "");
            //向订单明细插入记录
            orderItemMapper.insert(tbOrderItem);
        }
```