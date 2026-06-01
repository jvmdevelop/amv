package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.Anime;
import com.jvmd.anidromvost.model.Genre;
import com.jvmd.anidromvost.repository.AnimeRepo;
import com.jvmd.anidromvost.repository.GenreRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AnimeService {
    private AnimeRepo animeRepo;
    private GenreRepo genreRepo;
    private MinioStorageService storageService;

    public Anime create(Anime anime, List<Long> genreIds) {
        if (genreIds != null && !genreIds.isEmpty()) {
            List<Genre> genres = genreRepo.findAllById(genreIds);
            anime.setGenres(genres);
        }
        return animeRepo.save(anime);
    }

    public Anime findById(Long id) {
        return animeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Anime not found: " + id));
    }

    public Page<Anime> findAll(Pageable pageable) {
        return animeRepo.findAll(pageable);
    }

    public List<Anime> search(String query) {
        return animeRepo.findByTitleContainingIgnoreCase(query);
    }

    public Page<Anime> findByGenre(Long genreId, Pageable pageable) {
        return animeRepo.findByGenresId(genreId, pageable);
    }

    public Anime update(Long id, Anime data, List<Long> genreIds) {
        Anime anime = findById(id);
        anime.setTitle(data.getTitle());
        anime.setDescription(data.getDescription());
        anime.setStatus(data.getStatus());
        anime.setReleaseYear(data.getReleaseYear());
        if (genreIds != null) {
            anime.setGenres(genreRepo.findAllById(genreIds));
        }
        return animeRepo.save(anime);
    }

    public void delete(Long id) {
        Anime anime = findById(id);
        try {
            if (anime.getCoverImageKey() != null) {
                storageService.deleteImage(anime.getCoverImageKey());
            }
        } catch (Exception e) {
            log.warn("Failed to delete cover image: {}", e.getMessage());
        }
        animeRepo.delete(anime);
    }

    public Anime uploadCover(Long animeId, MultipartFile file) throws Exception {
        Anime anime = findById(animeId);
        if (anime.getCoverImageKey() != null) {
            storageService.deleteImage(anime.getCoverImageKey());
        }
        String key = storageService.uploadImage(file);
        anime.setCoverImageKey(key);
        return animeRepo.save(anime);
    }
}
