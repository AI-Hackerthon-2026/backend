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
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController {

	private final UserService userService;

	String JsonView = "jsonView";

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "사용자 등록", description = "새로운 사용자를 등록합니다.")
	public ModelAndView registerUser(@RequestBody UserRequestDto requestDto) {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success("사용자가 등록되었습니다.", userService.registerUser(requestDto)));
		return modelAndView;
	}

	@GetMapping("/{userId}")
	@Operation(summary = "사용자 조회", description = "사용자 ID로 사용자 정보를 조회합니다.")
	public ModelAndView getUserById(@PathVariable Long userId) {
		ModelAndView modelAndView = new ModelAndView(JsonView);
		modelAndView.addObject("data", ApiResponse.success(userService.getUserById(userId)));
		return modelAndView;
	}
}