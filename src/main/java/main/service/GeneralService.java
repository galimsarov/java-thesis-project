package main.service;

import main.request.others.ProfileRequest;
import main.request.others.SettingsRequest;
import main.request.postids.CommentRequest;
import main.request.postids.PostModerationRequest;
import main.response.others.*;
import main.response.results.ResultResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GeneralService {
    Blog getBlogInfo();
    Object imageUpload(MultipartFile multipartFile) throws IOException;
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
