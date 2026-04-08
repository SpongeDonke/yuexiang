package com.washplatform.controller;

import com.washplatform.entity.Carousel;
import com.washplatform.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carousels")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;

    @GetMapping
    public List<Carousel> findAll() {
        return carouselService.findAll();
    }

    @GetMapping("/status/{status}")
    public List<Carousel> findByStatus(@PathVariable Integer status) {
        return carouselService.findByStatus(status);
    }

    @GetMapping("/{id}")
    public Carousel findById(@PathVariable Integer id) {
        return carouselService.findById(id);
    }

    @PostMapping
    public Carousel save(@RequestBody Carousel carousel) {
        return carouselService.save(carousel);
    }

    @PutMapping("/{id}")
    public Carousel update(@PathVariable Integer id, @RequestBody Carousel carousel) {
        carousel.setId(id);
        return carouselService.save(carousel);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        carouselService.deleteById(id);
    }
}