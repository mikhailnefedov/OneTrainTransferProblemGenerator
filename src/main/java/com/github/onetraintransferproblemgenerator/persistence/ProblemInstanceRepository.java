package com.github.onetraintransferproblemgenerator.persistence;

import com.mongodb.client.MongoDatabase;

public class ProblemInstanceRepository extends BaseMongoRepository<ProblemInstance> {

    public ProblemInstanceRepository(MongoDatabase mongoDB) {
        super(mongoDB, "problemInstances", ProblemInstance.class);
    }
}
