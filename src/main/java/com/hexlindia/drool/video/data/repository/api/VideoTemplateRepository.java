package com.hexlindia.drool.video.data.repository.api;

import com.hexlindia.drool.common.data.doc.PostRef;
import com.hexlindia.drool.video.data.doc.VideoComment;
import com.hexlindia.drool.video.data.doc.VideoDoc;
import com.hexlindia.drool.video.dto.VideoCommentDto;
import com.hexlindia.drool.video.dto.VideoLikeUnlikeDto;
import com.hexlindia.drool.video.dto.VideoThumbnailDataAggregation;
import org.bson.types.ObjectId;

public interface VideoTemplateRepository {
    VideoDoc save(VideoDoc videoDoc);

    VideoDoc findByIdAndActiveTrue(String id);

    String saveVideoLikes(VideoLikeUnlikeDto videoLikeUnlikeDto);

    String deleteVideoLikes(VideoLikeUnlikeDto videoLikeUnlikeDto);

    VideoComment insertComment(PostRef postRef, VideoComment videoComment);

    boolean deleteComment(VideoCommentDto videoCommentDto);

    String saveCommentLike(VideoCommentDto videoCommentDto);

    String deleteCommentLike(VideoCommentDto videoCommentDto);

    VideoThumbnailDataAggregation getLatestThreeVideosByUser(String userId);

    boolean updateReviewId(ObjectId videoId, ObjectId reviewId);
}
