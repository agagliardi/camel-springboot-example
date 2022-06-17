package com.redhat.reproducer;

import org.springframework.beans.factory.FactoryBean;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

public class AWSClientFactory implements FactoryBean<AmazonS3Client> {
    private String endpoint;
    private String accessKey;
    private String secretKey;

    public AmazonS3Client getObject() throws Exception {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3Client conn = new AmazonS3Client(credentials);
        conn.setEndpoint(endpoint);
        return conn;
    }

    @Override
    public Class<?> getObjectType() {
        return AmazonS3Client.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

}
