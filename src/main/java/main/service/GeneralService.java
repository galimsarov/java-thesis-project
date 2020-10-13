package main.service;

import main.request.CommentRequest;
import main.response.AbstractResponse;
import main.response.Blog;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GeneralService {
    Blog getBlogInfo();
    Object imageUpload(MultipartFile multipartFile) throws IOException;
    AbstractResponse sendComment(CommentRequest commentRequest);
}
