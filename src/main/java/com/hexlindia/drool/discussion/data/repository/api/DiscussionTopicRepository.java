package com.hexlindia.drool.discussion.data.repository.api;

import com.hexlindia.drool.discussion.data.doc.DiscussionTopicDoc;
import com.hexlindia.drool.user.data.doc.UserRef;
import org.bson.types.ObjectId;

import java.util.Optional;

public interface DiscussionTopicRepository {

    DiscussionTopicDoc save(DiscussionTopicDoc discussionTopicDoc);

    Optional<DiscussionTopicDoc> findById(ObjectId id);

    boolean updateTopicTitle(String title, ObjectId discussionId);

    DiscussionTopicDoc incrementViews(ObjectId discussionId);

    DiscussionTopicDoc incrementLikes(ObjectId discussionId);

    DiscussionTopicDoc decrementLikes(ObjectId discussionId);

    DiscussionTopicDoc updateUser(ObjectId discussionId, UserRef newUserRef, UserRef oldUserRef);
}
