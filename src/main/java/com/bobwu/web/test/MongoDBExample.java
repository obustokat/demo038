package com.bobwu.web.test;


import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;

public class MongoDBExample {

    public static void main(String[] args) {
        // 连接到 MongoDB 服务器
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // 选择数据库
            MongoDatabase database = mongoClient.getDatabase("mydatabase");
            // 选择集合
            MongoCollection<Document> collection = database.getCollection("mycollection");
            MongoCollection<Document> sequenceCollection = database.getCollection("mysequence");

//            insert(collection);
//            update(collection);
//            delete(collection,4);
//            deleteMany(collection,4);
            query(collection);

            // Get the next sequence value for a sequence named "user_id"
//            int nextId = getNextSequence(sequenceCollection ,"user_id");
//            System.out.println("Next user ID: " + nextId);

            dropAll(collection);
//            dropAll(sequenceCollection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void dropAll(MongoCollection<Document> collection){
        // Drop all data from the collection
        collection.deleteMany(new Document());
    }
    public static int getNextSequence(MongoCollection<Document> sequenceCollection ,String sequenceName) {

        // Find the document for the sequence
        Document sequenceDoc = sequenceCollection.findOneAndUpdate(
                new Document("_id", sequenceName),
                new Document("$inc", new Document("value", 1)),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER)
        );

        // If the document doesn't exist, create it
        if (sequenceDoc == null) {
            sequenceDoc = new Document("_id", sequenceName).append("value", 1);
            sequenceCollection.insertOne(sequenceDoc);
        }

        // Return the next sequence value
        return sequenceDoc.getInteger("value");
    }

    public static void query(MongoCollection<Document> collection ){
        // 查询集合中的所有文档
        FindIterable<Document> documents = collection.find();

        // 迭代并输出文档内容
        for (Document doc : documents) {
            System.out.println(doc.toJson());
        }
    }

    public static void insert(MongoCollection<Document> collection){
        Document insert = new Document();
        insert.append("id",8);
        insert.append("name","zxc");
        insert.append("parentId",2);
        collection.insertOne(insert);
        System.out.println("新增成功！");
    }
    public static void update(MongoCollection<Document> collection){
        try {
            // 创建查询單個条件
            Document query = new Document("id", 8);

            // 多個條件
//            Document query = new Document();
//            query.append("name","qweml;");
//            query.append("title","122");

            // 创建更新操作
            Document update = new Document("$set", new Document("isDelete", 1));
//            Document updateDetail = new Document();
//            updateDetail.append("id",5);
//            updateDetail.append("parentId",3);
//            Document update = new Document("$set", updateDetail);

            // 执行更新
            collection.updateOne(query, update);
            System.out.println("文档更新成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void delete(MongoCollection<Document> collection ,Integer id){
        collection.deleteOne(Filters.eq("id", id));
    }

    public static void deleteMany(MongoCollection<Document> collection ,Integer id){
        collection.deleteMany(Filters.eq("id", id));
    }
}
