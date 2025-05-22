package com.jvmd.anidromvost.service;

import com.jvmd.anidromvost.model.News;
import com.jvmd.anidromvost.repository.NewsRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class NewsService {
    private NewsRepo newsRepo;

    public News post(News news) throws Exception {
        try {
            return  newsRepo.save(news);
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception("failed to save new news", e);
        }
    }


    public News findById(Long id) throws Exception {
        try {
            return newsRepo.findById(id).get();
        }catch (Exception exception){
            log.error("failed to find new news ", exception);
            throw new Exception("news no find");
        }
    }

    public boolean delete(News news){
        try {
            newsRepo.delete(news);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<News> findAllWithCountFilter(Long count){
        return newsRepo.findAll().stream().limit(count).collect(Collectors.toList());
    }
}
