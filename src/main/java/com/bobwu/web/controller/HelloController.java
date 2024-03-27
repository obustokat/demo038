package com.bobwu.web.controller;

import com.bobwu.web.bean.Ancestor;
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

    /**
     * TODO: 修改/刪除
     */
    @Autowired
    private HelloServiceImpl helloServiceimpl;
    final String TITLE = "宗親會族譜 (Genealogy Program)";
    final String RETURNURL = "ancestor/query/child/";

    @GetMapping("/query")
    public String index(Model model){
        int parentId = 0;
        List<Ancestor> ancestorList = helloServiceimpl.queryDataByParentId(parentId);
        for(Ancestor ancestor : ancestorList){
            log.info(ancestor.toString());
        }
        model.addAttribute("title" ,TITLE);
        model.addAttribute("parentId" ,parentId);
        model.addAttribute("parentIds" ,"");
        model.addAttribute("parentNames" ,"");
        model.addAttribute("persons" ,ancestorList);
        return "index";
    }

    @GetMapping("/query/parent/{id}")
    public String queryParentBy(@PathVariable("id") Integer id , Model model){
        log.info("----------------HelloController queryParentBy id = {}---------------",id);
        Ancestor ancestor = helloServiceimpl.queryDataById(id);
        Integer parentId = ancestor.getParentId();
        List<Ancestor> ancestorList = helloServiceimpl.queryDataByParentId(parentId);

        Map<String ,Object> map = new HashMap<>();
        List<Integer> parentIdList = new ArrayList<>();
        List<String> parentNameList = new ArrayList<>();
        map.put("id",parentId);
        map.put("parentIds",parentIdList);
        map.put("parentNames",parentNameList);
        loopParnet(map); // 回上層用

        model.addAttribute("title" ,TITLE);
        log.info("parentIds={} ,parentNames={}", map.get("parentIds") , map.get("parentNames"));
        model.addAttribute("parentIds" ,map.get("parentIds"));
        model.addAttribute("parentNames" ,map.get("parentNames"));
        model.addAttribute("persons" ,ancestorList);
        return "index";
    }


    @GetMapping("/query/child/{parentId}")
    public String queryChildBy(@PathVariable("parentId") Integer id , Model model){
        log.info("----------------HelloController queryChildBy id = {}---------------", id);
        List<Ancestor> ancestorList = helloServiceimpl.queryDataByParentId(id);
        for(Ancestor ancestor1 : ancestorList){
            log.info("ancestor={}",ancestor1);
        }
        Map<String ,Object> map = new HashMap<>();
        List<Integer> parentIdList = new ArrayList<>();
        List<String> parentNameList = new ArrayList<>();
        map.put("id",id);
        map.put("parentIds",parentIdList);
        map.put("parentNames",parentNameList);
        loopParnet(map); // 回上層用

        model.addAttribute("title" ,TITLE);
        log.info("parentIds = {} ,parentNames = {}", map.get("parentIds") , map.get("parentNames"));
        model.addAttribute("parentIds" ,map.get("parentIds"));
        model.addAttribute("parentNames" ,map.get("parentNames"));
        model.addAttribute("persons" ,ancestorList);
        return "index";
    }

    @PostMapping("/insert")
    @ResponseBody
    public String insertOne(@RequestBody Ancestor ancestor){
        log.info("----------------HelloController insertOne ancestor = {}---------------", ancestor);
        helloServiceimpl.insertOneData(ancestor);
        return RETURNURL+ancestor.getParentId();
    }

    @GetMapping("/queryBfUpdate")
    @ResponseBody
    public Ancestor queryBfUpdate(@RequestParam("id") Integer id){
        log.info("----------------HelloController queryBfUpdate id = {}---------------", id);
        return helloServiceimpl.queryDataById(id);
    }

    @PostMapping("/update")
    @ResponseBody
    public String update(@RequestBody Ancestor ancestor){
        Integer id = ancestor.getId();
        log.info("----------------HelloController update ancestor = {}---------------", id);
        helloServiceimpl.updateOneData(ancestor);
        return RETURNURL+ancestor.getParentId();
    }

    @PostMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam("id") Integer id){
        log.info("----------------HelloController delete id = {}---------------", id);
        Integer parentId = helloServiceimpl.queryDataById(id).getParentId();
        helloServiceimpl.deleteOneData(id);
        return RETURNURL+parentId;
    }

    // 階層
    private void loopParnet(Map<String ,Object> map){
        Integer id = (Integer) map.get("id");
        log.info("----------------HelloController loopParnet id = {}---------------", id);
        List<Integer> parentIdList = (List<Integer>) map.getOrDefault("parentIds", Collections.emptyList());
        List<String> parentNameList = (List<String>) map.getOrDefault("parentNames", Collections.emptyList());
        Ancestor ancestor = helloServiceimpl.queryDataById(id);
        var i = ancestor.getId();
        var parentId = ancestor.getParentId();
        var parentName = ancestor.getName();
        log.info("parentId={} ,parentName={}", parentId , parentName);
//        if(parentId != 0){
//            loopParnet(map);
//        }
        parentIdList.add(i);
        parentNameList.add(parentName);
        map.put("parentIds",parentIdList);
        map.put("parentNames",parentNameList);
    }
}
