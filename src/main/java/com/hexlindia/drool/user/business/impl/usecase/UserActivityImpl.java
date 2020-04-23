package com.hexlindia.drool.user.business.impl.usecase;

import com.hexlindia.drool.common.data.doc.CommentRef;
import com.hexlindia.drool.common.data.doc.ReplyRef;
import com.hexlindia.drool.discussion.data.doc.DiscussionTopicDoc;
import com.hexlindia.drool.product.data.doc.ReviewDoc;
import com.hexlindia.drool.user.business.api.usecase.UserActivity;
import com.hexlindia.drool.user.data.repository.api.UserActivityRepository;
import com.hexlindia.drool.video.data.doc.VideoDoc;
import com.hexlindia.drool.video.dto.VideoCommentDto;
import com.hexlindia.drool.video.dto.VideoLikeUnlikeDto;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class UserActivityImpl implements UserActivity {

    private final UserActivityRepository userActivityRepository;


    public UserActivityImpl(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @Override
    public UpdateResult addVideo(VideoDoc videoDoc) {
        return this.userActivityRepository.addVideo(videoDoc);
    }

    @Override
    public UpdateResult addVideoLike(VideoLikeUnlikeDto videoLikeUnlikeDto) {
        return this.userActivityRepository.addVideoLike(videoLikeUnlikeDto);
    }

    @Override
    public UpdateResult deleteVideoLike(VideoLikeUnlikeDto videoLikeUnlikeDto) {
        return this.userActivityRepository.deleteVideoLike(videoLikeUnlikeDto);
    }

    @Override
    public UpdateResult addVideoComment(ObjectId userId, CommentRef commentRef) {
        return this.userActivityRepository.addVideoComment(userId, commentRef);
    }

    @Override
    public UpdateResult updateVideoComment(ObjectId userId, CommentRef commentRef) {
        return this.userActivityRepository.updateVideoComment(userId, commentRef);
    }

    @Override
    public UpdateResult deleteVideoComment(VideoCommentDto videoCommentDto) {
        return this.userActivityRepository.deleteVideoComment(videoCommentDto);
    }

    @Override
    public UpdateResult addCommentLike(VideoCommentDto videoCommentDto) {
        return this.userActivityRepository.addCommentLike(videoCommentDto);
    }

    @Override
    public UpdateResult deleteCommentLike(VideoCommentDto videoCommentDto) {
        return this.userActivityRepository.deleteCommentLike(videoCommentDto);
    }

    @Override
    public UpdateResult addTextReview(ReviewDoc reviewDoc) {
        return this.userActivityRepository.addTextReview(reviewDoc);
    }

    @Override
    public UpdateResult addDiscussion(DiscussionTopicDoc discussionTopicDoc) {
        return this.userActivityRepository.addDiscussion(discussionTopicDoc);
    }

    @Override
    public UpdateResult addDiscussionReply(ObjectId userId, ReplyRef replyRef) {
        return this.userActivityRepository.addDiscussionReply(userId, replyRef);
    }

    @Override
    public UpdateResult updateDiscussionReply(ObjectId userId, ReplyRef replyRef) {
        return this.userActivityRepository.updateDiscussionReply(userId, replyRef);
    }

    @Override
    public UpdateResult deleteDiscussionReply(ObjectId userId, ObjectId replyId) {
        return this.userActivityRepository.deleteDiscussionReply(userId, replyId);
    }

    @Override
    public UpdateResult addDiscussionReplyLike(ObjectId userId, ReplyRef replyRef) {
        return this.userActivityRepository.addDiscussionReplyLike(userId, replyRef);
    }

    @Override
    public UpdateResult deleteDiscussionReplyLike(ObjectId userId, ObjectId replyId) {
        return this.userActivityRepository.deleteDiscussionReplyLike(userId, replyId);
    }
}
