package com.cyprinus.matrix.service;

import com.cyprinus.matrix.Config;
import com.cyprinus.matrix.entity.Picture;
import com.cyprinus.matrix.exception.BadRequestException;
import com.cyprinus.matrix.exception.ServerInternalException;
import com.cyprinus.matrix.repository.PictureRepository;
import com.cyprinus.matrix.util.OSSUtil;
import com.cyprinus.matrix.util.ObjectUtil;
import io.minio.MinioClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.HashMap;

@SuppressWarnings("unchecked")
@Service
public class PictureService {

    private final
    Config config;

    private final
    ObjectUtil objectUtil;

    private final
    PictureRepository pictureRepository;

    private final
    OSSUtil ossUtil;

    @Autowired
    public PictureService(OSSUtil ossUtil, PictureRepository pictureRepository, ObjectUtil objectUtil, Config config) {
        this.ossUtil = ossUtil;
        this.pictureRepository = pictureRepository;
        this.objectUtil = objectUtil;
        this.config = config;
    }

    @Transactional(rollbackOn = Throwable.class)
    public HashMap<String, Object> uploadNewPicture(MultipartFile file) throws ServerInternalException, BadRequestException {
        if (!file.isEmpty()) {
            try {
                String md5 = DigestUtils.md5Hex(file.getInputStream());
                Picture picture = new Picture();
                picture.setHash(md5);
                pictureRepository.save(picture);
                MinioClient minioClient = ossUtil.getMinioClient();
                minioClient.putObject("matrix", picture.get_id(), file.getInputStream(), "image/png");
                picture.setUrl(picture.get_id());
                pictureRepository.saveAndFlush(picture);
                picture.setUrl(config.getImagePathBase() + picture.get_id());
                return objectUtil.object2map(picture);
            } catch (Exception e) {
                throw new ServerInternalException(e);
            }
        } else throw new BadRequestException("空的文件！");
    }

}
