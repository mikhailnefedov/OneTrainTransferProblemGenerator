package com.github.onetraintransferproblemgenerator.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProblemInstanceRepository extends MongoRepository<ProblemInstance, ObjectId> {

    List<ProblemInstance> findAllByExperimentId(String experimentId);

    ProblemInstance findByExperimentIdAndInstanceId(String experimentId, String instanceId);
}
