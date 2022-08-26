package com.practice.smallcommunity.application;

import com.practice.smallcommunity.application.exception.ValidationErrorException;
import com.practice.smallcommunity.domain.board.Board;
import com.practice.smallcommunity.domain.content.Content;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.domain.post.Post;
import com.practice.smallcommunity.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final MemberService memberService;
    private final BoardService boardService;
    private final PostRepository postRepository;

    /**
     * 게시글을 등록하고, 성공하면 등록된 게시글 정보를 반환합니다.
     * @param boardId 게시판 ID
     * @param memberId 회원( 작성자 ) ID
     * @param title 제목
     * @param text 내용
     * @return 등록된 게시글
     * @throws ValidationErrorException
     *          등록 정보가 유효하지 않은 경우( 존재하지 않는 카테고리 OR 회원, ... )
     */
    @Transactional
    public Post write(Long boardId, Long memberId, String title, String text) {
        Board board = boardService.findOne(boardId);
        Member writer = memberService.findByUserId(memberId);
        Post post = Post.builder()
            .board(board)
            .writer(writer)
            .title(title)
            .content(new Content(writer, text))
            .build();

        return postRepository.save(post);
    }
}
