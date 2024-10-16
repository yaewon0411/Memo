# Memo

## 일정 관리 및 공유 애플리케이션

이 일정 관리 애플리케이션은 사용자들이 일정을 생성하고 관리할 수 있는 기능을 제공합니다.

사용자들은 일정에 댓글을 달거나 다른 사용자와 일정을 공유할 수 있으며, 댓글과 공유된 사용자들을 쉽게 관리할 수 있습니다.

JWT 인증을 통해 사용자 인증 및 권한 관리를 수행하여 안전하게 데이터를 보호합니다.

### ERD

<img src="Memo application.png" width = 500/>

### 주요 기능

#### 1. 일정 생성 및 관리

- 사용자는 일정을 생성하고 수정 및 삭제할 수 있습니다.
- 일정 생성 시 날씨 정보를 포함하여 저장할 수 있습니다.
- 일정의 공개 여부를 설정할 수 있습니다.

#### 2. 댓글 기능

- 일정에 대한 댓글을 작성, 수정, 삭제할 수 있습니다.
- 댓글 작성자는 본인의 댓글만 수정 또는 삭제할 수 있습니다.
- 관리자는 모든 댓글을 수정 또는 삭제할 수 있습니다.

#### 3. 일정 공유 및 사용자 배정

- 특정 일정을 다른 사용자와 공유할 수 있으며, 최대 5명의 사용자를 배정할 수 있습니다.
- 배정된 사용자는 해당 일정에 대한 접근 권한을 가집니다.

#### 4. 공개 일정 조회

- 모든 사용자는 공개된 일정을 조회할 수 있으며, 필터링 기능을 통해 특정 조건에 맞는 일정을 검색할 수 있습니다.

#### 5. JWT 기반 인증 및 권한 관리

- JWT 토큰을 사용하여 사용자 인증 및 권한 관리를 수행합니다.
- 각 기능은 사용자 역할에 따라 접근 권한이 제한됩니다. (예: 관리자만 일정 삭제/수정 가능)

#### 6. 유저 관리

- 회원 가입 및 로그인 기능을 통해 사용자를 관리합니다.
- 사용자는 본인의 정보를 수정하거나 계정을 삭제할 수 있습니다.

### API 엔드포인트

[API 명세서 바로가기](https://documenter.getpostman.com/view/27240528/2sAXxLBZon)

### 테스트

1. 클론 후 Active profiles가 test로 설정되었는지 확인 합니다
2. 더미 유저로 로그인 시 다음 값을 사용합니다

- **``email``** : root1234@naver.com
- **``password``** : root1234

```text
{
    "email": "root1234@naver.com",
    "password": "root1234"
}
```

### 일정 관련

- ```POST /api/schedules``` : 새로운 일정 생성
- ```PATCH /api/schedules/{scheduleId}``` : 일정 수정 (관리자 권한 필요)
- ```DELETE /api/schedules/{scheduleId}``` : 일정 삭제 (관리자 권한 필요)
- ```GET /api/public-schedules``` : 공개된 일정 목록 조회
- ```GET /api/schedules/{scheduleId}``` : 특정 일정 조회 (댓글, 배정된 사용자 정보 포함)

### 댓글 관련

- ```POST /api/schedules/{scheduleId}/comments``` : 일정에 댓글 작성
- ```PUT /api/schedules/{scheduleId}/comments/{commentId}``` : 댓글 수정
- ```DELETE /api/schedules/{scheduleId}/comments/{commentId}``` : 댓글 삭제

### 사용자 관련

- ```POST /api/join``` : 회원 가입
- ```POST /api/login``` : 로그인 (JWT 토큰 발급)
- ```GET /api/users``` : 사용자 정보 조회
- ```PATCH /api/users``` : 사용자 정보 수정
- ```DELETE /api/users``` : 사용자 계정 삭제

### 유효성 검사 및 오류 처리

- **커스텀 유효성 검사**: @Valid, @Pattern, @Min 등의 어노테이션을 사용하여 입력 데이터를 검사합니다. 오류 발생 시, 상세 메시지가 반환됩니다.
- **글로벌 예외 처리**: 중앙에서 오류를 처리하여 사용자가 적절한 HTTP 상태 코드와 오류 메시지를 받을 수 있도록 합니다 (예: 잘못된 입력, 리소스 없음).






