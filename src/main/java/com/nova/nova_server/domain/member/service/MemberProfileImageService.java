package com.nova.nova_server.domain.member.service;

import com.nova.nova_server.domain.member.dto.MemberResponseDto;
import com.nova.nova_server.domain.member.entity.Member;
import com.nova.nova_server.domain.member.entity.MemberProfileImage;
import com.nova.nova_server.domain.member.error.MemberErrorCode;
import com.nova.nova_server.domain.member.repository.MemberProfileImageRepository;
import com.nova.nova_server.domain.member.repository.MemberRepository;
import com.nova.nova_server.domain.member.util.ImageProcessor;
import com.nova.nova_server.global.apiPayload.exception.NovaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberProfileImageService {
    private final ImageProcessor imageProcessor;
    private final MemberRepository memberRepository;
    private final MemberProfileImageRepository memberProfileImageRepository;

    @Transactional
    public void uploadProfileImageRaw(Long memberId, MultipartFile file) throws IOException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NovaException(MemberErrorCode.MEMBER_NOT_FOUND));

        log.info("Original size: {} bytes", file.getSize());

        byte[] compressedImage = imageProcessor.compressImage(file);
        
        MemberProfileImage profileImage = memberProfileImageRepository.findById(memberId)
                .orElseGet(() -> MemberProfileImage.builder()
                        .member(member)
                        .build());

        profileImage.updateImage(compressedImage);
        memberProfileImageRepository.save(profileImage);
    }

    @Transactional(readOnly = true)
    public Optional<byte[]> getProfileImageRaw(Long memberId) {
        return memberProfileImageRepository.findById(memberId)
                .map(MemberProfileImage::getImage);
    }

    @Transactional
    public void deleteProfileImage(Long memberId) {
        if (!memberProfileImageRepository.existsById(memberId)) {
            throw new NovaException(MemberErrorCode.MEMBER_NOT_FOUND);
        }
        
        memberProfileImageRepository.deleteById(memberId);
    }
}
