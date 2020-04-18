package com.hexlindia.drool.common.data.doc;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class CommentRef {

    private ObjectId id;
    private String comment;
    private PostRef postRef;
    private LocalDateTime datePosted;

    public CommentRef(ObjectId id, String comment, PostRef postRef, LocalDateTime datePosted) {
        this.id = id;
        this.comment = comment;
        this.postRef = postRef;
        this.datePosted = datePosted;
    }

    public CommentRef() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PostRef getPostRef() {
        return postRef;
    }

    public void setPostRef(PostRef postRef) {
        this.postRef = postRef;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }
}
