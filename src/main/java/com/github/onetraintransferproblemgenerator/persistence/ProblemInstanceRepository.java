package com.github.onetraintransferproblemgenerator.persistence;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProblemInstanceRepository extends MongoRepository<ProblemInstance, ObjectId> {
}
