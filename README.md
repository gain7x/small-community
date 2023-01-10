<div align="center">
  <img src="https://user-images.githubusercontent.com/51999730/211035805-d3482114-791d-4d03-b059-c2e80ba81df9.png" alt="Logo" width="80" height="80" />
  <h3>작은 커뮤니티</h3>
  <p>
    커뮤니티 기능을 REST API로 제공하는 백엔드 애플리케이션입니다.
  </p>
  <a href="https://cijtoy.com">Site</a>
  |
  <a href="https://api.cijtoy.com/docs/index.html">Document</a>
</div>

<details>
  <summary>목차</summary>
  <ol>
    <li>
      <a href="#소개">소개</a>
      <ul>
        <li><a href="#기술-스택">기술스택</a></li>
        <li><a href="#배포-아키텍처">배포 아키텍처</a></li>
      </ul>
    </li>
    <li>
      <a href="#기능">기능</a>
      <ul>
        <li>
          <a href="#목록">목록</a>
          <ul>
            <li><a href="#인증-및-회원">인증 및 회원</a></li>
            <li><a href="#컨텐츠">컨텐츠</a></li>
            <li><a href="#부가기능">부가기능</a></li>
            <li><a href="#관리자">관리자</a></li>
          </ul>
        </li>
        <li><a href="#설명">설명</a></li>
      </ul>
    </li>
    <li>
      <a href="#참고사항">참고사항</a>
      <ul>
        <li><a href="#개발-환경">개발 환경</a></li>
        <li><a href="#테스트">테스트</a></li>
        <li><a href="#api-문서화">API 문서화</a></li>
      </ul>
    </li>
  </ol>
</details>

# 소개
작은 커뮤니티 프로젝트는 게시판, 답글 작성, 답글 채택, 게시글/답변 투표 등 일반적인 커뮤니티 기능을 제공합니다.  
다양한 기능을 제공하기보단 학습했던 기술을 실제로 사용해보고, 피드백하는 것에 중점을 두었습니다.
![작은_커뮤니티_대시보드](https://user-images.githubusercontent.com/51999730/211485557-8e93bfc1-fc68-4145-bc88-1dbd6f19d162.png)

## 기술 스택
![작은_커뮤니티_백엔드_기술스택](https://user-images.githubusercontent.com/51999730/211020479-01c788ca-abba-4938-90fb-022c94f30b30.png)

## 배포 아키텍처
배포는 AWS를 이용하였으며 사용한 주 서비스는 다음과 같습니다.
- Elastic Beanstalk
- CloudFront
- RDS
- Redis용 Amazon MemoryDB
- CodePipeline
  ![작은_커뮤니티_백엔드_배포_아키텍처](https://user-images.githubusercontent.com/51999730/211025909-48d150e6-216b-4e78-90a0-a050f5de85b4.png)
  Route53은 파일 서버( `files.cijtoy.com` ) 접근인 경우 CloudFront에 전달하고, API( `api.cijtoy.com` ) 호출인 경우 Elastic Beanstalk의 애플리케이션 로드 밸런서에 전달합니다.  
  CodePipeline은 연결된 깃허브 리포지토리에 변경사항이 발생하면 소스 코드를 다운로드하고, CodeBuild에게 빌드 작업을 위임합니다. CodeBuild는 S3 버킷에서 운영 환경 구성 파일( `application-prod-*.yml` )을 다운로드하는 등 지정된 작업을 수행하여 Elastic Beanstalk 배포용 아티팩트를 생성합니다. CodePipeline은 진행 상황 파악이 용이하도록 Slack 채널에 알림 메시지를 전송합니다.

# 기능
## 목록
### 인증 및 회원
- 사이트/소셜 회원가입
    - 사이트 회원은 이메일 인증 진행
- 사이트/소셜 로그인
- 내 활동 조회
    - 내가 쓴 게시글
    - 내가 쓴 답글
### 컨텐츠
- 게시글/답글 CRUD
- 게시글/답글 투표
- 답글 채택
- 게시글 검색
- 이미지 업로드
    - S3 버킷에 업로드 후 CloudFront 기준 접근 URI 제공
### 부가기능
- 알림
    - 내 컨텐츠 투표됨
    - 내가 쓴 게시글에 답글 추가됨
    - 시스템 알림 등
### 관리자
- 카테고리( 게시판 ) CRUD
- 회원 관리
- 게시글/답글 관리

## 설명
### JWT
스케일 아웃, 혹은 MSA 도입 시 인증 서버( 인스턴스, DB, ... )가 단일 장애점이 될 수 있음을 확인하였습니다.
인증 서버에 발생할 수 있는 과도한 부하를 완화하기 위하여 세션 비활성화 후 JWT를 사용하였습니다.

### 비세션 환경에서 REST API로 제공하는 OAuth2 인증
작은 커뮤니티 프로젝트는 세션 비활성화 후 JWT를 사용 중이며, 리액트 애플리케이션과 상호작용하기 위해 기능을 REST API로 제공합니다.  
스프링 시큐리티는 OAuth2 인증 처리를 세션과 MVC 기준으로 제공하기 때문에 일부 기능을 사용자 정의하였습니다. 예를 들어 OAuth2 인증 요청 정보는 세션이 아닌 쿠키에 저장되며, 인증 처리 후 리다이렉트되는 URI를 별도 지정할 수 있습니다.

### 이미지 업로드
게시글 저장 시 함께 업로드하는 첨부파일 개념이 아니며 일반적인 커뮤니티, 깃허브 등에서 제공하는 **이미지 삽입 시 즉시 업로드**하기 위한 기능입니다.  
업로드 이미지는 애플리케이션이 파일 크기 제한 등을 확인한 후 S3 파일 버킷에 추가하며, 객체에 접근 가능한 CloudFront 기준 URI를 반환합니다.

### 게시글 검색
데이터베이스 문자열 검색에 `Like '%문자열%'`문 사용 시 Full Table Scan 발생을 확인하였습니다.  
MySQL이 제공하는 FullText Search를 이용하여 게시글 검색 성능을 개선하였습니다.

# 참고사항
## 개발 환경
운영 환경과 유사한 로컬 개발 환경을 편리하게 구축할 수 있도록 Docker Compose를 포함합니다. 다음 명령을 실행하여 사용합니다.
```bash
docker compose up -d
```

## 테스트
Jacoco 플러그인을 이용하여 테스트 커버리지를 측정하고, 구문 커버리지 80% 이상을 유지하고 있습니다. 또한 운영 환경과 유사한 환경에서 동작을 테스트하기 위하여 일부 테스트는 Testcontainers를 사용합니다. 해당 테스트는 실행 속도가 느리기 때문에 SureFire 단위 테스트( `mvn test` ) 시 실행되지 않도록 `*IT.java` 파일명을 사용합니다.  
:bulb: Jacoco는 `verify` 단계에서 실행됩니다. 즉, Failsafe 통합 테스트 시 커버리지를 측정하므로 Testcontainers 테스트를 정상적으로 포함합니다.

## API 문서화
작은 커뮤니티 프로젝트는 API 문서화 및 문서의 신뢰성을 위하여 Spring REST Docs를 사용합니다. 문서는 [여기][api-docs]에서 확인할 수 있습니다.

[api-docs]: https://api.cijtoy.com/docs/index.html