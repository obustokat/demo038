package com.bobwu.web.service.impl;

import com.bobwu.web.bean.Ancestor;
import com.bobwu.web.service.HelloService;
import com.bobwu.web.utils.DateUtils;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HelloServiceImpl implements HelloService {
    private static final String COLLECTION_NAME = "mycollection";
    private static final String COLLECTION_SEQ = "mysequence";
    private final MongoOperations mongoOperations;

    @Autowired
    public HelloServiceImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Autowired
    private DateUtils dateUtils;

    /**
     * 自增主键
     * @param sequenceName
     * @return
     */
    private int getNextSequence(String sequenceName) {
        MongoCollection<Document> sequenceCollection = mongoOperations.getCollection(COLLECTION_SEQ);
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

    /**
     * Document to Bean
     * @param list
     * @param documents
     */
    private static void transList(List<Ancestor> list ,List<Document> documents){
        for (Document document : documents) {
            Ancestor ancestor = new Ancestor();
            ancestor.setId(document.getInteger("id"));
            ancestor.setName(document.getString("name"));
            ancestor.setTitle(document.getString("title"));
            ancestor.setAge(document.getInteger("age"));
            ancestor.setParentId(document.getInteger("parentId"));
            ancestor.setRemark(document.getString("remark"));
            ancestor.setStartDate(document.getString("startDate"));
            ancestor.setEndDate(document.getString("endDate"));
            ancestor.setCreateDate(document.getString("createDate"));
            ancestor.setUpdateDate(document.getString("updateDate"));
            ancestor.setSort(document.getInteger("sort"));
            ancestor.setIsDelete(document.getInteger("isDelete"));
            list.add(ancestor);
        }
    }

    /**
     * Document to Bean
     * @param ancestor
     * @param document
     */
    private static void transOne(Ancestor ancestor ,Document document){
        ancestor.setId(document.getInteger("id"));
        ancestor.setName(document.getString("name"));
        ancestor.setTitle(document.getString("title"));
        ancestor.setAge(document.getInteger("age"));
        ancestor.setParentId(document.getInteger("parentId"));
        ancestor.setRemark(document.getString("remark"));
        ancestor.setStartDate(document.getString("startDate"));
        ancestor.setEndDate(document.getString("endDate"));
        ancestor.setCreateDate(document.getString("createDate"));
        ancestor.setUpdateDate(document.getString("updateDate"));
        ancestor.setSort(document.getInteger("sort"));
        ancestor.setIsDelete(document.getInteger("isDelete"));
    }

    /**
     * insert Bean to Document
     * @param ancestor
     * @param document
     */
    private static void insertAppendOne(Ancestor ancestor ,Document document){
        document.append("id", ancestor.getId())
                .append("name", ancestor.getName())
                .append("title", ancestor.getTitle())
                .append("age", ancestor.getAge())
                .append("parentId", ancestor.getParentId())
                .append("remark", ancestor.getRemark())
                .append("startDate", ancestor.getStartDate())
                .append("endDate", ancestor.getEndDate())
                .append("createDate", ancestor.getCreateDate())
                .append("updateDate", ancestor.getUpdateDate())
                .append("isDelete", ancestor.getIsDelete())
                .append("sort", ancestor.getSort());
    }

    /**
     * update Bean to Document
     * @param ancestor
     * @param document
     */
    private static void updateAppendOne(Ancestor ancestor ,Document document){
        document.append("name", ancestor.getName())
                .append("title", ancestor.getTitle())
                .append("age", ancestor.getAge())
                .append("remark", ancestor.getRemark())
                .append("startDate", ancestor.getStartDate())
                .append("endDate", ancestor.getEndDate())
                .append("updateDate", ancestor.getUpdateDate())
                .append("isDelete", ancestor.getIsDelete());
    }

    /**
     * insert one data
     * @param ancestor
     */
    @Override
    public void insertOneData(Ancestor ancestor) {
        MongoCollection<Document> collection = mongoOperations.getCollection(COLLECTION_NAME);
        Integer nextId = getNextSequence("userid");
        log.info("nextId = {}",nextId);
        ancestor.setId(nextId);
        ancestor.setCreateDate(dateUtils.now());
        ancestor.setUpdateDate(null);
        ancestor.setIsDelete(0);
//        log.info("sort1={}",ancestor.getSort());

        List<Ancestor> ancestorList = queryDataByParentId(ancestor.getParentId());
        var sort = !ancestorList.isEmpty() ? ancestorList.size() + 1 : 1;
        ancestor.setSort(sort);

        try {
            Document document = new Document();
            insertAppendOne(ancestor ,document);

            collection.insertOne(document);
            log.info("新增成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update one data
     * @param ancestor
     */
    @Override
    public void updateOneData(Ancestor ancestor) {
        MongoCollection<Document> collection = mongoOperations.getCollection(COLLECTION_NAME);
        ancestor.setUpdateDate(dateUtils.now());
        ancestor.setIsDelete(0);
        try {
            log.info("id = {}" , ancestor.getId());
            // 创建查询条件
            Document query = new Document("id", ancestor.getId());
            // 创建更新操作
            Document document = new Document();
            updateAppendOne(ancestor ,document);
            Document update = new Document("$set", document);
            // 执行更新
            collection.updateOne(query, update);
            log.info("文档更新成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOneData(Integer id) {
        MongoCollection<Document> collection = mongoOperations.getCollection(COLLECTION_NAME);
        try {
            Document query = new Document("id", id);
            Document update = new Document("$set" ,new Document("isDelete" , 1));
            collection.updateOne(query, update);
            log.info("成员删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Ancestor queryDataById(Integer id) {
        MongoCollection<Document> collection = mongoOperations.getCollection(COLLECTION_NAME);
        Ancestor ancestor = new Ancestor();
        try {
            Document query = new Document();
            query.append("id", id);
            query.append("isDelete", 0);
            Document result = collection.find(query).first();
            if (result == null) {
                // 如果返回的 sequence 对象为空，可以根据需要进行相应的处理
                throw new RuntimeException("result not found");
            }
            transOne(ancestor ,result);
            return ancestor;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Ancestor> queryDataByParentId(Integer id) {
        log.info("------------queryDataByParentId start------------");
        log.info("parentId = {}",id);
        MongoCollection<Document> collection = mongoOperations.getCollection(COLLECTION_NAME);
        List<Document> resultList = new ArrayList<>();
        List<Ancestor> ancestorList = new ArrayList<>();
        try {
            Document query = new Document();
            query.append("parentId", id);
            query.append("isDelete", 0);
//            log.info("query={}",query);
            collection.find(query).into(resultList);
//            log.info("resultList={}",resultList);
            transList(ancestorList ,resultList);
//            log.info("------------queryDataByParentId end------------");
            return ancestorList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}