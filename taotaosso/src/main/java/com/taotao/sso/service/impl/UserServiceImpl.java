package com.taotao.sso.service.impl;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.sso.dao.JedisClient;
import com.taotao.sso.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 用户管理
 * Created by HuHaifan on 2017/5/3.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private JedisClient jedisClient;

    @Value("${REDIS_USER_SESSION_KEY}")
    private String REDIS_USER_SESSION_KEY;

    @Value("${SSO_SESSION_EXPIRE}")
    private int SSO_SESSION_EXPIRE;

    @Override
    public TaotaoResult checkData(String content, Integer type) {
        //创建查询条件
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        //对数据进行校验：1、2、3分别代表username、phone、email
        //用户名校验
        if (type == 1) {
            criteria.andUsernameEqualTo(content);
        } else if (type == 2) {//电话校验
            criteria.andPhoneEqualTo(content);
        } else  {//Email校验
            criteria.andEmailEqualTo(content);
        }
        //执行查询
        List<TbUser> list = tbUserMapper.selectByExample(example);
        if (list == null || list.size() == 0) {
            return TaotaoResult.ok(true);
        }
        return TaotaoResult.ok(false);
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public TaotaoResult createUser(TbUser user) {
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //md5加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        tbUserMapper.insert(user);
        return TaotaoResult.ok();
    }

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public TaotaoResult userLogin(String username, String password, HttpServletRequest request, HttpServletResponse response) {

        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> list = tbUserMapper.selectByExample(example);
        //如果没有此用户名
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

        //返回token
        return TaotaoResult.ok(token);
    }

    /**
     * 根据token查询用户信息
     * @param token
     * @return
     */
    @Override
    public TaotaoResult getUserByToken(String token) {

        //根据token从redis中查询用户信息
        String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token);
        //判断是否过期
        if (StringUtils.isBlank(json)) {
            return TaotaoResult.build(400, "登录已过期");
        }
        //更新过期时间
        jedisClient.expire(REDIS_USER_SESSION_KEY + ":" + token, SSO_SESSION_EXPIRE);

        return TaotaoResult.ok(JsonUtils.jsonToPojo(json, TbUser.class));
    }

    /**
     * 根据token退出相应用户
     * @param token
     * @return
     */
    @Override
    public TaotaoResult exitByToken(String token) {
        //根据token从redis中查询用户信息
        String json = jedisClient.get(REDIS_USER_SESSION_KEY + ":" + token);
        //判断是否过期
        if (StringUtils.isBlank(json)) {
            return TaotaoResult.build(400, "已安全退出");
        }
        //根据token删除掉redis中的相关信息
        jedisClient.del(REDIS_USER_SESSION_KEY + ":" + token);

        return TaotaoResult.ok();
    }
}
