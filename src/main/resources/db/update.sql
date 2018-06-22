CREATE TABLE public.cronjob
(
   id serial, 
   cronexpressie text, 
   type_ text, 
   username text, 
   password text, 
   project text, 
   log text, 
   exporthash text, 
   baseurl text, 
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



ALTER TABLE public.cronjob
  ADD COLUMN exporthash text;


ALTER TABLE public.cronjob
  ADD COLUMN baseurl text;


ALTER TABLE public.playservice_images
  ADD COLUMN pa_id character varying(255);



ALTER TABLE public.playservice_images_playadvisor
  ADD COLUMN pa_id character varying(255);



ALTER TABLE public.playservice_documents
  ADD COLUMN pa_id character varying(255);



ALTER TABLE public.playservice_documents_playadvisor
  ADD COLUMN pa_id character varying(255);



update playservice_images set pm_guid = null where location in (select pl.id from playservice_images pi inner join playservice_locations pl on pi.location  = pl.id where pl.pa_id is not null and pl.pm_guid is null) 
update playservice_images as pi set pa_id = (select pl.pa_id from playservice_locations pl where pl.id = pi.location ) where location in (select pl.id from playservice_images pi inner join playservice_locations pl on pi.location  = pl.id where pl.pa_id is not null and pl.pm_guid is null) 



update playmapping_type_group set catasset = 'Ondergrond/' || catasset where catasset ilike '%ondergrond%';
update playmapping_type_group set catasset = 'Speeltoestellen/' || catasset where not catasset like 'Ondergrond/%';
update playmapping_type_group set catasset = 'Speeltoestellen/Carrousel' where catasset = 'Speeltoestellen/Speeltoestellen/Carrousel';
update playmapping_type_group set catasset = 'Faciliteiten/Zitgelegenheden/Bank' where catasset = 'Speeltoestellen/Zitgelegenheden/Bank';
update playmapping_type_group set catasset = 'Faciliteiten' where catasset = 'Speeltoestellen/Faciliteiten';
update playmapping_type_group set catasset = 'Faciliteiten/Kunstobjecten' where catasset = 'Speeltoestellen/Faciliteiten/Kunstobjecten';
	
  ALTER TABLE public.playservice_locations
  ADD COLUMN lastmodified timestamp without time zone;

ALTER TABLE public.playservice_locations
  ADD COLUMN lastexported timestamp without time zone;

update playservice_locations set lastmodified =  date '02-01-1970' ;
update playservice_locations set lastexported =  date '01-01-1970' ;



ALTER TABLE public.cronjob RENAME exporthash  TO authkey;


ALTER TABLE public.playservice_locations
  ADD COLUMN removedfromplayadvisor boolean;
ALTER TABLE public.playservice_locations_playadvisor
  ADD COLUMN removedfromplayadvisor boolean;

ALTER TABLE public.playservice_locations
  ADD COLUMN removedfromplaymapping boolean;
ALTER TABLE public.playservice_locations_playadvisor
  ADD COLUMN removedfromplaymapping boolean;


ALTER TABLE public.cronjob 
  ADD COLUMN status character varying(255);

ALTER TABLE public.cronjob
  RENAME TO project;
ALTER TABLE public.project RENAME project  TO name;


ALTER TABLE public.playservice_locations
  ADD COLUMN status character varying(255);
ALTER TABLE public.playservice_locations_playadvisor
  ADD COLUMN status character varying(255);

  
ALTER TABLE public.project
  ADD COLUMN imagepath character varying(255);
