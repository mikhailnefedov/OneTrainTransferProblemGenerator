package com.github.onetraintransferproblemgenerator.persistence;

import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.HistoricalEvolutionData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoricalEvolutionDataRepository extends MongoRepository<HistoricalEvolutionData, ObjectId> {
}
