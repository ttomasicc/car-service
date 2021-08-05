/* Car Table */

CREATE TABLE IF NOT EXISTS car(
    id              BIGSERIAL,
    owner_id        BIGINT,
    date_created    DATE NOT NULL DEFAULT CURRENT_DATE,
    manufacturer    VARCHAR(50) NOT NULL ,
    model           VARCHAR(100) NOT NULL,
    production_year INT NOT NULL,
    serial_number   CHAR(6) NOT NULL UNIQUE,

    PRIMARY KEY (id)
);
