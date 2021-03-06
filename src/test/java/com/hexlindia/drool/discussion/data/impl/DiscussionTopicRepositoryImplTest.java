package com.hexlindia.drool.discussion.data.impl;

import com.hexlindia.drool.common.config.MongoDBTestConfig;
import com.hexlindia.drool.discussion.data.doc.DiscussionReplyDoc;
import com.hexlindia.drool.discussion.data.doc.DiscussionTopicDoc;
import com.hexlindia.drool.discussion.data.repository.api.DiscussionTopicRepository;
import com.hexlindia.drool.user.data.doc.UserRef;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoOperations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(MongoDBTestConfig.class)
class DiscussionTopicRepositoryImplTest {

    @Autowired
    private DiscussionTopicRepository discussionTopicRepository;

    private DiscussionTopicDoc insertDiscussionTopic;

    @Autowired
    private MongoOperations mongoOperations;

    @BeforeEach
    void setup() {
        insertDiscussionTopicDocs();
    }

    @Test
    void save() {
        DiscussionTopicDoc discussionTopicDoc = new DiscussionTopicDoc();
        discussionTopicDoc.setTitle("This topic is returned from db");
        ObjectId userId = ObjectId.get();
        discussionTopicDoc.setUserRef(new UserRef(userId, "shabana"));
        DiscussionReplyDoc discussionReplyDoc = new DiscussionReplyDoc();
        discussionReplyDoc.setReply("As I told it is a great reply");
        discussionReplyDoc.setUserRef(new UserRef(userId, "shabana"));
        discussionReplyDoc.setLikes(190);
        discussionTopicDoc.setDiscussionReplyDocList(Arrays.asList(discussionReplyDoc, new DiscussionReplyDoc(), new DiscussionReplyDoc()));

        discussionTopicDoc = discussionTopicRepository.save(discussionTopicDoc);
        assertNotNull(discussionTopicDoc.getId());

    }

    @Test
    void findById_ValidId() {
        assertTrue(discussionTopicRepository.findById(insertDiscussionTopic.getId()).isPresent());
    }

    @Test
    void findById_InvalidId() {
        assertFalse(discussionTopicRepository.findById(ObjectId.get()).isPresent());
    }

    @Test
    void updateTopicTitle() {
        assertTrue(discussionTopicRepository.updateTopicTitle("This title was updated", insertDiscussionTopic.getId()));
    }

    @Test
    void incrementViews() {
        DiscussionTopicDoc discussionTopicDoc = discussionTopicRepository.incrementViews(insertDiscussionTopic.getId());
        assertEquals(1191, discussionTopicDoc.getViews());
    }

    @Test
    void incrementLikes() {
        DiscussionTopicDoc discussionTopicDoc = discussionTopicRepository.incrementLikes(insertDiscussionTopic.getId());
        assertEquals(501, discussionTopicDoc.getLikes());

    }

    @Test
    void decrementLikes() {
        DiscussionTopicDoc discussionTopicDoc = discussionTopicRepository.decrementLikes(insertDiscussionTopic.getId());
        assertEquals(499, discussionTopicDoc.getLikes());
    }

    private void insertDiscussionTopicDocs() {
        DiscussionTopicDoc discussionTopicDoc = new DiscussionTopicDoc();
        discussionTopicDoc.setTitle("This a dummy discussion topic");
        ObjectId userId = ObjectId.get();
        discussionTopicDoc.setUserRef(new UserRef(userId, "shabana"));
        discussionTopicDoc.setActive(true);
        discussionTopicDoc.setViews(1190);
        discussionTopicDoc.setLikes(500);
        DiscussionReplyDoc discussionReplyDoc = new DiscussionReplyDoc();
        discussionReplyDoc.setReply("As I told it is a great reply");
        discussionReplyDoc.setUserRef(new UserRef(userId, "shabana"));
        discussionReplyDoc.setLikes(190);
        discussionTopicDoc.setDiscussionReplyDocList(Arrays.asList(discussionReplyDoc, new DiscussionReplyDoc(), new DiscussionReplyDoc()));

        mongoOperations.save(discussionTopicDoc);
        insertDiscussionTopic = discussionTopicDoc;
    }

    @Test
    void updateUser() {
        ObjectId userId = ObjectId.get();
        UserRef userRef = new UserRef(userId, "new");
        DiscussionTopicDoc discussionTopicDoc = discussionTopicRepository.updateUser(insertDiscussionTopic.getId(), userRef, insertDiscussionTopic.getUserRef());

        assertEquals(userId, discussionTopicDoc.getUserRef().getId());
        assertEquals("new", discussionTopicDoc.getUserRef().getUsername());

        assertEquals(insertDiscussionTopic.getUserRef().getId(), discussionTopicDoc.getOldUserRef().getId());
        assertEquals(insertDiscussionTopic.getUserRef().getUsername(), discussionTopicDoc.getOldUserRef().getUsername());
    }
}