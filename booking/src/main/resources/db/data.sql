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
    CONCAT('Concert ', seq) AS name,
    CONCAT('Host ', seq) AS host
FROM number_sequence
LIMIT 1000000;

-- 콘서트 일정 데이터 콘서트 당 2개씩 삽입
INSERT INTO concert_schedule (concert_id, concert_date)
SELECT
    seq AS concert_id,
    CASE
        WHEN MOD(seq, 2) = 0 THEN '2024-08-07'
        ELSE '2024-08-08'
        END AS concert_date
FROM number_sequence
LIMIT 2000000;

-- 콘서트 일정에 대해 좌석 50개씩 삽입
INSERT INTO concert_seat (concert_id, concert_schedule_id, user_id, seat_number, seat_price, seat_status, modified_at, expired_at)
SELECT
    cs.concert_id,
    cs.id AS concert_schedule_id,
    NULL AS user_id,
    sr.seat_number AS seat_number,
    1000 AS seat_price,
    'AVAILABLE' AS seat_status,
    NULL AS modified_at,
    NULL AS expired_at
FROM
    concert_schedule cs
        CROSS JOIN (
        SELECT @row := @row + 1 AS seat_number
        FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
             (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
             (SELECT @row := 0) r
        LIMIT 50
    ) sr;