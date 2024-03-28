package com.bobwu.web.service;

import com.bobwu.web.bean.Ancestor;
import com.bobwu.web.bean.Node;
import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface HelloService {

    Integer insertOneData(Ancestor ancestor);
    // 这里可以使用MongoTemplate或者MongoRepository来操作数据库
    void updateOneData(Ancestor ancestor);
    void updateParent(Integer id ,Integer moveId);
    void deleteOneData(Integer id);
    List<Ancestor> queryAll();
    void parentTree(Map<Integer ,List<Node>> map ,StringBuffer stringBuffer);
    Ancestor queryDataById(Integer id);
    List<Ancestor> queryDataByParentId(Integer id);
    List<Ancestor> queryDataByParentId(Integer id ,Integer myId);
    void queryAllParent(Map<String ,Object> map);
}
