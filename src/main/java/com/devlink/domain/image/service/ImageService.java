package com.devlink.domain.image.service;

import com.devlink.global.exception.CustomException;
import com.devlink.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

/**
 * 이미지 업로드 서비스
 * 로컬 파일 시스템에 UUID 기반 파일명으로 저장
 *
 * @since 2026.05.17
 * @version 1.0.0
 * @author 최준혁
 */
@Service
public class ImageService {

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
	private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
		"image/jpeg", "image/png", "image/gif", "image/webp"
	);

	@Value("${file.local.path}")
	private String uploadPath;

	public String upload(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new CustomException(ErrorCode.EMPTY_FILE);
		}

		validateFileType(file);

		try {
			File directory = new File(uploadPath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			String originalFilename = file.getOriginalFilename();
			String ext = extractExtension(originalFilename);
			String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

			Path filePath = Paths.get(uploadPath, uniqueFilename);
			Files.copy(file.getInputStream(), filePath);

			return "/image/" + uniqueFilename;

		} catch (IOException e) {
			throw new CustomException(ErrorCode.FILE_UPLOAD_FAILED);
		}
	}

	private void validateFileType(MultipartFile file) {
		String contentType = file.getContentType();
		if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
			throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
		}

		String originalFilename = file.getOriginalFilename();
		String ext = extractExtension(originalFilename);
		if (!ALLOWED_EXTENSIONS.contains(ext)) {
			throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
		}
	}

	private String extractExtension(String filename) {
		if (filename == null || !filename.contains(".")) {
			return "";
		}
		return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
	}
}