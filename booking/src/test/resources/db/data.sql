-- 유저 데이터 삽입
insert into user(amount)
values (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0),
       (0);

-- 대기열 토큰 데이터 삽입
insert into waiting_token(user_id, version, token, waiting_token_status, created_at, modified_at)
values (1, 0, 'valid-token', 'DEACTIVATE', CURRENT_TIMESTAMP(), null);

-- 콘서트 데이터 삽입
insert into CONCERT(NAME, HOST) values ('A 콘서트', 'A');
insert into CONCERT(NAME, HOST) values ('B 콘서트', 'B');

-- 콘서트 일정 데이터 삽입
insert into CONCERT_SCHEDULE(CONCERT_ID, CONCERT_DATE) values (1, '2024-07-10');
insert into CONCERT_SCHEDULE(CONCERT_ID, CONCERT_DATE) values (1, '2024-07-11');

-- 콘서트 1 (CONCERT_ID: 1) 일정 1 (CONCERT_SCHEDULE_ID: 1)에 대해 좌석 50개 삽입
-- 일정 1 좌석 50개 삽입
insert into CONCERT_SEAT (VERSION, CONCERT_ID, CONCERT_SCHEDULE_ID, USER_ID, SEAT_NUMBER, SEAT_PRICE, SEAT_STATUS, MODIFIED_AT, EXPIRED_AT)
values
    (0, 1, 1, NULL, 1, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 2, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 3, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 4, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 5, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 6, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 7, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 8, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 9, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 10, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 11, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 12, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 13, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 14, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 15, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 16, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 17, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 18, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 19, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 20, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 21, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 22, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 23, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 24, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 25, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 26, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 27, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 28, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 29, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 30, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 31, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 32, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 33, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 34, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 35, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 36, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 37, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 38, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 39, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 40, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 41, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 42, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 43, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 44, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 45, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 46, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 47, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 48, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 49, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 1, NULL, 50, 1000, 'AVAILABLE', NULL, NULL);

-- 일정 2 좌석 50개 삽입
insert into CONCERT_SEAT (VERSION, CONCERT_ID, CONCERT_SCHEDULE_ID, USER_ID, SEAT_NUMBER, SEAT_PRICE, SEAT_STATUS, MODIFIED_AT, EXPIRED_AT)
values
    (0, 1, 2, NULL, 1, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 2, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 3, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 4, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 5, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 6, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 7, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 8, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 9, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 10, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 11, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 12, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 13, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 14, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 15, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 16, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 17, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 18, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 19, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 20, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 21, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 22, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 23, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 24, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 25, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 26, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 27, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 28, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 29, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 30, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 31, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 32, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 33, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 34, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 35, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 36, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 37, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 38, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 39, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 40, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 41, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 42, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 43, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 44, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 45, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 46, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 47, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 48, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 49, 1000, 'AVAILABLE', NULL, NULL),
    (0, 1, 2, NULL, 50, 1000, 'AVAILABLE', NULL, NULL);