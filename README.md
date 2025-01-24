# 게시판 프로젝트
React와 Spring Boot를 활용한 SPA 방식의 간단한 게시판 CRUD 프로젝트입니다. <br/>

# 프로젝트 소개
본 프로젝트는 기본적인 게시글 작성 기능과 이미지 파일 업로드 기능을 제공합니다.<br/>  
프로젝트를 진행하며 다양한 문제를 직면하였고, 이를 해결하기 위해 할 수 있는 범위 내에서 개선 작업을 진행하여 성능 최적화를 이루었습니다.

# 주요 기능
- Spring Security의 JWT를 활용한 회원 인증 및 인가
- Spring Security의 OAuth2를 활용한 소셜 로그인
- Spring의 Global Controller Advice를 활용한 예외 처리
- JdbcTemplate과 Spring Data JPA를 활용한 데이터 처리

# 개발 환경
| Package            | Version   |
|--------------------|-----------|
| Node.js            | 20.15.0  |
| React              | 18.3.10   |
| Java                 | 17.0.10   |
| Spring Data JPA      | 3.2.5     |
| Spring Security      | 3.2.5     |
| Spring OAuth2        | 3.2.5     |
| JJWT Jackson         | 0.11.5    |
| SLF4J                | 2.0.13    |
| Lombok               | 1.18.32   |
| MySQL                | 8.3.0     |

# 시스템 아키텍처
![blog-architecture](https://github.com/user-attachments/assets/8ec042f3-902d-4299-8d1c-0a26423a8c2d)

# 도메인 정의 (ERD)
![blog-erd](https://github.com/user-attachments/assets/124238c2-6199-47e7-a5ea-2362ef1f6d02)

# API 명세서
회원 명세서
![스크린샷 2024-12-26 174548](https://github.com/user-attachments/assets/55df03ab-6b96-445f-b868-6cff315ce830)

게시글 명세서
![스크린샷 2024-12-26 175121](https://github.com/user-attachments/assets/0f1d0920-d98d-4bc0-92bb-9b045fa1859f)

파일 명세서
![스크린샷 2024-12-26 175400](https://github.com/user-attachments/assets/94921961-c68f-4fe2-9e79-864d64bb92c9)

좋아요 명세서
![스크린샷 2024-12-26 175516](https://github.com/user-attachments/assets/1011711d-4674-4eae-96da-de8020e5f529)

댓글 명세서
![스크린샷 2024-12-26 175632](https://github.com/user-attachments/assets/a5270732-bbda-4099-878c-d43a41c5a984)

# 트러블 슈팅
- N+1
- 보상 트랜잭션
- 리소스 최적화 (Base64 to Blob)
- 네트워크 최적화 (PromiseAll)
