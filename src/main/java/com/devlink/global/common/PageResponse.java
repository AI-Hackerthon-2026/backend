package com.devlink.global.common;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 페이지 응답 DTO
 * Spring Data Page 결과를 공통 응답 형식으로 변환합니다.
 *
 * @since 2026.05.17
 * @version 1.0.0
 * @author DevLink Team
 */
public class PageResponse<T> {

    private final List<T> content;
    private final int currentPage;
    private final int totalPages;
    private final long totalElements;
    private final int size;

    public PageResponse(List<T> content, int currentPage, int totalPages, long totalElements, int size) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize());
    }

    public List<T> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getSize() {
        return size;
    }
}
