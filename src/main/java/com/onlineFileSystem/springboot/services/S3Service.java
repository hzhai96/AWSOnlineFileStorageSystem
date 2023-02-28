package com.onlineFileSystem.springboot.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.onlineFileSystem.springboot.clients.AwsS3Client;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;


public class S3Service {
    private final String bucketName = "coen241projectfiles";
    private S3Client client = new AwsS3Client().client();

    public void uploadOject(String key, String type, InputStream stream) 
        throws S3Exception, AwsServiceException, SdkClientException, IOException {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(type)
            .build();

        client.putObject(request, RequestBody.fromInputStream(stream, stream.available()));

        WaiterResponse<HeadObjectResponse> waiterResponse = createWaiter(key);

        waiterResponse.matched().response().ifPresent(System.out::println);
    }

    public void createFoler(String key) 
        throws S3Exception, AwsServiceException, SdkClientException, IOException {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        client.putObject(request, RequestBody.empty());

        WaiterResponse<HeadObjectResponse> waiterResponse = createWaiter(key);

        waiterResponse.matched().response().ifPresent(System.out::println);
    }

    public void deleteObject(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        client.deleteObject(request);

        WaiterResponse<HeadObjectResponse> waiterResponse = createWaiter(key);

        waiterResponse.matched().response().ifPresent(System.out::println);
    }

    public void copyObject(String sourceKey, String destinationKey) {
        CopyObjectRequest request = CopyObjectRequest.builder()
            .sourceBucket(bucketName)
            .sourceKey(sourceKey)
            .destinationBucket(bucketName)
            .destinationKey(destinationKey)
            .build();
        
            client.copyObject(request);

            WaiterResponse<HeadObjectResponse> waiterResponse = createWaiter(destinationKey);

            waiterResponse.matched().response().ifPresent(System.out::println);
    }

    public List<S3Object> listObjects(String prefix) {
        ListObjectsRequest request = ListObjectsRequest.builder()
            .bucket(bucketName)
            .prefix(prefix)
            .build();

        ListObjectsResponse response = client.listObjects(request);
        List<S3Object> objects = response.contents();

        return objects;
    }

    private WaiterResponse<HeadObjectResponse> createWaiter(String key) {
        S3Waiter waiter = client.waiter();
        HeadObjectRequest waitRequest = HeadObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        WaiterResponse<HeadObjectResponse> waiterResponse = waiter.waitUntilObjectNotExists(waitRequest);

        return waiterResponse;
    }
}
