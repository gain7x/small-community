package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.dto.PostDto;
import com.practice.smallcommunity.application.exception.ValidationError;
import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.application.exception.ValidationErrorStatus;
import com.practice.smallcommunity.domain.category.Category;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.PostRepository;
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
     * @param category 카테고리
     * @param writer 회원( 작성자 )
     * @param dto 게시글 정보
     * @return 등록된 게시글
     * @throws ValidationErrorException
     *          등록 정보가 유효하지 않은 경우( 존재하지 않는 카테고리 OR 회원, ... )
     */
    public Post write(Category category, Member writer, PostDto dto) {
        Post post = Post.builder()
            .category(category)
            .writer(writer)
            .title(dto.getTitle())
            .text(dto.getText())
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
        return postRepository.findByIdAndEnableIsTrue(postId)
            .orElseThrow(() -> new ValidationErrorException("게시글을 찾을 수 없습니다.",
                ValidationError.of(ValidationErrorStatus.NOT_FOUND, "postId")));
    }

    /**
     * 게시글을 수정하고, 성공하면 수정된 게시글을 반환합니다.
     * @param postId 게시글 ID
     * @param loginId 게시글을 수정하려는 현재 회원 ID
     * @param dto 수정 정보
     * @return 수정된 게시글
     * @throws ValidationErrorException
     *          게시글 작성자가 아닌 경우,
     *          정보가 유효하지 않은 경우( 존재하지 않는 게시글 ID, ... ),
     */
    public Post update(Long postId, Long loginId, PostDto dto) {
        Post findPost = findEnabledPost(postId);
        validateUpdater(findPost, loginId);
        findPost.updateTitle(dto.getTitle());
        findPost.updateContent(dto.getText());

        return findPost;
    }

    /**
     * 게시글을 삭제 상태로 변경합니다.
     * @param postId 게시글 ID
     * @param loginId 게시글을 삭제하려는 현재 회원 ID
     * @throws ValidationErrorException
     *          게시글 작성자가 아닌 경우,
     *          ID가 일치하는 게시글이 없는 경우
     */
    public void disable(Long postId, Long loginId) {
        Post findPost = findEnabledPost(postId);
        validateUpdater(findPost, loginId);
        findPost.delete();
    }

    private void validateUpdater(Post post, Long loginId) {
        Long writerId = post.getWriter().getId();
        if (!writerId.equals(loginId)) {
            throw new ValidationErrorException("게시글 작성자가 아닙니다.",
                ValidationError.of(ValidationErrorStatus.UNAUTHORIZED));
        }
    }
}
