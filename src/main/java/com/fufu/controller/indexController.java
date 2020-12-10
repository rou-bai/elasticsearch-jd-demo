package com.fufu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class indexController {
    @GetMapping({"/index", "index"})
    public String index(){
        return "index";
    }

}