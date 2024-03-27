package com.bobwu.web.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Node{
    private int id;
    private String name;
    private int parentId;
}
