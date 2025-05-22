package com.jvmd.anidromvost.controllers;

import com.jvmd.anidromvost.model.News;
import com.jvmd.anidromvost.service.NewsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/public/news")
    public ResponseEntity<List<News>> getNews(@RequestParam Long count) {
        return ResponseEntity.ok(newsService.findAllWithCountFilter(count));
    }

    @PostMapping("/api/v1/news/post")
    public ResponseEntity<News> postNews(@RequestBody News news) throws Exception {
        return ResponseEntity.ok(newsService.post(news));
    }

    @PostMapping("/api/v1/news/delete")
    public ResponseEntity<Boolean> deleteNews(@RequestBody News news) throws Exception {
        return ResponseEntity.ok(newsService.delete(news));
    }


}
