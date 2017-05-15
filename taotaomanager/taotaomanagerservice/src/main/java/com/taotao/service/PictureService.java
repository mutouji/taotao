package com.taotao.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by HuHaifan on 2017/4/19.
 */
public interface PictureService {
    Map uploadPicture(MultipartFile uploadFile);
}
