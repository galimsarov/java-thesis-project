package main.controller;

import main.utils.MediaTypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.io.FileInputStream;
import java.io.IOException;

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

    @GetMapping("/img")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String fileName) throws IOException {
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, fileName);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(fileName));
        return ResponseEntity.ok().contentType(mediaType).body(resource);
    }
}
