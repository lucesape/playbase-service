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

-- View: public.v_locations_rd

-- DROP VIEW public.v_locations_rd;

-- View: public.v_locations_rd

-- DROP VIEW public.v_locations_rd;

CREATE OR REPLACE VIEW public.v_locations_rd AS 
 SELECT pl.id,
    pl.title,
    pl.pa_content,
    pl.pm_content,
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
    st_transform(st_geomfromtext(((('POINT('::text || pl.longitude) || ' '::text) || pl.latitude) || ')'::text, 4326), 28992) AS geom,
    ((( SELECT sum(
                CASE
                    WHEN date_part('days'::text, now() - to_date(NULLIF(ple.installeddate::text, ''::text), 'YYYY-MM-DD'::text)::timestamp with time zone) < 730::double precision THEN 1
                    ELSE 0
                END) AS sum
           FROM playservice_location_equipment ple
          WHERE ple.location = pl.id))::double precision / (( SELECT count(*) AS count
           FROM playservice_location_equipment ple
          WHERE ple.location = pl.id))::double precision) > 0.3::double precision AS newasset,
    pl.averagerating,
    ( SELECT playservice_images.url
           FROM playservice_images
          WHERE playservice_images.location = pl.id
          ORDER BY playservice_images.equipment DESC, playservice_images.lastupdated DESC
         LIMIT 1) AS image,
        CASE
            WHEN (( SELECT COALESCE(sum(
                    CASE
                        WHEN ( SELECT
                                CASE
                                    WHEN ptg.locationcategory = ANY (ARRAY[5, 6, 7, 8, 9, 10, 11, 12, 13, 24, 26]) THEN true
                                    ELSE false
                                END AS "case"
                           FROM playmapping_type_group ptg
                          WHERE ptg.id = ple.type_) THEN 1
                        ELSE 0
                    END), 0::bigint) AS "coalesce"
               FROM playservice_location_equipment ple
              WHERE ple.location = pl.id)) > 0 THEN true
            ELSE false
        END AS issport,
        CASE
            WHEN (( SELECT COALESCE(sum(
                    CASE
                        WHEN ( SELECT
                                CASE
                                    WHEN ptg.locationcategory = ANY (ARRAY[19, 20, 21, 22, 23]) THEN true
                                    ELSE false
                                END AS "case"
                           FROM playmapping_type_group ptg
                          WHERE ptg.id = ple.type_) THEN 1
                        ELSE 0
                    END), 0::bigint) AS "coalesce"
               FROM playservice_location_equipment ple
              WHERE ple.location = pl.id)) > 0 THEN true
            ELSE false
        END AS isplayground
   FROM playservice_locations pl;

ALTER TABLE public.v_locations_rd
  OWNER TO playbase;




-- View: public.v_locations_pa_rd

-- DROP VIEW public.v_locations_pa_rd;

CREATE OR REPLACE VIEW public.v_locations_pa_rd AS 
 SELECT pl.id,
    pl.title,
    pl.pa_content,
    pl.pm_content,
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
