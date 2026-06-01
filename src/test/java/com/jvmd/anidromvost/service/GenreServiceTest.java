package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.Genre;
import com.jvmd.anidromvost.repository.GenreRepo;
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
class GenreServiceTest {

    @Mock
    private GenreRepo genreRepo;

    @InjectMocks
    private GenreService genreService;

    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = Genre.builder().id(1L).name("Action").description("Action anime").build();
    }

    @Test
    void create_savesAndReturnsGenre() {
        when(genreRepo.save(any(Genre.class))).thenReturn(genre);

        Genre result = genreService.create(genre);

        assertEquals("Action", result.getName());
        verify(genreRepo).save(genre);
    }

    @Test
    void findAll_returnsList() {
        when(genreRepo.findAll()).thenReturn(List.of(genre));

        List<Genre> result = genreService.findAll();

        assertEquals(1, result.size());
        assertEquals("Action", result.get(0).getName());
    }

    @Test
    void findById_found() {
        when(genreRepo.findById(1L)).thenReturn(Optional.of(genre));

        Genre result = genreService.findById(1L);

        assertEquals("Action", result.getName());
    }

    @Test
    void findById_notFound_throws() {
        when(genreRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> genreService.findById(99L));
    }

    @Test
    void update_changesFields() {
        Genre updated = Genre.builder().name("Romance").description("Love stories").build();
        when(genreRepo.findById(1L)).thenReturn(Optional.of(genre));
        when(genreRepo.save(any(Genre.class))).thenAnswer(i -> i.getArgument(0));

        Genre result = genreService.update(1L, updated);

        assertEquals("Romance", result.getName());
        assertEquals("Love stories", result.getDescription());
    }

    @Test
    void delete_callsRepo() {
        genreService.delete(1L);

        verify(genreRepo).deleteById(1L);
    }
}
