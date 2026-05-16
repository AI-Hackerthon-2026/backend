package com.devlink.domain.like.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 공감 토글 응답 DTO
 *
 * @since 2026.05.16
 * @version 1.0.0
 * @author 신태훈, 조하겸
 */
@Getter
@Builder
public class LikeResponse {

	/** 공감 여부 (true: 공감 추가, false: 공감 취소) */
	private boolean isLiked;

	/** 현재 공감 수 */
	private int likeCount;
}
