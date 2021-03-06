package com.hexlindia.drool.video.services.api.rest;

import com.hexlindia.drool.video.dto.VideoCommentDto;
import com.hexlindia.drool.video.dto.VideoDtoMOngo;
import com.hexlindia.drool.video.dto.VideoLikeUnlikeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/${rest.uri.version}/video")
public interface VideoRestService {

    @PostMapping("/save")
    ResponseEntity<VideoDtoMOngo> save(@RequestBody VideoDtoMOngo videoDtoMOngo);

    @DeleteMapping("/delete")
    ResponseEntity<Boolean> delete(@RequestBody VideoDtoMOngo videoDtoMOngo);

    @PutMapping(value = "/likes/increment")
    ResponseEntity<String> incrementVideoLikes(@RequestBody VideoLikeUnlikeDto videoLikeUnlikeDto);

    @PutMapping(value = "/likes/decrement")
    ResponseEntity<String> decrementVideoLikes(@RequestBody VideoLikeUnlikeDto videoLikeUnlikeDto);

    @PutMapping(value = "/insert/comment")
    ResponseEntity<VideoCommentDto> insertComment(@RequestBody VideoCommentDto videoCommentDto);

    @PutMapping(value = "/delete/comment")
    ResponseEntity<Boolean> deleteComment(@RequestBody VideoCommentDto videoCommentDto);

    @PutMapping(value = "/comment/likes/increment")
    ResponseEntity<String> saveCommentLike(@RequestBody VideoCommentDto videoCommentDto);

    @PutMapping(value = "/comment/likes/decrement")
    ResponseEntity<String> deleteCommentLike(@RequestBody VideoCommentDto videoCommentDto);


}
