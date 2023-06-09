package com.github.onetraintransferproblemgenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class OneTrainTransferProblemGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneTrainTransferProblemGeneratorApplication.class, args);
    }

}
