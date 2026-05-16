# DevLink Backend

가천대학교 학생들을 위한 포트폴리오 공유 및 랭킹 플랫폼 백엔드 서버입니다.

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.0 |
| Security | Spring Security (세션 기반) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Build | Gradle |
| Deploy | WAR → Apache Tomcat 10 |
| Proxy | Nginx |
| Infra | GCP (Google Cloud Platform) |
| Docs | SpringDoc OpenAPI (Swagger) |

---

## 주요 기능

### 인증 (Auth)
- 가천대 포털 SSO 연동 (Jsoup 크롤링)
- 세션 기반 로그인 / 로그아웃
- 첫 로그인 시 회원가입 플로우 (학번, 이름, 학년 입력)

### 포트폴리오 (Portfolio)
- 포트폴리오 CRUD
- 카테고리 / 기술스택 / 정렬 필터링 (페이지네이션)
- 참여자 관리 (수정 권한, 소유자 구분)
- 공감(좋아요) 기능
- 학기별 랭킹 (이번 학기 / 지난 학기 / 전체)
- 메인 배너 TOP 3 (학기별 공감 1위)

### 이미지 (Image)
- 이미지 업로드 (jpg, jpeg, png, gif, webp / 최대 5MB)
- MIME 타입 + 확장자 이중 검증
- 업로드 경로: `/tmp/devlink/images/`

### Q&A
- 포트폴리오별 질문 등록 (인증된 사용자)
- 답변 등록 (포트폴리오 참여자만)
- 조회 권한: 참여자는 전체, 일반 사용자는 본인 질문만
- 질문 삭제: 작성자 또는 참여자

### 사용자 (User)
- 내 프로필 조회 / 수정
- 내 포트폴리오 목록 조회
- 사용자 검색

### 기술스택 (Skill)
- 기술스택 목록 조회 (카테고리별)

---

## API 문서

서버 실행 후 아래 URL에서 Swagger UI로 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui.html
```

---

## 로컬 실행

### 요구사항
- Java 21
- PostgreSQL

### 실행
```bash
# Java 21로 빌드 (시스템 Java 버전이 다를 경우)
JAVA_HOME=/path/to/java-21 ./gradlew bootRun
```


---

## WAR 빌드 및 배포

```bash
# WAR 빌드
JAVA_HOME=/path/to/java-21 ./gradlew bootWar

# 결과물
build/libs/devlink-0.0.1-SNAPSHOT.war
```

Tomcat 10 `webapps/` 디렉토리에 배포합니다.

### 서버 설정 (최초 1회)
```bash
# 이미지 저장 디렉토리 생성
sudo mkdir -p /tmp/devlink/images
sudo chmod 777 /tmp/devlink/images

# Tomcat systemd 설정
sudo mkdir -p /etc/systemd/system/tomcat10.service.d
sudo tee /etc/systemd/system/tomcat10.service.d/override.conf << EOF
[Service]
PrivateTmp=false
ReadWritePaths=/tmp
UMask=0022
EOF

sudo systemctl daemon-reload
sudo systemctl restart tomcat10

# nginx 이미지 서빙을 위한 그룹 설정
sudo usermod -aG tomcat www-data
sudo systemctl restart nginx
```

### Nginx 설정
```nginx
server {
    listen 80;
    server_name YOUR_SERVER_IP;

    client_max_body_size 50M;

    location / {
        root /var/www/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /image/ {
        alias /tmp/devlink/images/;
        autoindex off;
    }

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

---

## 프로젝트 구조

```
src/main/java/com/devlink
├── domain
│   ├── auth        # 인증 (포털 SSO 로그인/회원가입)
│   ├── image       # 이미지 업로드
│   ├── like        # 공감
│   ├── portfolio   # 포트폴리오 CRUD / 랭킹
│   ├── qna         # Q&A
│   ├── skill       # 기술스택
│   └── user        # 사용자 프로필
└── global
    ├── common      # ApiResponse, PageResponse
    ├── config      # SecurityConfig, WebConfig
    ├── exception   # GlobalExceptionHandler, ErrorCode
    └── portal      # 가천대 포털 SSO 서비스
```

---

## 학기 기준

| 학기 | 기간 |
|------|------|
| 1학기 | 3월 1일 ~ 8월 31일 |
| 2학기 | 9월 1일 ~ 익년 2월 말일 |

랭킹 및 TOP 조회는 포트폴리오의 **프로젝트 종료일(`end_date`)** 기준으로 학기를 판단합니다.

---

## 팀원

| 이름  | 역할  |
|-----|-----|
| 신태훈 | 백엔드 |
| 조하겸 | 백엔드 |
| 최준혁 | 백엔드 |
| 국승민 | 프론트엔드 |
| 김진우 | 프론트엔드  |
| 박종현 | 프론트엔드  |