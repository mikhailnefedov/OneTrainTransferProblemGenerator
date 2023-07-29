package com.github.onetraintransferproblemgenerator.persistence;

import com.github.onetraintransferproblemgenerator.controller.generation.expandv2.PrelimInformation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PrelimInformationRepository extends MongoRepository<PrelimInformation, ObjectId> {

    List<PrelimInformation> findAllByExperimentId(String experimentId);
}
