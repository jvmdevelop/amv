package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.Anime;
import com.jvmd.anidromvost.service.AnimeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/anime")
@AllArgsConstructor
public class AnimeController {
    private AnimeService animeService;

    @GetMapping
    public ResponseEntity<Page<Anime>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(animeService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Anime> getById(@PathVariable Long id) {
        return ResponseEntity.ok(animeService.findById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Anime>> search(@RequestParam String q) {
        return ResponseEntity.ok(animeService.search(q));
    }
}
