package com.washplatform.repository;

import com.washplatform.entity.Carousel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarouselRepository extends JpaRepository<Carousel, Integer> {
    List<Carousel> findByStatusOrderBySortAsc(Integer status);
}