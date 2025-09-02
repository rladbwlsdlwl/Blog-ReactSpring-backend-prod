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
![myblog-arc.png](attachment:5f81bba9-4220-4804-a10b-77faca36256e:myblog-arc.png)

# 도메인 정의 (ERD)
![myblogdb-erd.png](attachment:b7d5a3c6-06f6-4574-a5d4-c2a7016fb8df:myblogdb-erd.png)

# API 명세서
회원 명세서
![image.png](attachment:969a0543-cfe9-46a5-b6da-9ce9cb8e7271:image.png)

게시글 명세서
![image.png](attachment:34840f15-2144-495f-bafd-35f8c885516e:image.png)

파일 명세서
![image.png](attachment:4ed6df4a-2e00-4812-8a28-9cf5660ee8b6:image.png)

좋아요 명세서
![image.png](attachment:0ea67ea8-af88-4c44-8e61-09ca3574cb99:image.png)

댓글 명세서
![image.png](attachment:7f100aa0-9936-43c4-8dd2-ebc9e4b5adf5:image.png)

# 트러블 슈팅
- [N+1](https://proximal-paint-99f.notion.site/N-1-972a5ec80b9442b19d9d3731aa0e3e14)
- [보상 트랜잭션](https://proximal-paint-99f.notion.site/75f1afe815484e8eaac765674e652efd)
- [이미지 처리 최적화](https://proximal-paint-99f.notion.site/Base64-Blob-ca716948251847fea3f75ed269b84bb1?pvs=73)
- [네트워크 최적화](https://proximal-paint-99f.notion.site/PromiseAll-16f006cea10180308ddcfb1cab27ae7a?pvs=73)
