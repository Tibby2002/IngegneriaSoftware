package com.example.cryptotracker.Connections;

import static com.mongodb.client.model.Filters.eq;

import android.util.Log;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoClientConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public MongoClientConnection() {
        String connectionString = "mongodb+srv://username:Password1@cluster0.7kvlfmv.mongodb.net/?retryWrites=true&w=majority";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("main");
    }

    public void insertDocument(Document document, String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.insertOne(document);
    }

    public List<Document> findDocuments(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> documents = new ArrayList<>();
        collection.find().into(documents);
        return documents;
    }

    public Document findUser(String email) {
        Log.d("database", "kek" + database);
        MongoCollection<Document> collection = database.getCollection("users");
        List<Document> documents = new ArrayList<>();
        collection.find(eq("email", email)).into(documents);
        if (documents.size() > 1) {
            throw new RuntimeException("Duplicate email");
        }
        return documents.get(0);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}