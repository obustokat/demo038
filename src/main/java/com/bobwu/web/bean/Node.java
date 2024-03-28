package com.bobwu.web.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Node{
    private Integer id;
    private String name;
    private Integer parentId;
}
