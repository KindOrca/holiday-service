# Holiday Service
#### 플랜잇스퀘어 Backend 과제
Nager.Date API 기반 전 세계 공휴일 데이터 수집 · 조회 · 관리 서비스

<br>

## 프로젝트 개요
##### 외부 API를 기반으로 최근 5년(2020~2025) 공휴일 데이터를 저장·조회·관리

- 최초 실행 시 국가 목록과 최근 5년 공휴일을 저장
- 조건 기반 공휴일 검색 (연도, 국가, 기간, 타입)
- 데이터 재동기화(Upsert)
- 연도·국가별 데이터 삭제
- Swagger UI 기반 API 문서 자동 노출
- 스케줄 기반 자동 업데이트 — 구현 완료

<br>

## 기술 스택
| Category   | Stack                        |
| ---------- | ---------------------------- |
| Language   | Java 21                      |
| Framework  | Spring Boot 3.4              |
| DB         | H2 (In-memory)               |
| ORM        | JPA + QueryDSL 5 |
| Build Tool | Gradle                       |
| Test       | JUnit 5            |
| Docs       | Swagger         |


<br>

## 기능 명세

- 국가 조회 + 5년치 데이터 자동 수집
공휴일 검색 (페이징)
- 국가 코드 또는 국가 이름으로 검색 가능 (예: "KR" 또는 "Korea" 또는 "대한민국")
- 연도/국가/기간/타입 기반 조회
데이터 재동기화 (Refresh)	
- API 재호출 후 기존 데이터 삭제 후 저장
데이터 삭제	
- 국가 + 연도로 공휴일 전체 삭제
자동 배치 실행	
- 매년 1월 2일 01:00 KST 자동 업데이트

<br>

## 실행 방법
```
https://github.com/KindOrca/holiday-service.git
cd holiday-service
.\gradlew bootRun
```

<br>

## API 문서

실행 후 Swagger UI 접근

http://localhost:8080/swagger-ui.html

<br>

## 주요 엔드포인트
| Method   | URL                                  | 설명          |
| -------- | ------------------------------------ | ----------- |
| `GET`    | `/api/v1/holiday/search`                   | 공휴일 조회(페이징) |
| `PUT`   | `/api/v1/holiday/refresh` | 데이터 재동기화    |
| `DELETE` | `/api/v1/holiday/delete`         | 연도+국가 단위 삭제 |


<br>

## 테스트
```
.\gradlew clean test
```
테스트 성공 스크린샷 첨부
<img width="1916" height="638" alt="image" src="https://github.com/user-attachments/assets/7f023d62-7d50-4faa-bdcf-2e3c25f5d23d" />


<br>

## 스케줄 작업
```
@Scheduled(cron = "0 0 1 2 1 *")
```
- 매년 1월 2일 01:00 자동 실행
- 전년도 + 올해 데이터 비교 후 재업데이트

<br>

## 데이터 모델 구조

Holiday (메인 엔티티)

@ElementCollection 기반:
| 테이블              | 설명           |
| ---------------- | ------------ |
| HOLIDAYS         | 기본 공휴일 저장    |
| HOLIDAY_TYPES    | 공휴일 타입       |
| HOLIDAY_COUNTIES | 특정 지역 대상 공휴일 |


다대일 구조가 아니며 별도 엔티티 정의 없이 컬렉션으로 저장

<br>

## 설계 의도
본 서비스는 외부 공공 Holiday API를 기반으로 공휴일 데이터를 수집·관리하는 것을 목표로 하며, 데이터 저장 구조 및 동기화 전략을 다음 기준으로 설계했습니다.

####  1. Country 데이터는 DB 대신 In-Memory Cache(Map)으로 관리

- 외부 API는 국가 목록을 제공하지만, 국가 데이터는 매우 정적이고 변경 빈도가 극히 낮다는 특징이 있습니다.
- 국가 목록은 사용자 요청에 따라 지속적으로 조회되는 데이터가 아니라, 공휴일 수집 시 한 번 필요하고 그 후에는 반복적으로 변경되거나 사용되는 데이터가 아니기 때문입니다.

따라서 다음 이유로 별도의 DB 테이블을 생성하지 않았습니다

- 국가 목록은 거의 변하지 않는 데이터이며, 영속 관리 대상보다 캐싱 데이터에 가까움
- CRUD 필요성	국가 목록에는 Insert/Update/Delete 요구사항이 없음
호출 목적	국가 목록은 Holiday Batch 생성 시에만 필요하며, 그 외 비즈니스 로직의 주체 역할을 하지 않음
- 성능 관점	Redis 또는 DB 조회 비용 없이 애플리케이션 내부 Cache에서 O(1)으로 접근 가능

결론적으로, 국가 데이터는 JPA 영속 객체가 아닌 
- ConcurrentHashMap<String, String> 기반 캐시 구조 
- @PostConstruct 시점 초기화
- API 호출 최소화로 운용
```
private final Map<String, String> countries = new ConcurrentHashMap<>();
```

#### 2. Holiday 데이터는 DB에 영속 저장

반면 Holiday 데이터는 다음 이유로 DB 테이블로 설계했습니다:

- 매년 Refresh 동작을 통해 Upsert가 필요
- 국가별/연도별/타입 기반 검색 및 필터링 지원
- 페이지네이션 적용이 필요
- QueryDSL로 조건 검색 요구사항 존재

Holiday 데이터는 변동 가능성이 있으며, 검색 요구가 복잡하기 때문에 JPA + QueryDSL 기반의 구조가 적합하다고 판단

또한 types 및 counties 컬렉션 필드는 별도 테이블을 만들지 않고 @ElementCollection을 사용하여 단순 종속 값으로 설계

#### 3. API 호출 비용 최소화 + 운영 비용 최적화
- 애플리케이션 최초 기동 시 국가 목록은 한 번만 호출하여 캐싱
- 공휴일 데이터는 배치/요청 기반으로만 호출 및 DB 저장
- 불필요한 외부 API 호출을 피하고 유지 비용 최소화

<br>

## 커밋 전략
| Prefix      | 의미           |
| ----------- | ------------ |
| `feat:`     | 기능 추가        |
| `fix:`      | 버그 수정        |
| `chore:`    | build, 설정 변경 |
| `refactor:` | 구조 개선        |
