package com.bookreum.dev.domain.user;

import com.bookreum.dev.domain.post.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	//내가 쓴 글 목록
	@GetMapping("/{userId}/posts")
	public List<PostDto.Response> getMyPosts(@PathVariable Integer userId){
		return userService.getPostsByUser(userId);  // 여기 수정
	}
	
	//내가 마음 누른 글 목록
	@GetMapping("/{userId}/liked-posts")
	public List<PostDto.Response> getLikedPosts(@PathVariable Integer userId){
		return userService.getLikedPosts(userId);
	}
}
