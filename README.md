# Concert-Reservation
```
🎸 콘서트 예약 서비스
```

## ✏️ Description
- `콘서트 예약 서비스`를 구현해 봅니다.
- 대기열 시스템을 구축하고, 예약 서비스는 작업가능한 유저만 수행할 수 있도록 해야합니다.
- 사용자는 좌석예약 시에 미리 충전한 잔액을 이용합니다.
- 좌석 예약 요청시에, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 합니다.

## 📝 Requirements
- 아래 5가지 API 를 구현합니다.
    - 유저 토큰 발급 API
    - 예약 가능 날짜 / 좌석 API
    - 좌석 예약 요청 API
    - 잔액 충전 / 조회 API
    - 결제 API
- 각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
- 다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.
- 동시성 이슈를 고려하여 구현합니다.
- 대기열 개념을 고려해 구현합니다.

## 🔑 API Specs
1️⃣ **`주요` 유저 대기열 토큰 기능**
- 서비스를 이용할 토큰을 발급받는 API를 작성합니다.
- 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함합니다.
- 이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능합니다.

> 기본적으로 폴링으로 본인의 대기열을 확인한다고 가정하며, 다른 방안 또한 고려해보고 구현해 볼 수 있습니다.

**2️⃣ `기본` 예약 가능 날짜 / 좌석 API**
- 예약가능한 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성합니다.
- 예약 가능한 날짜 목록을 조회할 수 있습니다.
- 날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있습니다.

> 좌석 정보는 1 ~ 50 까지의 좌석번호로 관리됩니다.

3️⃣ **`주요` 좌석 예약 요청 API**
- 날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API 를 작성합니다.
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 약 5분간 임시 배정됩니다. ( 시간은 정책에 따라 자율적으로 정의합니다. )
- 만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되어야 하며 다른 사용자는 예약할 수 없어야 한다.

4️⃣ **`기본`**  **잔액 충전 / 조회 API**
- 결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성합니다.
- 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.

5️⃣ **`주요` 결제 API**
- 결제 처리하고 결제 내역을 생성하는 API 를 작성합니다.
- 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료시킵니다.

💡 **KEY POINT**
- 유저간 대기열을 요청 순서대로 정확하게 제공할 방법을 고민해 봅니다.
- 동시에 여러 사용자가 예약 요청을 했을 때, 좌석이 중복으로 배정 가능하지 않도록 합니다.

### 💠 API 명세
- `Endpoint` - API 의 URL 및 기능을 설명할 수 있는 적절한 HTTP Method
- `Request` - Param, Query, Body 등 API 호출 시 전달되어야 할 매개변수 및 데이터
- `Response` - API 의 응답 코드, 데이터 등에 대한 명세 및 적절한 예제
- `Error` - API 호출 중 발생할 수 있는 예외 케이스에 대해 명시
- `Authorization` - 필요한 인증, 권한에 대해서도 명시

| 항목   | API          | EndPoint                  | Header                  | Request                                                                                                                                         | Response                                                    | Error                                                                                 | Authorization
|------|--------------|---------------------------|--------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------|---------------------------------------------------------------------------------------|--------------|
| 설명   | 유저 대기열 토큰 기능 | `POST` /concert/waiting/token | Content-Type: application/json | `/concert/waiting/token`<br>{<br> "userId": 1 <br> "concertId": 1<br>}<br><br>`userId`: <Long, body> 유저 ID <br>`concertId`: <Long, body> 콘서트 ID | SUCCESS<br>{<br> "accessToken": "xxxx.yyyyy.zzzzz"<br>}<br> | <br>ERROR <br>{<br> "statusCode": 500, <br> "messages": ["대기열에 진입할 수 없습니다."]<br>}<br> |-    
|      | 예약 가능 날짜     | `GET` /concert/schedule/{concertId} |  Content-Type: application/json | `/concert/schedule/1`<br>`concertId`: <Long, path> 콘서트 ID<br>| SUCCESS<br>{<br>"concertDate":"2024-07-05"<br>}             | ERROR<br>{<br>"statusCode":500,<br>"messages":[대기열에 진입할 수 없습니다."]<br>}|token
|      | 예약 가능 좌석     | `GET` /concert/seats/{concertId} |  Content-Type: application/json | `/concert/seats/1`<br>{<br>"concertDate": "2024-07-05"<br>}<br>`concertId`: <Long, path> 콘서트 ID|SUCCESS<br>{<br>"seatNumber":1<br>}|ERROR<br>{<br>"statusCode":500,<br>"messages":[대기열에 진입할 수 없습니다."]<br>}|token
|      | 좌석 예약 요청     | `POST` /concert/seats/booking |  Content-Type: application/json | `/concert/seats/booking`<br>{<br>"concertDate": "2024-07-05"<br>"concertSeatId": 1<br>} | SUCCESS<br>{<br>"reservationStatus": "RESERVING"<br>}|ERROR<br>{<br>"statusCode":500,<br>"messages":[대기열에 진입할 수 없습니다."]<br>}|token
|      | 잔액 충전        | `POST` /payment/charge | Content-Type: application/json | `/payment/charge/1`<br>{<br>"userId": 1,<br>"payment": 2000<br>}|SUCCESS<br>{<br>"payment": 2000<br>}|ERROR<br>{<br>"statusCode":500,<br>"messages":["사용자를 찾을 수 없습니다."]<br>}<br><br>{<br>"statusCode":500,<br>"messages"["충전할 금액이 없습니다."]<br>}|-
|      | 잔액 조회        | `GET` /payment/{userId} | - | `/payment/1`<br>`userId`: <Long, path> 유저 ID | SUCCESS<br>{<br>"payment": 2000<br>}|ERROR<br>{<br>"statusCode":500,<br>"messages":["사용자를 찾을 수 없습니다."]<br>}<br>|-
|      | 결제           | `POST` /payment | Content-Type: application/json | `/concert/seats/payment`<br>{<br>"concertSeatId": 1,<br>"reservationId": 1<br>} | SUCCESS<br>{<br>"seatNumber":1<br>} | ERROR<br>{<br>"statusCode":500,<br>"messages":[대기열에 진입할 수 없습니다."]<br>}<br><br>ERROR<br>{<br>"statusCode":500,<br>"messages":[좌석 임시 배정에 실패하였습니다."]<br>}<br><br>ERROR<br>{<br>"statusCode":500,<br>"messages":[잔액이 없습니다."]<br>}|token
---

## 📆 Milestone
![image](https://github.com/kdelay/Concert-Booking/assets/90545043/dfc79bae-82fc-4c7f-b9c8-21c051061093)


## 📊 ERD Diagram
```mermaid
erDiagram
    %% 사용자
    user {
        bigint id PK "유저 PK"
    }
    
    %% 대기열 토큰 정보
    %% user(1) : waiting_slot(N)
    waiting_token {
        bigint id PK "대기열 PK"
        bigint user_id FK "유저 PK"
        string token UK "토큰 정보(UUID)"
        %% DEACTIVATE(비활성화), ACTIVATE(활성화), EXPIRED(만료됨)
        string waiting_token_status "대기열 토큰 상태(enum)"
        timestamp created_at "대기열 토큰 요청 시간"
        timestamp update_active_time "토큰 상태 변경 시간"
        timestamp expired_at "대기열 토큰 만료 시간"
    }

    %% 콘서트
    concert {
        bigint id PK "콘서트 PK"
        string name "콘서트 이름"
        string host "콘서트 주최자"
    }

    %% 콘서트 날짜
    %% concert(1) : concert_schedule(N)
    concert_schedule {
        bigint id PK "콘서트 날짜 PK"
        bigint concert_id FK "콘서트 PK"
        date concert_date "콘서트 날짜"
    }

    %% 콘서트 좌석
    %% concert_schedule(1) : concert_seat(N)
    concert_seat {
        bigint id PK "콘서트 좌석 PK"
        bigint concert_id FK "콘서트 PK"
        bigint concert_schedule_id FK "콘서트 날짜 PK"
        bigint user_id FK "유저 PK"
        int seat_number "콘서트 좌석 번호"
        int seat_price "콘서트 좌석 금액"
        %% AVAILABLE(예약 가능), TEMPORARY(임시 좌석 배정중), RESERVED(예약됨)
        string seat_status "콘서트 좌석 번호 상태(enum)"
        timestamp expired_at "임시 배정 만료 시간"
    }
    
    %% 예약
    %% user(1) : reservation(N)
    reservation {
        bigint id PK "콘서트 예약 PK"
        bigint concert_seat_id FK "콘서트 좌석 PK"
        bigint user_id FK "유저 PK"
        string concert_name "콘서트 이름"
        date concert_date "콘서트 날짜"
        %% RESERVING(예약중), RESERVED(예약됨), CANCELED(예약 취소)
        string reservation_status "예약 상태(enum)"
        timestamp created_at "콘서트 예약 요청 시간"
        timestamp updated_state_time "콘서트 예약 상태 변경 시간"
    }
    
    %% 결제
    %% reservation(1) : payment(0 or 1)
    payment {
        bigint id PK "콘서트 결제 PK"
        bigint reservation_id FK "콘서트 예약 PK"
        decimal price "결제 금액"
        %% COMPLETED(결제 완료), CANCELED(결제 취소)
        string payment_state "결제 상태(enum)"
        timestamp created_at "콘서트 결제 요청 시간"
        timestamp updated_state_time "콘서트 결제 상태 변경 시간"
    }

    %% 잔액
    %% balance(1) : balance_history(N)
    balance {
      bigint id PK "잔액 PK"
      bigint user_id FK "유저 PK"
      decimal amount "잔액"
    }
    
    %% 잔액 히스토리
    balance_history {
        bigint id PK "잔액 히스토리 PK"
        bigint user_id FK "유저 PK"
        decimal amount "충전 금액" 
        timestamp created_at "생성 날짜"
        timestamp updated_at "수정 날짜"
    }

    user ||--|{ waiting_token : has
    concert ||--|{ concert_schedule : contains
    concert_schedule ||--|{ concert_seat : contains
    reservation ||--o| payment : has
    balance ||--|{ balance_history : contains
    
```

## 👤 Sequence Diagram
### 토큰 발급 API
```mermaid
sequenceDiagram
    autonumber
    actor 사용자
    사용자 ->>+ 대기열: 토큰 요청
    Note over 사용자, 대기열: 사용자 식별자(pk)
    대기열 ->> 대기열: 대기중인 토큰이 없는 경우 새로 발급, 있는 경우 반환
    대기열 -->>- 사용자: 토큰, 대기열 정보
```
### 예약 가능 날짜 조회 API
```mermaid
sequenceDiagram
    autonumber
    actor 사용자

    사용자 ->>+ 대기열: 대기열 요청
    break 대기열 검증 실패
      대기열 -->> 사용자: 400 Error 반환
    end
  
    사용자 ->>+ 예약 가능 날짜: 콘서트 날짜 조회 요청
    Note over 사용자, 예약 가능 날짜: Active 토큰, 콘서트 정보
    예약 가능 날짜 -->>- 사용자: 응답
    Note over 사용자, 예약 가능 날짜: 예약 가능한 날짜
```

### 좌석 조회 API
```mermaid
sequenceDiagram
    autonumber
    actor 사용자

    사용자 ->>+ 대기열: 대기열 요청
    break 대기열 검증 실패
        대기열 -->> 사용자: 400 Error 반환
    end
  
    사용자 ->>+ 예약 가능 좌석: 콘서트 좌석 조회 요청
    Note over 사용자, 예약 가능 좌석: Active 토큰, 콘서트 날짜 정보
    예약 가능 좌석 -->>- 사용자: 응답
    Note over 사용자, 예약 가능 좌석: 해당 날짜의 예약 가능한 좌석번호
    Note over 사용자: 좌석 번호: 1~50
```

### 좌석 예약 요청 API
```mermaid
sequenceDiagram
    autonumber
    actor 사용자

    사용자 ->>+ 대기열: 대기열 요청
    break 대기열 검증 실패
      대기열 -->> 사용자: 400 Error 반환
    end
  
    사용자 ->>+ 좌석 예약: Active 토큰, 좌석 예약 요청
    Note over 사용자, 좌석 예약: 콘서트 날짜, 좌석 정보
    좌석 예약 ->> 좌석 예약: 좌석 예약 상태(lock) 변경
    좌석 예약 -->>- 사용자: 좌석 임시 배정 완료
    Note over 사용자, 좌석 예약: 결제 정보, 임시 배정 유효 시간
```

### 잔액 충전 API
```mermaid
sequenceDiagram
    autonumber
    actor 사용자
    
    사용자 ->>+ 잔액 충전: 잔액 충전 요청
    Note over 사용자, 잔액 충전: 사용자 식별자(pk), 충전할 금액
    
    잔액 충전 -->>- 사용자: 잔액 반환
```

### 잔액 조회 API
```mermaid
sequenceDiagram
    autonumber
    actor 사용자
    
    사용자 ->>+ 잔액 조회: 잔액 조회 요청
    Note over 사용자, 잔액 조회: 사용자 식별자(pk)
  
    잔액 조회 -->>- 사용자: 잔액 반환
```

### 결제 API
```mermaid
sequenceDiagram
    autonumber
    actor 사용자

    사용자 ->>+ 대기열: 대기열 요청
    break 대기열 검증 실패
      대기열 -->> 사용자: 400 Error 반환
    end
    
    사용자 ->>+ 결제: 결제 요청
    Note over 사용자, 결제: Active 토큰, 결제 정보, 좌석 배정 번호
    결제 ->>+ 좌석 예약: 임시 배정 체크
    Note over 결제, 좌석 예약: 좌석 배정 번호
    좌석 예약 -->>- 결제: 임시 배정 여부
    
    break 좌석 임시 배정 안됨 (or 만료 5분)
        결제 -->> 사용자: 좌석 임시 배정 안됨
    end
    
    alt 잔액 있음
        결제 ->>+ 잔액 조회: 잔액 차감
        Note over 결제, 잔액 조회: 사용자 식별자(pk)
        잔액 조회 -->> 결제: 결제 성공
        결제 ->> 좌석 예약: 결제 완료
        Note over 결제, 좌석 예약: 좌석 배정 번호
        결제 ->> 대기열: 대기열 토큰 만료
        좌석 예약 -->> 사용자: 예약 완료
        Note over 사용자, 좌석 예약: 좌석 배정 번호
    else 잔액 없음
        결제 ->>+ 잔액 조회: 잔액 차감
        Note over 결제, 잔액 조회: 사용자 식별자(pk)
        잔액 조회 -->>- 결제: 잔액 부족
        결제 -->>- 사용자: 잔액 부족
    end
```

# Tree
```text
└── src
    ├── main
    │   ├── java
    │   │   └── booking
    │   │       ├── BookingApplication.java
    │   │       ├── concert
    │   │       │   ├── domain
    │   │       │   │   ├── repository
    │   │       │   │   │   └── ConcertRepository.java
    │   │       │   │   └── service
    │   │       │   │       └── ConcertService.java
    │   │       │   ├── infrastructure
    │   │       │   │   ├── entity
    │   │       │   │   ├── impl
    │   │       │   │   └── jpa
    │   │       │   └── presentation
    │   │       │       ├── controller
    │   │       │       │   └── ConcertController.java
    │   │       │       ├── request
    │   │       │       │   ├── BookingSeatsRequest.java
    │   │       │       │   └── WaitingTokenRequest.java
    │   │       │       └── response
    │   │       │           ├── BookingSeatsResponse.java
    │   │       │           ├── SearchScheduleResponse.java
    │   │       │           ├── SearchSeatsResponse.java
    │   │       │           └── WaitingTokenResponse.java
    │   │       ├── payment
    │   │       │   ├── domain
    │   │       │   │   ├── repository
    │   │       │   │   └── service
    │   │       │   ├── infrastructure
    │   │       │   │   ├── entity
    │   │       │   │   ├── impl
    │   │       │   │   └── jpa
    │   │       │   └── presentation
    │   │       │       ├── controller
    │   │       │       │   └── PaymentController.java
    │   │       │       ├── request
    │   │       │       │   ├── ChargeRequest.java
    │   │       │       │   └── PayRequest.java
    │   │       │       └── response
    │   │       │           ├── ChargeResponse.java
    │   │       │           ├── PayResponse.java
    │   │       │           └── SearchPaymentResponse.java
    │   │       ├── reservation
    │   │       │   ├── domain
    │   │       │   │   ├── ReservationStatus.java
    │   │       │   │   ├── repository
    │   │       │   │   │   └── ReservationRepository.java
    │   │       │   │   └── service
    │   │       │   │       └── ReservationService.java
    │   │       │   ├── infrastructure
    │   │       │   │   ├── entity
    │   │       │   │   ├── impl
    │   │       │   │   └── jpa
    │   │       │   └── presentation
    │   │       │       └── controller
    │   │       └── waiting
    │   │           ├── domain
    │   │           │   ├── repository
    │   │           │   │   └── WaitingRepository.java
    │   │           │   └── service
    │   │           │       └── WaitingService.java
    │   │           ├── infrastructure
    │   │           │   ├── entity
    │   │           │   ├── impl
    │   │           │   └── jpa
    │   │           └── presentation
    │   │               └── controller
    │   └── resources
    │       ├── application.properties
    │       ├── static
    │       └── templates
    └── test
        └── java
            └── booking
                └── BookingApplicationTests.java

```
