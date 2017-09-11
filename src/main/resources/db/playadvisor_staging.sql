
-- Table: public.playservice_locations_playadvisor

-- DROP TABLE public.playservice_locations;_playadvisor

CREATE TABLE public.playservice_locations_playadvisor
(
  id serial,
  title character varying(255) NOT NULL,
  pa_title character varying(255),
  content character varying(255),
  summary character varying(255),
  street character varying(255),
  "number" character varying(255),
  numberextra character varying(255),
  postalcode character varying(255),
  municipality character varying(255),
  area character varying(255),
  country character varying(255),
  website character varying(255),
  email character varying(255),
  phone character varying(255),
  longitude double precision,
  latitude double precision,
  pm_guid character varying(255),
  pa_id character varying(255),
  parking integer,
  averagerating integer,
  geom geometry(Point,4326),
  CONSTRAINT playservice_locations_playadvisor_pkey PRIMARY KEY (id),
  CONSTRAINT playservicelocations_playadvisor_fk1 FOREIGN KEY (parking)
      REFERENCES public.playservice_parking_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_locations_playadvisor
  OWNER TO playbase;

-- Table: public.playservice_location_equipment_playadvisor

-- DROP TABLE public.playservice_location_equipment;_playadvisor

CREATE TABLE public.playservice_location_equipment_playadvisor
(
  id serial,
  location integer,
  equipment integer,
  pa_guid character varying(255),
  longitude double precision,
  latitude double precision,
  type_ integer,
  installeddate character varying(255),
  pm_guid character varying(255),
  name character varying(255),
  priceindexation integer,
  priceinstallation integer,
  pricemaintenance integer,
  pricepurchase integer,
  pricereinvestment integer,
  depth integer,
  width integer,
  height integer,
  endoflifeyear integer,
  freefallheight integer,
  safetyzonelength integer,
  safetyzonewidth integer,
  manufacturer character varying(255),
  material character varying(255),
  product character varying(255),
  productid character varying(255),
  productvariantid character varying(255),
  serialnumber character varying(255),
  geom geometry(Point,4326),
  CONSTRAINT playservice_location_equipment_playadvisor_pkey PRIMARY KEY (id),
  CONSTRAINT playservicelocationequipment_playadvisor_fk1 FOREIGN KEY (location)
      REFERENCES public.playservice_locations_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationequipment_playadvisor_fk2 FOREIGN KEY (equipment)
      REFERENCES public.playservice_equipment_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationequipmenttype_playadvisor_fk FOREIGN KEY (type_)
      REFERENCES public.playmapping_type_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_location_equipment_playadvisor
  OWNER TO playbase;


-- Table: public.playservice_location_equipment_agecategories_playadvisor

-- DROP TABLE public.playservice_location_equipment_agecategories;_playadvisor

CREATE TABLE public.playservice_location_equipment_agecategories_playadvisor
(
  location_equipment integer NOT NULL,
  agecategory integer NOT NULL,
  CONSTRAINT playservice_location_equipment_agecategories_playadvisor_pkey PRIMARY KEY (location_equipment, agecategory),
  CONSTRAINT playservicelocationagecategories_playadvisor_fk1 FOREIGN KEY (agecategory)
      REFERENCES public.playservice_agecategories_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationequipmentagecategories_playadvisor_fk2 FOREIGN KEY (location_equipment)
      REFERENCES public.playservice_location_equipment_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_location_equipment_agecategories_playadvisor
  OWNER TO playbase;
-- Table: public.playservice_location_facilities_playadvisor

-- DROP TABLE public.playservice_location_facilities;_playadvisor

CREATE TABLE public.playservice_location_facilities_playadvisor
(
  location integer NOT NULL,
  facility integer NOT NULL,
  CONSTRAINT playservice_location_facilities_playadvisor_pkey PRIMARY KEY (location, facility),
  CONSTRAINT playservicelocationfacilities_playadvisor_fk1 FOREIGN KEY (location)
      REFERENCES public.playservice_locations_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationfacilities_playadvisor_fk2 FOREIGN KEY (facility)
      REFERENCES public.playservice_facilities_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_location_facilities_playadvisor
  OWNER TO playbase;


-- Table: public.playservice_documents_playadvisor

-- DROP TABLE public.playservice_documents;_playadvisor

CREATE TABLE public.playservice_documents_playadvisor
(
  id serial,
  url text,
  caption character varying(255),
  location integer NOT NULL,
  equipment integer,
  pm_guid character varying(255),
  CONSTRAINT playservice_documents_playadvisor_pkey PRIMARY KEY (id),
  CONSTRAINT playservice_documents_playadvisor_fk FOREIGN KEY (equipment)
      REFERENCES public.playservice_location_equipment_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservice_documents_playadvisor_fk1 FOREIGN KEY (location)
      REFERENCES public.playservice_locations_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_documents_playadvisor
  OWNER TO playbase;


-- Table: public.playservice_images_playadvisor

-- DROP TABLE public.playservice_images;_playadvisor

CREATE TABLE public.playservice_images_playadvisor
(
  id serial,
  url text,
  caption character varying(255),
  location integer NOT NULL,
  equipment integer,
  pm_guid character varying(255),
  CONSTRAINT playservice_images_playadvisor_pkey PRIMARY KEY (id),
  CONSTRAINT playserviceimages_equipment_playadvisor_fk FOREIGN KEY (equipment)
      REFERENCES public.playservice_location_equipment_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playserviceimages_playadvisor_fk1 FOREIGN KEY (location)
      REFERENCES public.playservice_locations_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_images_playadvisor
  OWNER TO playbase;


-- Table: public.playservice_location_accessibility_playadvisor

-- DROP TABLE public.playservice_location_accessibility;_playadvisor

CREATE TABLE public.playservice_location_accessibility_playadvisor
(
  location integer NOT NULL,
  accessibility integer NOT NULL,
  CONSTRAINT playservice_location_accessibility_playadvisor_pkey PRIMARY KEY (location, accessibility),
  CONSTRAINT playservicelocationaccessibility_playadvisor_fk1 FOREIGN KEY (location)
      REFERENCES public.playservice_locations_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationaccessibility_playadvisor_fk2 FOREIGN KEY (accessibility)
      REFERENCES public.playservice_accessibility_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_location_accessibility_playadvisor
  OWNER TO playbase;


-- Table: public.playservice_location_agecategories_playadvisor

-- DROP TABLE public.playservice_location_agecategories;_playadvisor

CREATE TABLE public.playservice_location_agecategories_playadvisor
(
  location integer NOT NULL,
  agecategory integer NOT NULL,
  CONSTRAINT playservice_location_agecategories_playadvisor_pkey PRIMARY KEY (location, agecategory),
  CONSTRAINT playservicelocationagecategories_playadvisor_fk1 FOREIGN KEY (agecategory)
      REFERENCES public.playservice_agecategories_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationagecategories_playadvisor_fk2 FOREIGN KEY (location)
      REFERENCES public.playservice_locations_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_location_agecategories_playadvisor
  OWNER TO playbase;


-- Table: public.playservice_location_categories_playadvisor

-- DROP TABLE public.playservice_location_categories;_playadvisor

CREATE TABLE public.playservice_location_categories_playadvisor
(
  location integer NOT NULL,
  category integer NOT NULL,
  CONSTRAINT playservice_location_categories_playadvisor_pkey PRIMARY KEY (location, category),
  CONSTRAINT playservicelocationcategories_playadvisor_fk1 FOREIGN KEY (location)
      REFERENCES public.playservice_locations_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationcategories_fk2 FOREIGN KEY (category)
      REFERENCES public.playservice_categories_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_location_categories_playadvisor
  OWNER TO playbase;