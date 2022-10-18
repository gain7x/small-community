package com.practice.smallcommunity.interfaces.member;

import com.practice.smallcommunity.application.member.MemberService;
import com.practice.smallcommunity.domain.member.Member;
import com.practice.smallcommunity.interfaces.BaseResponse;
import com.practice.smallcommunity.interfaces.CurrentUser;
import com.practice.smallcommunity.interfaces.member.dto.MemberPasswordChangeRequest;
import com.practice.smallcommunity.interfaces.member.dto.MemberResponse;
import com.practice.smallcommunity.interfaces.member.dto.MemberUpdateRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper mapper;

    @GetMapping("/{userId}")
    public BaseResponse<MemberResponse> find(@PathVariable Long userId) {
        Member result = memberService.findByUserId(userId);
        return BaseResponse.Ok(mapper.toDto(result));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping
    public void update(@CurrentUser Long loginId, @Valid @RequestBody MemberUpdateRequest dto) {
        Member result = memberService.update(loginId, dto.getNickname());

        log.info("Member information has been updated. id: {}, email: {}, nickname: {}", loginId,
            result.getEmail(), result.getNickname());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/password")
    public void changePassword(@CurrentUser Long loginId, @Valid @RequestBody
    MemberPasswordChangeRequest dto) {
        Member result = memberService.changePassword(loginId, dto.getCurrentPassword(),
            dto.getNewPassword());

        log.info("Member password has been changed. id: {}, email: {}", loginId, result.getEmail());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void withdrawal(@CurrentUser Long loginId) {
        Member result = memberService.withdrawal(loginId);

        log.info("Member has been withdrawn. id: {}, email: {}", loginId, result.getEmail());
    }
}
