package main.controller;

import main.response.Blog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    @Value("${title}")
    private String title;

    @Value("${subtitle}")
    private String subtitle;

    @Value("${phone}")
    private String phone;

    @Value("${email}")
    private String email;

    @Value("${copyright}")
    private String copyright;

    @Value("${copyrightFrom}")
    private String copyrightFrom;

    private Blog getBlogInfo() {
        return new Blog(title, subtitle, phone, email, copyright, copyrightFrom);
    }

    @GetMapping("/init")
    public Blog getCommonData() {
        return getBlogInfo();
    }
}
