-- 숫자 시퀀스 200만개 데이터 생성
INSERT INTO number_sequence (seq)
WITH RECURSIVE numbers(seq) AS (
    SELECT 1
    UNION ALL
    SELECT seq + 1
    FROM numbers
    WHERE seq < 2000000
)
SELECT seq
FROM numbers;

-- 유저 데이터 100만개 삽입
INSERT INTO user (amount)
SELECT 0
FROM number_sequence
LIMIT 1000000;

-- 콘서트 데이터 100만개 삽입
INSERT INTO concert (name, host)
SELECT
    'Concert ' || seq AS name,
    'Host ' || seq AS host
FROM number_sequence
LIMIT 1000000;

-- 콘서트 일정 데이터 콘서트 당 2개씩 삽입
INSERT INTO concert_schedule (concert_id, concert_date)
SELECT
    seq AS concert_id,
    CASE
        WHEN MOD(seq, 2) = 0 THEN DATE '2024-08-07'
        ELSE DATE '2024-08-08'
        END AS concert_date
FROM number_sequence
LIMIT 2000000;

-- 콘서트 1 (CONCERT_ID: 1) 일정 1 (CONCERT_SCHEDULE_ID: 1)에 대해 좌석 50개 삽입
INSERT INTO concert_seat (concert_id, concert_schedule_id, user_id, seat_number, seat_price, seat_status, modified_at, expired_at)
SELECT
    cs.concert_id,
    cs.id AS concert_schedule_id,
    NULL AS user_id,
    sr.seat_number,
    1000 AS seat_price,
    'AVAILABLE' AS seat_status,
    NULL AS modified_at,
    NULL AS expired_at
FROM
    concert_schedule cs
        CROSS JOIN (
        SELECT ROW_NUMBER() OVER () AS seat_number
        FROM number_sequence
        LIMIT 50
    ) sr;

-- 예약 데이터 삽입
INSERT INTO reservation (concert_seat_id, user_id, concert_name, concert_date, reservation_status, created_at, modified_at)
values (1, 1, 'Concert 1', '2024-08-07', 'RESERVING', now(), null);

-- 결제 데이터 삽입
INSERT INTO payment (reservation_id, price, payment_state, created_at, modified_at)
values (1, 1000, 'PENDING', now(), null);