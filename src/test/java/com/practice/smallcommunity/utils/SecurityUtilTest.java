package com.practice.smallcommunity.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.testutils.TestSecurityUtil;
import org.junit.jupiter.api.Test;

class SecurityUtilTest {

    @Test
    void 현재_사용자가_관리자_권한을_가진_경우_TRUE를_반환한다() {
        //given
        TestSecurityUtil.setAdminAuthentication();

        //when
        //then
        assertThat(SecurityUtil.isAdmin()).isTrue();
    }

    @Test
    void 현재_사용자가_관리자_권한이_없으면_FALSE를_반환한다() {
        //given
        TestSecurityUtil.setUserAuthentication();

        //when
        //then
        assertThat(SecurityUtil.isAdmin()).isFalse();
    }
}