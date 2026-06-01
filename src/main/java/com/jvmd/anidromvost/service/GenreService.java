package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.Genre;
import com.jvmd.anidromvost.repository.GenreRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {
    private GenreRepo genreRepo;

    public Genre create(Genre genre) {
        return genreRepo.save(genre);
    }

    public List<Genre> findAll() {
        return genreRepo.findAll();
    }

    public Genre findById(Long id) {
        return genreRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found: " + id));
    }

    public Genre update(Long id, Genre data) {
        Genre genre = findById(id);
        genre.setName(data.getName());
        genre.setDescription(data.getDescription());
        return genreRepo.save(genre);
    }

    public void delete(Long id) {
        genreRepo.deleteById(id);
    }
}
