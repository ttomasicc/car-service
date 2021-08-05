/* Car Check-Up Table */

CREATE TABLE IF NOT EXISTS car_check_up(
    id          BIGSERIAL,
    car_id      BIGINT,
    date_time   TIMESTAMP NOT NULL,
    worker      VARCHAR(100) NOT NULL,
    price       NUMERIC(6, 2) CHECK (price >= 0) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (car_id) REFERENCES car(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
