package com.api.backend.team.service.file;

import com.api.backend.team.data.type.ImgType;
import com.api.backend.team.service.file.impl.ImgStoreImpl;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Profile("dev")
public class LocalImgService implements ImgStoreImpl {

  private static final String path = System.getProperty("user.dir") + "/img/";
  @Override
  public String uploadImg(MultipartFile multipartFile, ImgType imgType , String name) {
    try{
      String reFileName =  imgType + "_" + name + "_" + multipartFile.getOriginalFilename();
      File directory = new File(path);

      if (!directory.exists()) {
        directory.mkdirs();
      }

      multipartFile.transferTo(new File(directory, reFileName));
      return path + reFileName;
    }catch (Exception e){
      log.warn("이미지 저장을 실패 했습니다. : " + e.getMessage());
    }
    return null;
  }
}
