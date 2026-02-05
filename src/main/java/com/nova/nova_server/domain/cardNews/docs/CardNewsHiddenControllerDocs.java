package com.nova.nova_server.domain.cardNews.docs;

import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "카드뉴스 API")
public interface CardNewsHiddenControllerDocs {

    @Operation(
            summary = "카드뉴스 숨기기",
            description = "카드뉴스를 숨김 처리합니다.<br/>" +
                    "숨긴 카드뉴스는 피드에 표시되지 않습니다."
    )
    ResponseEntity<ApiResponse<Void>> hideCardNews(
            @AuthenticationPrincipal Long memberId,
            @Parameter(description = "카드뉴스 ID")
            @PathVariable Long cardNewsId
    );

    @Operation(
            summary = "카드뉴스 숨김 내역 초기화",
            description = "현재 로그인된 유저가 숨김 처리했던 내역을 모두 삭제합니다."
    )
    ResponseEntity<ApiResponse<Void>> deleteAllHiddenCardNews(
            @AuthenticationPrincipal Long memberId
    );

}
