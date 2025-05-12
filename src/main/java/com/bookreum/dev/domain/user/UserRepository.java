package com.bookreum.dev.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    // JpaRepository를 상속하면 findById(), save(), deleteById() 등이 자동으로 제공됩니다.
	Optional<UserEntity> findByKakaoId(String kakaoId);
}
