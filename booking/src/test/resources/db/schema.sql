-- 유저
DROP TABLE IF EXISTS user;
CREATE TABLE IF NOT EXISTS user (
  id BIGINT NOT NULL COMMENT '유저 PK',
  amount DECIMAL(7,0) COMMENT '잔액',
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 대기열
DROP TABLE IF EXISTS waiting_token;
CREATE TABLE IF NOT EXISTS waiting_token (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '대기열 PK',
    user_id BIGINT COMMENT '유저 PK',
    token VARCHAR(255) NOT NULL COMMENT '토큰 정보(uuid)',
    waiting_token_status ENUM('ACTIVATE','DEACTIVATE','EXPIRED') COMMENT '대기열 토큰 상태',
    created_at DATETIME(6) COMMENT '대기열 토큰 요청 시간',
    modified_at DATETIME(6) COMMENT '상태 변경 시간',
    PRIMARY KEY (id),
    UNIQUE (token)
) ENGINE=InnoDB;

-- 콘서트
DROP TABLE IF EXISTS concert;
CREATE TABLE IF NOT EXISTS concert (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '콘서트 PK',
    name VARCHAR(20) NOT NULL COMMENT '콘서트 이름',
    host VARCHAR(10) NOT NULL COMMENT '콘서트 주최자',
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 콘서트 날짜
DROP TABLE IF EXISTS concert_schedule;
CREATE TABLE IF NOT EXISTS concert_schedule (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '콘서트 날짜 PK',
    concert_id BIGINT COMMENT '콘서트 PK',
    concert_date DATE NOT NULL COMMENT '콘서트 날짜',
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 콘서트 좌석
DROP TABLE IF EXISTS concert_seat;
CREATE TABLE IF NOT EXISTS concert_seat (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '콘서트 좌석 PK',
    concert_id BIGINT COMMENT '콘서트 PK',
    concert_schedule_id BIGINT COMMENT '콘서트 날짜 PK',
    seat_number INT NOT NULL COMMENT '콘서트 좌석 번호',
    seat_price DECIMAL(7,0) COMMENT '콘서트 좌석 금액',
    modified_at DATETIME(6) COMMENT '상태 변경 시간',
    expired_at DATETIME(6) COMMENT '좌석 임시 배정 만료 시간',
    user_id BIGINT COMMENT '유저 PK',
    seat_status ENUM('AVAILABLE','RESERVED','TEMPORARY') COMMENT '콘서트 좌석 상태',
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 예약
DROP TABLE IF EXISTS reservation;
CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '콘서트 예약 PK',
    concert_seat_id BIGINT NOT NULL COMMENT '콘서트 좌석 PK',
    user_id BIGINT NOT NULL COMMENT '유저 PK',
    concert_name VARCHAR(20) NOT NULL COMMENT '콘서트 이름',
    concert_date DATE NOT NULL COMMENT '콘서트 날짜',
    reservation_status ENUM('CANCELED','RESERVED','RESERVING') COMMENT '예약 상태',
    created_at DATETIME(6) COMMENT '콘서트 예약 요청 시간',
    modified_at DATETIME(6) COMMENT '상태 변경 시간',
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- 결제
DROP TABLE IF EXISTS payment;
CREATE TABLE IF NOT EXISTS payment (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '콘서트 결제 PK',
    reservation_id BIGINT COMMENT '콘서트 예약 PK',
    price DECIMAL(7,0) COMMENT '결제 금액',
    payment_state ENUM('PENDING', 'CANCELED','COMPLETED') COMMENT '결제 상태',
    created_at DATETIME(6) COMMENT '콘서트 결제 요청 시간',
    modified_at DATETIME(6) COMMENT '상태 변경 시간',
    PRIMARY KEY (id),
    UNIQUE (reservation_id)
) ENGINE=InnoDB;
