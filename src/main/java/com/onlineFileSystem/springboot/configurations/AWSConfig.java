package com.onlineFileSystem.springboot.configurations;

import org.springframework.beans.factory.annotation.Configurable;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

@Configurable
public class AWSConfig {

    public AwsCredentialsProvider credentials() {
        return DefaultCredentialsProvider.builder().build();
    }
}
