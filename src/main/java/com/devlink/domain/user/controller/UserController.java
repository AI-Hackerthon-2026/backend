package com.devlink.domain.user.controller;

import com.devlink.domain.user.dto.UserRequestDto;
import com.devlink.domain.user.dto.UserResponseDto;
import com.devlink.domain.user.service.UserService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 컨트롤러
 *
 * @since : 2026.05.16
 * @version : 0.0.1
 * @author : DevLink Team
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController {

	private final UserService userService;

	/**
	 * 사용자 등록 API
	 *
	 * @param requestDto 사용자 등록 요청
	 * @return 등록된 사용자 정보
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
	public ApiResponse<UserResponseDto> registerUser(@RequestBody UserRequestDto requestDto) {
		return ApiResponse.success("사용자가 등록되었습니다.", userService.registerUser(requestDto));
	}

	/**
	 * 사용자 단건 조회 API
	 *
	 * @param userId 사용자 ID
	 * @return 사용자 정보
	 */
	@GetMapping("/{userId}")
	@Operation(summary = "사용자 조회", description = "사용자 ID로 사용자 정보를 조회합니다.")
	public ApiResponse<UserResponseDto> getUserById(@PathVariable Long userId) {
		return ApiResponse.success(userService.getUserById(userId));
	}
}