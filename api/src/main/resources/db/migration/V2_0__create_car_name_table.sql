/* Car Name Table */

CREATE TABLE IF NOT EXISTS car_name
(
    id           BIGSERIAL,
    manufacturer VARCHAR(50) NOT NULL,
    model        VARCHAR(50) NOT NULL,
    UNIQUE (manufacturer, model),
    PRIMARY KEY (id)
);

-- drop `manufacturer` and `model` column from public.car table
-- add column `name` to public.car table and reference it to public.car_name table

ALTER TABLE IF EXISTS car
    DROP COLUMN IF EXISTS manufacturer CASCADE,
    DROP COLUMN IF EXISTS model CASCADE,
    ADD COLUMN IF NOT EXISTS name_id BIGINT
        REFERENCES car_name (id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT;
