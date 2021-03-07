package main.service;

import main.model.request.others.ProfileRequest;
import main.model.request.others.SettingsRequest;
import main.model.request.postids.CommentRequest;
import main.model.request.postids.PostModerationRequest;
import main.model.response.others.*;
import main.model.response.results.ResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GeneralService {
    Blog getBlogInfo();
//    Object imageUpload(MultipartFile multipartFile) throws IOException;
    ResponseEntity imageUpload(MultipartFile multipartFile) throws IOException;
    Object sendComment(CommentRequest request);
    TagsResponse getListOfTags(String query);
    ResultResponse postModeration(PostModerationRequest request);
    YearsPostsResponse numberOfPosts(Integer year);
    ResultResponse editProfile(ProfileRequest request);
    ResultResponse editProfileWithPhoto(ProfileRequest request)
            throws IOException;
    StatisticsResponse myStatistics();
    Object allStatistics();
    SettingsResponse getSettings();
    void putSettings(SettingsRequest request);
}
