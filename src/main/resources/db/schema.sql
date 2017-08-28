--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.2
-- Dumped by pg_dump version 9.6.2

-- Started on 2017-05-29 16:42:48 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3812 (class 1262 OID 62813)
-- Name: playbase; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE playbase WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';


\connect playbase

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12429)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 3814 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- TOC entry 2 (class 3079 OID 62814)
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- TOC entry 3815 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 240 (class 1259 OID 64787)
-- Name: buurt_2016; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE buurt_2016 (
    gid integer NOT NULL,
    bu_code character varying(10),
    bu_naam character varying(60),
    wk_code character varying(8),
    gm_code character varying(6),
    gm_naam character varying(60),
    ind_wbi numeric(10,0),
    water character varying(4),
    oad numeric(10,0),
    sted numeric(10,0),
    aant_inw numeric(10,0),
    aant_man numeric(10,0),
    aant_vrouw numeric(10,0),
    p_00_14_jr numeric(10,0),
    p_15_24_jr numeric(10,0),
    p_25_44_jr numeric(10,0),
    p_45_64_jr numeric(10,0),
    p_65_eo_jr numeric(10,0),
    p_ongehuwd numeric(10,0),
    p_gehuwd numeric(10,0),
    p_gescheid numeric(10,0),
    p_verweduw numeric(10,0),
    bev_dichth numeric(10,0),
    aantal_hh numeric(10,0),
    p_eenp_hh numeric(10,0),
    p_hh_z_k numeric(10,0),
    p_hh_m_k numeric(10,0),
    gem_hh_gr double precision,
    p_west_al numeric(10,0),
    p_n_w_al numeric(10,0),
    p_marokko numeric(10,0),
    p_ant_aru numeric(10,0),
    p_surinam numeric(10,0),
    p_turkije numeric(10,0),
    p_over_nw numeric(10,0),
    opp_tot numeric(10,0),
    opp_land numeric(10,0),
    opp_water numeric(10,0),
    geom geometry(MultiPolygon,28992)
);


--
-- TOC entry 239 (class 1259 OID 64785)
-- Name: buurt_2016_gid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE buurt_2016_gid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3816 (class 0 OID 0)
-- Dependencies: 239
-- Name: buurt_2016_gid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE buurt_2016_gid_seq OWNED BY buurt_2016.gid;


--
-- TOC entry 242 (class 1259 OID 67402)
-- Name: gem_2016; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE gem_2016 (
    gid integer NOT NULL,
    gm_code character varying(6),
    gm_naam character varying(60),
    water character varying(4),
    oad numeric(10,0),
    sted numeric(10,0),
    aant_inw numeric(10,0),
    aant_man numeric(10,0),
    aant_vrouw numeric(10,0),
    p_00_14_jr numeric(10,0),
    p_15_24_jr numeric(10,0),
    p_25_44_jr numeric(10,0),
    p_45_64_jr numeric(10,0),
    p_65_eo_jr numeric(10,0),
    p_ongehuwd numeric(10,0),
    p_gehuwd numeric(10,0),
    p_gescheid numeric(10,0),
    p_verweduw numeric(10,0),
    bev_dichth numeric(10,0),
    aantal_hh numeric(10,0),
    p_eenp_hh numeric(10,0),
    p_hh_z_k numeric(10,0),
    p_hh_m_k numeric(10,0),
    gem_hh_gr double precision,
    p_west_al numeric(10,0),
    p_n_w_al numeric(10,0),
    p_marokko numeric(10,0),
    p_ant_aru numeric(10,0),
    p_surinam numeric(10,0),
    p_turkije numeric(10,0),
    p_over_nw numeric(10,0),
    opp_tot numeric(10,0),
    opp_land numeric(10,0),
    opp_water numeric(10,0),
    geom geometry(MultiPolygon,28992)
);


--
-- TOC entry 241 (class 1259 OID 67400)
-- Name: gem_2016_gid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE gem_2016_gid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3817 (class 0 OID 0)
-- Dependencies: 241
-- Name: gem_2016_gid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE gem_2016_gid_seq OWNED BY gem_2016.gid;


--
-- TOC entry 201 (class 1259 OID 64287)
-- Name: omnummertabel; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE omnummertabel (
    gid integer NOT NULL,
    boomid character varying(254),
    project character varying(254),
    boomidn character varying(20),
    projectn character varying(25),
    uid character varying(50),
    omnummeren character varying(2)
);


--
-- TOC entry 202 (class 1259 OID 64293)
-- Name: omnummertabel_gid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE omnummertabel_gid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3818 (class 0 OID 0)
-- Dependencies: 202
-- Name: omnummertabel_gid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE omnummertabel_gid_seq OWNED BY omnummertabel.gid;


--
-- TOC entry 203 (class 1259 OID 64295)
-- Name: pc6_playlocations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE pc6_playlocations (
    locaties bigint,
    toestellen bigint,
    adressen bigint,
    postcode character varying(7),
    the_geom geometry(MultiPolygon,28992),
    toesteladres bigint
);


--
-- TOC entry 204 (class 1259 OID 64301)
-- Name: pc6_playlocations_150m; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE pc6_playlocations_150m (
    locaties bigint,
    toestellen bigint,
    adressen bigint,
    toesteladres bigint,
    postcode character varying(7),
    the_geom geometry(MultiPolygon,28992)
);


--
-- TOC entry 205 (class 1259 OID 64307)
-- Name: playlocations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playlocations (
    fid integer NOT NULL,
    wsg84_geom geometry(Point,4326),
    id character varying,
    adres character varying,
    uid character varying,
    updates character varying,
    "Lat" character varying,
    "Lng" character varying,
    naam character varying,
    ref character varying,
    the_geom geometry(Point,28992),
    aantal bigint,
    buffer150m geometry(MultiPolygon,28992)
);


--
-- TOC entry 206 (class 1259 OID 64313)
-- Name: playlocations_fid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playlocations_fid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3819 (class 0 OID 0)
-- Dependencies: 206
-- Name: playlocations_fid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playlocations_fid_seq OWNED BY playlocations.fid;


--
-- TOC entry 207 (class 1259 OID 64315)
-- Name: playmapping_assets_houten; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playmapping_assets_houten (
    fid integer NOT NULL,
    id character varying(255),
    locationid character varying(255),
    locationname character varying(255),
    lastupdated character varying(255),
    name character varying(255),
    assettype character varying(255),
    manufacturer character varying(255),
    product character varying(255),
    serialnumber character varying(255),
    material character varying(255),
    height numeric,
    depth numeric,
    width numeric,
    installeddate character varying(255),
    endoflifeyear numeric,
    freefallheight numeric,
    safetyzonelength numeric,
    safetyzonewidth numeric,
    agegrouptoddlers character varying(255),
    agegroupjuniors character varying(255),
    agegroupseniors character varying(255),
    pricepurchase numeric,
    priceinstallation numeric,
    pricereinvestment numeric,
    pricemaintenance numeric,
    priceindexation numeric,
    lat numeric,
    lng numeric,
    "group" character varying(255)
);


--
-- TOC entry 208 (class 1259 OID 64321)
-- Name: playmapping_assets_houten_fid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playmapping_assets_houten_fid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3820 (class 0 OID 0)
-- Dependencies: 208
-- Name: playmapping_assets_houten_fid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playmapping_assets_houten_fid_seq OWNED BY playmapping_assets_houten.fid;


--
-- TOC entry 209 (class 1259 OID 64323)
-- Name: playmapping_normen; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playmapping_normen (
    naam character varying(255) NOT NULL,
    type character varying(255) NOT NULL,
    eenheid character varying(50),
    leeftijdscategorie character varying(255),
    vervangingstermijn bigint,
    aanschafwaarde numeric,
    onderhoudskosten numeric
);


--
-- TOC entry 210 (class 1259 OID 64329)
-- Name: playmapping_type_group; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playmapping_type_group (
    categorie character varying(255),
    assettype character varying(255),
    groep character varying(255),
    catasset character varying(255)
);


--
-- TOC entry 211 (class 1259 OID 64335)
-- Name: playservice_accessibility_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_accessibility_list (
    id integer NOT NULL,
    accessibility character varying(255) NOT NULL
);


--
-- TOC entry 212 (class 1259 OID 64338)
-- Name: playservice_accessibility_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playservice_accessibility_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3821 (class 0 OID 0)
-- Dependencies: 212
-- Name: playservice_accessibility_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playservice_accessibility_id_seq OWNED BY playservice_accessibility_list.id;


--
-- TOC entry 213 (class 1259 OID 64340)
-- Name: playservice_agecategories_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_agecategories_list (
    id integer NOT NULL,
    agecategory character varying(255) NOT NULL
);


--
-- TOC entry 214 (class 1259 OID 64343)
-- Name: playservice_agecategories_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playservice_agecategories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3822 (class 0 OID 0)
-- Dependencies: 214
-- Name: playservice_agecategories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playservice_agecategories_id_seq OWNED BY playservice_agecategories_list.id;


--
-- TOC entry 215 (class 1259 OID 64345)
-- Name: playservice_categories_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_categories_list (
    id integer NOT NULL,
    category character varying(255) NOT NULL,
    main character varying(255)
);


--
-- TOC entry 216 (class 1259 OID 64351)
-- Name: playservice_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playservice_categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3823 (class 0 OID 0)
-- Dependencies: 216
-- Name: playservice_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playservice_categories_id_seq OWNED BY playservice_categories_list.id;


--
-- TOC entry 217 (class 1259 OID 64353)
-- Name: playservice_equipment_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_equipment_list (
    id integer NOT NULL,
    equipment character varying(255) NOT NULL
);


--
-- TOC entry 218 (class 1259 OID 64356)
-- Name: playservice_equipment_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playservice_equipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3824 (class 0 OID 0)
-- Dependencies: 218
-- Name: playservice_equipment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playservice_equipment_id_seq OWNED BY playservice_equipment_list.id;


--
-- TOC entry 219 (class 1259 OID 64358)
-- Name: playservice_facilities_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_facilities_list (
    id integer NOT NULL,
    facility character varying(255) NOT NULL
);


--
-- TOC entry 220 (class 1259 OID 64361)
-- Name: playservice_facilities_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playservice_facilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3825 (class 0 OID 0)
-- Dependencies: 220
-- Name: playservice_facilities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playservice_facilities_id_seq OWNED BY playservice_facilities_list.id;


--
-- TOC entry 221 (class 1259 OID 64363)
-- Name: playservice_locations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_locations (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    content character varying(255),
    summary character varying(255),
    street character varying(255),
    number character varying(255),
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
    averagerating integer,
    parking integer
);


--
-- TOC entry 222 (class 1259 OID 64369)
-- Name: playservice_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playservice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3826 (class 0 OID 0)
-- Dependencies: 222
-- Name: playservice_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playservice_id_seq OWNED BY playservice_locations.id;


--
-- TOC entry 223 (class 1259 OID 64371)
-- Name: playservice_images; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_images (
    id integer NOT NULL,
    url text,
    caption character varying(255),
    location integer NOT NULL,
    equipment integer
);


--
-- TOC entry 224 (class 1259 OID 64377)
-- Name: playservice_images_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playservice_images_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3827 (class 0 OID 0)
-- Dependencies: 224
-- Name: playservice_images_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playservice_images_id_seq OWNED BY playservice_images.id;


--
-- TOC entry 225 (class 1259 OID 64379)
-- Name: playservice_location_accessibility; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_location_accessibility (
    location integer NOT NULL,
    accessibility integer NOT NULL
);


--
-- TOC entry 226 (class 1259 OID 64382)
-- Name: playservice_location_agecategories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_location_agecategories (
    location integer NOT NULL,
    agecategory integer NOT NULL
);


--
-- TOC entry 227 (class 1259 OID 64385)
-- Name: playservice_location_categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_location_categories (
    location integer NOT NULL,
    category integer NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 64388)
-- Name: playservice_location_equipment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_location_equipment (
    id integer NOT NULL,
    location integer,
    equipment integer,
    pa_guid character varying(255)
);


--
-- TOC entry 229 (class 1259 OID 64391)
-- Name: playservice_location_equipment_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playservice_location_equipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3828 (class 0 OID 0)
-- Dependencies: 229
-- Name: playservice_location_equipment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playservice_location_equipment_id_seq OWNED BY playservice_location_equipment.id;


--
-- TOC entry 230 (class 1259 OID 64393)
-- Name: playservice_location_facilities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_location_facilities (
    location integer NOT NULL,
    facility integer NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 64396)
-- Name: playservice_parking_list; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_parking_list (
    id integer NOT NULL,
    parking character varying(255) NOT NULL
);


--
-- TOC entry 232 (class 1259 OID 64399)
-- Name: playservice_parking_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE playservice_parking_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3829 (class 0 OID 0)
-- Dependencies: 232
-- Name: playservice_parking_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE playservice_parking_id_seq OWNED BY playservice_parking_list.id;


--
-- TOC entry 233 (class 1259 OID 64407)
-- Name: pm_assets_api; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE pm_assets_api (
    fid integer NOT NULL,
    id character varying(255),
    locationid character varying(255),
    locationname character varying(255),
    lastupdated character varying(255),
    name character varying(255),
    assettype character varying(255),
    manufacturer character varying(255),
    serialnumber character varying(255),
    installeddate character varying(255),
    endoflifeyear numeric,
    safetyzonelength numeric,
    safetyzonewidth numeric,
    agegrouptoddlers character varying(255),
    agegroupjuniors character varying(255),
    agegroupseniors character varying(255),
    pricepurchase numeric,
    priceinstallation numeric,
    pricereinvestment numeric,
    pricemaintenance numeric,
    priceindexation numeric,
    lat numeric,
    lng numeric,
    groep character varying(255)
);


--
-- TOC entry 234 (class 1259 OID 64413)
-- Name: pm_assets_api_fid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE pm_assets_api_fid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3830 (class 0 OID 0)
-- Dependencies: 234
-- Name: pm_assets_api_fid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE pm_assets_api_fid_seq OWNED BY pm_assets_api.fid;


--
-- TOC entry 235 (class 1259 OID 64423)
-- Name: pm_locations_api; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE pm_locations_api (
    fid integer NOT NULL,
    id character varying(255),
    name character varying(255),
    lastupdated character varying(255),
    lat numeric,
    lng numeric
);


--
-- TOC entry 236 (class 1259 OID 64429)
-- Name: pm_locations_api_fid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE pm_locations_api_fid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3831 (class 0 OID 0)
-- Dependencies: 236
-- Name: pm_locations_api_fid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE pm_locations_api_fid_seq OWNED BY pm_locations_api.fid;


--
-- TOC entry 237 (class 1259 OID 64433)
-- Name: projectindeling; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE projectindeling (
    id integer NOT NULL,
    the_geom geometry,
    project character varying(255),
    label character varying(255),
    projectid character varying(255),
    CONSTRAINT enforce_dims_the_geom CHECK ((st_ndims(the_geom) = 2)),
    CONSTRAINT enforce_srid_the_geom CHECK ((st_srid(the_geom) = 28992))
);


--
-- TOC entry 238 (class 1259 OID 64441)
-- Name: projectindeling_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE projectindeling_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3832 (class 0 OID 0)
-- Dependencies: 238
-- Name: projectindeling_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE projectindeling_id_seq OWNED BY projectindeling.id;


--
-- TOC entry 247 (class 1259 OID 68962)
-- Name: temp_json; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE temp_json (
    "values" text,
    id integer NOT NULL
);


--
-- TOC entry 249 (class 1259 OID 69018)
-- Name: temp_json_2; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE temp_json_2 (
    id integer NOT NULL,
    "values" text
);


--
-- TOC entry 248 (class 1259 OID 69016)
-- Name: temp_json_2_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE temp_json_2_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3833 (class 0 OID 0)
-- Dependencies: 248
-- Name: temp_json_2_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE temp_json_2_id_seq OWNED BY temp_json_2.id;


--
-- TOC entry 251 (class 1259 OID 69027)
-- Name: temp_json_3; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE temp_json_3 (
    id integer NOT NULL,
    "values" text
);


--
-- TOC entry 250 (class 1259 OID 69025)
-- Name: temp_json_3_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE temp_json_3_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3834 (class 0 OID 0)
-- Dependencies: 250
-- Name: temp_json_3_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE temp_json_3_id_seq OWNED BY temp_json_3.id;


--
-- TOC entry 252 (class 1259 OID 77164)
-- Name: temp_json_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE temp_json_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3835 (class 0 OID 0)
-- Dependencies: 252
-- Name: temp_json_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE temp_json_id_seq OWNED BY temp_json.id;


--
-- TOC entry 245 (class 1259 OID 68946)
-- Name: v_pm_assets_api_rd; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW v_pm_assets_api_rd AS
 SELECT paa.id,
    paa.assettype AS type,
    playmapping_type_group.groep,
    paa.locationid,
    paa.locationname AS name,
    paa.safetyzonelength,
    paa.safetyzonewidth,
    paa.manufacturer,
    paa.installeddate,
    paa.endoflifeyear,
    paa.pricepurchase,
    paa.priceinstallation,
    paa.priceindexation,
    paa.agegrouptoddlers,
    paa.agegroupjuniors,
    paa.agegroupseniors,
        CASE
            WHEN (char_length((paa.lat)::text) > 1) THEN st_snaptogrid(st_transform(st_geomfromtext((((('POINT('::text || paa.lng) || ' '::text) || paa.lat) || ')'::text), 4326), 28992), (1)::double precision)
            ELSE st_snaptogrid(st_transform(st_geomfromtext((((('POINT('::text || pla.lng) || ' '::text) || pla.lat) || ')'::text), 4326), 28992), (1)::double precision)
        END AS the_geom
   FROM ((pm_assets_api paa
     LEFT JOIN pm_locations_api pla ON (((paa.locationid)::text = (pla.id)::text)))
     LEFT JOIN playmapping_type_group ON (((paa.assettype)::text = (playmapping_type_group.catasset)::text)));


--
-- TOC entry 246 (class 1259 OID 68956)
-- Name: v_pm_assets_api_compleet; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW v_pm_assets_api_compleet AS
 SELECT a.groep,
    a.id,
    n.type,
    n.eenheid,
        CASE
            WHEN ((n.type)::text = 'veiligheidsondergrond'::text) THEN ((a.safetyzonelength * a.safetyzonewidth) / (1000000)::numeric)
            ELSE (0)::numeric
        END AS omvang,
    n.leeftijdscategorie,
    n.vervangingstermijn,
        CASE
            WHEN ((n.type)::text = 'veiligheidsondergrond'::text) THEN ((n.onderhoudskosten * (a.safetyzonelength * a.safetyzonewidth)) / (1000000)::numeric)
            ELSE n.onderhoudskosten
        END AS onderhoudskosten,
        CASE
            WHEN ((n.type)::text = 'veiligheidsondergrond'::text) THEN ((n.aanschafwaarde * (a.safetyzonelength * a.safetyzonewidth)) / (1000000)::numeric)
            ELSE n.aanschafwaarde
        END AS aanschafwaarde,
        CASE
            WHEN (n.vervangingstermijn > 0) THEN
            CASE
                WHEN ((n.type)::text = 'veiligheidsondergrond'::text) THEN (((n.aanschafwaarde / (n.vervangingstermijn)::numeric) * (a.safetyzonelength * a.safetyzonewidth)) / (1000000)::numeric)
                ELSE (n.aanschafwaarde / (n.vervangingstermijn)::numeric)
            END
            ELSE (0)::numeric
        END AS afschrijving,
        CASE
            WHEN (length((a.installeddate)::text) > 0) THEN ((date_part('year'::text, to_date((a.installeddate)::text, 'YYYY-MM-DD'::text)) + (n.vervangingstermijn)::double precision))::integer
            ELSE ((date_part('year'::text, now()) + ((0.5 * (n.vervangingstermijn)::numeric))::double precision))::integer
        END AS vervangjaar1,
        CASE
            WHEN (length((a.installeddate)::text) > 0) THEN ((date_part('year'::text, to_date((a.installeddate)::text, 'YYYY-MM-DD'::text)) + ((2)::double precision * (n.vervangingstermijn)::double precision)))::integer
            ELSE ((date_part('year'::text, now()) + ((1.5 * (n.vervangingstermijn)::numeric))::double precision))::integer
        END AS vervangjaar2,
        CASE
            WHEN (length((a.installeddate)::text) > 0) THEN ((date_part('year'::text, to_date((a.installeddate)::text, 'YYYY-MM-DD'::text)) + ((3)::double precision * (n.vervangingstermijn)::double precision)))::integer
            ELSE ((date_part('year'::text, now()) + ((2.5 * (n.vervangingstermijn)::numeric))::double precision))::integer
        END AS vervangjaar3,
    a.locationid,
    a.name,
    a.manufacturer,
    a.installeddate,
        CASE
            WHEN (length((a.installeddate)::text) > 0) THEN (date_part('year'::text, to_date((a.installeddate)::text, 'YYYY-MM-DD'::text)))::integer
            ELSE ((date_part('year'::text, now()) - ((n.vervangingstermijn / 2))::double precision))::integer
        END AS installedyear,
    a.endoflifeyear,
        CASE
            WHEN ((a.agegrouptoddlers)::text = 'True'::text) THEN 1
            ELSE 0
        END AS agegrouptoddlers,
        CASE
            WHEN ((a.agegroupjuniors)::text = 'True'::text) THEN 1
            ELSE 0
        END AS agegroupjuniors,
        CASE
            WHEN ((a.agegroupseniors)::text = 'True'::text) THEN 1
            ELSE 0
        END AS agegroupseniors,
    a.pricepurchase,
    a.priceinstallation,
    a.the_geom
   FROM (v_pm_assets_api_rd a
     JOIN playmapping_normen n ON (((a.groep)::text = (n.naam)::text)));


--
-- TOC entry 244 (class 1259 OID 67856)
-- Name: wijk_2016; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE wijk_2016 (
    gid integer NOT NULL,
    wk_code character varying(8),
    wk_naam character varying(60),
    gm_code character varying(6),
    gm_naam character varying(60),
    ind_wbi numeric(10,0),
    water character varying(4),
    oad numeric(10,0),
    sted numeric(10,0),
    aant_inw numeric(10,0),
    aant_man numeric(10,0),
    aant_vrouw numeric(10,0),
    p_00_14_jr numeric(10,0),
    p_15_24_jr numeric(10,0),
    p_25_44_jr numeric(10,0),
    p_45_64_jr numeric(10,0),
    p_65_eo_jr numeric(10,0),
    p_ongehuwd numeric(10,0),
    p_gehuwd numeric(10,0),
    p_gescheid numeric(10,0),
    p_verweduw numeric(10,0),
    bev_dichth numeric(10,0),
    aantal_hh numeric(10,0),
    p_eenp_hh numeric(10,0),
    p_hh_z_k numeric(10,0),
    p_hh_m_k numeric(10,0),
    gem_hh_gr double precision,
    p_west_al numeric(10,0),
    p_n_w_al numeric(10,0),
    p_marokko numeric(10,0),
    p_ant_aru numeric(10,0),
    p_surinam numeric(10,0),
    p_turkije numeric(10,0),
    p_over_nw numeric(10,0),
    opp_tot numeric(10,0),
    opp_land numeric(10,0),
    opp_water numeric(10,0),
    geom geometry(MultiPolygon,28992)
);


--
-- TOC entry 243 (class 1259 OID 67854)
-- Name: wijk_2016_gid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE wijk_2016_gid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3836 (class 0 OID 0)
-- Dependencies: 243
-- Name: wijk_2016_gid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE wijk_2016_gid_seq OWNED BY wijk_2016.gid;


--
-- TOC entry 3617 (class 2604 OID 64790)
-- Name: buurt_2016 gid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY buurt_2016 ALTER COLUMN gid SET DEFAULT nextval('buurt_2016_gid_seq'::regclass);


--
-- TOC entry 3618 (class 2604 OID 67405)
-- Name: gem_2016 gid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY gem_2016 ALTER COLUMN gid SET DEFAULT nextval('gem_2016_gid_seq'::regclass);


--
-- TOC entry 3600 (class 2604 OID 64443)
-- Name: omnummertabel gid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY omnummertabel ALTER COLUMN gid SET DEFAULT nextval('omnummertabel_gid_seq'::regclass);


--
-- TOC entry 3601 (class 2604 OID 64444)
-- Name: playlocations fid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playlocations ALTER COLUMN fid SET DEFAULT nextval('playlocations_fid_seq'::regclass);


--
-- TOC entry 3602 (class 2604 OID 64445)
-- Name: playmapping_assets_houten fid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playmapping_assets_houten ALTER COLUMN fid SET DEFAULT nextval('playmapping_assets_houten_fid_seq'::regclass);


--
-- TOC entry 3603 (class 2604 OID 64446)
-- Name: playservice_accessibility_list id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_accessibility_list ALTER COLUMN id SET DEFAULT nextval('playservice_accessibility_id_seq'::regclass);


--
-- TOC entry 3604 (class 2604 OID 64447)
-- Name: playservice_agecategories_list id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_agecategories_list ALTER COLUMN id SET DEFAULT nextval('playservice_agecategories_id_seq'::regclass);


--
-- TOC entry 3605 (class 2604 OID 64448)
-- Name: playservice_categories_list id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_categories_list ALTER COLUMN id SET DEFAULT nextval('playservice_categories_id_seq'::regclass);


--
-- TOC entry 3606 (class 2604 OID 64449)
-- Name: playservice_equipment_list id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_equipment_list ALTER COLUMN id SET DEFAULT nextval('playservice_equipment_id_seq'::regclass);


--
-- TOC entry 3607 (class 2604 OID 64450)
-- Name: playservice_facilities_list id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_facilities_list ALTER COLUMN id SET DEFAULT nextval('playservice_facilities_id_seq'::regclass);


--
-- TOC entry 3609 (class 2604 OID 64451)
-- Name: playservice_images id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_images ALTER COLUMN id SET DEFAULT nextval('playservice_images_id_seq'::regclass);


--
-- TOC entry 3610 (class 2604 OID 64452)
-- Name: playservice_location_equipment id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_equipment ALTER COLUMN id SET DEFAULT nextval('playservice_location_equipment_id_seq'::regclass);


--
-- TOC entry 3608 (class 2604 OID 64453)
-- Name: playservice_locations id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_locations ALTER COLUMN id SET DEFAULT nextval('playservice_id_seq'::regclass);


--
-- TOC entry 3611 (class 2604 OID 64454)
-- Name: playservice_parking_list id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_parking_list ALTER COLUMN id SET DEFAULT nextval('playservice_parking_id_seq'::regclass);


--
-- TOC entry 3612 (class 2604 OID 64456)
-- Name: pm_assets_api fid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY pm_assets_api ALTER COLUMN fid SET DEFAULT nextval('pm_assets_api_fid_seq'::regclass);


--
-- TOC entry 3613 (class 2604 OID 64458)
-- Name: pm_locations_api fid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY pm_locations_api ALTER COLUMN fid SET DEFAULT nextval('pm_locations_api_fid_seq'::regclass);


--
-- TOC entry 3614 (class 2604 OID 64459)
-- Name: projectindeling id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectindeling ALTER COLUMN id SET DEFAULT nextval('projectindeling_id_seq'::regclass);


--
-- TOC entry 3620 (class 2604 OID 77166)
-- Name: temp_json id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY temp_json ALTER COLUMN id SET DEFAULT nextval('temp_json_id_seq'::regclass);


--
-- TOC entry 3621 (class 2604 OID 69021)
-- Name: temp_json_2 id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY temp_json_2 ALTER COLUMN id SET DEFAULT nextval('temp_json_2_id_seq'::regclass);


--
-- TOC entry 3622 (class 2604 OID 69030)
-- Name: temp_json_3 id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY temp_json_3 ALTER COLUMN id SET DEFAULT nextval('temp_json_3_id_seq'::regclass);


--
-- TOC entry 3619 (class 2604 OID 67859)
-- Name: wijk_2016 gid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY wijk_2016 ALTER COLUMN gid SET DEFAULT nextval('wijk_2016_gid_seq'::regclass);


--
-- TOC entry 3661 (class 2606 OID 64792)
-- Name: buurt_2016 buurt_2016_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY buurt_2016
    ADD CONSTRAINT buurt_2016_pkey PRIMARY KEY (gid);


--
-- TOC entry 3664 (class 2606 OID 67407)
-- Name: gem_2016 gem_2016_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY gem_2016
    ADD CONSTRAINT gem_2016_pkey PRIMARY KEY (gid);


--
-- TOC entry 3624 (class 2606 OID 64649)
-- Name: omnummertabel omnummertabel_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY omnummertabel
    ADD CONSTRAINT omnummertabel_pkey PRIMARY KEY (gid);


--
-- TOC entry 3626 (class 2606 OID 64651)
-- Name: playlocations playlocations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playlocations
    ADD CONSTRAINT playlocations_pkey PRIMARY KEY (fid);


--
-- TOC entry 3629 (class 2606 OID 64653)
-- Name: playservice_accessibility_list playservice_accessibility_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_accessibility_list
    ADD CONSTRAINT playservice_accessibility_pkey PRIMARY KEY (id);


--
-- TOC entry 3631 (class 2606 OID 64655)
-- Name: playservice_agecategories_list playservice_agecategories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_agecategories_list
    ADD CONSTRAINT playservice_agecategories_pkey PRIMARY KEY (id);


--
-- TOC entry 3633 (class 2606 OID 64657)
-- Name: playservice_categories_list playservice_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_categories_list
    ADD CONSTRAINT playservice_categories_pkey PRIMARY KEY (id);


--
-- TOC entry 3635 (class 2606 OID 64659)
-- Name: playservice_equipment_list playservice_equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_equipment_list
    ADD CONSTRAINT playservice_equipment_pkey PRIMARY KEY (id);


--
-- TOC entry 3637 (class 2606 OID 64661)
-- Name: playservice_facilities_list playservice_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_facilities_list
    ADD CONSTRAINT playservice_facilities_pkey PRIMARY KEY (id);


--
-- TOC entry 3641 (class 2606 OID 64663)
-- Name: playservice_images playservice_images_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_images
    ADD CONSTRAINT playservice_images_pkey PRIMARY KEY (id);


--
-- TOC entry 3643 (class 2606 OID 64665)
-- Name: playservice_location_accessibility playservice_location_accessibility_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_accessibility
    ADD CONSTRAINT playservice_location_accessibility_pkey PRIMARY KEY (location, accessibility);


--
-- TOC entry 3645 (class 2606 OID 64667)
-- Name: playservice_location_agecategories playservice_location_agecategories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_agecategories
    ADD CONSTRAINT playservice_location_agecategories_pkey PRIMARY KEY (location, agecategory);


--
-- TOC entry 3647 (class 2606 OID 64669)
-- Name: playservice_location_categories playservice_location_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_categories
    ADD CONSTRAINT playservice_location_categories_pkey PRIMARY KEY (location, category);


--
-- TOC entry 3649 (class 2606 OID 64671)
-- Name: playservice_location_equipment playservice_location_equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_equipment
    ADD CONSTRAINT playservice_location_equipment_pkey PRIMARY KEY (id);


--
-- TOC entry 3651 (class 2606 OID 64673)
-- Name: playservice_location_facilities playservice_location_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_facilities
    ADD CONSTRAINT playservice_location_facilities_pkey PRIMARY KEY (location, facility);


--
-- TOC entry 3639 (class 2606 OID 64675)
-- Name: playservice_locations playservice_locations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_locations
    ADD CONSTRAINT playservice_locations_pkey PRIMARY KEY (id);


--
-- TOC entry 3653 (class 2606 OID 64677)
-- Name: playservice_parking_list playservice_parking_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_parking_list
    ADD CONSTRAINT playservice_parking_pkey PRIMARY KEY (id);


--
-- TOC entry 3655 (class 2606 OID 68984)
-- Name: pm_assets_api prim_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pm_assets_api
    ADD CONSTRAINT prim_key PRIMARY KEY (fid);


--
-- TOC entry 3657 (class 2606 OID 64683)
-- Name: projectindeling projectindeling_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY projectindeling
    ADD CONSTRAINT projectindeling_pkey PRIMARY KEY (id);


--
-- TOC entry 3669 (class 2606 OID 77174)
-- Name: temp_json sdf; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY temp_json
    ADD CONSTRAINT sdf PRIMARY KEY (id);


--
-- TOC entry 3667 (class 2606 OID 67861)
-- Name: wijk_2016 wijk_2016_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY wijk_2016
    ADD CONSTRAINT wijk_2016_pkey PRIMARY KEY (gid);


--
-- TOC entry 3659 (class 1259 OID 67395)
-- Name: buurt_2016_geom_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX buurt_2016_geom_idx ON buurt_2016 USING gist (geom);


--
-- TOC entry 3662 (class 1259 OID 67852)
-- Name: gem_2016_geom_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX gem_2016_geom_idx ON gem_2016 USING gist (geom);


--
-- TOC entry 3658 (class 1259 OID 64684)
-- Name: projectindeling_the_geom_1318346528253; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX projectindeling_the_geom_1318346528253 ON projectindeling USING gist (the_geom);


--
-- TOC entry 3627 (class 1259 OID 64685)
-- Name: spatial_playlocations_the_geom; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX spatial_playlocations_the_geom ON playlocations USING gist (wsg84_geom);


--
-- TOC entry 3665 (class 1259 OID 68933)
-- Name: wijk_2016_geom_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX wijk_2016_geom_idx ON wijk_2016 USING gist (geom);


--
-- TOC entry 3671 (class 2606 OID 64688)
-- Name: playservice_images playserviceimages_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_images
    ADD CONSTRAINT playserviceimages_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3672 (class 2606 OID 64693)
-- Name: playservice_location_accessibility playservicelocationaccessibility_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_accessibility
    ADD CONSTRAINT playservicelocationaccessibility_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3673 (class 2606 OID 64698)
-- Name: playservice_location_accessibility playservicelocationaccessibility_fk2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_accessibility
    ADD CONSTRAINT playservicelocationaccessibility_fk2 FOREIGN KEY (accessibility) REFERENCES playservice_accessibility_list(id);


--
-- TOC entry 3674 (class 2606 OID 64703)
-- Name: playservice_location_agecategories playservicelocationagecategories_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_agecategories
    ADD CONSTRAINT playservicelocationagecategories_fk1 FOREIGN KEY (agecategory) REFERENCES playservice_agecategories_list(id);


--
-- TOC entry 3675 (class 2606 OID 64708)
-- Name: playservice_location_agecategories playservicelocationagecategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_agecategories
    ADD CONSTRAINT playservicelocationagecategories_fk2 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3676 (class 2606 OID 64713)
-- Name: playservice_location_categories playservicelocationcategories_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_categories
    ADD CONSTRAINT playservicelocationcategories_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3677 (class 2606 OID 64718)
-- Name: playservice_location_categories playservicelocationcategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_categories
    ADD CONSTRAINT playservicelocationcategories_fk2 FOREIGN KEY (category) REFERENCES playservice_categories_list(id);


--
-- TOC entry 3678 (class 2606 OID 64723)
-- Name: playservice_location_equipment playservicelocationequipment_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_equipment
    ADD CONSTRAINT playservicelocationequipment_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3679 (class 2606 OID 64728)
-- Name: playservice_location_equipment playservicelocationequipment_fk2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_equipment
    ADD CONSTRAINT playservicelocationequipment_fk2 FOREIGN KEY (equipment) REFERENCES playservice_equipment_list(id);


--
-- TOC entry 3680 (class 2606 OID 64733)
-- Name: playservice_location_facilities playservicelocationfacilities_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_facilities
    ADD CONSTRAINT playservicelocationfacilities_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3681 (class 2606 OID 64738)
-- Name: playservice_location_facilities playservicelocationfacilities_fk2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_location_facilities
    ADD CONSTRAINT playservicelocationfacilities_fk2 FOREIGN KEY (facility) REFERENCES playservice_facilities_list(id);


--
-- TOC entry 3670 (class 2606 OID 64743)
-- Name: playservice_locations playservicelocations_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY playservice_locations
    ADD CONSTRAINT playservicelocations_fk1 FOREIGN KEY (parking) REFERENCES playservice_parking_list(id);


-- Completed on 2017-05-29 16:42:48 CEST

--
-- PostgreSQL database dump complete
--

