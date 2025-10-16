CREATE TABLE "provider" (
  "id" uuid PRIMARY KEY,
  "org_id" uuid NOT NULL,
  "name" text NOT NULL,
  "display_name" text NOT NULL,
  "description" text,
  "provider_type" varchar(50) NOT NULL DEFAULT 'INDIVIDUAL',
  "logo_url" text,
  "cover_image_url" text,
  "verification_status" varchar(30) DEFAULT 'PENDING',
  "onboarding_completed" boolean DEFAULT false,
  "timezone" varchar(100) DEFAULT 'UTC',
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz,
  "is_active" boolean NOT NULL DEFAULT true
);

CREATE TABLE "provider_contact" (
  "id" uuid PRIMARY KEY,
  "provider_id" uuid NOT NULL,
  "email" varchar(255) UNIQUE NOT NULL,
  "phone" varchar(20) NOT NULL,
  "website" varchar(255),
  "created_at" timestamptz NOT NULL,
  "updated_at" timestamptz,
  "is_active" boolean NOT NULL DEFAULT true
);

CREATE TABLE "provider_location" (
  "id" uuid PRIMARY KEY,
  "provider_id" uuid NOT NULL,
  "name" varchar(150) NOT NULL,
  "address_line1" text NOT NULL,
  "address_line2" text,
  "city" varchar(100) NOT NULL,
  "state" varchar(100) NOT NULL,
  "country" varchar(100) NOT NULL,
  "postal_code" varchar(20) NOT NULL,
  "latitude" decimal(9,6),
  "longitude" decimal(9,6),
  "is_primary" boolean DEFAULT false,
  "is_active" boolean DEFAULT true,
  "created_at" timestamptz,
  "updated_at" timestamptz
);

ALTER TABLE "provider_contact" ADD FOREIGN KEY ("provider_id") REFERENCES "provider" ("id");

ALTER TABLE "provider_location" ADD FOREIGN KEY ("provider_id") REFERENCES "provider" ("id");