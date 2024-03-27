package com.bobwu.web.service;

import com.bobwu.web.bean.Ancestor;
import org.bson.Document;

import java.util.List;

public interface HelloService {

    void insertOneData(Ancestor ancestor);
    // 这里可以使用MongoTemplate或者MongoRepository来操作数据库
    void updateOneData(Ancestor ancestor);
    void deleteOneData(Integer id);
    Ancestor queryDataById(Integer id);
    List<Ancestor> queryDataByParentId(Integer id);
}
