package com.hexlindia.drool.discussion.business.impl.usecase;

import com.hexlindia.drool.common.to.ActivityTo;
import com.hexlindia.drool.common.util.DateTimeUtil;
import com.hexlindia.drool.discussion.business.api.usecase.DiscussionReply;
import com.hexlindia.drool.discussion.business.api.usecase.DiscussionReplyUserLike;
import com.hexlindia.drool.discussion.business.api.usecase.DiscussionTopic;
import com.hexlindia.drool.discussion.data.entity.DiscussionReplyActivityEntity;
import com.hexlindia.drool.discussion.data.entity.DiscussionReplyEntity;
import com.hexlindia.drool.discussion.data.entity.DiscussionReplyUserLikeId;
import com.hexlindia.drool.discussion.data.repository.DiscussionReplyRepository;
import com.hexlindia.drool.discussion.exception.DiscussionReplyNotFoundException;
import com.hexlindia.drool.discussion.to.DiscussionReplyTo;
import com.hexlindia.drool.discussion.to.mapper.DiscussionReplyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Slf4j
public class DiscussionReplyImpl implements DiscussionReply {

    private final DiscussionReplyRepository discussionReplyRepository;
    private final DiscussionReplyMapper discussionReplyMapper;
    private final DiscussionReplyUserLike discussionReplyUserLike;
    private final DiscussionTopic discussionTopic;

    @Autowired
    public DiscussionReplyImpl(DiscussionReplyRepository discussionReplyRepository, DiscussionReplyMapper discussionReplyMapper, DiscussionReplyUserLike discussionReplyUserLike, DiscussionTopic discussionTopic) {
        this.discussionReplyRepository = discussionReplyRepository;
        this.discussionReplyMapper = discussionReplyMapper;
        this.discussionReplyUserLike = discussionReplyUserLike;
        this.discussionTopic = discussionTopic;
    }

    @Override
    @Transactional
    public DiscussionReplyTo post(DiscussionReplyTo discussionReplyTo) {
        DiscussionReplyEntity discussionReplyEntity = this.discussionReplyRepository.save(discussionReplyMapper.toEntity(discussionReplyTo));
        log.debug("Discussion Reply: '{}', id: '{}' for Discussion ID: {} posted", discussionReplyEntity.getReply(), discussionReplyEntity.getId(), discussionReplyTo.getDiscussionTopicId());
        DiscussionReplyActivityEntity discussionReplyActivityEntity = new DiscussionReplyActivityEntity(discussionReplyEntity.getId());
        discussionReplyActivityEntity.setDatePosted(DateTimeUtil.getCurrentTimestamp());
        discussionReplyEntity.setDiscussionReplyActivityEntity(discussionReplyActivityEntity);
        discussionReplyEntity = discussionReplyRepository.save(discussionReplyEntity);
        log.debug("Discussion Reply Id: '{}' activity row created", discussionReplyEntity.getId());
        discussionTopic.saveReply(discussionReplyEntity, discussionReplyTo.getDiscussionTopicId());
        return discussionReplyMapper.toTransferObject(discussionReplyEntity);
    }

    @Override
    public DiscussionReplyTo findById(Long id) {
        return this.discussionReplyMapper.toTransferObject(findInRepository("Reply search", id));
    }

    @Override
    public DiscussionReplyTo updateReply(DiscussionReplyTo discussionReplyTo) {
        DiscussionReplyEntity discussionReplyEntity = findInRepository("Reply update", discussionReplyTo.getId());
        discussionReplyEntity.setReply(discussionReplyTo.getReply());
        return discussionReplyMapper.toTransferObject(discussionReplyRepository.save(discussionReplyEntity));
    }

    @Override
    @Transactional
    public void incrementLikesByOne(ActivityTo activityTo) {
        DiscussionReplyEntity discussionReplyEntity = findInRepository("Reply likes increment", activityTo.getPostId());
        DiscussionReplyActivityEntity discussionReplyActivityEntity = discussionReplyEntity.getDiscussionReplyActivityEntity();
        discussionReplyActivityEntity.setLikes(discussionReplyActivityEntity.getLikes() + 1);
        discussionReplyEntity.setDiscussionReplyActivityEntity(discussionReplyActivityEntity);
        discussionReplyRepository.save(discussionReplyEntity);
        discussionReplyUserLike.save(new DiscussionReplyUserLikeId(activityTo.getCurrentUserId(), activityTo.getPostId()));

        discussionTopic.setLastDateActiveToNow(discussionReplyEntity.getDiscussionTopicEntity().getId());
    }

    @Override
    public void decrementLikesByOne(ActivityTo activityTo) {
        DiscussionReplyEntity discussionReplyEntity = findInRepository("Topic likes decrement", activityTo.getPostId());
        DiscussionReplyActivityEntity discussionReplyActivityEntity = discussionReplyEntity.getDiscussionReplyActivityEntity();
        discussionReplyActivityEntity.setLikes(discussionReplyActivityEntity.getLikes() - 1);
        discussionReplyEntity.setDiscussionReplyActivityEntity(discussionReplyActivityEntity);
        discussionReplyRepository.save(discussionReplyEntity);
        discussionReplyUserLike.remove(new DiscussionReplyUserLikeId(activityTo.getCurrentUserId(), activityTo.getPostId()));

        discussionTopic.setLastDateActiveToNow(discussionReplyEntity.getDiscussionTopicEntity().getId());
    }

    @Override
    @Transactional
    public DiscussionReplyTo deactivateReply(Long id) {
        DiscussionReplyEntity discussionReplyEntity = findInRepository("Deactivate reply", id);
        discussionReplyEntity.setActive(false);
        discussionReplyEntity = discussionReplyRepository.save(discussionReplyEntity);
        log.debug("Discussion Reply Id: '{}' is deactivated", discussionReplyEntity.getId());
        discussionTopic.decrementRepliesByOne(discussionReplyEntity.getDiscussionTopicEntity().getId());
        return discussionReplyMapper.toTransferObject(discussionReplyEntity);
    }

    private DiscussionReplyEntity findInRepository(String action, Long id) {
        Optional<DiscussionReplyEntity> discussionReplyEntityOptional = discussionReplyRepository.findById(id);
        if (discussionReplyEntityOptional.isPresent()) {
            return discussionReplyEntityOptional.get();
        }
        StringBuilder errorMessage = new StringBuilder(action);
        errorMessage.append(" failed. Discussion reply with id " + id + " not found");
        throw new DiscussionReplyNotFoundException(errorMessage.toString());
    }
}