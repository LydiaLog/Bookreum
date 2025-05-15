package com.bookreum.dev.domain.club.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bookreum.dev.domain.club.entity.ClubEntity;

@Repository
public interface  ClubRepository extends JpaRepository<ClubEntity, Integer> {

}
