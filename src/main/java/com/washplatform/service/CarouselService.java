package com.washplatform.service;

import com.washplatform.entity.Carousel;

import java.util.List;

public interface CarouselService {
    List<Carousel> findAll();
    List<Carousel> findByStatus(Integer status);
    Carousel findById(Integer id);
    Carousel save(Carousel carousel);
    void deleteById(Integer id);
}