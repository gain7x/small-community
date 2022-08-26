package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.dto.PostDto;
import com.practice.smallcommunity.application.exception.ValidationError;
import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.application.exception.ValidationErrorStatus;
import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.PostRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {

    private final PostRepository postRepository;

    /**
     * 게시글을 등록하고, 성공하면 등록된 게시글 정보를 반환합니다.
     * @param board 게시판
     * @param writer 회원( 작성자 )
     * @param dto 게시글 정보
     * @return 등록된 게시글
     * @throws ValidationErrorException
     *          등록 정보가 유효하지 않은 경우( 존재하지 않는 카테고리 OR 회원, ... )
     */
    public Post write(Board board, Member writer, PostDto dto) {
        Post post = Post.builder()
            .board(board)
            .writer(writer)
            .title(dto.getTitle())
            .content(new Content(writer, dto.getText()))
            .build();

        return postRepository.save(post);
    }

    /**
     * 삭제 상태가 아닌 게시글을 조회합니다.
     * @param postId 게시글 ID
     * @return 게시글
     * @throws ValidationErrorException
     *          ID가 일치하는 게시글이 없거나, 삭제 상태인 경우
     */
    @Transactional(readOnly = true)
    public Post findEnabledPost(Long postId) {
        Optional<Post> findPost = postRepository.findById(postId);
        if (findPost.isEmpty() || !findPost.get().isEnable()) {
            throw new ValidationErrorException("게시글을 찾을 수 없습니다.",
                ValidationError.of(ValidationErrorStatus.NOT_FOUND, "postId"));
        }

        return findPost.get();
    }

    /**
     * 게시글을 수정하고, 성공하면 수정된 게시글을 반환합니다.
     * @param postId 게시글 ID
     * @param dto 수정 정보
     * @return 수정된 게시글
     * @throws ValidationErrorException
     *          정보가 유효하지 않은 경우( 존재하지 않는 게시글 ID, ... )
     */
    public Post update(Long postId, PostDto dto) {
        Post findPost = findEnabledPost(postId);
        findPost.updateTitle(dto.getTitle());
        findPost.updateContent(dto.getText());
        return findPost;
    }

    /**
     * 게시글을 삭제 상태로 변경합니다.
     * @param postId 게시글 ID
     */
    public void delete(Long postId) {
        Post findPost = findEnabledPost(postId);
        findPost.delete();
    }
}
