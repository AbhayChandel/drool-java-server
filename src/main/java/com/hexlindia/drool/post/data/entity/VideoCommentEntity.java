package com.hexlindia.drool.post.data.entity;

import com.hexlindia.drool.user.data.entity.UserAccountEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "video_comment")
@Data
public class VideoCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "video_comment_id_generator")
    @SequenceGenerator(name = "video_comment_id_generator", sequenceName = "video_comment_id_seq", allocationSize = 1)
    private long id;

    private String comment;
    private LocalDateTime datePosted;
    private int likes;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccountEntity user;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private VideoEntity video;


}
