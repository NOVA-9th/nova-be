package com.nova.nova_server.infra;

import com.nova.nova_server.global.apiPayload.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

	@GetMapping("/health")
	public ApiResponse<String> health() {
		return ApiResponse.success("OK");
	}
}