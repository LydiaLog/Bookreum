package com.bookreum.dev.domain.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  ClubRepository extends JpaRepository<ClubEntity, Long> {

}
