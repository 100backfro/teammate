package com.api.backend.team.service.file.impl;

import com.api.backend.team.data.type.ImgType;
import org.springframework.web.multipart.MultipartFile;

public interface ImgStoreImpl {

  String uploadImg(MultipartFile multipartFile, ImgType imgType , String name);

}
