package com.nova.nova_server.domain.cardNews.controller;

import com.nova.nova_server.domain.cardNews.docs.CardNewsHiddenControllerDocs;
import com.nova.nova_server.domain.cardNews.service.CardNewsHiddenService;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cardnews")
public class CardNewsHiddenController implements CardNewsHiddenControllerDocs {

    private final CardNewsHiddenService cardNewsHiddenService;

    @PostMapping("/{cardNewsId}/hidden")
    public ResponseEntity<ApiResponse<Void>> hideCardNews(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long cardNewsId
    ) {
        cardNewsHiddenService.hideCardNews(memberId, cardNewsId);
        return ResponseEntity.ok(ApiResponse.successWithNoData());
    }

    @DeleteMapping("/hidden")
    public ResponseEntity<ApiResponse<Void>> deleteAllHiddenCardNews(
            @AuthenticationPrincipal Long memberId
    ) {
        cardNewsHiddenService.deleteAllHiddenCardNews(memberId);
        return ResponseEntity.ok(ApiResponse.successWithNoData());
    }

}
