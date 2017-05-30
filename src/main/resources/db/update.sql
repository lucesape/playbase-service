alter table playservice_location_equipment
add column  longitude double precision,
  add column latitude double precision,
  add column type_ integer,
  add column installeddate character varying(255),
  add column pm_guid character varying(255),
  add column name character varying(255);


ALTER TABLE public.playservice_location_equipment
  ADD CONSTRAINT playservicelocationequipmenttype_fk FOREIGN KEY (type_) REFERENCES public.playmapping_type_group (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

alter table playmapping_type_group add column id serial primary key;

alter table playservice_location_equipment 
add column PriceIndexation integer,
add column PriceInstallation integer,
add column PriceMaintenance integer,
add column PricePurchase integer,
add column PriceReInvestment integer;


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
