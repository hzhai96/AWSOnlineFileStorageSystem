package com.onlineFileSystem.springboot.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {
    public AWSConfig config = new AWSConfig();

    @Bean
    public S3Client client() {
        return S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(config.credentials())
            .build();
    }
}
