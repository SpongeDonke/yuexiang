package com.washplatform.repository;

import com.washplatform.entity.WashType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WashTypeRepository extends JpaRepository<WashType, Integer> {
}