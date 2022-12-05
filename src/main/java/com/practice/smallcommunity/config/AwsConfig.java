package com.practice.smallcommunity.config;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class AwsConfig {

    @Value("${cloud.aws.s3.profile}")
    private String s3Profile;

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
            .withCredentials(new ProfileCredentialsProvider(s3Profile))
            .withRegion(Regions.AP_NORTHEAST_2)
            .build();
    }
}
