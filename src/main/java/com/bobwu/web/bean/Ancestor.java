package com.bobwu.web.bean;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Ancestor")
@Data
public class Ancestor {
    @Id
    private Integer id; // 自增ID
    private String name;
    private String title; //身份别ex:長子、次子
    private Integer age;
    private Integer parentId;
//    private Integer childId;
    private String remark; //備註
    private String startDate; // 生
    private String endDate; // 歿
    private String createDate;
    private String updateDate;
    private Integer sort;
    private Integer isDelete;
}
