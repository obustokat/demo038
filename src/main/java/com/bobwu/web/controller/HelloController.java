package com.bobwu.web.controller;

import com.bobwu.web.bean.Ancestor;
import com.bobwu.web.bean.Node;
import com.bobwu.web.service.impl.HelloServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/ancestor")
public class HelloController {

    @Autowired
    private HelloServiceImpl helloServiceimpl;
    final String TITLE = "宗親會族譜 (Genealogy Program)";
    final String RETURNURL = "ancestor/query/child/";

//    @GetMapping("/parentTree")
//    public String parentTree(Model model){
//        Map<Integer ,List<Node>> map = new HashMap<>();
//        helloServiceimpl.parentTree(map);
//        log.info("map = {}", map);
//        model.addAttribute("title" ,TITLE);
//        return "index";
//    }

    @GetMapping("/query")
    public String index(Model model){
        int parentId = 0;
        List<Ancestor> ancestorList = helloServiceimpl.queryDataByParentId(parentId);
        for(Ancestor ancestor : ancestorList){
            log.info(ancestor.toString());
        }
        model.addAttribute("title" ,TITLE);
        model.addAttribute("parentId" ,parentId);
        model.addAttribute("parents" ,"");
        model.addAttribute("persons" ,ancestorList);
        return "index2";
    }

    @GetMapping("/query/parent/{id}")
    public String queryParentBy(@PathVariable("id") Integer id , Model model){
        Ancestor ancestor = helloServiceimpl.queryDataById(id);
        Integer parentId = ancestor.getParentId();
        log.info("parentId = {}" ,parentId);
        List<Ancestor> ancestorList = helloServiceimpl.queryDataByParentId(parentId);
        for(Ancestor ancestor1 : ancestorList){
            log.info("queryParentBy ancestor1 = {}", ancestor1);
        }
        Map<String ,Object> map = new HashMap<>();
        map.put("parentId" ,parentId);
        helloServiceimpl.queryAllParent(map);
        new TreeMap<>(map);
        log.info("map = {}", map);

        model.addAttribute("title" ,TITLE);
        model.addAttribute("parents" ,map);
        model.addAttribute("persons" ,ancestorList);
        return "index2";
    }


    @GetMapping("/query/child/{parentId}")
    public String queryChildBy(@PathVariable("parentId") Integer id , Model model){
        List<Ancestor> ancestorList = helloServiceimpl.queryDataByParentId(id);
        for(Ancestor ancestor1 : ancestorList){
            log.info("ancestor={}",ancestor1);
        }
        Map<String ,Object> map = new TreeMap<>();
        map.put("parentId" ,id);
        helloServiceimpl.queryAllParent(map);
        log.info("map = {}", map);

        model.addAttribute("title" ,TITLE);
        model.addAttribute("parents" ,map);
        model.addAttribute("persons" ,ancestorList);
        return "index2";
    }

    @PostMapping("/insert")
    @ResponseBody
    public String insertOne(@RequestBody Ancestor ancestor){
        helloServiceimpl.insertOneData(ancestor);
        return RETURNURL+ancestor.getParentId();
    }

    @GetMapping("/queryBfUpdate")
    @ResponseBody
    public Ancestor queryBfUpdate(@RequestParam("id") Integer id){
        return helloServiceimpl.queryDataById(id);
    }

    @GetMapping("/queryBfMove")
    @ResponseBody
    public List<Node> queryBfMove(@RequestParam("id") Integer id, Model model){
        Integer parentId = helloServiceimpl.queryDataById(id).getParentId();
        log.info("id = {}" ,id);
        log.info("parentId = {}" ,parentId);
        List<Ancestor> ancestorList = helloServiceimpl.queryDataByParentId(parentId ,id);
        List<Node> nodeList = new ArrayList<>();
        for(Ancestor ancestor : ancestorList){
            if(!Objects.equals(ancestor.getId(), id)){
                nodeList.add(new Node(ancestor.getId() , ancestor.getName() , ancestor.getParentId()));
            }
        }
        return nodeList;
    }

    /**
     * 是否有子层
     * @param id
     * @return
     */
    @GetMapping("/queryBfDelete")
    @ResponseBody
    public boolean queryBfDelete(@RequestParam("id") Integer id){
        return !helloServiceimpl.queryDataByParentId(id).isEmpty();
    }

    @PostMapping("/update")
    @ResponseBody
    public String update(@RequestBody Ancestor ancestor){
        helloServiceimpl.updateOneData(ancestor);
        return RETURNURL+ancestor.getParentId();
    }

    @PostMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam("id") Integer id){
        Integer parentId = helloServiceimpl.queryDataById(id).getParentId();
        helloServiceimpl.deleteOneData(id);
        return RETURNURL+parentId;
    }

    @GetMapping("/move")
    @ResponseBody
    public String move(@RequestParam("id") Integer id ,@RequestParam("moveId") Integer moveId){
        helloServiceimpl.updateParent(id ,moveId);
        Integer parentId = helloServiceimpl.queryDataById(id).getParentId();
        return RETURNURL+parentId;
    }
}
