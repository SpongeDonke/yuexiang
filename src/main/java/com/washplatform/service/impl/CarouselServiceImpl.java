package com.washplatform.service.impl;

import com.washplatform.entity.Carousel;
import com.washplatform.repository.CarouselRepository;
import com.washplatform.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CarouselServiceImpl implements CarouselService {
    @Autowired
    private CarouselRepository carouselRepository;

    @Override
    public List<Carousel> findAll() {
        return carouselRepository.findAll();
    }

    @Override
    public List<Carousel> findByStatus(Integer status) {
        return carouselRepository.findByStatusOrderBySortAsc(status);
    }

    @Override
    public Carousel findById(Integer id) {
        return carouselRepository.findById(id).orElse(null);
    }

    @Override
    public Carousel save(Carousel carousel) {
        if (carousel.getId() == null) {
            carousel.setCreatedAt(new Date());
        }
        carousel.setUpdatedAt(new Date());
        return carouselRepository.save(carousel);
    }

    @Override
    public void deleteById(Integer id) {
        carouselRepository.deleteById(id);
    }
}