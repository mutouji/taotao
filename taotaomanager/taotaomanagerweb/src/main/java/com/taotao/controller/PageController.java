package com.taotao.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by HuHaifan on 2017/4/17.
 */
@Controller
public class PageController {
    /**
     * 打开首页
     */

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showIndex() {
        return "index";
    }

    @RequestMapping(value = "/{page}")
    public String showPage(@PathVariable String page) {
        return page;
    }
}
