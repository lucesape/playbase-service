-- View: public.v_equipment

-- DROP VIEW public.v_equipment;

-- View: public.v_equipment_rd

-- DROP VIEW public.v_equipment_rd;

CREATE OR REPLACE VIEW public.v_equipment_rd AS 
 SELECT ple.id,
    ple.location,
    ple.equipment,
    ple.pa_guid,
    ple.longitude,
    ple.latitude,
    ple.type_,
    ple.installeddate,
    ple.pm_guid,
    ple.name,
    ple.priceindexation,
    ple.priceinstallation,
    ple.pricemaintenance,
    ple.pricepurchase,
    ple.pricereinvestment,
    ple.depth,
    ple.width,
    ple.height,
    ple.endoflifeyear,
    ple.freefallheight,
    ple.safetyzonelength,
    ple.safetyzonewidth,
    ple.manufacturer,
    ple.material,
    ple.product,
    ple.productid,
    ple.productvariantid,
    ple.serialnumber,
    st_snaptogrid(st_transform(st_geomfromtext(((('POINT('::text || ple.longitude) || ' '::text) || ple.latitude) || ')'::text, 4326), 28992), 1::double precision) AS geom
   FROM playservice_location_equipment ple;

ALTER TABLE public.v_equipment_rd
  OWNER TO playbase;

-- View: public.v_locations_rd

-- DROP VIEW public.v_locations_rd;

CREATE OR REPLACE VIEW public.v_locations_rd AS 
 SELECT pl.id,
    pl.title,
    pl.content,
    pl.summary,
    pl.street,
    pl.number,
    pl.numberextra,
    pl.postalcode,
    pl.municipality,
    pl.area,
    pl.country,
    pl.website,
    pl.email,
    pl.phone,
    pl.longitude,
    pl.latitude,
    pl.pm_guid,
    pl.pa_id,
    pl.parking,
    st_buffer(st_transform(st_geomfromtext(((('POINT('::text || pl.longitude) || ' '::text) || pl.latitude) || ')'::text, 4326), 28992), 25::double precision) AS geom,
    pl.averagerating
   FROM playservice_locations pl;

ALTER TABLE public.v_locations_rd
  OWNER TO playbase;


-- View: public.v_locations_pa_rd

-- DROP VIEW public.v_locations_pa_rd;

CREATE OR REPLACE VIEW public.v_locations_pa_rd AS 
 SELECT pl.id,
    pl.title,
    pl.content,
    pl.summary,
    pl.street,
    pl.number,
    pl.numberextra,
    pl.postalcode,
    pl.municipality,
    pl.area,
    pl.country,
    pl.website,
    pl.email,
    pl.phone,
    pl.longitude,
    pl.latitude,
    pl.pm_guid,
    pl.pa_id,
    pl.parking,
    st_buffer(st_transform(st_geomfromtext(((('POINT('::text || pl.longitude) || ' '::text) || pl.latitude) || ')'::text, 4326), 28992), 25::double precision) AS geom,
    pl.averagerating
   FROM playservice_locations_playadvisor pl;

ALTER TABLE public.v_locations_pa_rd
  OWNER TO playbase;

-- View: public.v_equipment_pa_rd

-- DROP VIEW public.v_equipment_pa_rd;

CREATE OR REPLACE VIEW public.v_equipment_pa_rd AS 
 SELECT ple.id,
    ple.location,
    ple.equipment,
    ple.pa_guid,
    ple.longitude,
    ple.latitude,
    ple.type_,
    ple.installeddate,
    ple.pm_guid,
    ple.name,
    ple.priceindexation,
    ple.priceinstallation,
    ple.pricemaintenance,
    ple.pricepurchase,
    ple.pricereinvestment,
    ple.depth,
    ple.width,
    ple.height,
    ple.endoflifeyear,
    ple.freefallheight,
    ple.safetyzonelength,
    ple.safetyzonewidth,
    ple.manufacturer,
    ple.material,
    ple.product,
    ple.productid,
    ple.productvariantid,
    ple.serialnumber,
    st_snaptogrid(st_transform(st_geomfromtext(((('POINT('::text || ple.longitude) || ' '::text) || ple.latitude) || ')'::text, 4326), 28992), 1::double precision) AS geom
   FROM playservice_location_equipment_playadvisor ple;

ALTER TABLE public.v_equipment_pa_rd
  OWNER TO playbase;
