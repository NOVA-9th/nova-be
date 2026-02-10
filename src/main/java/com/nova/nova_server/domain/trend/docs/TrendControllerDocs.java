package com.nova.nova_server.domain.trend.docs;

import com.nova.nova_server.domain.trend.dto.TrendResponse;
import com.nova.nova_server.domain.trend.dto.SkillTrendResponse;
import com.nova.nova_server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Trend API", description = "트렌드 관련 API")
public interface TrendControllerDocs {

    @Operation(summary = "실시간 트렌드 키워드 순위 조회", description = "최근 7일간의 키워드 언급량을 분석하여 트렌드 키워드 순위를 조회합니다.")
    ApiResponse<TrendResponse> getTopKeywords();

    @Operation(summary = "관심사별 기술 스택 트렌드 조회", description = "전체 기간 동안의 관심사별 언급량을 분석하고, 각 관심사 내에서 인기 있는 키워드 언급량을 조회합니다.")
    ApiResponse<SkillTrendResponse> getSkillTrend();
}
