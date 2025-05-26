package com.bookreum.dev.domain.club.service;

import org.springframework.data.domain.Pageable; 
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.bookreum.dev.domain.club.entity.ClubCommentEntity;
import com.bookreum.dev.domain.club.entity.ClubEntity;
import com.bookreum.dev.domain.club.entity.CommentLikeEntity;
import com.bookreum.dev.domain.club.repository.ClubCommentRepository;
import com.bookreum.dev.domain.club.repository.CommentLikeRepository;
import com.bookreum.dev.domain.user.UserEntity;
import com.bookreum.dev.domain.user.UserService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClubCommentService {

    private final ClubCommentRepository commentRepository;
    private final ClubService clubService;
    private final UserService userService;
    private final CommentLikeRepository likeRepository;
    
    /** 댓글 등록 */
    @Transactional
    public ClubCommentEntity addComment(Integer clubId, Integer userId, String content) {
        ClubEntity club = clubService.getClub(clubId);
        UserEntity author = userService.getUserEntity(userId);
        return commentRepository.save(
            ClubCommentEntity.builder().club(club).author(author).content(content).build()
        );
    }

    /** 전체 댓글 조회 */
    @Transactional(readOnly = true)
    public List<ClubCommentEntity> listComments(Integer clubId) {
        ClubEntity club = clubService.getClub(clubId);
        return commentRepository.findByClubWithAuthorOrderByCreatedAtAsc(club);
    }

    /** 페이징 댓글 조회 */
    @Transactional(readOnly = true)
    public Page<ClubCommentEntity> listComments(Integer clubId, Pageable pageable) {
        ClubEntity club = clubService.getClub(clubId);
        return commentRepository.findByClubOrderByCreatedAtAsc(club, pageable);
    }

    /** 댓글 단건 조회 */
    @Transactional(readOnly = true)
    public ClubCommentEntity getCommentEntity(Integer commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 ID: " + commentId));
    }

    /** 댓글 수정 */
    @Transactional
    public ClubCommentEntity updateComment(Integer commentId, String content) {
        ClubCommentEntity comment = commentRepository.findByIdWithAuthor(commentId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글 ID: " + commentId));
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    /** 댓글 삭제 */
    @Transactional
    public void deleteComment(Integer commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new IllegalArgumentException("존재하지 않는 댓글 ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }
    /** 댓글 좋아요 */
    @Transactional
    public void likeComment(Integer commentId, Integer userId) {
        ClubCommentEntity comment = getCommentEntity(commentId);
        UserEntity user = userService.getUserEntity(userId);
        if (likeRepository.existsByCommentAndUser(comment, user)) {
            throw new IllegalStateException("이미 좋아요한 댓글입니다.");
        }
        likeRepository.save(CommentLikeEntity.builder()
            .comment(comment)
            .user(user)
            .build());
    }

    /** 댓글 좋아요 취소 */
    @Transactional
    public void unlikeComment(Integer commentId, Integer userId) {
        ClubCommentEntity comment = getCommentEntity(commentId);
        UserEntity user = userService.getUserEntity(userId);
        CommentLikeEntity like = likeRepository.findByCommentAndUser(comment, user)
            .orElseThrow(() -> new IllegalStateException("좋아요가 존재하지 않습니다."));
        likeRepository.delete(like);
    }

    /** 댓글 좋아요 개수 조회 */
    @Transactional(readOnly = true)
    public long countLikes(Integer commentId) {
        ClubCommentEntity comment = getCommentEntity(commentId);
        return likeRepository.countByComment(comment);
    }
}

