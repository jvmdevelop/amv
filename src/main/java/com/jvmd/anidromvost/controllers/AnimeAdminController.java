package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.Anime;
import com.jvmd.anidromvost.service.AnimeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/anime")
@AllArgsConstructor
public class AnimeAdminController {
    private AnimeService animeService;

    @PostMapping
    public ResponseEntity<Anime> create(
            @RequestBody Anime anime,
            @RequestParam(required = false) List<Long> genreIds) {
        return ResponseEntity.ok(animeService.create(anime, genreIds));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Anime> update(
            @PathVariable Long id,
            @RequestBody Anime anime,
            @RequestParam(required = false) List<Long> genreIds) {
        return ResponseEntity.ok(animeService.update(id, anime, genreIds));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        animeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cover")
    public ResponseEntity<Anime> uploadCover(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(animeService.uploadCover(id, file));
    }
}
