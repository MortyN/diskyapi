package com.disky.api.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.disky.api.Exceptions.UserImageUploadException;
import com.disky.api.controller.UserController;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.logging.Logger;

@Service
public class S3Util {
    static AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.EU_NORTH_1)
            .build();
    static String bucketName = System.getenv("DISKYS3NAME");

    public static File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        multipart.transferTo(convFile);
        return convFile;
    }

    public static void s3UploadPhoto(MultipartFile tempFile, String fileName) throws UserImageUploadException {
        Logger log = Logger.getLogger(String.valueOf(S3Util.class));
        try {
            File file = multipartToFile(tempFile, fileName+".jpeg");
            log.info(System.getenv("AWS_ACCESS_KEY_ID"));
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getName(),file).withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(putObjectRequest);
            log.info("successfully uploaded image");
        } catch (SdkClientException | IOException e) {
            throw new UserImageUploadException(e.getMessage());
        }
    }

    public static void s3DeletePhoto(String keyName) throws UserImageUploadException {
        try {
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
        } catch (SdkClientException e) {
             throw new UserImageUploadException(e.getMessage());
        }
    }

}
