# 게시판 프로젝트
React와 Spring Boot를 활용한 SPA 방식의 간단한 게시판 CRUD 프로젝트입니다. <br/> <br/>
frontend Repository: https://github.com/rladbwlsdlwl/Blog-ReactSpring-frontend <br/>
backend Repository: https://github.com/rladbwlsdlwl/Blog-ReactSpring-backend-prod <br/>

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
<img width="961" height="329" alt="myblog-arc" src="https://github.com/user-attachments/assets/1d7319f9-972d-4bea-853b-240c24fd2d21" />

# 도메인 정의 (ERD)
<img width="941" height="750" alt="myblogdb-erd" src="https://github.com/user-attachments/assets/2b78756e-03c1-444f-839e-a2524a6751a8" />

# API 명세서
회원 명세서
<img width="1111" height="892" alt="Member" src="https://github.com/user-attachments/assets/404bdfc5-72a7-45a5-9a21-587299e210fd" />


게시글 명세서
<img width="1100" height="577" alt="Board" src="https://github.com/user-attachments/assets/65cf9c70-a747-45f5-a69c-8f2e0dd44a0a" />


파일 명세서
<img width="1102" height="808" alt="File" src="https://github.com/user-attachments/assets/475605bf-da1b-4ff5-be74-f324cd540d0f" />


좋아요 명세서
<img width="1102" height="310" alt="Likes" src="https://github.com/user-attachments/assets/d85848b4-6e7d-49c0-a3fc-5a9355a45f88" />


댓글 명세서
<img width="1102" height="310" alt="image" src="https://github.com/user-attachments/assets/f4cf323c-a027-445c-8744-0045c5d3b02e" />


# 트러블 슈팅
- [N+1](https://proximal-paint-99f.notion.site/N-1-972a5ec80b9442b19d9d3731aa0e3e14)
- [보상 트랜잭션](https://proximal-paint-99f.notion.site/75f1afe815484e8eaac765674e652efd)
- [이미지 처리 최적화](https://proximal-paint-99f.notion.site/Base64-Blob-ca716948251847fea3f75ed269b84bb1?pvs=73)
- [네트워크 최적화](https://proximal-paint-99f.notion.site/PromiseAll-16f006cea10180308ddcfb1cab27ae7a?pvs=73)
