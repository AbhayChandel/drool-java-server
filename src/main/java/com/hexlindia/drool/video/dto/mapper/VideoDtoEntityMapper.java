package com.hexlindia.drool.video.dto.mapper;

import com.hexlindia.drool.post.data.entity.VideoEntity;
import com.hexlindia.drool.video.dto.VideoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoDtoEntityMapper {

    @Mapping(source = "sourceId", target = "sourceVideoId")
    @Mapping(source = "postType", target = "type", ignore = true)
    VideoEntity toEntity(VideoDto videoDto);
}
