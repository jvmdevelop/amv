package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.Anime;
import com.jvmd.anidromvost.model.AnimeStatus;
import com.jvmd.anidromvost.model.Episode;
import com.jvmd.anidromvost.repository.AnimeRepo;
import com.jvmd.anidromvost.repository.EpisodeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EpisodeServiceTest {

    @Mock
    private EpisodeRepo episodeRepo;

    @Mock
    private AnimeRepo animeRepo;

    @Mock
    private MinioStorageService storageService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private EpisodeService episodeService;

    private Anime anime;
    private Episode episode;

    @BeforeEach
    void setUp() {
        anime = Anime.builder()
                .id(1L)
                .title("Naruto")
                .status(AnimeStatus.COMPLETED)
                .episodes(new ArrayList<>())
                .build();
        episode = Episode.builder()
                .id(1L)
                .anime(anime)
                .episodeNumber(1)
                .title("Pilot")
                .build();
    }

    @Test
    void create_episode() {
        when(animeRepo.findById(1L)).thenReturn(Optional.of(anime));
        when(episodeRepo.save(any(Episode.class))).thenAnswer(i -> {
            Episode e = i.getArgument(0);
            e.setId(1L);
            return e;
        });

        Episode result = episodeService.create(1L, 1, "Pilot");

        assertEquals(1, result.getEpisodeNumber());
        assertEquals("Pilot", result.getTitle());
        assertEquals(anime, result.getAnime());
    }

    @Test
    void create_animeNotFound_throws() {
        when(animeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> episodeService.create(99L, 1, "Pilot"));
    }

    @Test
    void findByAnimeId_returnsSortedList() {
        Episode ep2 = Episode.builder().id(2L).anime(anime).episodeNumber(2).title("Ep 2").build();
        when(episodeRepo.findByAnimeIdOrderByEpisodeNumber(1L)).thenReturn(List.of(episode, ep2));

        List<Episode> result = episodeService.findByAnimeId(1L);

        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getEpisodeNumber());
        assertEquals(2, result.get(1).getEpisodeNumber());
    }

    @Test
    void findById_found() {
        when(episodeRepo.findById(1L)).thenReturn(Optional.of(episode));

        Episode result = episodeService.findById(1L);

        assertEquals("Pilot", result.getTitle());
    }

    @Test
    void findById_notFound_throws() {
        when(episodeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> episodeService.findById(99L));
    }

    @Test
    void uploadVideo_savesKeyAndNotifies() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "ep1.mp4", "video/mp4", new byte[]{1, 2, 3});
        when(episodeRepo.findById(1L)).thenReturn(Optional.of(episode));
        when(storageService.uploadVideo(file)).thenReturn("videos/uuid_ep1.mp4");
        when(episodeRepo.save(any(Episode.class))).thenAnswer(i -> i.getArgument(0));

        Episode result = episodeService.uploadVideo(1L, file);

        assertEquals("videos/uuid_ep1.mp4", result.getVideoKey());
        verify(messagingTemplate).convertAndSend(eq("/topic/new-episodes"), any(Object.class));
    }

    @Test
    void uploadVideo_replacesOldVideo() throws Exception {
        episode.setVideoKey("videos/old.mp4");
        MockMultipartFile file = new MockMultipartFile("file", "ep1.mp4", "video/mp4", new byte[]{1, 2, 3});
        when(episodeRepo.findById(1L)).thenReturn(Optional.of(episode));
        when(storageService.uploadVideo(file)).thenReturn("videos/new.mp4");
        when(episodeRepo.save(any(Episode.class))).thenAnswer(i -> i.getArgument(0));

        episodeService.uploadVideo(1L, file);

        verify(storageService).deleteVideo("videos/old.mp4");
    }

    @Test
    void delete_removesVideoAndEntity() throws Exception {
        episode.setVideoKey("videos/test.mp4");
        when(episodeRepo.findById(1L)).thenReturn(Optional.of(episode));

        episodeService.delete(1L);

        verify(storageService).deleteVideo("videos/test.mp4");
        verify(episodeRepo).delete(episode);
    }
}
