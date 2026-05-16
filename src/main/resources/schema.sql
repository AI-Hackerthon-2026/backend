-- DevLink PostgreSQL 초기화 스크립트
-- 실행 순서: users → portfolios → skills → portfolio_participants → portfolio_skills → likes

-- ===========================
-- 사용자 테이블
-- ===========================
CREATE TABLE IF NOT EXISTS users (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    portal_id  VARCHAR(50)  NOT NULL UNIQUE,
    student_id VARCHAR(9)   NOT NULL UNIQUE,
    grade      INTEGER      NOT NULL,
    github_link VARCHAR(300),
    user_level VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- ===========================
-- 포트폴리오 테이블
-- ===========================
CREATE TABLE IF NOT EXISTS portfolios (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users(id),
    project_name    VARCHAR(100) NOT NULL,
    category        VARCHAR(20)  NOT NULL,
    summary         VARCHAR(300) NOT NULL,
    description     TEXT         NOT NULL,
    thumbnail_url   VARCHAR(500),
    github_link     VARCHAR(300) UNIQUE,
    deployment_link VARCHAR(300),
    start_date      DATE         NOT NULL,
    end_date        DATE         NOT NULL,
    like_count      INTEGER      NOT NULL DEFAULT 0,
    is_deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP,
    updated_at      TIMESTAMP
);

-- ===========================
-- 기술스택 테이블
-- ===========================
CREATE TABLE IF NOT EXISTS skills (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE,
    category   VARCHAR(30),
    created_at TIMESTAMP
);

-- ===========================
-- 포트폴리오 참여자 테이블
-- ===========================
CREATE TABLE IF NOT EXISTS portfolio_participants (
    id           BIGSERIAL PRIMARY KEY,
    portfolio_id BIGINT      NOT NULL REFERENCES portfolios(id),
    user_id      BIGINT      NOT NULL REFERENCES users(id),
    role         VARCHAR(100) NOT NULL,
    can_edit     BOOLEAN      NOT NULL DEFAULT TRUE,
    is_owner     BOOLEAN      NOT NULL DEFAULT FALSE,
    joined_at    TIMESTAMP,
    CONSTRAINT uk_portfolio_user UNIQUE (portfolio_id, user_id)
);

-- ===========================
-- 포트폴리오-기술스택 매핑 테이블
-- ===========================
CREATE TABLE IF NOT EXISTS portfolio_skills (
    id           BIGSERIAL PRIMARY KEY,
    portfolio_id BIGINT NOT NULL REFERENCES portfolios(id),
    skill_id     BIGINT NOT NULL REFERENCES skills(id),
    created_at   TIMESTAMP,
    CONSTRAINT uk_portfolio_skill UNIQUE (portfolio_id, skill_id)
);

-- ===========================
-- 공감(좋아요) 테이블
-- ===========================
CREATE TABLE IF NOT EXISTS likes (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id),
    portfolio_id BIGINT NOT NULL REFERENCES portfolios(id),
    created_at   TIMESTAMP,
    CONSTRAINT uk_user_portfolio UNIQUE (user_id, portfolio_id)
);