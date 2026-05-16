package com.devlink.domain.image.service;

import com.devlink.global.exception.CustomException;
import com.devlink.global.exception.ErrorCode;
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
 * 저장 경로: {java.io.tmpdir}/devlink/images/
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

	public static final String UPLOAD_PATH =
		System.getProperty("java.io.tmpdir") + File.separator + "devlink" + File.separator + "images" + File.separator;

	public String upload(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new CustomException(ErrorCode.EMPTY_FILE);
		}

		validateFileType(file);

		try {
			File directory = new File(UPLOAD_PATH);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			String originalFilename = file.getOriginalFilename();
			String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

			Path filePath = Paths.get(UPLOAD_PATH, uniqueFilename);
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

		String ext = extractExtension(file.getOriginalFilename());
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
