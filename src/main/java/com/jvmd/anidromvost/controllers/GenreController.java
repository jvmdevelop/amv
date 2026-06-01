package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.Genre;
import com.jvmd.anidromvost.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
@AllArgsConstructor
public class GenreController {
    private GenreService genreService;

    @GetMapping
    public ResponseEntity<List<Genre>> list() {
        return ResponseEntity.ok(genreService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.findById(id));
    }
}
