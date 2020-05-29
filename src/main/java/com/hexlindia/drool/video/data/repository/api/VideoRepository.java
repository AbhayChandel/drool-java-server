package com.hexlindia.drool.video.data.repository.api;

import com.hexlindia.drool.post.data.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<VideoEntity, Long> {
}
