package main.service;

import main.request.CommentRequest;
import main.request.EditProfileWithPasswordRequest;
import main.request.EditProfileWithPhotoRequest;
import main.request.PostModerationRequest;
import main.response.AbstractResponse;
import main.response.Blog;
import main.response.SettingsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GeneralService {
    Blog getBlogInfo();
    Object imageUpload(MultipartFile multipartFile) throws IOException;
    AbstractResponse sendComment(CommentRequest commentRequest);
    AbstractResponse getListOfTags(String query);
    AbstractResponse postModeration(PostModerationRequest request);
    AbstractResponse numberOfPosts(Integer year);
    AbstractResponse editProfile(EditProfileWithPasswordRequest request);
    AbstractResponse editProfile(
            EditProfileWithPhotoRequest request) throws IOException;
    SettingsResponse getSettings();
    AbstractResponse myStatistics();
    Object allStatistics();
}
