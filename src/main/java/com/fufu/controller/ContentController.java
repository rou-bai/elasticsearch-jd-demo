package com.fufu.controller;

import com.fufu.pojo.Content;
import com.fufu.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class ContentController {
    @Autowired
    private ContentService contentService;

    @GetMapping("/parse/{keyword}")
    public Boolean insertKeyword(@PathVariable(name="keyword") String keyword) throws Exception{
        return contentService.parseContent(keyword);
    }

    @GetMapping("/")
    public String searchKeyword(@RequestParam(name="keyword", required = false) String keyword,
                                                   @RequestParam(name="pageFrom", defaultValue = "0") Integer pageFrom,
                                                   @RequestParam(name="pageSize", defaultValue = "20") Integer pageSize,
                                                   Model model) throws IOException {
        if(keyword == "" || keyword == null){
            return "index";
        }
        List<Content> lists = contentService.search(keyword, pageFrom, pageSize);
        model.addAttribute("contents", lists);

        return "index";
    }
}
