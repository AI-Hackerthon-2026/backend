package com.devlink.domain.image.controller;

import com.devlink.domain.image.dto.ImageUploadResponse;
import com.devlink.domain.image.service.ImageService;
import com.devlink.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 이미지 업로드 컨트롤러
 * 포트폴리오 썸네일 및 마크다운 본문 이미지 업로드
 *
 * @since 2026.05.17
 * @version 1.0.0
 * @author 최준혁
 */
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Tag(name = "Image", description = "이미지 업로드 API")
public class ImageController {

	private final ImageService imageService;

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "이미지 업로드", description = "포트폴리오 썸네일 또는 마크다운 본문에 삽입할 이미지를 업로드합니다. (최대 5MB, jpg/png/gif/webp)")
	public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
		@RequestParam("file") MultipartFile file) {
		String imageUrl = imageService.upload(file);
		return ResponseEntity.ok(ApiResponse.success("이미지가 성공적으로 업로드되었습니다.", ImageUploadResponse.of(imageUrl)));
	}
}