ALTER TABLE public.playservice_locations RENAME content  TO pa_content;
ALTER TABLE public.playservice_locations ADD COLUMN pm_content text;
ALTER TABLE public.playservice_locations_playadvisor RENAME content  TO pa_content;
ALTER TABLE public.playservice_locations_playadvisor ADD COLUMN pm_content text;