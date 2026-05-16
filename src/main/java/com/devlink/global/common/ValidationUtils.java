package com.devlink.global.common;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 공통 입력 검증 유틸리티
 * URL 형식, GitHub 링크 형식 등 서버 측 검증에 사용합니다.
 *
 * @since 2026.05.17
 * @version 1.0.0
 */
public class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean isValidUrl(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        try {
            URI uri = new URI(value.trim());
            String scheme = uri.getScheme();
            String host = uri.getHost();
            return scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    && host != null && !host.isBlank();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static boolean isValidGitHubLink(String value) {
        if (!isValidUrl(value)) {
            return false;
        }

        try {
            URI uri = new URI(value.trim());
            String host = uri.getHost().toLowerCase();
            String path = uri.getPath();
            return (host.equals("github.com") || host.endsWith(".github.com"))
                    && path != null && !path.isBlank() && !path.equals("/");
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
