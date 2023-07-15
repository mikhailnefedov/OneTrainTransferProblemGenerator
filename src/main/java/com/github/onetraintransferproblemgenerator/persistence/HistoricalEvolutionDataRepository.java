package com.github.onetraintransferproblemgenerator.persistence;

import com.github.onetraintransferproblemgenerator.solvers.evolutionary.models.HistoricalEvolutionData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistoricalEvolutionDataRepository extends MongoRepository<HistoricalEvolutionData, ObjectId> {

    List<HistoricalEvolutionData> findAllByExperimentId(String experimentId);
}
