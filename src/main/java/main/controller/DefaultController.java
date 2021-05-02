package main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;

@Controller
public class DefaultController {
    @Autowired
    private ServletContext servletContext;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping(
            method = {RequestMethod.OPTIONS, RequestMethod.GET},
            value = "/**/{path:[^\\.]*}")
    public String redirectToIndex() {
        return "index";
    }
}
