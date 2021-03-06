package com.hexlindia.drool.product.data.repository.impl;

import com.hexlindia.drool.product.business.impl.usecase.ReviewType;
import com.hexlindia.drool.product.data.doc.ProductAspectTemplates;
import com.hexlindia.drool.product.data.doc.ProductDoc;
import com.hexlindia.drool.product.data.doc.ReviewDoc;
import com.hexlindia.drool.product.data.repository.api.ProductReviewRepository;
import com.hexlindia.drool.product.dto.AspectVotingDto;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class ProductReviewRepositoryImpl implements ProductReviewRepository {

    private static final String REVIEWS_PATH = "reviews.";

    private static final String TEXT_REVIEWS_ARRAY = "textReviews";

    private static final String VIDEO_REVIEWS_ARRAY = "videoReviews";

    private static final String TEXT_REVIEWS_COUNT = "textReviewsCount";

    private static final String VIDEO_REVIEWS_COUNT = "videoReviewsCount";

    private static final String TOTAL_REVIEWS_COUNT = "totalReviewsCount";

    private static final String PRODUCT_COLLECTION_NAME = "products";


    private final MongoOperations mongoOperations;

    public ProductReviewRepositoryImpl(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public boolean setVideoId(ObjectId productId, ObjectId reviewId, ObjectId videoId) {
        Update update = new Update().set("reviews.videoReviews.$[review].videoId", videoId).filterArray(Criteria.where("review._id").is(reviewId));
        UpdateResult results = mongoOperations.updateFirst(new Query(where("id").is(productId)), update, ProductDoc.class);
        return results.getModifiedCount() > 0;
    }

    @Override
    public ProductAspectTemplates getAspectTemplates(ObjectId id) {

        MatchOperation matchProduct = match(new Criteria("_id").is(id));
        LookupOperation lookupExternalAspectTemplates = LookupOperation.newLookup().
                from("aspect_templates")
                .localField("aspects.external_aspects")
                .foreignField("_id")
                .as("easpects");

        ProjectionOperation project = Aggregation.project()
                .and("easpects").concatArrays("aspects.internal_aspects").as("allaspects");

        AggregationResults<ProductAspectTemplates> aspectTemplates = this.mongoOperations.aggregate(Aggregation.newAggregation(
                matchProduct,
                lookupExternalAspectTemplates, project
        ), PRODUCT_COLLECTION_NAME, ProductAspectTemplates.class);

        return aspectTemplates.getUniqueMappedResult();
    }


    @Override
    public ReviewDoc save(ReviewDoc reviewDoc, ObjectId productId, List<AspectVotingDto> aspectVotingDtoList) {
        reviewDoc.setDatePosted(LocalDateTime.now());

        Update update = new Update();
        setReviewUpdate(update, reviewDoc);
        setAspectResultUpdate(update, aspectVotingDtoList);
        UpdateResult reviewSaveResult = mongoOperations.updateFirst(new Query(where("id").is(productId)), update, ProductDoc.class);
        if (reviewSaveResult.getModifiedCount() > 0) {
            return reviewDoc;
        }
        return null;
    }

    private Update setReviewUpdate(Update update, ReviewDoc reviewDoc) {
        return update.addToSet(getReviewsArray(reviewDoc.getReviewType()), reviewDoc).inc(REVIEWS_PATH + TOTAL_REVIEWS_COUNT, 1).inc(getReviewsCount(reviewDoc.getReviewType()), 1);
    }

    private Update setAspectResultUpdate(Update update, List<AspectVotingDto> aspectVotingDtoList) {

        if (!aspectVotingDtoList.isEmpty()) {
            List<ObjectId> aspectIdList = new ArrayList<>();
            List<String> aspectSelectedOptionsList = new ArrayList<>();
            for (AspectVotingDto aspectVotingDto : aspectVotingDtoList) {
                aspectIdList.add(new ObjectId(aspectVotingDto.getAspectId()));
                aspectSelectedOptionsList.addAll(aspectVotingDto.getSelectedOptions());
            }

            update = update.inc("aspects.aspect_results.$[aspect].votes", 1);
            update = update.inc("aspects.aspect_results.$[aspect].options.$[option].votes", 1);
            update = update.filterArray(Criteria.where("aspect._id").in(aspectIdList));
            update = update.filterArray(Criteria.where("option.name").in(aspectSelectedOptionsList));
        }
        return update;
    }

    private String getReviewsArray(ReviewType reviewType) {
        return REVIEWS_PATH + (reviewType.equals(ReviewType.text) ? TEXT_REVIEWS_ARRAY : VIDEO_REVIEWS_ARRAY);
    }

    private String getReviewsCount(ReviewType reviewType) {
        return REVIEWS_PATH + (reviewType.equals(ReviewType.text) ? TEXT_REVIEWS_COUNT : VIDEO_REVIEWS_COUNT);
    }
}
