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
