-- insert test car name data

INSERT INTO car_name (manufacturer, model)
VALUES ('Ferrari', 'Rossa'),
       ('Alvis', 'TB 21'),
       ('Cadillac', 'CTS Coupe'),
       ('Rolls-Royce', 'Silver Spur'),
       ('Audi', 'RS4'),
       ('Honda', 'HP-X'),
       ('Moretti', '1200'),
       ('Mercedes-Benz', 'SLK'),
       ('BMW', '725'),
       ('Subaru', 'B'),
       ('Chevrolet', 'Malibu'),
       ('Vector', 'Wiegert'),
       ('Ford', 'Galaxie'),
       ('Eagle', 'Vision'),
       ('AMC', 'AMX')
ON CONFLICT DO NOTHING;
