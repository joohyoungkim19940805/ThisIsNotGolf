package com.hide_and_fps.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@ComponentScan(basePackages = "com.brazeMonitoringAssistant.web", basePackageClasses = { ThreadPoolTaskSchedulerRunner.class })
public class ThreadPoolTaskSchedulerConfig {
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix("monitoringAutoSaveThread");
        return threadPoolTaskScheduler;
    }
    
    /*
    @Bean
    public AmazonS3ClientBuilder amazonS3Builder() {
    	return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider( new BasicAWSCredentials(AwsS3Configuration.AWS_ACCESS_KEY_ID, AwsS3Configuration.AWS_SECRET_ACCESS_KEY) ))
                .withRegion(AwsS3Configuration.AWS_DEFAULT_REGION)
    }
    */
}
