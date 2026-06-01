package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.Anime;
import com.jvmd.anidromvost.model.AnimeStatus;
import com.jvmd.anidromvost.model.Genre;
import com.jvmd.anidromvost.repository.AnimeRepo;
import com.jvmd.anidromvost.repository.GenreRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimeServiceTest {

    @Mock
    private AnimeRepo animeRepo;

    @Mock
    private GenreRepo genreRepo;

    @Mock
    private MinioStorageService storageService;

    @InjectMocks
    private AnimeService animeService;

    private Anime anime;
    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = Genre.builder().id(1L).name("Action").build();
        anime = Anime.builder()
                .id(1L)
                .title("Naruto")
                .description("Ninja story")
                .status(AnimeStatus.COMPLETED)
                .releaseYear(2002)
                .genres(new ArrayList<>())
                .episodes(new ArrayList<>())
                .build();
    }

    @Test
    void create_withGenres() {
        when(genreRepo.findAllById(List.of(1L))).thenReturn(List.of(genre));
        when(animeRepo.save(any(Anime.class))).thenAnswer(i -> i.getArgument(0));

        Anime result = animeService.create(anime, List.of(1L));

        assertEquals("Naruto", result.getTitle());
        assertEquals(1, result.getGenres().size());
        assertEquals("Action", result.getGenres().get(0).getName());
    }

    @Test
    void create_withoutGenres() {
        when(animeRepo.save(any(Anime.class))).thenReturn(anime);

        Anime result = animeService.create(anime, null);

        assertEquals("Naruto", result.getTitle());
        verify(genreRepo, never()).findAllById(any());
    }

    @Test
    void findById_found() {
        when(animeRepo.findById(1L)).thenReturn(Optional.of(anime));

        Anime result = animeService.findById(1L);

        assertEquals("Naruto", result.getTitle());
    }

    @Test
    void findById_notFound_throws() {
        when(animeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> animeService.findById(99L));
    }

    @Test
    void findAll_returnsPaginatedResult() {
        Page<Anime> page = new PageImpl<>(List.of(anime));
        when(animeRepo.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Anime> result = animeService.findAll(PageRequest.of(0, 20));

        assertEquals(1, result.getTotalElements());
        assertEquals("Naruto", result.getContent().get(0).getTitle());
    }

    @Test
    void search_byTitle() {
        when(animeRepo.findByTitleContainingIgnoreCase("naru")).thenReturn(List.of(anime));

        List<Anime> result = animeService.search("naru");

        assertEquals(1, result.size());
    }

    @Test
    void update_changesFields() {
        Anime updated = Anime.builder()
                .title("Naruto Shippuden")
                .description("Updated")
                .status(AnimeStatus.COMPLETED)
                .releaseYear(2007)
                .build();
        when(animeRepo.findById(1L)).thenReturn(Optional.of(anime));
        when(animeRepo.save(any(Anime.class))).thenAnswer(i -> i.getArgument(0));

        Anime result = animeService.update(1L, updated, null);

        assertEquals("Naruto Shippuden", result.getTitle());
        assertEquals(2007, result.getReleaseYear());
    }

    @Test
    void delete_removesCoverAndEntity() throws Exception {
        anime.setCoverImageKey("covers/test.jpg");
        when(animeRepo.findById(1L)).thenReturn(Optional.of(anime));

        animeService.delete(1L);

        verify(storageService).deleteImage("covers/test.jpg");
        verify(animeRepo).delete(anime);
    }

    @Test
    void delete_noCover_stillDeletes() {
        when(animeRepo.findById(1L)).thenReturn(Optional.of(anime));

        animeService.delete(1L);

        verify(animeRepo).delete(anime);
    }
}
