CREATE TABLE IF NOT EXISTS organisations_schema.shipments
(
    id                 UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    shipped_items      INT          NOT NULL,
    time_shipped       TIMESTAMP    NOT NULL DEFAULT now(),
    order_id           UUID         NOT NULL
);

ALTER TABLE organisations_schema.shipments
ADD FOREIGN KEY (order_id) REFERENCES organisations_schema.orders(id)
ON DELETE SET NULL ON UPDATE CASCADE;
