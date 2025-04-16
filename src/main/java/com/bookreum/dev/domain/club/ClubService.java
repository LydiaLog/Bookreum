package com.bookreum.dev.domain.club;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubService {
	 private final ClubRepository clubRepository;

	    public List<ClubDTO> getAllClubs() {
	        return clubRepository.findAll().stream()
	                .map(ClubDTO::from)
	                .toList();
	    }

	    public ClubDTO createClub(ClubDTO dto) {
	        ClubEntity club = ClubEntity.builder()
	                .title(dto.getTitle())
	                .description(dto.getDescription())
	                .build();

	        return ClubDTO.from(clubRepository.save(club));
	    }

}
