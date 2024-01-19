CREATE TABLE IF NOT EXISTS organisations_schema.orders
(
    id                 UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    total_items        INT          NOT NULL,
    time_placed        TIMESTAMP    NOT NULL DEFAULT now(),
    organisation_id    UUID         NOT NULL
);

ALTER TABLE organisations_schema.orders
ADD FOREIGN KEY (organisation_id) REFERENCES organisations_schema.organisations(id)
ON DELETE SET NULL ON UPDATE CASCADE;
