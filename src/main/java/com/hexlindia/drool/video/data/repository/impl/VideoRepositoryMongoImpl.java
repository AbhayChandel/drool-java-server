package com.hexlindia.drool.video.data.repository.impl;

import com.hexlindia.drool.common.data.doc.PostRef;
import com.hexlindia.drool.common.util.MetaFieldValueFormatter;
import com.hexlindia.drool.video.data.doc.VideoComment;
import com.hexlindia.drool.video.data.doc.VideoDoc;
import com.hexlindia.drool.video.data.repository.api.VideoRepositoryMongo;
import com.hexlindia.drool.video.dto.VideoCommentDto;
import com.hexlindia.drool.video.dto.VideoLikeUnlikeDto;
import com.hexlindia.drool.video.dto.VideoThumbnailDataAggregation;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@Slf4j
@RequiredArgsConstructor
public class VideoRepositoryMongoImpl implements VideoRepositoryMongo {

    private static final String VIDEO_COLLECTION_NAME = "videos";

    private final MongoOperations mongoOperations;

    private static final String COMMENT_LIST = "commentList";


    @Override
    public VideoDoc save(VideoDoc videoDoc) {
        videoDoc.setDatePosted(LocalDateTime.now());
        videoDoc.setActive(true);
        return this.mongoOperations.insert(videoDoc);
    }

    @Override
    public boolean updateVideo(VideoDoc videoDoc) {
        Update update = new Update().set("productRefList", videoDoc.getProductRefList()).set("title", videoDoc.getTitle()).set("description", videoDoc.getDescription());
        return mongoOperations.updateFirst(new Query(where("id").is(videoDoc.getId())), update, VideoDoc.class).getModifiedCount() > 0;
    }

    @Override
    public DeleteResult deleteVideo(ObjectId id) {
        return mongoOperations.remove(Query.query(new Criteria("_id").is(id)), VideoDoc.class);
    }

    @Override
    public Optional<VideoDoc> findByIdAndActiveTrue(ObjectId id) {
        /*VideoDoc videoDoc = mongoOperations.findOne(query(where("_id").is(id).andOperator(where("active").is(true))), VideoDoc.class);
        return videoDoc == null ? Optional.empty() : Optional.of(videoDoc);*/
        MatchOperation match = match(new Criteria("_id").is(id).andOperator(new Criteria("active").is(true)));
        ProjectionOperation project = Aggregation.project("type", "reviewId", "title", "description", "sourceId", "datePosted", "likes", "views", "productRefList", "userRef", COMMENT_LIST).
                and(ArrayOperators.arrayOf(ConditionalOperators.ifNull(COMMENT_LIST).then(Collections.emptyList())).length()).as("totalComments");


        AggregationResults<VideoDoc> results = this.mongoOperations.aggregate(Aggregation.newAggregation(
                match,
                project
        ), "videos", VideoDoc.class);
        VideoDoc videoDoc = results.getUniqueMappedResult();
        return videoDoc == null ? Optional.empty() : Optional.of(videoDoc);
    }

    @Override
    public VideoThumbnailDataAggregation getLatestThreeVideosByUser(ObjectId userId) {

        MatchOperation matchUserVideos = match(new Criteria("userRef._id").is(userId).andOperator(new Criteria("active").is(true)));
        FacetOperation facet = facet(sort(Sort.Direction.DESC, "datePosted"), limit(3)).as("videoThumbnailList")
                .and(count().as("totalVideoCount")).as("count");
        ProjectionOperation project = project("videoThumbnailList", "count.totalVideoCount");

        AggregationResults<VideoThumbnailDataAggregation> results = this.mongoOperations.aggregate(Aggregation.newAggregation(
                matchUserVideos,
                facet,
                project
        ), VIDEO_COLLECTION_NAME, VideoThumbnailDataAggregation.class);

        return results.getUniqueMappedResult();
    }


    @Override
    public String saveVideoLikes(VideoLikeUnlikeDto videoLikeUnlikeDto) {
        VideoDoc videoDoc = mongoOperations.findAndModify(new Query(where("id").is(videoLikeUnlikeDto.getVideoId())), new Update().inc("likes", 1), FindAndModifyOptions.options().returnNew(true), VideoDoc.class);
        return MetaFieldValueFormatter.getCompactFormat(videoDoc.getLikes());
    }

    @Override
    public String deleteVideoLikes(VideoLikeUnlikeDto videoLikeUnlikeDto) {
        VideoDoc videoDoc = mongoOperations.findAndModify(new Query(where("id").is(videoLikeUnlikeDto.getVideoId())), new Update().inc("likes", -1), FindAndModifyOptions.options().returnNew(true), VideoDoc.class);
        return MetaFieldValueFormatter.getCompactFormat(videoDoc.getLikes());
    }

    @Override
    public VideoComment insertComment(PostRef postRef, VideoComment videoComment) {
        videoComment.setDatePosted(LocalDateTime.now());
        UpdateResult commentInsertResult = mongoOperations.updateFirst(new Query(where("id").is(postRef.getId())), new Update().addToSet(COMMENT_LIST, videoComment), VideoDoc.class);
        if (commentInsertResult.getModifiedCount() > 0) {
            return videoComment;
        }
        return null;
    }

    @Override
    public VideoCommentDto updateComment(VideoCommentDto videoCommentDto) {
        Update update = new Update().set("commentList.$.comment", videoCommentDto.getComment());
        mongoOperations.updateFirst(findComment(videoCommentDto), update, VideoDoc.class);
        return videoCommentDto;
    }

    private Query findComment(VideoCommentDto videoCommentDto) {
        return Query.query(Criteria.where("_id").is(videoCommentDto.getPostRefDto().getId()).andOperator(Criteria.where(COMMENT_LIST).elemMatch(Criteria.where("_id").is(videoCommentDto.getId()))));
    }

    @Override
    public boolean deleteComment(VideoCommentDto videoCommentDto) {
        Query queryVideo = Query.query(Criteria.where("_id").is(videoCommentDto.getPostRefDto().getId()));
        Query queryComment = Query.query(Criteria.where("_id").is(videoCommentDto.getId()));
        Update update = new Update().pull(COMMENT_LIST, queryComment);
        UpdateResult commentDeleteResult = mongoOperations.updateFirst(queryVideo, update, VideoDoc.class);
        return (commentDeleteResult.getModifiedCount() > 0);
    }

    @Override
    public String saveCommentLike(VideoCommentDto videoCommentDto) {
        Update update = new Update().inc("commentList.$.likes", 1);
        UpdateResult commentLikeResult = mongoOperations.updateFirst(findComment(videoCommentDto), update, VideoDoc.class);
        return (commentLikeResult.getMatchedCount() > 0 && commentLikeResult.getModifiedCount() > 0) ? Integer.toString(Integer.valueOf(videoCommentDto.getLikes()) + 1) : videoCommentDto.getLikes();
    }

    @Override
    public String deleteCommentLike(VideoCommentDto videoCommentDto) {
        Update update = new Update().inc("commentList.$.likes", -1);
        UpdateResult commentLikeResult = mongoOperations.updateFirst(findComment(videoCommentDto), update, VideoDoc.class);
        return (commentLikeResult.getMatchedCount() > 0 && commentLikeResult.getModifiedCount() > 0) ? Integer.toString(Integer.valueOf(videoCommentDto.getLikes()) - 1) : videoCommentDto.getLikes();
    }
}
