package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.*;
import com.jvmd.anidromvost.repository.EpisodeRepo;
import com.jvmd.anidromvost.repository.WatchRoomRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchRoomServiceTest {

    @Mock
    private WatchRoomRepo watchRoomRepo;

    @Mock
    private EpisodeRepo episodeRepo;

    @InjectMocks
    private WatchRoomService watchRoomService;

    private User host;
    private Episode episode;
    private WatchRoom room;

    @BeforeEach
    void setUp() {
        host = User.builder().id(1L).name("host").email("h@t.com").password("hash").role(ERole.USER).build();
        Anime anime = Anime.builder().id(1L).title("Naruto").build();
        episode = Episode.builder().id(1L).anime(anime).episodeNumber(1).title("Pilot").build();
        room = WatchRoom.builder().id(1L).code("ABC123").episode(episode).host(host).active(true).build();
    }

    @Test
    void create_generatesCodeAndSaves() {
        when(episodeRepo.findById(1L)).thenReturn(Optional.of(episode));
        when(watchRoomRepo.save(any(WatchRoom.class))).thenAnswer(i -> {
            WatchRoom r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        WatchRoom result = watchRoomService.create(host, 1L);

        assertNotNull(result.getCode());
        assertEquals(6, result.getCode().length());
        assertEquals(episode, result.getEpisode());
        assertEquals(host, result.getHost());
    }

    @Test
    void create_episodeNotFound_throws() {
        when(episodeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> watchRoomService.create(host, 99L));
    }

    @Test
    void findByCode_found() {
        when(watchRoomRepo.findByCodeAndActiveTrue("ABC123")).thenReturn(Optional.of(room));

        WatchRoom result = watchRoomService.findByCode("ABC123");

        assertEquals("ABC123", result.getCode());
        assertTrue(result.isActive());
    }

    @Test
    void findByCode_notFound_throws() {
        when(watchRoomRepo.findByCodeAndActiveTrue("NOPE")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> watchRoomService.findByCode("NOPE"));
    }

    @Test
    void close_byHost_deactivatesRoom() {
        when(watchRoomRepo.findByCodeAndActiveTrue("ABC123")).thenReturn(Optional.of(room));
        when(watchRoomRepo.save(any(WatchRoom.class))).thenAnswer(i -> i.getArgument(0));

        watchRoomService.close("ABC123", host);

        assertFalse(room.isActive());
        verify(watchRoomRepo).save(room);
    }

    @Test
    void close_byNonHost_throws() {
        User other = User.builder().id(2L).name("other").build();
        when(watchRoomRepo.findByCodeAndActiveTrue("ABC123")).thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class, () -> watchRoomService.close("ABC123", other));
    }
}
