package com.practice.smallcommunity.attachment.domain;

import com.practice.smallcommunity.common.domain.BaseTimeEntity;
import com.practice.smallcommunity.member.domain.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UploadFile extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "upload_file_seq_gen")
    @SequenceGenerator(name = "upload_file_seq_gen", sequenceName = "upload_file_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member uploader;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String objectKey;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private long fileSize;

    @Builder
    public UploadFile(Member uploader, String bucket, String objectKey, String url, String originalFilename, long fileSize) {
        this.uploader = uploader;
        this.bucket = bucket;
        this.objectKey = objectKey;
        this.originalFilename = originalFilename;
        this.url = url;
        this.fileSize = fileSize;
    }
}
