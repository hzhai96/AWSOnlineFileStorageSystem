package com.onlineFileSystem.springboot.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesRequest;
import software.amazon.awssdk.services.s3.model.GetObjectAttributesResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

@Service
public class S3Service {
    private final String bucketName = "coen241projectfiles";
    @Autowired
    S3Client client;

    public void uploadOject(String key, String type, InputStream stream) throws S3Exception, AwsServiceException, SdkClientException, IOException {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(type)
            .build();

        client.putObject(request, RequestBody.fromInputStream(stream, stream.available()));

        WaiterResponse<HeadObjectResponse> waiterResponse = createWaiter(key);

        waiterResponse.matched().response().ifPresent(System.out::println);
    }

    public void createFoler(String key) throws S3Exception, AwsServiceException, SdkClientException, IOException {
        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        client.putObject(request, RequestBody.empty());

        WaiterResponse<HeadObjectResponse> waiterResponse = createWaiter(key);

        waiterResponse.matched().response().ifPresent(System.out::println);
    }

    public void deleteObject(String key) throws S3Exception, AwsServiceException, SdkClientException {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        
        client.deleteObject(request);

        WaiterResponse<HeadObjectResponse> waiterResponse = createWaiter(key);

        waiterResponse.matched().response().ifPresent(System.out::println);
    }

    public void copyObject(String sourceKey, String destinationKey) throws S3Exception, AwsServiceException, SdkClientException {
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

    public void renameObject(String key, String newKey) throws S3Exception, AwsServiceException, SdkClientException {
        copyObject(key, newKey);
        deleteObject(newKey);
    }

    public List<S3Object> listObjects(String prefix) throws S3Exception, AwsServiceException, SdkClientException, IOException {
        ListObjectsRequest request = ListObjectsRequest.builder()
            .bucket(bucketName)
            .prefix(prefix)
            .build();

        ListObjectsResponse response = client.listObjects(request);
        List<S3Object> objects = response.contents();

        return objects;
    }

    public List<String> listObjectsFromFile(String key) throws S3Exception, AwsServiceException, SdkClientException, IOException {
        String line;
        List<String> objects = new ArrayList<String>();
        GetObjectRequest request = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        byte[] bytes = client.getObjectAsBytes(request).asByteArray();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
        while ((line = reader.readLine()) != null) {
            objects.add(line);
        }

        return objects;
    }

    public void shareObject(String sender, String senderKey, String receiver) throws S3Exception, AwsServiceException, SdkClientException, IOException {
        String receiverSharedKey = "/" + receiver + "/shared/contents.txt";
        String senderSharingKey = "/" + sender + "/sharing/contetns.txt";

        GetObjectRequest receiverGetRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(receiverSharedKey)
            .build();
        PutObjectRequest receiverPutRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(receiverSharedKey)
            .contentType("plain/text")
            .build();
        GetObjectRequest senderGetRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(senderSharingKey)
            .build();
        PutObjectRequest senderPutRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(senderSharingKey)
            .contentType("plain/text")
            .build();
        GetObjectAttributesRequest attributesRequest = GetObjectAttributesRequest.builder()
            .bucket(bucketName)
            .key(senderKey)
            .build();

        GetObjectAttributesResponse getObjectAttributesResponse = client.getObjectAttributes(attributesRequest);
        String size = String.valueOf(getObjectAttributesResponse.objectSize());
        String newData = senderKey + "," + size + "\n";

        updateShareInfomation(receiverGetRequest, receiverPutRequest, newData);
        updateShareInfomation(senderGetRequest, senderPutRequest, newData);
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

    private void updateShareInfomation(GetObjectRequest getRequest, PutObjectRequest putObjectRequest, String newData) {        
        ResponseBytes<GetObjectResponse> objectBytes = client.getObjectAsBytes(getRequest);
        byte[] dataByteArray = objectBytes.asByteArray();
        byte[] newDataByteArray = newData.getBytes();

        byte[] finalDataByteArray = new byte[dataByteArray.length + newDataByteArray.length];
        System.arraycopy(dataByteArray, 0, finalDataByteArray, 0, dataByteArray.length);
        System.arraycopy(newDataByteArray, 0, finalDataByteArray, dataByteArray.length, newDataByteArray.length);

        client.putObject(putObjectRequest, RequestBody.fromBytes(finalDataByteArray));
    }
}
