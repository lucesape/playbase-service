CREATE TABLE public.cronjob
(
   id serial, 
   cronexpressie text, 
   type_ text, 
   username text, 
   password text, 
   project text, 
   log text, 
   importedstring text,
   mailaddress text,
   lastrun timestamp without time zone
) 
WITH (
  OIDS = FALSE
)
;
ALTER TABLE public.cronjob
  OWNER TO playbase;

ALTER TABLE public.cronjob
  ADD CONSTRAINT prim_key_cronjob PRIMARY KEY (id);

ALTER TABLE public.playservice_location_equipment_agecategories
  DROP CONSTRAINT playservicelocationequipmentagecategories_fk2;
ALTER TABLE public.playservice_location_equipment_agecategories
  ADD CONSTRAINT playservicelocationequipmentagecategories_fk2 FOREIGN KEY (location_equipment) REFERENCES public.playservice_location_equipment (id) ON UPDATE NO ACTION ON DELETE CASCADE;

ALTER TABLE public.playservice_locations
  ADD COLUMN project character varying(255);


ALTER TABLE public.playservice_locations_playadvisor
  ADD COLUMN project character varying(255);
