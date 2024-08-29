# 🎸 콘서트 예약 서비스

## 📄 Documents
- [비즈니스 로직 중 발생 가능한 동시성 이슈 파악](https://github.com/kdelay/Concert-Booking/wiki/%EB%B9%84%EC%A6%88%EB%8B%88%EC%8A%A4-%EB%A1%9C%EC%A7%81-%EC%A4%91-%EB%B0%9C%EC%83%9D-%EA%B0%80%EB%8A%A5%ED%95%9C-%EB%8F%99%EC%8B%9C%EC%84%B1-%EC%9D%B4%EC%8A%88-%ED%8C%8C%EC%95%85)
- [대기열 Redis 이관 및 Cache Service 도입](https://cojyeon.tistory.com/320)
- [부하를 적절하게 축소하기 위한 방안 고려](https://github.com/kdelay/Concert-Booking/wiki/%EB%B6%80%ED%95%98%EB%A5%BC-%EC%A0%81%EC%A0%88%ED%95%98%EA%B2%8C-%EC%B6%95%EC%86%8C%ED%95%98%EA%B8%B0-%EC%9C%84%ED%95%9C-%EB%B0%A9%EC%95%88-%EA%B3%A0%EB%A0%A4)
- [MSA 서비스 분리 확장 설계](https://github.com/kdelay/Concert-Booking/wiki/MSA-%EC%84%9C%EB%B9%84%EC%8A%A4-%EB%B6%84%EB%A6%AC-%ED%99%95%EC%9E%A5-%EC%84%A4%EA%B3%84)
- [부하 테스트](https://github.com/kdelay/Concert-Booking/wiki/%EB%B6%80%ED%95%98-%ED%85%8C%EC%8A%A4%ED%8A%B8)

## ✏️ Description
- 대기열 시스템을 통해 유입 트래픽을 관리할 수 있다.
- 예약 서비스는 토큰이 있는 유저만 수행할 수 있다.
- 좌석 예약 요청 시, 결제가 이루어지지 않더라도 일정 시간동안 다른 유저가 해당 좌석에 접근할 수 없도록 한다.
- 결제 시, 사용자는 미리 충전한 잔액을 이용한다.

## 📝 API
- 대기열 토큰 등록 및 조회 API
- 콘서트 목록 조회 API
- 콘서트 일정 목록 조회 API
- 콘서트 좌석 목록 조회 API
- 콘서트 예약 요청 API
- 콘서트 좌석 결제 API

### 🔑 API Specs
1️⃣ **유저 대기열 토큰 기능**
- 서비스를 이용할 토큰을 발급받는 API 를 작성한다.
- 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함한다.
- 이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능하다.

> 기본적으로 폴링으로 본인의 대기열을 확인한다고 가정한다.

2️⃣ **콘서트 날짜 / 좌석 API**
- 콘서트 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성한다.
- 콘서트 날짜 목록을 조회할 수 있다.
- 날짜 정보를 입력받아 예약 가능한 좌석 정보를 조회할 수 있다.

> 좌석 정보는 1 ~ 50 까지의 좌석 번호로 관리된다.

3️⃣ **콘서트 좌석 예약 요청 API**
- 날짜와 좌석 정보를 입력 받아 좌석을 예약 처리하는 API 를 작성한다.
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 약 5분간 임시 배정된다.
- 만약 배정 시간 내에 결제가 완료되지 않는다면, 좌석에 대한 임시 배정은 해제되어야 하며 다른 사용자는 예약할 수 있어야 한다.

4️⃣ **유저 잔액 충전 / 조회 API**
- 결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성한다.
- 사용자 식별자 및 충전할 금액을 받아 잔액을 충전한다.
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회한다.

5️⃣ **결제 API**
- 결제 처리하고 결제 내역을 생성하는 API 를 작성한다.
- 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료시킨다.

💡 **KEY POINT**
- 유저 간 대기열 요청을 순서대로 정확하게 어떻게 제공할 것인가?
- 동시에 여러 사용자가 예약 요청을 했을 때, 좌석이 중복으로 배정 가능하지 않도록 해야 한다.

---

## 📆 Milestone
https://github.com/users/kdelay/projects/7/views/4

## 📊 ERD Diagram
```mermaid
erDiagram
    %% 사용자
    user {
        bigint id PK "유저 PK"
        decimal amount "잔액"
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
        timestamp modified_at "대기열 토큰 상태 변경 시간"
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
        decimal seat_price "콘서트 좌석 금액"
        %% AVAILABLE(예약 가능), TEMPORARY(임시 좌석 배정중), RESERVED(예약됨)
        string seat_status "콘서트 좌석 번호 상태(enum)"
        timestamp modified_at "콘서트 좌석 상태 변경 시간"
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
        timestamp modified_at "콘서트 예약 상태 변경 시간"
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
        timestamp modified_at "콘서트 결제 상태 변경 시간"
    }

    user ||--|{ waiting_token : has
    concert ||--|{ concert_schedule : contains
    concert_schedule ||--|{ concert_seat : contains
    reservation ||--o| payment : has
    
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

---

# 🌲 Tree
```text
├── api
│   ├── concert
│   │   ├── application
│   │   │   └── DataPlatformSendService.java
│   │   ├── domain
│   │   │   ├── Concert.java
│   │   │   ├── ConcertRepository.java
│   │   │   ├── ConcertSchedule.java
│   │   │   ├── ConcertSeat.java
│   │   │   ├── ConcertService.java
│   │   │   ├── MockApiClient.java
│   │   │   ├── Payment.java
│   │   │   ├── PaymentOutbox.java
│   │   │   ├── Reservation.java
│   │   │   ├── enums
│   │   │   │   ├── ConcertSeatStatus.java
│   │   │   │   ├── PaymentOutboxState.java
│   │   │   │   ├── PaymentState.java
│   │   │   │   └── ReservationStatus.java
│   │   │   ├── event
│   │   │   │   ├── PaymentEventPublisher.java
│   │   │   │   └── PaymentSuccessEvent.java
│   │   │   └── message
│   │   │       ├── PaymentMessageOutboxManager.java
│   │   │       └── PaymentMessageSender.java
│   │   ├── infrastructure
│   │   │   ├── ConcertMapper.java
│   │   │   ├── MockApiClientImpl.java
│   │   │   ├── db
│   │   │   │   ├── ConcertEntity.java
│   │   │   │   ├── ConcertScheduleEntity.java
│   │   │   │   ├── ConcertSeatEntity.java
│   │   │   │   ├── PaymentEntity.java
│   │   │   │   ├── PaymentOutboxEntity.java
│   │   │   │   ├── ReservationEntity.java
│   │   │   │   └── repository
│   │   │   │       ├── ConcertRepositoryImpl.java
│   │   │   │       ├── JpaConcertRepository.java
│   │   │   │       ├── JpaConcertScheduleRepository.java
│   │   │   │       ├── JpaConcertSeatRepository.java
│   │   │   │       ├── JpaPaymentOutboxRepository.java
│   │   │   │       ├── JpaPaymentRepository.java
│   │   │   │       ├── JpaReservationRepository.java
│   │   │   │       └── PaymentMessageOutboxManagerImpl.java
│   │   │   ├── kafka
│   │   │   │   └── PaymentKafkaMessageSender.java
│   │   │   └── spring
│   │   │       └── PaymentSpringEventPublisher.java
│   │   └── interfaces
│   │       ├── ConcertController.java
│   │       ├── consumer
│   │       │   └── PaymentMessageConsumer.java
│   │       ├── event
│   │       │   └── PaymentEventListener.java
│   │       ├── request
│   │       │   ├── BookingSeatsRequest.java
│   │       │   └── PayRequest.java
│   │       └── response
│   │           ├── BookingSeatsResponse.java
│   │           ├── PayResponse.java
│   │           ├── SearchPaymentResponse.java
│   │           ├── SearchScheduleResponse.java
│   │           └── SearchSeatsResponse.java
│   ├── user
│   │   ├── domain
│   │   │   ├── User.java
│   │   │   ├── UserRepository.java
│   │   │   └── UserService.java
│   │   ├── infrastructure
│   │   │   ├── JpaUserRepository.java
│   │   │   ├── UserEntity.java
│   │   │   └── UserRepositoryImpl.java
│   │   └── interfaces
│   │       ├── ChargeRequest.java
│   │       ├── ChargeResponse.java
│   │       ├── SearchAmountResponse.java
│   │       └── UserController.java
│   └── waiting
│       ├── domain
│       │   ├── WaitingService.java
│       │   ├── WaitingToken.java
│       │   ├── WaitingTokenRepository.java
│       │   └── enums
│       │       └── WaitingTokenStatus.java
│       ├── infrastructure
│       │   ├── RedisLockRepository.java
│       │   └── WaitingTokenRepositoryImpl.java
│       └── interfaces
│           ├── TokenResponse.java
│           ├── WaitingController.java
│           └── event
└── support
    ├── JsonUtil.java
    ├── LoggingFilter.java
    ├── WaitingInterceptor.java
    ├── config
    │   ├── AsyncConfig.java
    │   ├── FilterConfig.java
    │   ├── KafkaConfig.java
    │   ├── RedisCacheConfig.java
    │   ├── RedisConfig.java
    │   └── WebMvcConfig.java
    ├── exception
    │   ├── BaseException.java
    │   ├── CustomBadRequestException.java
    │   ├── CustomNotFoundException.java
    │   ├── ErrorCode.java
    │   └── Exception.java
    ├── handler
    │   ├── ApiControllerAdvice.java
    │   ├── ApiResultResponse.java
    │   ├── ErrorResponse.java
    │   ├── LockHandler.java
    │   └── TransactionHandler.java
    └── scheduler
        ├── ConcertSeatScheduler.java
        ├── KafkaRepublishScheduler.java
        └── WaitingTokenScheduler.java
```
