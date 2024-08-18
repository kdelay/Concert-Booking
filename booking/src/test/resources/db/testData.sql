INSERT INTO user (amount) VALUES (1000);
INSERT INTO user (amount) VALUES (2000);

INSERT INTO concert (name, host) VALUES ('Rock Fest', 'Rock Inc.');
INSERT INTO concert (name, host) VALUES ('Jazz Night', 'Jazz Corp.');

INSERT INTO concert_schedule (concert_id, concert_date) VALUES (1, '2024-08-15');
INSERT INTO concert_schedule (concert_id, concert_date) VALUES (2, '2024-09-01');

INSERT INTO concert_seat (concert_id, concert_schedule_id, seat_number, seat_price, seat_status) VALUES (1, 1, 1, 100, 'AVAILABLE');
INSERT INTO concert_seat (concert_id, concert_schedule_id, seat_number, seat_price, seat_status) VALUES (1, 1, 2, 100, 'AVAILABLE');

INSERT INTO reservation (concert_seat_id, user_id, concert_name, concert_date, reservation_status, created_at) VALUES (1, 1, 'Rock Fest', '2024-08-15', 'RESERVING', NOW());

INSERT INTO payment (reservation_id, price, payment_state, created_at) VALUES (1, 100, 'PENDING', NOW());