package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.News;
import com.jvmd.anidromvost.repository.NewsRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class NewsService {
    private NewsRepo newsRepo;

    public News post(News news) {
        return newsRepo.save(news);
    }

    public News findById(Long id) {
        return newsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
    }

    public News update(Long id, News newsData) {
        News existing = findById(id);
        existing.setTitle(newsData.getTitle());
        existing.setContent(newsData.getContent());
        return newsRepo.save(existing);
    }

    public void deleteById(Long id) {
        if (!newsRepo.existsById(id)) {
            throw new RuntimeException("News not found with id: " + id);
        }
        newsRepo.deleteById(id);
    }

    public List<News> findAllWithCountFilter(Long count) {
        return newsRepo.findAll(PageRequest.of(0, count.intValue())).getContent();
    }
}
