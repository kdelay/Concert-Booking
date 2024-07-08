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