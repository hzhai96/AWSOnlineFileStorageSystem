package com.onlineFileSystem.springboot.configurations;

import org.springframework.beans.factory.annotation.Configurable;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;

@Configurable
public class AWSConfig {

    public AwsCredentials credentials() {
        AwsCredentials credentials = 
            AwsBasicCredentials.create("", "");
        return credentials;
    }
}
