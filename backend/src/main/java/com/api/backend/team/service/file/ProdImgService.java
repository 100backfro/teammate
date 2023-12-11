package com.api.backend.team.service.file;

import com.api.backend.team.data.type.ImgType;
import com.api.backend.team.service.file.impl.ImgStoreImpl;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("prod")
public class ProdImgService implements ImgStoreImpl {

  @Override
  public String uploadImg(MultipartFile multipartFile, ImgType imgType , String name) {
    return null;
  }

}
