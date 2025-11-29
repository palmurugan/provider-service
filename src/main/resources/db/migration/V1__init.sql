CREATE TABLE "provider"
(
    "id"                   uuid PRIMARY KEY,
    "org_id"               uuid        NOT NULL,
    "name"                 text        NOT NULL,
    "display_name"         text        NOT NULL,
    "description"          text,
    "provider_type"        varchar(50) NOT NULL DEFAULT 'INDIVIDUAL',
    "logo_url"             text,
    "cover_image_url"      text,
    "verification_status"  varchar(30)          DEFAULT 'PENDING',
    "onboarding_completed" boolean              DEFAULT false,
    "timezone"             varchar(100)         DEFAULT 'UTC',
    "created_at"           timestamptz NOT NULL,
    "updated_at"           timestamptz,
    "is_active"            boolean     NOT NULL DEFAULT true
);

CREATE TABLE "provider_contact"
(
    "id"          uuid PRIMARY KEY,
    "provider_id" uuid                NOT NULL,
    "email"       varchar(255) UNIQUE NOT NULL,
    "phone"       varchar(20)         NOT NULL,
    "website"     varchar(255),
    "created_at"  timestamptz         NOT NULL,
    "updated_at"  timestamptz,
    "is_active"   boolean             NOT NULL DEFAULT true
);

CREATE TABLE "provider_location"
(
    "id"            uuid PRIMARY KEY,
    "provider_id"   uuid         NOT NULL,
    "name"          varchar(150) NOT NULL,
    "address_line1" text         NOT NULL,
    "address_line2" text,
    "city"          varchar(100) NOT NULL,
    "state"         varchar(100) NOT NULL,
    "country"       varchar(100) NOT NULL,
    "postal_code"   varchar(20)  NOT NULL,
    "latitude"      decimal(9, 6),
    "longitude"     decimal(9, 6),
    "is_primary"    boolean DEFAULT false,
    "is_active"     boolean DEFAULT true,
    "created_at"    timestamptz,
    "updated_at"    timestamptz
);

ALTER TABLE "provider_contact"
    ADD FOREIGN KEY ("provider_id") REFERENCES "provider" ("id");

ALTER TABLE "provider_location"
    ADD FOREIGN KEY ("provider_id") REFERENCES "provider" ("id");

CREATE TABLE provider_service
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id       uuid NOT NULL,
    provider_id  UUID NOT NULL,
    category_id  UUID NOT NULL,
    title        TEXT NOT NULL,
    duration     INT  not null,
    unit         VARCHAR(12)      DEFAULT 'MINIUTES',
    price        NUMERIC(12, 2),
    currency     VARCHAR(3)       DEFAULT 'INR',
    max_capacity INT              DEFAULT 1,
    is_active    BOOLEAN          DEFAULT true,
    metadata     JSONB, -- any custom options or tags
    created_at   TIMESTAMPTZ      DEFAULT now(),
    updated_at   TIMESTAMPTZ      DEFAULT now()
);


CREATE TYPE config_type AS ENUM (
  'RECURRING',
  'ONE_TIME',
  'OVERRIDE'
);

CREATE TABLE availability_config
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id             UUID    NOT NULL REFERENCES provider (id),
    service_id              UUID REFERENCES provider_service (id), -- NULL means applies to all services
    config_type             varchar NOT NULL DEFAULT 'RECURRING',
    start_date              DATE    NOT NULL,
    end_date                DATE,                                  -- NULL means ongoing
    start_time              TIME    NOT NULL,
    end_time                TIME    NOT NULL,
    timezone                TEXT             DEFAULT 'UTC',
    recurrence_config       JSONB   NOT NULL DEFAULT '{}',
    max_concurrent_bookings INTEGER          DEFAULT 1,
    is_active               BOOLEAN          DEFAULT true,
    metadata                JSONB            DEFAULT '{}',
    created_at              TIMESTAMPTZ      DEFAULT now(),
    updated_at              TIMESTAMPTZ      DEFAULT now(),
    CHECK (start_time < end_time),
    CHECK (end_date IS NULL OR end_date >= start_date)
);

-- Location related table changes
CREATE TABLE provider.locations
(
    id          UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    org_id      UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    city        VARCHAR(100) NOT NULL,
    state       VARCHAR(100) NOT NULL,
    country     VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    latitude    DECIMAL(10, 8),
    longitude   DECIMAL(11, 8),
    is_active   BOOLEAN               DEFAULT true,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(100),
    updated_by  VARCHAR(100)
);

CREATE TABLE provider.service_locations
(
    id          UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    org_id      UUID      NOT NULL,
    service_id  UUID      NOT NULL,
    location_id UUID      NOT NULL,
    is_primary  BOOLEAN            DEFAULT false,
    is_active   BOOLEAN            DEFAULT true,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE provider.service_locations
    ADD FOREIGN KEY ("service_id") REFERENCES provider.provider_service ("id");
ALTER TABLE provider.service_locations
    ADD FOREIGN KEY ("location_id") REFERENCES provider.locations ("id");


-- Booking related tables
CREATE TABLE provider.slots
(
    id                  uuid PRIMARY key,
    org_id              uuid NOT NULL,
    provider_id         uuid NOT NULL,
    provider_service_id uuid NOT NULL,
    slot_date           DATE NOT NULL,
    start_time          TIME NOT NULL,
    end_time            TIME NOT NULL,
    duration_minutes    INT  NOT NULL,
    capacity            INT         DEFAULT 1,
    booked_count        INT         DEFAULT 0,
    status              varchar(50) DEFAULT 'available',
    created_at          TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (provider_id) REFERENCES provider (id),
    FOREIGN KEY (provider_service_id) REFERENCES provider_service (id)
);

alter table provider.provider_service
    add column description TEXT;