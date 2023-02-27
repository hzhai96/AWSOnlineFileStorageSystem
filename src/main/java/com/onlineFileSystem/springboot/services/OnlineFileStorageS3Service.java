package com.onlineFileSystem.springboot.services;

import java.io.File;

import com.onlineFileSystem.springboot.clients.AwsS3Client;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;


public class OnlineFileStorageS3Service {
    public S3Client client = new AwsS3Client().client();

    public void uploadOject(String bucketName, String key, String path) {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        client.putObject(request, RequestBody.fromFile(new File(path)));

        S3Waiter waiter = client.waiter();
        HeadObjectRequest waitRequest = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        WaiterResponse<HeadObjectResponse> waiterResponse = waiter.waitUntilObjectExists(waitRequest);

        waiterResponse.matched().response().ifPresent(System.out::println);
    }

    public void deleteObject(String bucketName, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        client.deleteObject(request);

        S3Waiter waiter = client.waiter();
        HeadObjectRequest waitRequest = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        WaiterResponse<HeadObjectResponse> waiterResponse = waiter.waitUntilObjectNotExists(waitRequest);

        waiterResponse.matched().response().ifPresent(System.out::println);
    }
}
