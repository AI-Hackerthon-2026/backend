package com.devlink.domain.image.dto;

import lombok.Getter;

/**
 * 이미지 업로드 응답 DTO
 *
 * @since 2026.05.17
 * @version 1.0.0
 * @author 최준혁
 */
@Getter
public class ImageUploadResponse {

	private final String imageUrl;

	private ImageUploadResponse(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public static ImageUploadResponse of(String imageUrl) {
		return new ImageUploadResponse(imageUrl);
	}
}