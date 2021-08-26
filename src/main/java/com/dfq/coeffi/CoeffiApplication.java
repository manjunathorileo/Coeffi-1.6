package com.dfq.coeffi;

import com.dfq.coeffi.headCountGE.RawDataReaderForCurrentDay;
import com.dfq.coeffi.policy.document.FileStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class CoeffiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CoeffiApplication.class, args);
    }



    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CoeffiApplication.class);
    }
}