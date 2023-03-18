package com.practice.smallcommunity.post.application;

import com.practice.smallcommunity.post.application.dto.PostDto;
import com.practice.smallcommunity.common.exception.BusinessException;
import com.practice.smallcommunity.common.exception.ErrorCode;
import com.practice.smallcommunity.category.Category;
import com.practice.smallcommunity.member.Member;
import com.practice.smallcommunity.post.domain.Post;
import com.practice.smallcommunity.post.domain.PostRepository;
import com.practice.smallcommunity.reply.Reply;
import com.practice.smallcommunity.utils.SecurityUtil;
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
     * @throws BusinessException
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
     * @throws BusinessException
     *          ID가 일치하는 게시글이 없거나, 삭제 상태인 경우
     */
    @Transactional(readOnly = true)
    public Post findPost(Long postId) {
        return postRepository.findByIdAndEnableIsTrue(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POST));
    }

    /**
     * 삭제 상태가 아닌 게시글을 조회합니다.
     *  본문을 페치조인으로 가져옵니다.
     * @param postId 게시글 ID
     * @return 게시글
     * @throws BusinessException
     *          ID가 일치하는 게시글이 없거나, 삭제 상태인 경우
     */
    @Transactional(readOnly = true)
    public Post findPostFetchMainText(Long postId) {
        return postRepository.findPostFetchJoin(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_POST));
    }

    /**
     * 삭제 상태가 아닌 게시글을 조회합니다.
     *  본문을 페치조인으로 가져오며, 로그인 회원이 조회한 경우 조회수를 1 증가시킵니다.
     * @param postId 게시글 ID
     * @return 게시글
     * @throws BusinessException
     * ID가 일치하는 게시글이 없거나, 삭제 상태인 경우
     */
    public Post viewPost(Long postId) {
        Post findPost = findPostFetchMainText(postId);
        if (SecurityUtil.isLoggedIn()) {
            findPost.increaseViewCount();
        }

        return findPost;
    }

    /**
     * 게시글을 수정하고, 성공하면 수정된 게시글을 반환합니다.
     * @param postId 게시글 ID
     * @param loginId 게시글을 수정하려는 현재 회원 ID
     * @param dto 수정 정보
     * @return 수정된 게시글
     * @throws BusinessException
     *          게시글 작성자가 아닌 경우,
     *          정보가 유효하지 않은 경우( 존재하지 않는 게시글 ID, ... ),
     */
    public Post update(Long postId, Long loginId, PostDto dto) {
        Post findPost = findPostFetchMainText(postId);
        validatePostWriter(findPost, loginId);
        findPost.updateTitle(dto.getTitle());
        findPost.updateContent(dto.getText());

        return findPost;
    }

    /**
     * 게시글을 삭제 상태로 변경합니다.
     *  관리자는 본인이 작성하지 않은 게시글도 삭제할 수 있습니다.
     * @param postId 게시글 ID
     * @param loginId 게시글을 삭제하려는 현재 회원 ID( 작성자 )
     * @throws BusinessException
     *          ID가 일치하는 게시글이 없는 경우,
     *          게시글 작성자가 아닌 경우
     */
    public void disable(Long postId, Long loginId) {
        Post findPost = findPost(postId);
        if (!SecurityUtil.isAdmin()) {
            validatePostWriter(findPost, loginId);
        }
        findPost.delete();
    }

    /**
     * 답글을 채택합니다.
     * @param postId  게시글 ID
     * @param loginId 답글을 채택하려는 회원 ID( 작성자 )
     * @param reply   채택 대상 답글
     * @throws BusinessException
     *          ID가 일치하는 게시글이 없는 경우,
     *          게시글 작성자가 아닌 경우,
     *          이미 채택된 답글이 있는 경우,
     *          해당 게시글에 작성된 답글이 아닌 경우,
     *          게시글 작성자가 본인의 답글을 채택하는 경우
     */
    public void accept(Long postId, Long loginId, Reply reply) {
        Post findPost = findPost(postId);

        validatePostWriter(findPost, loginId);
        if (findPost.getAcceptedReply() != null) {
            throw new BusinessException(ErrorCode.EXIST_ACCEPTED_REPLY);
        }
        if (!isReplyWriteToPost(postId, reply) || isReplyWriter(loginId, reply)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }

        findPost.accept(reply);
    }

    private boolean isReplyWriteToPost(Long postId, Reply reply) {
        return reply.getPost().getId().equals(postId);
    }

    private boolean isReplyWriter(Long loginId, Reply reply) {
        return reply.getWriter().getId().equals(loginId);
    }

    /**
     * 회원이 게시글 작성자인지 검증합니다.
     * @param post    게시글
     * @param memberId 회원 ID
     */
    private void validatePostWriter(Post post, Long memberId) {
        if (!post.getWriter().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR);
        }
    }
}
