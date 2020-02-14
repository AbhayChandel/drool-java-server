package com.hexlindia.drool.video.business.impl.usecase;

import com.hexlindia.drool.video.data.doc.ProductRef;
import com.hexlindia.drool.video.data.doc.UserRef;
import com.hexlindia.drool.video.data.doc.VideoDoc;
import com.hexlindia.drool.video.data.repository.api.VideoRepository;
import com.hexlindia.drool.video.dto.ProductRefDto;
import com.hexlindia.drool.video.dto.UserRefDto;
import com.hexlindia.drool.video.dto.VideoDto;
import com.hexlindia.drool.video.dto.mapper.VideoDocDtoMapper;
import com.hexlindia.drool.video.exception.VideoNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class VideoImplTest {

    private VideoImpl videoImplSpy;

    @Mock
    private VideoDocDtoMapper videoDocDtoMapperMock;

    @Mock
    private VideoRepository videoRepositoryMock;

    @BeforeEach
    void setUp() {
        this.videoImplSpy = Mockito.spy(new VideoImpl(videoDocDtoMapperMock, videoRepositoryMock));
    }

    @Test
    void insert_PassingObjectToRepositoryLayer() {
        VideoDoc videoDocMock = new VideoDoc("review", "L'oreal Collosal Kajal Review", "This is a fake video review for L'oreal kajal", "vQ765gh",
                new ProductRef("abc", "Loreal Kajal", "kajal"),
                new UserRef("123", "shabana"));
        when(this.videoDocDtoMapperMock.toDoc(any())).thenReturn(videoDocMock);
        when(this.videoRepositoryMock.insert((VideoDoc) any())).thenReturn(videoDocMock);
        this.videoImplSpy.insert(null);
        ArgumentCaptor<VideoDoc> videoDocArgumentCaptor = ArgumentCaptor.forClass(VideoDoc.class);
        verify(this.videoRepositoryMock, times(1)).insert(videoDocArgumentCaptor.capture());
        assertEquals("review", videoDocArgumentCaptor.getValue().getType());
        assertEquals("L'oreal Collosal Kajal Review", videoDocArgumentCaptor.getValue().getTitle());
        assertEquals("This is a fake video review for L'oreal kajal", videoDocArgumentCaptor.getValue().getDescription());
        assertEquals("vQ765gh", videoDocArgumentCaptor.getValue().getSourceId());
        assertEquals("abc", videoDocArgumentCaptor.getValue().getProductRef().getId());
        assertEquals("Loreal Kajal", videoDocArgumentCaptor.getValue().getProductRef().getName());
        assertEquals("kajal", videoDocArgumentCaptor.getValue().getProductRef().getType());
        assertEquals("123", videoDocArgumentCaptor.getValue().getUserRef().getId());
        assertEquals("shabana", videoDocArgumentCaptor.getValue().getUserRef().getUsername());
    }

    @Test
    void insert_ObjectReturnedFromRepositoryLayerIsReceived() {
        when(this.videoDocDtoMapperMock.toDoc(any())).thenReturn(new VideoDoc());
        when(this.videoRepositoryMock.insert((VideoDoc) any())).thenReturn(new VideoDoc());
        VideoDto videoDtoMock = new VideoDto("review", "L'oreal Collosal Kajal Review", "This is a fake video review for L'oreal kajal", "vQ765gh",
                new ProductRefDto("abc", "Loreal Kajal", "kajal"),
                new UserRefDto("123", "shabana"));
        videoDtoMock.setId("456");
        when(this.videoDocDtoMapperMock.toDto(any())).thenReturn(videoDtoMock);
        VideoDto videoDto = this.videoImplSpy.insert(null);
        assertEquals("456", videoDto.getId());
        assertEquals("L'oreal Collosal Kajal Review", videoDto.getTitle());
    }

    @Test
    void findById_testPassingEntityToRepository() {
        when(this.videoRepositoryMock.findById("abc")).thenReturn(Optional.of(new VideoDoc()));
        videoImplSpy.findById("abc");
        ArgumentCaptor<String> idArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(videoRepositoryMock, times(1)).findById(idArgumentCaptor.capture());
        assertEquals("abc", idArgumentCaptor.getValue());
    }

    @Test
    void findById_testFindUnavailableVideo() {
        when(this.videoRepositoryMock.findById("abc")).thenReturn(Optional.empty());
        Assertions.assertThrows(VideoNotFoundException.class, () -> videoImplSpy.findById("abc"));
    }

}