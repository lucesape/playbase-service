set session authorization playbase;
alter table playservice_location_equipment
add column  longitude double precision,
  add column latitude double precision,
  add column type_ integer,
  add column installeddate character varying(255),
  add column pm_guid character varying(255),
  add column name character varying(255);


alter table playmapping_type_group add column id serial primary key;
ALTER TABLE public.playservice_location_equipment
  ADD CONSTRAINT playservicelocationequipmenttype_fk FOREIGN KEY (type_) REFERENCES public.playmapping_type_group (id) ON UPDATE NO ACTION ON DELETE NO ACTION;


ALTER table playmapping_type_group  add column equipment_type integer;

ALTER TABLE public.playmapping_type_group
  ADD CONSTRAINT playmapping_equipment_fkey FOREIGN KEY (equipment_type) REFERENCES public.playservice_equipment_list (id) ON UPDATE NO ACTION ON DELETE NO ACTION;


alter table playservice_location_equipment 
add column priceindexation integer,
add column priceinstallation integer,
add column pricemaintenance integer,
add column pricepurchase integer,
add column pricereinvestment integer;



alter table playservice_location_equipment 
add column depth integer, 
add column width integer, 
add column height integer, 
add column endoflifeyear integer, 
add column freefallheight integer, 
add column safetyzonelength integer, 
add column safetyzonewidth integer, 
add column manufacturer character varying(255), 
add column material character varying(255), 
add column product character varying(255), 
add column productid character varying(255), 
add column productvariantid character varying(255), 
add column serialnumber character varying(255);

-- Table: public.playservice_location_agecategories

-- DROP TABLE public.playservice_location_agecategories;

CREATE TABLE public.playservice_location_equipment_agecategories
(
  location_equipment integer NOT NULL,
  agecategory integer NOT NULL,
  CONSTRAINT playservice_location_equipment_agecategories_pkey PRIMARY KEY (location_equipment, agecategory),
  CONSTRAINT playservicelocationagecategories_fk1 FOREIGN KEY (agecategory)
      REFERENCES public.playservice_agecategories_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationequipmentagecategories_fk2 FOREIGN KEY (location_equipment)
      REFERENCES public.playservice_location_equipment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.playservice_location_equipment_agecategories
  OWNER TO playbase;

ALTER TABLE public.playservice_images
  ADD COLUMN pm_guid character varying(255);

ALTER TABLE public.playservice_images
  ADD CONSTRAINT playserviceimages_equipment_fk FOREIGN KEY (equipment) REFERENCES public.playservice_location_equipment (id) ON UPDATE NO ACTION ON DELETE NO ACTION;


CREATE TABLE public.playservice_documents
(
  id serial,
  url text,
  caption character varying(255),
  location integer NOT NULL,
  equipment integer,
  pm_guid character varying(255),
  CONSTRAINT playservice_documents_pkey PRIMARY KEY (id),
  CONSTRAINT playservice_documents_fk FOREIGN KEY (equipment)
      REFERENCES public.playservice_location_equipment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservice_documents_fk1 FOREIGN KEY (location)
      REFERENCES public.playservice_locations (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (y
  OIDS=FALSE
);
ALTER TABLE public.playservice_documents
  OWNER TO playbase;




alter table playservice_locations add column geom geometry(Point, 4326)

alter table playservice_location_equipment add column geom geometry(Point, 4326)



alter table playservice_locations add column averagerating integer;

alter table playservice_locations_playadvisor add column averagerating integer;

--drop view v_locations_pa_rd ;
alter table playservice_locations_playadvisor alter column content type text;
drop view v_locations_rd ;
alter table playservice_locations alter column content type text;


ALTER TABLE public.playservice_location_agecategories
  ADD COLUMN pa_id character varying(255);

  ALTER TABLE public.playservice_location_agecategories_playadvisor
  ADD COLUMN pa_id character varying(255);


ALTER TABLE public.playservice_location_categories
  ADD COLUMN pa_id character varying(255);

  ALTER TABLE public.playservice_location_categories_playadvisor
  ADD COLUMN pa_id character varying(255);



ALTER TABLE public.playservice_location_facilities
  ADD COLUMN pa_id character varying(255);

  ALTER TABLE public.playservice_location_facilities_playadvisor
  ADD COLUMN pa_id character varying(255);

  ALTER TABLE public.playservice_location_categories
  ADD COLUMN pm_guid character varying(255);
ALTER TABLE public.playmapping_type_group
  ADD CONSTRAINT fk_location_equipment_type FOREIGN KEY (locationcategory) REFERENCES public.playservice_categories_list (id) ON UPDATE NO ACTION ON DELETE NO ACTION;
