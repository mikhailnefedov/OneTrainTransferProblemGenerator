package com.github.onetraintransferproblemgenerator.persistence;

import com.github.onetraintransferproblemgenerator.controller.generation.expandfeatures.ConflictEvolutionData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConflictEvolutionDataRepository extends MongoRepository<ConflictEvolutionData, ObjectId> {
}
