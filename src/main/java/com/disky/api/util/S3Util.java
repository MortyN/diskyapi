package com.disky.api.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//@Service
//class S3Util {
//
//    val s3Client = S3Client { region = "eu-north-1" }
//
//    suspend fun uploadPhoto(keyname: String?, file: File?): String {
//        val newObjectMetadata = mutableMapOf<String, String>()
//        newObjectMetadata["aws-sdk-kotlin"] = "test"
//        val fileToUpload = Files.readAllBytes(
//                Paths.get(file?.absolutePath ?: ""))
//        val byteStreamOfFile = ByteStream.fromBytes(fileToUpload)
//        val putObjectResponse = s3Client.putObject {
//            bucket = bucketName
//            key = keyname
//            metadata = newObjectMetadata
//            body = byteStreamOfFile
//        }
//        return "Successfully uploaded the file with the etag: ${putObjectResponse.eTag}"
//    }
//
//    companion object {
//        private const val bucketName = "prod-disky-images"
//    }
//}


@Service
public class S3Util {
    static Regions clientRegion = Regions.EU_NORTH_1;
    static String bucketName = "prod-disky-images";
    String stringObjKeyName = "*** String object key name ***";
    String fileObjKeyName = "*** File object key name ***";

    public static File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        multipart.transferTo(convFile);
        return convFile;
    }

    public static void s3UploadPhoto(MultipartFile tempfile, String fileName) {
        if (tempfile == null)return;
        try {
//            InputStream stream = new ByteArrayInputStream(file.getBytes());
            File file = multipartToFile(tempfile, fileName+".jpeg");
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();

            // Upload a file as a new object with ContentType and title specified.
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/*");
            metadata.addUserMetadata("title", "someTitle");

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, file.getName(),file).withCannedAcl(CannedAccessControlList.PublicRead);
//            s3Client.putObject(bucketName, fileName, stream, metadata);
            s3Client.putObject(putObjectRequest);

        } catch (
                AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
