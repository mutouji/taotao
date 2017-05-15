package com.taotao.portal.service;

import com.taotao.pojo.TbUser;

/**
 * Created by HuHaifan on 2017/5/3.
 */
public interface UserService {

    TbUser getUserByToken(String token);
}
