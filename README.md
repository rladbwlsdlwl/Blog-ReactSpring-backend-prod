# 블로그 프로젝트
React와 Spring Boot를 활용한 SPA 블로그 CRUD 프로젝트입니다. <br/> <br/>
frontend Repository: https://github.com/rladbwlsdlwl/Blog-ReactSpring-frontend <br/>
backend Repository: https://github.com/rladbwlsdlwl/Blog-ReactSpring-backend-prod <br/>

# 프로젝트 소개
본 프로젝트는 기본적인 게시글 CRUD 및 이미지 파일 업로드 기능을 제공하며, <br/>  
개발 과정에서 발생한 성능 이슈와 구조적 문제를 해결하기 위해
***쿼리 최적화, 페이징 방식 개선, 파일 처리 최적화*** 등의 리팩토링을 진행했습니다. <br/>  
단순 기능 구현에 그치지 않고, 안정성과 효율성을 고려한 구조 개선을 지속적으로 수행하고 있습니다.

# 트러블 슈팅
- [N+1](https://proximal-paint-99f.notion.site/N-1-972a5ec80b9442b19d9d3731aa0e3e14)
- [이미지 처리 최적화](https://proximal-paint-99f.notion.site/Base64-Blob-ca716948251847fea3f75ed269b84bb1?pvs=73)
- [대용량 페이징 최적화](https://proximal-paint-99f.notion.site/PromiseAll-16f006cea10180308ddcfb1cab27ae7a?pvs=73)
- [테이블 관계 최적화](https://proximal-paint-99f.notion.site/2e6006cea1018042a4f3e15567cd2730?pvs=73)

# 주요 기능
- Spring Security의 JWT를 활용한 회원 인증 및 인가
- Spring Security의 OAuth2를 활용한 소셜 로그인
- Spring의 Global Controller Advice를 활용한 예외 처리
- JdbcTemplate과 Spring Data JPA를 활용한 데이터 처리
- Mockito와 Spring boot test 활용한 단위테스트 및 통합 테스트

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

# CI/CD 아키텍처
<img width="1614" height="695" alt="BLOG_SYS_ARC" src="https://github.com/user-attachments/assets/e23e9c12-66f3-4cb0-a367-d646c79a701e" />

# 도메인 정의 (ERD)
<img width="1245" height="737" alt="image" src="https://github.com/user-attachments/assets/8fe8b318-8240-494c-b2ef-e88c1bdb18a2" />



# API 명세서
회원 명세서
<img width="1111" height="892" alt="Member" src="https://github.com/user-attachments/assets/404bdfc5-72a7-45a5-9a21-587299e210fd" />


게시글 명세서
<img width="1220" height="578" alt="image" src="https://github.com/user-attachments/assets/bd6283af-04cc-4a12-851d-224f05939326" />



파일 명세서
<img width="1227" height="381" alt="image" src="https://github.com/user-attachments/assets/67904b87-e683-4c20-b398-34371675117e" />


좋아요 명세서
<img width="1227" height="257" alt="image" src="https://github.com/user-attachments/assets/473b6e20-3957-4887-908b-019881a8f84d" />



댓글 명세서
<img width="1102" height="310" alt="image" src="https://github.com/user-attachments/assets/f4cf323c-a027-445c-8744-0045c5d3b02e" />



해시태그 명세서
<img width="1222" height="90" alt="image" src="https://github.com/user-attachments/assets/2448b6dd-53b2-46f5-bc7c-e570d2e5bf37" />
