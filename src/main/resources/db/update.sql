CREATE TABLE public.cronjob
(
   id serial, 
   cronexpressie text, 
   type_ text, 
   username text, 
   password text, 
   project text
) 
WITH (
  OIDS = FALSE
)
;
ALTER TABLE public.cronjob
  OWNER TO playbase;
