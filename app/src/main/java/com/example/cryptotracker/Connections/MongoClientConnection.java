package com.example.cryptotracker.Connections;

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
    public static void main(String[] args) {
        String connectionString = "mongodb+srv://username:<password>@cluster0.7kvlfmv.mongodb.net/?retryWrites=true&w=majority";

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        try {
            mongoClient = MongoClients.create(settings);
            database = mongoClient.getDatabase("main");

//            // Insert a document
//            Document newDocument = new Document("name", "John Doe")
//                    .append("age", 30)
//                    .append("city", "New York");
//            insertDocument(newDocument, "myCollection");
//
//            // Find documents
//            List<Document> foundDocuments = findDocuments("myCollection");
//            for (Document doc : foundDocuments) {
//                System.out.println(doc.toJson());
//            }
        } catch (MongoException e) {
            e.printStackTrace();
        } finally {
            if (mongoClient != null) {
                mongoClient.close();
            }
        }
    }

    public static void insertDocument(Document document, String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.insertOne(document);
    }

    public static List<Document> findDocuments(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> documents = new ArrayList<>();
        collection.find().into(documents);
        return documents;
    }
}