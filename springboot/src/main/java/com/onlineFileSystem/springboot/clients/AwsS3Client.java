package com.onlineFileSystem.springboot.clients;

import org.springframework.context.annotation.Bean;

import com.onlineFileSystem.springboot.configurations.AWSConfig;

import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class AwsS3Client {
    public AWSConfig config = new AWSConfig();

    @Bean
    public S3Client client() {
        // TODO: StaticCredentialsProvider should be replaced for security issue
        return S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(StaticCredentialsProvider.create(config.credentials()))
            .build();
    }
}
