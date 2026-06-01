package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.News;
import com.jvmd.anidromvost.service.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/news")
public class NewsController {
    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<News>> getNews(@RequestParam(defaultValue = "20") Long count) {
        return ResponseEntity.ok(newsService.findAllWithCountFilter(count));
    }

    @PostMapping
    public ResponseEntity<News> postNews(@RequestBody News news) throws Exception {
        return ResponseEntity.ok(newsService.post(news));
    }

    @PutMapping("/{id}")
    public ResponseEntity<News> updateNews(@PathVariable Long id, @RequestBody News news) throws Exception {
        return ResponseEntity.ok(newsService.update(id, news));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) throws Exception {
        newsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
