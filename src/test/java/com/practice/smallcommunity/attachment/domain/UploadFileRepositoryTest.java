package com.practice.smallcommunity.attachment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.practice.smallcommunity.member.domain.Member;
import com.practice.smallcommunity.member.domain.MemberRepository;
import com.practice.smallcommunity.testutils.DomainGenerator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class UploadFileRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    UploadFileRepository uploadFileRepository;

    Member uploader = DomainGenerator.createMember("A");
    UploadFile uploadFile = DomainGenerator.createUploadFile(uploader, "A");

    @BeforeEach
    void setUp() {
        memberRepository.save(uploader);
    }

    @Test
    void 저장_및_조회() {
        //when
        uploadFileRepository.save(uploadFile);
        em.flush();
        em.clear();
        UploadFile findItem = uploadFileRepository.findById(uploadFile.getId()).orElseThrow();

        //then
        assertThat(uploadFile.getId()).isEqualTo(findItem.getId());
        assertThat(uploadFile.getOriginalFilename()).isEqualTo(findItem.getOriginalFilename());
    }

    @Test
    void 여러개_저장_및_조회() {
        //given
        UploadFile uploadFile2 = DomainGenerator.createUploadFile(uploader,"B");

        //when
        uploadFileRepository.save(uploadFile);
        uploadFileRepository.save(uploadFile2);

        long count = uploadFileRepository.count();
        List<UploadFile> all = uploadFileRepository.findAll();

        //then
        assertThat(count).isEqualTo(2);
        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 삭제() {
        //given
        uploadFileRepository.save(uploadFile);

        //when
        UploadFile findItem = uploadFileRepository.findById(uploadFile.getId()).orElseThrow();
        uploadFileRepository.delete(findItem);

        //then
        assertThat(uploadFileRepository.count()).isEqualTo(0);
    }
}