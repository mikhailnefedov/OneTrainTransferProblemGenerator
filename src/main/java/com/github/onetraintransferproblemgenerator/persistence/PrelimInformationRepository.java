package com.github.onetraintransferproblemgenerator.persistence;

import com.github.onetraintransferproblemgenerator.controller.generation.expandv2.PrelimInformation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrelimInformationRepository extends MongoRepository<PrelimInformation, ObjectId> {

    PrelimInformation findByExperimentId(String experimentId);
}
