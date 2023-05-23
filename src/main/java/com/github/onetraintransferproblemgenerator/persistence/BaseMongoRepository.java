package com.github.onetraintransferproblemgenerator.persistence;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class BaseMongoRepository<T> {

    protected MongoCollection<T> collection;

    public BaseMongoRepository(MongoDatabase mongoDatabase, String collectionName, Class<T> documentClass) {
        collection = mongoDatabase.getCollection(collectionName, documentClass);
    }

    public MongoCollection<T> getCollection() { return collection; }
}

