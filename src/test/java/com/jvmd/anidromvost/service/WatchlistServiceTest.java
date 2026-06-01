package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.Anime;
import com.jvmd.anidromvost.model.User;
import com.jvmd.anidromvost.model.ERole;
import com.jvmd.anidromvost.model.WatchlistEntry;
import com.jvmd.anidromvost.repository.AnimeRepo;
import com.jvmd.anidromvost.repository.WatchlistRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceTest {

    @Mock
    private WatchlistRepo watchlistRepo;

    @Mock
    private AnimeRepo animeRepo;

    @InjectMocks
    private WatchlistService watchlistService;

    private User user;
    private Anime anime;
    private WatchlistEntry entry;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("testuser").email("test@test.com").password("hash").role(ERole.USER).build();
        anime = Anime.builder().id(1L).title("Naruto").build();
        entry = WatchlistEntry.builder().id(1L).user(user).anime(anime).favorite(false).build();
    }

    @Test
    void addToWatchlist_success() {
        when(watchlistRepo.existsByUserIdAndAnimeId(1L, 1L)).thenReturn(false);
        when(animeRepo.findById(1L)).thenReturn(Optional.of(anime));
        when(watchlistRepo.save(any(WatchlistEntry.class))).thenAnswer(i -> i.getArgument(0));

        WatchlistEntry result = watchlistService.addToWatchlist(user, 1L);

        assertEquals(anime, result.getAnime());
        assertFalse(result.isFavorite());
    }

    @Test
    void addToWatchlist_alreadyExists_throws() {
        when(watchlistRepo.existsByUserIdAndAnimeId(1L, 1L)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> watchlistService.addToWatchlist(user, 1L));
    }

    @Test
    void addToWatchlist_animeNotFound_throws() {
        when(watchlistRepo.existsByUserIdAndAnimeId(1L, 99L)).thenReturn(false);
        when(animeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> watchlistService.addToWatchlist(user, 99L));
    }

    @Test
    void removeFromWatchlist_callsRepo() {
        watchlistService.removeFromWatchlist(1L, 1L);

        verify(watchlistRepo).deleteByUserIdAndAnimeId(1L, 1L);
    }

    @Test
    void toggleFavorite_togglesFlag() {
        when(watchlistRepo.findByUserIdAndAnimeId(1L, 1L)).thenReturn(Optional.of(entry));
        when(watchlistRepo.save(any(WatchlistEntry.class))).thenAnswer(i -> i.getArgument(0));

        WatchlistEntry result = watchlistService.toggleFavorite(1L, 1L);

        assertTrue(result.isFavorite());
    }

    @Test
    void toggleFavorite_notInWatchlist_throws() {
        when(watchlistRepo.findByUserIdAndAnimeId(1L, 99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> watchlistService.toggleFavorite(1L, 99L));
    }

    @Test
    void getWatchlist_returnsList() {
        when(watchlistRepo.findByUserId(1L)).thenReturn(List.of(entry));

        List<WatchlistEntry> result = watchlistService.getWatchlist(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getFavorites_returnsOnlyFavorites() {
        WatchlistEntry fav = WatchlistEntry.builder().id(2L).user(user).anime(anime).favorite(true).build();
        when(watchlistRepo.findByUserIdAndFavoriteTrue(1L)).thenReturn(List.of(fav));

        List<WatchlistEntry> result = watchlistService.getFavorites(1L);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isFavorite());
    }
}
