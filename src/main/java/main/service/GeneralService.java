package main.service;

import main.request.*;
import main.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GeneralService {
    Blog getBlogInfo();
    Object imageUpload(MultipartFile multipartFile) throws IOException;
    BasicResponse sendComment(BasicRequest request);
    AdditionalResponse getListOfTags(String query);
    BasicResponse postModeration(BasicRequest request);
    AdditionalResponse numberOfPosts(Integer year);
    Object editProfile(BasicRequest request);
    Object editProfileWithPhoto(BasicRequest request) throws IOException;
    BasicResponse myStatistics();
    Object allStatistics();
    BasicResponse getSettings();
    void putSettings(AdditionalRequest request);
}
