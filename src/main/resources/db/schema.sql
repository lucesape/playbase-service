--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.5
-- Dumped by pg_dump version 9.6.5

-- Started on 2017-10-17 12:24:45 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 202 (class 1259 OID 64323)
-- Name: playmapping_normen; Type: TABLE; Schema: public; Owner: playbase
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


ALTER TABLE playmapping_normen OWNER TO playbase;

--
-- TOC entry 203 (class 1259 OID 64329)
-- Name: playmapping_type_group; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playmapping_type_group (
    categorie character varying(255),
    assettype character varying(255),
    groep character varying(255),
    catasset character varying(255),
    id integer NOT NULL,
    equipment_type integer,
    locationcategory integer
);


ALTER TABLE playmapping_type_group OWNER TO playbase;

--
-- TOC entry 226 (class 1259 OID 97336)
-- Name: playmapping_type_group_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playmapping_type_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playmapping_type_group_id_seq OWNER TO playbase;

--
-- TOC entry 3835 (class 0 OID 0)
-- Dependencies: 226
-- Name: playmapping_type_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playmapping_type_group_id_seq OWNED BY playmapping_type_group.id;


--
-- TOC entry 204 (class 1259 OID 64335)
-- Name: playservice_accessibility_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_accessibility_list (
    id integer NOT NULL,
    accessibility character varying(255) NOT NULL
);


ALTER TABLE playservice_accessibility_list OWNER TO playbase;

--
-- TOC entry 205 (class 1259 OID 64338)
-- Name: playservice_accessibility_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_accessibility_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_accessibility_id_seq OWNER TO playbase;

--
-- TOC entry 3836 (class 0 OID 0)
-- Dependencies: 205
-- Name: playservice_accessibility_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_accessibility_id_seq OWNED BY playservice_accessibility_list.id;


--
-- TOC entry 206 (class 1259 OID 64340)
-- Name: playservice_agecategories_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_agecategories_list (
    id integer NOT NULL,
    agecategory character varying(255) NOT NULL
);


ALTER TABLE playservice_agecategories_list OWNER TO playbase;

--
-- TOC entry 207 (class 1259 OID 64343)
-- Name: playservice_agecategories_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_agecategories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_agecategories_id_seq OWNER TO playbase;

--
-- TOC entry 3837 (class 0 OID 0)
-- Dependencies: 207
-- Name: playservice_agecategories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_agecategories_id_seq OWNED BY playservice_agecategories_list.id;


--
-- TOC entry 208 (class 1259 OID 64345)
-- Name: playservice_categories_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_categories_list (
    id integer NOT NULL,
    category character varying(255) NOT NULL,
    main character varying(255)
);


ALTER TABLE playservice_categories_list OWNER TO playbase;

--
-- TOC entry 209 (class 1259 OID 64351)
-- Name: playservice_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_categories_id_seq OWNER TO playbase;

--
-- TOC entry 3838 (class 0 OID 0)
-- Dependencies: 209
-- Name: playservice_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_categories_id_seq OWNED BY playservice_categories_list.id;


--
-- TOC entry 245 (class 1259 OID 173762)
-- Name: playservice_comment; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_comment (
    id integer NOT NULL,
    playadvisor_id integer,
    post_id integer,
    location integer,
    content text,
    stars integer,
    author character varying(255),
    date character varying(255)
);


ALTER TABLE playservice_comment OWNER TO playbase;

--
-- TOC entry 246 (class 1259 OID 173768)
-- Name: playservice_comment_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_comment_id_seq OWNER TO playbase;

--
-- TOC entry 3839 (class 0 OID 0)
-- Dependencies: 246
-- Name: playservice_comment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_comment_id_seq OWNED BY playservice_comment.id;


--
-- TOC entry 247 (class 1259 OID 173778)
-- Name: playservice_comment_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_comment_playadvisor (
    id integer NOT NULL,
    playadvisor_id integer,
    post_id integer,
    location integer,
    content text,
    stars integer,
    author character varying(255),
    date character varying(255)
);


ALTER TABLE playservice_comment_playadvisor OWNER TO playbase;

--
-- TOC entry 248 (class 1259 OID 173784)
-- Name: playservice_comment_playadvisor_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_comment_playadvisor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_comment_playadvisor_id_seq OWNER TO playbase;

--
-- TOC entry 3840 (class 0 OID 0)
-- Dependencies: 248
-- Name: playservice_comment_playadvisor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_comment_playadvisor_id_seq OWNED BY playservice_comment_playadvisor.id;


--
-- TOC entry 229 (class 1259 OID 97423)
-- Name: playservice_documents; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_documents (
    id integer NOT NULL,
    url text,
    caption character varying(255),
    location integer NOT NULL,
    equipment integer,
    pm_guid character varying(255)
);


ALTER TABLE playservice_documents OWNER TO playbase;

--
-- TOC entry 228 (class 1259 OID 97421)
-- Name: playservice_documents_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_documents_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_documents_id_seq OWNER TO playbase;

--
-- TOC entry 3841 (class 0 OID 0)
-- Dependencies: 228
-- Name: playservice_documents_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_documents_id_seq OWNED BY playservice_documents.id;


--
-- TOC entry 238 (class 1259 OID 141332)
-- Name: playservice_documents_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_documents_playadvisor (
    id integer NOT NULL,
    url text,
    caption character varying(255),
    location integer NOT NULL,
    equipment integer,
    pm_guid character varying(255)
);


ALTER TABLE playservice_documents_playadvisor OWNER TO playbase;

--
-- TOC entry 237 (class 1259 OID 141330)
-- Name: playservice_documents_playadvisor_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_documents_playadvisor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_documents_playadvisor_id_seq OWNER TO playbase;

--
-- TOC entry 3842 (class 0 OID 0)
-- Dependencies: 237
-- Name: playservice_documents_playadvisor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_documents_playadvisor_id_seq OWNED BY playservice_documents_playadvisor.id;


--
-- TOC entry 210 (class 1259 OID 64353)
-- Name: playservice_equipment_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_equipment_list (
    id integer NOT NULL,
    equipment character varying(255) NOT NULL
);


ALTER TABLE playservice_equipment_list OWNER TO playbase;

--
-- TOC entry 211 (class 1259 OID 64356)
-- Name: playservice_equipment_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_equipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_equipment_id_seq OWNER TO playbase;

--
-- TOC entry 3843 (class 0 OID 0)
-- Dependencies: 211
-- Name: playservice_equipment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_equipment_id_seq OWNED BY playservice_equipment_list.id;


--
-- TOC entry 212 (class 1259 OID 64358)
-- Name: playservice_facilities_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_facilities_list (
    id integer NOT NULL,
    facility character varying(255) NOT NULL
);


ALTER TABLE playservice_facilities_list OWNER TO playbase;

--
-- TOC entry 213 (class 1259 OID 64361)
-- Name: playservice_facilities_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_facilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_facilities_id_seq OWNER TO playbase;

--
-- TOC entry 3844 (class 0 OID 0)
-- Dependencies: 213
-- Name: playservice_facilities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_facilities_id_seq OWNED BY playservice_facilities_list.id;


--
-- TOC entry 214 (class 1259 OID 64363)
-- Name: playservice_locations; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_locations (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    content text,
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
    parking integer,
    geom geometry(Point,4326),
    averagerating integer,
    pa_title character varying(255)
);


ALTER TABLE playservice_locations OWNER TO playbase;

--
-- TOC entry 215 (class 1259 OID 64369)
-- Name: playservice_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_id_seq OWNER TO playbase;

--
-- TOC entry 3845 (class 0 OID 0)
-- Dependencies: 215
-- Name: playservice_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_id_seq OWNED BY playservice_locations.id;


--
-- TOC entry 216 (class 1259 OID 64371)
-- Name: playservice_images; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_images (
    id integer NOT NULL,
    url text,
    caption character varying(255),
    location integer NOT NULL,
    equipment integer,
    pm_guid character varying(255)
);


ALTER TABLE playservice_images OWNER TO playbase;

--
-- TOC entry 217 (class 1259 OID 64377)
-- Name: playservice_images_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_images_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_images_id_seq OWNER TO playbase;

--
-- TOC entry 3846 (class 0 OID 0)
-- Dependencies: 217
-- Name: playservice_images_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_images_id_seq OWNED BY playservice_images.id;


--
-- TOC entry 240 (class 1259 OID 141353)
-- Name: playservice_images_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_images_playadvisor (
    id integer NOT NULL,
    url text,
    caption character varying(255),
    location integer NOT NULL,
    equipment integer,
    pm_guid character varying(255)
);


ALTER TABLE playservice_images_playadvisor OWNER TO playbase;

--
-- TOC entry 239 (class 1259 OID 141351)
-- Name: playservice_images_playadvisor_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_images_playadvisor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_images_playadvisor_id_seq OWNER TO playbase;

--
-- TOC entry 3847 (class 0 OID 0)
-- Dependencies: 239
-- Name: playservice_images_playadvisor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_images_playadvisor_id_seq OWNED BY playservice_images_playadvisor.id;


--
-- TOC entry 218 (class 1259 OID 64379)
-- Name: playservice_location_accessibility; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_accessibility (
    location integer NOT NULL,
    accessibility integer NOT NULL
);


ALTER TABLE playservice_location_accessibility OWNER TO playbase;

--
-- TOC entry 241 (class 1259 OID 141372)
-- Name: playservice_location_accessibility_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_accessibility_playadvisor (
    location integer NOT NULL,
    accessibility integer NOT NULL
);


ALTER TABLE playservice_location_accessibility_playadvisor OWNER TO playbase;

--
-- TOC entry 219 (class 1259 OID 64382)
-- Name: playservice_location_agecategories; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_agecategories (
    location integer NOT NULL,
    agecategory integer NOT NULL,
    pa_id character varying(255)
);


ALTER TABLE playservice_location_agecategories OWNER TO playbase;

--
-- TOC entry 242 (class 1259 OID 141387)
-- Name: playservice_location_agecategories_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_agecategories_playadvisor (
    location integer NOT NULL,
    agecategory integer NOT NULL,
    pa_id character varying(255)
);


ALTER TABLE playservice_location_agecategories_playadvisor OWNER TO playbase;

--
-- TOC entry 220 (class 1259 OID 64385)
-- Name: playservice_location_categories; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_categories (
    location integer NOT NULL,
    category integer NOT NULL,
    pa_id character varying(255),
    pm_guid character varying(255)
);


ALTER TABLE playservice_location_categories OWNER TO playbase;

--
-- TOC entry 243 (class 1259 OID 141402)
-- Name: playservice_location_categories_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_categories_playadvisor (
    location integer NOT NULL,
    category integer NOT NULL,
    pa_id character varying(255)
);


ALTER TABLE playservice_location_categories_playadvisor OWNER TO playbase;

--
-- TOC entry 221 (class 1259 OID 64388)
-- Name: playservice_location_equipment; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_equipment (
    id integer NOT NULL,
    location integer,
    equipment integer,
    pa_guid character varying(255),
    longitude double precision,
    latitude double precision,
    type_ integer,
    installeddate character varying(255),
    pm_guid character varying(255),
    name character varying(255),
    priceindexation integer,
    priceinstallation integer,
    pricemaintenance integer,
    pricepurchase integer,
    pricereinvestment integer,
    depth integer,
    width integer,
    height integer,
    endoflifeyear integer,
    freefallheight integer,
    safetyzonelength integer,
    safetyzonewidth integer,
    manufacturer character varying(255),
    material character varying(255),
    product character varying(255),
    productid character varying(255),
    productvariantid character varying(255),
    serialnumber character varying(255),
    geom geometry(Point,4326)
);


ALTER TABLE playservice_location_equipment OWNER TO playbase;

--
-- TOC entry 227 (class 1259 OID 97353)
-- Name: playservice_location_equipment_agecategories; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_equipment_agecategories (
    location_equipment integer NOT NULL,
    agecategory integer NOT NULL
);


ALTER TABLE playservice_location_equipment_agecategories OWNER TO playbase;

--
-- TOC entry 235 (class 1259 OID 141300)
-- Name: playservice_location_equipment_agecategories_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_equipment_agecategories_playadvisor (
    location_equipment integer NOT NULL,
    agecategory integer NOT NULL
);


ALTER TABLE playservice_location_equipment_agecategories_playadvisor OWNER TO playbase;

--
-- TOC entry 222 (class 1259 OID 64391)
-- Name: playservice_location_equipment_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_location_equipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_location_equipment_id_seq OWNER TO playbase;

--
-- TOC entry 3848 (class 0 OID 0)
-- Dependencies: 222
-- Name: playservice_location_equipment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_location_equipment_id_seq OWNED BY playservice_location_equipment.id;


--
-- TOC entry 234 (class 1259 OID 141276)
-- Name: playservice_location_equipment_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_equipment_playadvisor (
    id integer NOT NULL,
    location integer,
    equipment integer,
    pa_guid character varying(255),
    longitude double precision,
    latitude double precision,
    type_ integer,
    installeddate character varying(255),
    pm_guid character varying(255),
    name character varying(255),
    priceindexation integer,
    priceinstallation integer,
    pricemaintenance integer,
    pricepurchase integer,
    pricereinvestment integer,
    depth integer,
    width integer,
    height integer,
    endoflifeyear integer,
    freefallheight integer,
    safetyzonelength integer,
    safetyzonewidth integer,
    manufacturer character varying(255),
    material character varying(255),
    product character varying(255),
    productid character varying(255),
    productvariantid character varying(255),
    serialnumber character varying(255),
    geom geometry(Point,4326)
);


ALTER TABLE playservice_location_equipment_playadvisor OWNER TO playbase;

--
-- TOC entry 233 (class 1259 OID 141274)
-- Name: playservice_location_equipment_playadvisor_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_location_equipment_playadvisor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_location_equipment_playadvisor_id_seq OWNER TO playbase;

--
-- TOC entry 3849 (class 0 OID 0)
-- Dependencies: 233
-- Name: playservice_location_equipment_playadvisor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_location_equipment_playadvisor_id_seq OWNED BY playservice_location_equipment_playadvisor.id;


--
-- TOC entry 223 (class 1259 OID 64393)
-- Name: playservice_location_facilities; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_facilities (
    location integer NOT NULL,
    facility integer NOT NULL,
    pa_id character varying(255)
);


ALTER TABLE playservice_location_facilities OWNER TO playbase;

--
-- TOC entry 236 (class 1259 OID 141315)
-- Name: playservice_location_facilities_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_facilities_playadvisor (
    location integer NOT NULL,
    facility integer NOT NULL,
    pa_id character varying(255)
);


ALTER TABLE playservice_location_facilities_playadvisor OWNER TO playbase;

--
-- TOC entry 232 (class 1259 OID 141260)
-- Name: playservice_locations_playadvisor; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_locations_playadvisor (
    id integer NOT NULL,
    title character varying(255) NOT NULL,
    content text,
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
    parking integer,
    geom geometry(Point,4326),
    averagerating integer,
    pa_title character varying(255)
);


ALTER TABLE playservice_locations_playadvisor OWNER TO playbase;

--
-- TOC entry 231 (class 1259 OID 141258)
-- Name: playservice_locations_playadvisor_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_locations_playadvisor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_locations_playadvisor_id_seq OWNER TO playbase;

--
-- TOC entry 3850 (class 0 OID 0)
-- Dependencies: 231
-- Name: playservice_locations_playadvisor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_locations_playadvisor_id_seq OWNED BY playservice_locations_playadvisor.id;


--
-- TOC entry 224 (class 1259 OID 64396)
-- Name: playservice_parking_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_parking_list (
    id integer NOT NULL,
    parking character varying(255) NOT NULL
);


ALTER TABLE playservice_parking_list OWNER TO playbase;

--
-- TOC entry 225 (class 1259 OID 64399)
-- Name: playservice_parking_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--

CREATE SEQUENCE playservice_parking_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE playservice_parking_id_seq OWNER TO playbase;

--
-- TOC entry 3851 (class 0 OID 0)
-- Dependencies: 225
-- Name: playservice_parking_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: playbase
--

ALTER SEQUENCE playservice_parking_id_seq OWNED BY playservice_parking_list.id;


--
-- TOC entry 3544 (class 2604 OID 97338)
-- Name: playmapping_type_group id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playmapping_type_group ALTER COLUMN id SET DEFAULT nextval('playmapping_type_group_id_seq'::regclass);


--
-- TOC entry 3545 (class 2604 OID 64446)
-- Name: playservice_accessibility_list id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_accessibility_list ALTER COLUMN id SET DEFAULT nextval('playservice_accessibility_id_seq'::regclass);


--
-- TOC entry 3546 (class 2604 OID 64447)
-- Name: playservice_agecategories_list id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_agecategories_list ALTER COLUMN id SET DEFAULT nextval('playservice_agecategories_id_seq'::regclass);


--
-- TOC entry 3547 (class 2604 OID 64448)
-- Name: playservice_categories_list id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_categories_list ALTER COLUMN id SET DEFAULT nextval('playservice_categories_id_seq'::regclass);


--
-- TOC entry 3559 (class 2604 OID 173770)
-- Name: playservice_comment id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_comment ALTER COLUMN id SET DEFAULT nextval('playservice_comment_id_seq'::regclass);


--
-- TOC entry 3560 (class 2604 OID 173786)
-- Name: playservice_comment_playadvisor id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_comment_playadvisor ALTER COLUMN id SET DEFAULT nextval('playservice_comment_playadvisor_id_seq'::regclass);


--
-- TOC entry 3554 (class 2604 OID 97426)
-- Name: playservice_documents id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_documents ALTER COLUMN id SET DEFAULT nextval('playservice_documents_id_seq'::regclass);


--
-- TOC entry 3557 (class 2604 OID 141335)
-- Name: playservice_documents_playadvisor id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_documents_playadvisor ALTER COLUMN id SET DEFAULT nextval('playservice_documents_playadvisor_id_seq'::regclass);


--
-- TOC entry 3548 (class 2604 OID 64449)
-- Name: playservice_equipment_list id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_equipment_list ALTER COLUMN id SET DEFAULT nextval('playservice_equipment_id_seq'::regclass);


--
-- TOC entry 3549 (class 2604 OID 64450)
-- Name: playservice_facilities_list id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_facilities_list ALTER COLUMN id SET DEFAULT nextval('playservice_facilities_id_seq'::regclass);


--
-- TOC entry 3551 (class 2604 OID 64451)
-- Name: playservice_images id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_images ALTER COLUMN id SET DEFAULT nextval('playservice_images_id_seq'::regclass);


--
-- TOC entry 3558 (class 2604 OID 141356)
-- Name: playservice_images_playadvisor id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_images_playadvisor ALTER COLUMN id SET DEFAULT nextval('playservice_images_playadvisor_id_seq'::regclass);


--
-- TOC entry 3552 (class 2604 OID 64452)
-- Name: playservice_location_equipment id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment ALTER COLUMN id SET DEFAULT nextval('playservice_location_equipment_id_seq'::regclass);


--
-- TOC entry 3556 (class 2604 OID 141279)
-- Name: playservice_location_equipment_playadvisor id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_playadvisor ALTER COLUMN id SET DEFAULT nextval('playservice_location_equipment_playadvisor_id_seq'::regclass);


--
-- TOC entry 3550 (class 2604 OID 64453)
-- Name: playservice_locations id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_locations ALTER COLUMN id SET DEFAULT nextval('playservice_id_seq'::regclass);


--
-- TOC entry 3555 (class 2604 OID 141263)
-- Name: playservice_locations_playadvisor id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_locations_playadvisor ALTER COLUMN id SET DEFAULT nextval('playservice_locations_playadvisor_id_seq'::regclass);


--
-- TOC entry 3553 (class 2604 OID 64454)
-- Name: playservice_parking_list id; Type: DEFAULT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_parking_list ALTER COLUMN id SET DEFAULT nextval('playservice_parking_id_seq'::regclass);


--
-- TOC entry 3786 (class 0 OID 64323)
-- Dependencies: 202
-- Data for Name: playmapping_normen; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playmapping_normen (naam, type, eenheid, leeftijdscategorie, vervangingstermijn, aanschafwaarde, onderhoudskosten) FROM stdin;
wipveer speeltoestellen stuk    0 t/m 5 jaar    12  803 60.75
meerpersoons wipveer    speeltoestellen stuk    0 t/m 5 jaar    10  1430    64.8333333333333
wip speeltoestellen stuk    0 t/m 11 jaar   10  2500    55.4595238095238
enkel duikelrek speeltoestellen stuk    6 t/m 11 jaar   14  420 16.9166666666667
meerdelig duikelrek speeltoestellen stuk    6 t/m 11 jaar   14  750 25.8333333333333
schommel    speeltoestellen stuk    0 t/m 11 jaar   10  3000    73.23
evenwichtstoestel   speeltoestellen stuk    0 t/m 11 jaar   12  605 12.6833333333333
bokspringpaal (set) speeltoestellen stuk    0 t/m 11 jaar   14  275 8.1
glijbaan laag   speeltoestellen stuk    0 t/m 5 jaar    12  3200    36.6833333333333
glijbaan hoog   speeltoestellen stuk    0 t/m 11 jaar   12  3740    36.4833333333333
taludglijbaan   speeltoestellen stuk    0 t/m 11 jaar   12  4235    32.5
zandbak speeltoestellen stuk    0 t/m 5 jaar    20  1925    64
zandspeeltoestel    speeltoestellen stuk    0 t/m 5 jaar    10  1150    27.675
draaitoestel klein  speeltoestellen stuk    0 t/m 11 jaar   10  1700    58.9166666666667
draaitoestel groot  speeltoestellen stuk    0 t/m 11 jaar   10  3740    47.9166666666667
klimelement speeltoestellen stuk    0 t/m 11 jaar   12  2970    55.625
ruimtenet   speeltoestellen stuk    0 t/m 11 jaar   12  6900    115.416666666667
speelhuis   speeltoestellen stuk    0 t/m 5 jaar    12  2475    58.4166666666667
combinatie klein    speeltoestellen stuk    0 t/m 5 jaar    12  4500    71.0416666666667
combinatie middel   speeltoestellen stuk    0 t/m 11 jaar   12  7500    83.5833333333333
combinatie groot    speeltoestellen stuk    0 t/m 11 jaar   14  13000   105.416666666667
uitgebreide combinatietoestel   speeltoestellen stuk    6 t/m 11 jaar   16  30000   210
uitgebreide klimtorens  speeltoestellen stuk    6 t/m 11 jaar   16  55000   240
JOP speeltoestellen stuk    12 t/m 18 jaar  10  10000   84.5833333333333
kabelbaan   speeltoestellen stuk    6 t/m 11 jaar   10  8500    74.8958333333333
tafeltennistafel    sporttoestellen stuk    6 t/m 18 jaar   16  2200    18.9583333333333
voetbaldoel sporttoestellen stuk    6 t/m 18 jaar   14  1375    33.3055555555556
basketbalpaal   sporttoestellen stuk    6 t/m 18 jaar   14  1900    28.75
voetbal-basketbaldoel   sporttoestellen stuk    6 t/m 18 jaar   14  7500    70.4166666666667
pannaveld klein sporttoestellen stuk    6 t/m 18 jaar   14  6500    \N
pannaveld   sporttoestellen stuk    6 t/m 18 jaar   14  15000   140.833333333333
pannaveld groot sporttoestellen stuk    6 t/m 18 jaar   14  40000   \N
volleybalset    sporttoestellen stuk    6 t/m 18 jaar   12  825 14.2916666666667
jeu de boulesbaan   sporttoestellen stuk    n.v.t.  12  4550    53.75
skateboard groot    sporttoestellen stuk    6 t/m 18 jaar   12  50000   279.375
skateboard middel   sporttoestellen stuk    6 t/m 18 jaar   10  12500   141.25
skateboard klein    sporttoestellen stuk    6 t/m 18 jaar   10  2500    75.625
fitness sporttoestellen stuk    6 t/m 18 jaar   12  2500    76
speelprikkel divers speelaanleiding stuk    0 t/m 11 jaar   20  2000    0
knikkertegel    speelaanleiding stuk    0 t/m 11 jaar   20  57.5    0
hinkelbaan  speelaanleiding stuk    0 t/m 11 jaar   20  402.5   0
poef    speelaanleiding stuk    n.v.t.  20  290 0
schaaktafel speelaanleiding stuk    6 t/m 18 jaar   14  936 9.41666666666667
speeltafel  speelaanleiding stuk    0 t/m 11 jaar   12  680 19.6666666666667
kunst   speelaanleiding stuk    0 t/m 11 jaar   20  6000    0
asfaltvloer verharding  m2  n.v.t.  30  130 0.901314643352583
elementverharding (tegels/klinkers) verharding  m2  n.v.t.  30  55  0.345706905726618
betonvloer (skaten) verharding  m2  n.v.t.  30  90  1.63305555555556
halfverharding (gralux) verharding  m2  n.v.t.  20  65  0.949181714172014
kunstgrastrapvelden verharding  m2  n.v.t.  12  95  8.47833799182972
grastrapveld  (excl. drainage)  verharding  m2  n.v.t.  30  15  1.63207430731575
ballenvanger    afscheiding m1  n.v.t.  12  180 10.1666666666667
hekwerk afscheiding m1  n.v.t.  12  85  4.125
afscheiding afscheiding m1  n.v.t.  12  45  2.08333333333333
afvalbak    meubilair ed    stuk    n.v.t.  10  185 5.16666666666667
bank    meubilair ed    stuk    n.v.t.  10  460 25.8333333333333
picknickset meubilair ed    stuk    n.v.t.  10  1560    43
zitelement (beton)  meubilair ed    stuk    n.v.t.  12  860 0
valdempend zand veiligheidsondergrond   m2  n.v.t.  15  27.24   7.505
houtsnipper: gekleurde vezel    veiligheidsondergrond   m2  n.v.t.  9   58.85   7.949
houtsnipper: gekleurd verkleind/gezeefd veiligheidsondergrond   m2  n.v.t.  9   53.22   8.725
franse schors   veiligheidsondergrond   m2  n.v.t.  5   44.62   9.375
Bumpgrass natuurlijke toplaag van gras  veiligheidsondergrond   m2  n.v.t.  7   55.42   19.19
rubbertegels    veiligheidsondergrond   m2  n.v.t.  15  132 4.6
rubbertegels op zand    veiligheidsondergrond   m2  n.v.t.  15  72.05   4.6
rubber-betontegels 30 x 30  veiligheidsondergrond   m2  n.v.t.  15  136.9   4.95
rubbertegels 100 x 100  veiligheidsondergrond   m2  n.v.t.  15  98.53   4.6
in-situ /gegoten rubber veiligheidsondergrond   m2  n.v.t.  15  190 3.9
kunstgras foam en zandbed   veiligheidsondergrond   m2  n.v.t.  15  81.8    9.99166666666667
kunstgras polyethyleen  veiligheidsondergrond   m2  n.v.t.  15  87  9.99166666666667
Grind   veiligheidsondergrond   m2  n.v.t.  15  50  4
\.


--
-- TOC entry 3787 (class 0 OID 64329)
-- Dependencies: 203
-- Data for Name: playmapping_type_group; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playmapping_type_group (categorie, assettype, groep, catasset, id, equipment_type, locationcategory) FROM stdin;
Carrousel   Anders (specificeer in naam veld)   draaitoestel groot  Carrousel/Anders (specificeer in naam veld) 3   7   19
Speeltoestellen Carrousel   draaitoestel groot  Speeltoestellen/Carrousel   4   7   19
Draaiende toestellen    Pirouette / Rotator draaitoestel klein  Draaiende toestellen/Pirouette / Rotator    5   7   19
Carrousel   Type B Klassieke draaimolen met meedraaiende vloer  draaitoestel klein  Carrousel/Type B Klassieke draaimolen met meedraaiende vloer    6   7   19
Ondergrond algemeen Klinkers    elementverharding (tegels/klinkers) Ondergrond algemeen/Klinkers    7   \N  19
Behendigheid    Duikelrek - enkel   enkel duikelrek Behendigheid/Duikelrek - enkel  8   11  19
Behendigheid    Anders (specificeer in naam veld)   evenwichtstoestel   Behendigheid/Anders (specificeer in naam veld)  9   15  19
Behendigheid    Balanceerbalk   evenwichtstoestel   Behendigheid/Balanceerbalk  10  15  19
Behendigheid    Hangbrug    evenwichtstoestel   Behendigheid/Hangbrug   11  15  19
Behendigheid    Loopbrug    evenwichtstoestel   Behendigheid/Loopbrug   12  15  19
Behendigheid    Wiebelplaat evenwichtstoestel   Behendigheid/Wiebelplaat    13  15  19
Veiligheidsondergrond   Boomschors  franse schors   Veiligheidsondergrond/Boomschors    15  \N  19
Klimtoestellen  Anders (specificeer in naam veld)   klimelement Klimtoestellen/Anders (specificeer in naam veld)    16  5   19
Klimtoestellen  Frame   klimelement Klimtoestellen/Frame    17  5   19
Klimtoestellen  Frame & net klimelement Klimtoestellen/Frame & net  18  14  19
Speeltoestellen Klimtoestellen  klimelement Speeltoestellen/Klimtoestellen  19  5   19
Klimtoestellen  Klimwand    klimelement Klimtoestellen/Klimwand 20  5   19
Behendigheid    Knikkertegel    knikkertegel    Behendigheid/Knikkertegel   21  15  19
Ondergrond algemeen Kunstgras (zonder valdempende onderbouw)    kunstgras foam en zandbed   Ondergrond algemeen/Kunstgras (zonder valdempende onderbouw)    22  \N  19
Veiligheidsondergrond   Veiligheidsgras kunstgras polyethyleen  Veiligheidsondergrond/Veiligheidsgras   23  \N  19
Behendigheid    Duikelrek - meerdelig   meerdelig duikelrek Behendigheid/Duikelrek - meerdelig  24  15  19
Wiptoestellen   Type 2B - Enkelpunts - Meerdere richtingen  meerpersoons wipveer    Wiptoestellen/Type 2B - Enkelpunts - Meerdere richtingen    25  3   19
Wiptoestellen   Type 3A - Meerpunts - 1 richting    meerpersoons wipveer    Wiptoestellen/Type 3A - Meerpunts - 1 richting  26  3   19
Wiptoestellen   Type 3B - Meerpunts - Meerdere richtingen   meerpersoons wipveer    Wiptoestellen/Type 3B - Meerpunts - Meerdere richtingen 27  3   19
Speeltoestellen Sociaal spel    poef    Speeltoestellen/Sociaal spel    30  16  19
Veiligheidsondergrond   Rubbertegels    rubber-betontegels 30 x 30  Veiligheidsondergrond/Rubbertegels  31  \N  19
Veiligheidsondergrond   Rubbertegels 50x50  rubber-betontegels 30 x 30  Veiligheidsondergrond/Rubbertegels 50x50    32  \N  19
Klimtoestellen  Net ruimtenet   Klimtoestellen/Net  33  14  19
Schommel    Anders (specificeer in naam veld)   schommel    Schommel/Anders (specificeer in naam veld)  34  2   19
Speeltoestellen Schommel    schommel    Speeltoestellen/Schommel    35  2   19
Schommel    Type 1 - Rotatie om 1 as    schommel    Schommel/Type 1 - Rotatie om 1 as   36  2   19
Schommel    Type 2 - Rotatie op meerdere assen  schommel    Schommel/Type 2 - Rotatie op meerdere assen 37  2   19
Schommel    Type 3 - Rotatie om 1 punt (Bandenzwaai)    schommel    Schommel/Type 3 - Rotatie om 1 punt (Bandenzwaai)   38  2   19
Sociaal spel    Speelhuis   speelhuis   Sociaal spel/Speelhuis  41  12  19
Speeltoestellen Anders  speelprikkel divers Speeltoestellen/Anders  42  \N  19
Waterspel   Anders (specificeer in naam veld)   speelprikkel divers Waterspel/Anders (specificeer in naam veld) 43  10  19
Behendigheid    Kruipbuis   speelprikkel divers Behendigheid/Kruipbuis  44  15  19
Behendigheid    Pleinplakkers   speelprikkel divers Behendigheid/Pleinplakkers  45  15  19
Waterspel   Pompen en spelen    speelprikkel divers Waterspel/Pompen en spelen  46  10  19
Waterspel   Pontje  speelprikkel divers Waterspel/Pontje    47  10  19
Sociaal spel    Speel boot  speelprikkel divers Sociaal spel/Speel boot 48  \N  19
Sociaal spel    Speel paneel    speelprikkel divers Sociaal spel/Speel paneel   49  \N  19
Sociaal spel    Speelaanleiding speelprikkel divers Sociaal spel/Speelaanleiding    50  18  19
Speeltoestellen speelprikkel divers speelprikkel divers Speeltoestellen/speelprikkel divers 51  \N  19
Speeltoestellen Zand spel   zandbak Speeltoestellen/Zand spel   52  8   19
Zand spel   Zandbak zandbak Zand spel/Zandbak   53  8   19
Zand spel   Anders (specificeer in naam veld)   zandspeeltoestel    Zand spel/Anders (specificeer in naam veld) 54  8   19
Zand spel   Zand spel   zandspeeltoestel    Zand spel/Zand spel 55  8   19
Ondergrond algemeen Water       Ondergrond algemeen/Water   56  \N  19
Behendigheid    Trim parcours   \N  Behendigheid/Trim parcours  57  15  19
Binnenvloer Linoleum    \N  Binnenvloer/Linoleum    58  \N  19
\N  Faciliteiten    \N  Faciliteiten    59  \N  19
Faciliteiten    Kunstobjecten   \N  Faciliteiten/Kunstobjecten  60  \N  19
Natuurlijke elementen   Natuurlijke elementen/anders    \N  Natuurlijke elementen/Natuurlijke elementen/anders  61  \N  19
Glijbaan    Aangebouwd - Type 1 uitloop - Traditioneel  glijbaan hoog   Glijbaan/Aangebouwd - Type 1 uitloop - Traditioneel 62  1   19
Glijbaan    Vrijstaand - Type 2 uitloop - Traditioneel  glijbaan hoog   Glijbaan/Vrijstaand - Type 2 uitloop - Traditioneel 63  1   19
Ondergrond algemeen Zandbak/speelzand   valdempend zand Ondergrond algemeen/Zandbak/speelzand   64  \N  19
Behendigheid    Turn brug   evenwichtstoestel   Behendigheid/Turn brug  65  15  19
Carrousel   Type C Hang- en zweefmolens draaitoestel groot  Carrousel/Type C Hang- en zweefmolens   67  7   19
\N  \N  afscheiding \N  68  \N  19
\N  \N  afvalbak    \N  69  \N  19
\N  \N  ballenvanger    \N  70  \N  19
\N  \N  combinatie groot    \N  71  6   19
\N  \N  skateboard klein    \N  72  \N  9
\N  Anders lage rotator draaitoestel klein  Anders lage rotator 74  7   19
\N  Type A Draaiende stoeltjes  draaitoestel klein  Type A Draaiende stoeltjes  75  7   19
\N  Type E Reuzen draaischijf   draaitoestel klein  Type E Reuzen draaischijf   76  7   19
\N  Behendigheid    enkel duikelrek Behendigheid    77  11  19
\N  Enterrek    enkel duikelrek Enterrek    78  11  19
\N  Looptouw    evenwichtstoestel   Looptouw    79  15  19
\N  Turn brug   evenwichtstoestel   Turn brug   80  15  19
Ondergrond algemeen Asfalt  asfaltvloer Ondergrond algemeen/Asfalt  81  \N  19
Ondergrond algemeen Asfaltbeton asfaltvloer Ondergrond algemeen/Asfaltbeton 82  \N  19
Zitgelegenheden Bank    bank    Zitgelegenheden/Bank    83  \N  19
Ondergrond algemeen Beton   betonvloer (skaten) Ondergrond algemeen/Beton   87  \N  19
Ondergrond algemeen Betontegels betonvloer (skaten) Ondergrond algemeen/Betontegels 88  \N  19
Ondergrond algemeen Betontegels 30 x 30 betonvloer (skaten) Ondergrond algemeen/Betontegels 30 x 30 89  \N  19
Ondergrond algemeen Stelcon platen  betonvloer (skaten) Ondergrond algemeen/Stelcon platen  90  \N  19
Behendigheid    Bokspringpalen  bokspringpaal (set) Behendigheid/Bokspringpalen 91  15  19
Ondergrond algemeen Gras    Bumpgrass natuurlijke toplaag van gras  Ondergrond algemeen/Gras    92  \N  19
Ondergrond algemeen Gras / grond    Bumpgrass natuurlijke toplaag van gras  Ondergrond algemeen/Gras / grond    93  \N  19
Schommel    Type 2 - Rotatie op meerdere assen  combinatie klein    Schommel/Type 2 - Rotatie op meerdere assen 94  2   19
Schommel    Speel platform  schommel    Schommel/Type 4 - Contactschommel   104 2   19
Sociaal spel    Speel platform  speelprikkel divers Sociaal spel/Speel platform 105 18  19
Sociaal spel    Speeltafel  speelprikkel divers Sociaal spel/Speeltafel 106 18  19
Speeltoestellen Wiptoestellen   wip Speeltoestellen/Wiptoestellen   107 3   19
Veiligheidsondergrond   Gras    Bumpgrass natuurlijke toplaag van gras  Veiligheidsondergrond/Gras  108 \N  19
Veiligheidsondergrond   Rubbertegels 100x100    rubbertegels 100 x 100  Veiligheidsondergrond/Rubbertegels 100x100  109 \N  19
Veiligheidsondergrond   Rubbertegels 30x30  rubbertegels 100 x 100  Veiligheidsondergrond/Rubbertegels 30x30    110 \N  19
Veiligheidsondergrond   Valdempend Kunstgras    kunstgras polyethyleen  Veiligheidsondergrond/Valdempend Kunstgras  111 \N  19
Wiptoestellen   Anders (specificeer in naam veld)   wip Wiptoestellen/Anders (specificeer in naam veld) 112 3   19
Wiptoestellen   Type 6 - Schommelwip met enkelvoudige hoge as   wip Wiptoestellen/Type 6 - Schommelwip met enkelvoudige hoge as 113 3   19
Zand spel   Tafel   zandspeeltoestel    Zand spel/Tafel 114 8   19
\N  \N  halfverharding (gralux) \N  115 \N  19
\N  Vrijstaand - Type 2 uitloop - Traditioneel  glijbaan laag   Vrijstaand - Type 2 uitloop - Traditioneel  116 1   19
\N  Grind   Grind   Grind   117 \N  19
\N  Houtsnippers    houtsnipper: gekleurd verkleind/gezeefd Houtsnippers    118 \N  19
\N  Aangebouwd- Type 2 uitloop - Traditioneel   klimelement Aangebouwd- Type 2 uitloop - Traditioneel   119 \N  19
\N  Duoslaper   klimelement Duoslaper   120 \N  19
Glijbaan    Vrijstaand - Type 1 glijbaan hoog   Glijbaan/Vrijstaand - Type 1    121 1   19
Speeltoestellen Glijbaan    glijbaan laag   Speeltoestellen/Glijbaan    122 1   19
Glijbaan    Vrijstaand - Type 1 uitloop - Traditioneel  glijbaan laag   Glijbaan/Vrijstaand - Type 1 uitloop - Traditioneel 123 1   19
Glijbaan    Vrijstaand - Type 2 glijbaan laag   Glijbaan/Vrijstaand - Type 2    124 1   19
Ondergrond algemeen Gravel  Grind   Ondergrond algemeen/Gravel  125 \N  19
Behendigheid    Hinkelbaan  hinkelbaan  Behendigheid/Hinkelbaan 126 15  19
Veiligheidsondergrond   Gesmeerde insitu rubber vloer   in-situ /gegoten rubber Veiligheidsondergrond/Gesmeerde insitu rubber vloer 127 \N  19
Sociaal spel    JOP JOP Sociaal spel/JOP    130 18  19
Kabelbaan   Kabelbaan   kabelbaan   Kabelbaan/Kabelbaan 131 13  19
Speeltoestellen Kabelbaan   kabelbaan   Speeltoestellen/Kabelbaan   132 13  19
Behendigheid    Aaneengeschakelde toestellen    klimelement Behendigheid/Aaneengeschakelde toestellen   133 5   19
\N  \N  grastrapveld  (excl. drainage)  \N  138 \N  19
\N  \N  hekwerk \N  139 \N  19
\N  \N  houtsnipper: gekleurde vezel    \N  140 \N  19
\N  \N  kunst   \N  141 \N  19
\N  \N  pannaveld klein \N  142 16  19
\N  \N  picknickset \N  143 \N  19
\N  \N  rubbertegels 100 x 100  \N  144 \N  19
\N  \N  rubbertegels op zand    \N  145 \N  19
\N  \N  schaaktafel \N  146 \N  19
\N  \N  speeltafel  \N  147 \N  19
\N  \N  uitgebreide combinatietoestel   \N  148 6   19
\N  \N  uitgebreide klimtorens  \N  149 5   19
\N  \N  voetbal-basketbaldoel   \N  150 16  19
\N  Eigen bouw  klimelement Eigen bouw  151 5   19
\N  Valdempend Kunstgras    kunstgras polyethyleen  Valdempend Kunstgras    152 \N  19
\N  Basketbal veld  kunstgrastrapvelden Basketbal veld  153 16  8
\N  Tennis banen    kunstgrastrapvelden Tennis banen    154 16  26
\N  Rubbertegels 30x30  rubber-betontegels 30 x 30  Rubbertegels 30x30  155 \N  19
\N  Rubber grastegels   rubbertegels    Rubber grastegels   156 \N  19
\N  Anders (migratie)   speelprikkel divers Anders (migratie)   166 \N  19
\N  Speeltoestellen speelprikkel divers Speeltoestellen 167 \N  19
\N  Speeltafel  speeltafel  Speeltafel  168 \N  19
\N  Wiptoestellen   wipveer Wiptoestellen   170 3   19
\N  Telefoon palen      Telefoon palen  171 \N  19
Sociaal spel    Speeltrein  speelprikkel divers Sociaal spel/Speeltrein 172 17  19
Behendigheid    Stapstenen  speelprikkel divers Behendigheid/Stapstenen 173 15  19
Glijbaan    Tallud / helling - Type 2 uitloop - Traditioneel    taludglijbaan   Glijbaan/Tallud / helling - Type 2 uitloop - Traditioneel   175 1   19
Glijbaan    Talud / helling - Type 1    taludglijbaan   Glijbaan/Talud / helling - Type 1   176 1   19
Ondergrond algemeen Grond   valdempend zand Ondergrond algemeen/Grond   177 \N  19
Veiligheidsondergrond   Valdempend Zand valdempend zand Veiligheidsondergrond/Valdempend Zand   178 \N  19
Wiptoestellen   Type 1 - wip 1 richting wip Wiptoestellen/Type 1 - wip 1 richting   182 3   19
Wiptoestellen   Type 2A - Enkelpunts - 1 richting   wipveer Wiptoestellen/Type 2A - Enkelpunts - 1 richting 183 3   19
Speeltoestellen Combinatietoestellen    combinatie middel   Speeltoestellen/Combinatietoestellen    1   6   19
Combinatietoestellen    Glijbaan aan huisje combinatie middel   Combinatietoestellen/Glijbaan aan huisje    2   12  19
Rijdend spel    Skateboard - Anders skateboard groot    Rijdend spel/Skateboard - Anders    39  17  9
Rijdend spel    Anders (specificeer in naam veld)   skateboard middel   Rijdend spel/Anders (specificeer in naam veld)  40  17  9
Combinatietoestellen    Combinatie - Peuter combinatie middel   Combinatietoestellen/Combinatie - Peuter    66  6   19
\N  Combinatie - Peuter combinatie middel   Combinatie - Peuter 73  6   19
Combinatietoestellen    Anders (Specifceer in veld) combinatie middel   Combinatietoestellen/Anders (Specifceer in veld)    95  6   19
Anders  Anders (Specificeer in naam veld)   combinatie middel   Anders/Anders (Specificeer in naam veld)    96  6   19
Glijbaan    Anders (specificeer in naam veld)   combinatie middel   Glijbaan/Anders (specificeer in naam veld)  97  1   19
Combinatietoestellen    Combinatie - Kind   combinatie middel   Combinatietoestellen/Combinatie - Kind  98  6   19
Combinatietoestellen    Combinatie - Kleuter    combinatie middel   Combinatietoestellen/Combinatie - Kleuter   99  6   19
Rijdend spel    Skateboard - Pyramid    skateboard middel   Rijdend spel/Skateboard - Pyramid   100 17  9
Rijdend spel    Skateboard - Ramp   skateboard middel   Rijdend spel/Skateboard - Ramp  101 17  9
Rijdend spel    Skateboard - Spine  skateboard middel   Rijdend spel/Skateboard - Spine 102 17  9
Rijdend spel    Skateboard -  Faciliteit    skateboard middel   Rijdend spel/Skateboard -  Faciliteit   103 17  9
Rijdend spel    Skateboard - Bench  skateboard middel   Rijdend spel/Skateboard - Bench 134 17  9
Rijdend spel    Skateboard - Fun Box    skateboard middel   Rijdend spel/Skateboard - Fun Box   135 17  9
Rijdend spel    Skateboard - Grind Rail skateboard middel   Rijdend spel/Skateboard - Grind Rail    136 17  9
Rijdend spel    Skateboard - Platform   skateboard middel   Rijdend spel/Skateboard - Platform  137 17  9
\N  Skateboard - Flat Ramp  skateboard groot    Skateboard - Flat Ramp  157 19  9
\N  Skateboard - Fun Box    skateboard groot    Skateboard - Fun Box    158 19  9
\N  Skateboard - Grind Box  skateboard groot    Skateboard - Grind Box  159 19  9
\N  Skateboard - Grind Rail skateboard groot    Skateboard - Grind Rail 160 19  9
\N  Skateboard - Half Pipe  skateboard groot    Skateboard - Half Pipe  161 19  9
\N  Skateboard - Jump Box   skateboard groot    Skateboard - Jump Box   162 19  9
\N  Skateboard - Jump Ramp  skateboard groot    Skateboard - Jump Ramp  163 19  9
\N  Skateboard - Spine  skateboard groot    Skateboard - Spine  164 19  9
\N  Skateboard - Â Faciliteit   skateboard klein    Skateboard - Â Faciliteit   165 19  9
\N  Basketbal bord  voetbal-basketbaldoel   Basketbal bord  169 16  8
Sporttoestellen Fitness fitness Sporttoestellen/Fitness 14  16  24
Sporttoestellen Pannaveld   Pannaveld   Sporttoestellen/Pannaveld   28  16  7
Sporttoestellen Voetbal veld    pannaveld groot Sporttoestellen/Voetbal veld    29  16  7
Sporttoestellen Ballenvanger    basketbalpaal   Sporttoestellen/Ballenvanger    84  16  8
Sporttoestellen Basketbalpaal   basketbalpaal   Sporttoestellen/Basketbalpaal   85  16  8
Sporttoestellen Korfbalpaal basketbalpaal   Sporttoestellen/Korfbalpaal 86  16  8
Sporttoestellen Anders (Specificeer in naam veld)   jeu de boulesbaan   Sporttoestellen/Anders (Specificeer in naam veld)   128 \N  12
Sporttoestellen Jeu de Boules   jeu de boulesbaan   Sporttoestellen/Jeu de Boules   129 \N  12
Sporttoestellen Tafeltennistafel    tafeltennistafel    Sporttoestellen/Tafeltennistafel    174 16  11
Sporttoestellen Doel    voetbaldoel Sporttoestellen/Doel    179 16  7
Sporttoestellen Mini Goal   voetbaldoel Sporttoestellen/Mini Goal   180 16  7
Sporttoestellen Volleybal set   volleybalset    Sporttoestellen/Volleybal set   181 16  13
\.


--
-- TOC entry 3852 (class 0 OID 0)
-- Dependencies: 226
-- Name: playmapping_type_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playmapping_type_group_id_seq', 183, true);


--
-- TOC entry 3853 (class 0 OID 0)
-- Dependencies: 205
-- Name: playservice_accessibility_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_accessibility_id_seq', 5, true);


--
-- TOC entry 3788 (class 0 OID 64335)
-- Dependencies: 204
-- Data for Name: playservice_accessibility_list; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_accessibility_list (id, accessibility) FROM stdin;
1   Mindervaliden parkeerplaats
2   Rolstoelvriendelijk
3   Invalidentoilet
4   samenspeelplek
5   Inclusive playground
6   NVWA verscherpt toezicht
7   NVWA voldoet
\.


--
-- TOC entry 3854 (class 0 OID 0)
-- Dependencies: 207
-- Name: playservice_agecategories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_agecategories_id_seq', 5, true);


--
-- TOC entry 3790 (class 0 OID 64340)
-- Dependencies: 206
-- Data for Name: playservice_agecategories_list; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_agecategories_list (id, agecategory) FROM stdin;
1   0 - 5 jaar
2   6 - 11 jaar
3   12 - 18 jaar
4   Volwassenen
5   Senioren
\.


--
-- TOC entry 3855 (class 0 OID 0)
-- Dependencies: 209
-- Name: playservice_categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_categories_id_seq', 22, true);


--
-- TOC entry 3792 (class 0 OID 64345)
-- Dependencies: 208
-- Data for Name: playservice_categories_list; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_categories_list (id, category, main) FROM stdin;
5   Outdoor fitness toestellen  Sport & Fitness
6   Fitness parcours    Sport & Fitness
7   Voetbalveld/Pannakooi   Sport & Fitness
8   Basketbalpaal   Sport & Fitness
9   Skatebaan   Sport & Fitness
10  Fietscrossbaan  Sport & Fitness
11  Tafeltennistafel    Sport & Fitness
12  Jeu de boulesbaan   Sport & Fitness
13  Volleybalveld   Sport & Fitness
14  Pretpark    Amusement
15  Waterpark   Amusement
16  Dierentuin  Amusement
17  Kinderboerderij Amusement
18  Zwembad Amusement
19  Openbare speeltuin  Speeltuinen
20  Speeltuinvereniging Speeltuinen
21  Binnenspeeltuin Speeltuinen
22  Natuurspeeltuin Speeltuinen
23  Speeltuinen Speeltuinen
25  Amusement   Amusement
26  Tennis  Sport & Fitness
24  Sport & Fitness Sport & Fitness
\.


--
-- TOC entry 3827 (class 0 OID 173762)
-- Dependencies: 245
-- Data for Name: playservice_comment; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_comment (id, playadvisor_id, post_id, location, content, stars, author, date) FROM stdin;
\.


--
-- TOC entry 3856 (class 0 OID 0)
-- Dependencies: 246
-- Name: playservice_comment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_comment_id_seq', 5, true);


--
-- TOC entry 3829 (class 0 OID 173778)
-- Dependencies: 247
-- Data for Name: playservice_comment_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_comment_playadvisor (id, playadvisor_id, post_id, location, content, stars, author, date) FROM stdin;
\.


--
-- TOC entry 3857 (class 0 OID 0)
-- Dependencies: 248
-- Name: playservice_comment_playadvisor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_comment_playadvisor_id_seq', 2160, true);


--
-- TOC entry 3813 (class 0 OID 97423)
-- Dependencies: 229
-- Data for Name: playservice_documents; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_documents (id, url, caption, location, equipment, pm_guid) FROM stdin;
\.


--
-- TOC entry 3858 (class 0 OID 0)
-- Dependencies: 228
-- Name: playservice_documents_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_documents_id_seq', 10148, true);


--
-- TOC entry 3821 (class 0 OID 141332)
-- Dependencies: 238
-- Data for Name: playservice_documents_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_documents_playadvisor (id, url, caption, location, equipment, pm_guid) FROM stdin;
\.


--
-- TOC entry 3859 (class 0 OID 0)
-- Dependencies: 237
-- Name: playservice_documents_playadvisor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_documents_playadvisor_id_seq', 1, false);


--
-- TOC entry 3860 (class 0 OID 0)
-- Dependencies: 211
-- Name: playservice_equipment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_equipment_id_seq', 14, true);


--
-- TOC entry 3794 (class 0 OID 64353)
-- Dependencies: 210
-- Data for Name: playservice_equipment_list; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_equipment_list (id, equipment) FROM stdin;
1   Glijbaan
2   Schommel
3   Wip
4   Wipkip
5   Klimtoestel
6   Combinatietoestel
7   Draaitoestel
8   Zandbak
9   Trampoline
10  Zand-/waterspeeltoestel
11  Duikelrek
12  Speelhuis
13  Kabelbaan
14  Klimnet
15  Behendigheid
16  Sporttoestellen/fitness
17  Rijdend spel
18  Sociaal spel
19  Skatebaan
\.


--
-- TOC entry 3861 (class 0 OID 0)
-- Dependencies: 213
-- Name: playservice_facilities_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_facilities_id_seq', 12, true);


--
-- TOC entry 3796 (class 0 OID 64358)
-- Dependencies: 212
-- Data for Name: playservice_facilities_list; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_facilities_list (id, facility) FROM stdin;
1   Kracht
2   Cardio
3   Fitness oefeningen aanwezig
4   Toilet
5   Verschoontafel
6   Winkeltje
7   Restaurant
8   WiFi
9   Beheerder/toezicht aanwezig
10  Bankje
11  Picknicktafel
12  Afgesloten terrein/hek aanwezig
13  Rolstoelvriendelijk
\.


--
-- TOC entry 3862 (class 0 OID 0)
-- Dependencies: 215
-- Name: playservice_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_id_seq', 67410, true);


--
-- TOC entry 3800 (class 0 OID 64371)
-- Dependencies: 216
-- Data for Name: playservice_images; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_images (id, url, caption, location, equipment, pm_guid) FROM stdin;
\.


--
-- TOC entry 3863 (class 0 OID 0)
-- Dependencies: 217
-- Name: playservice_images_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_images_id_seq', 395467, true);


--
-- TOC entry 3823 (class 0 OID 141353)
-- Dependencies: 240
-- Data for Name: playservice_images_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_images_playadvisor (id, url, caption, location, equipment, pm_guid) FROM stdin;
\.


--
-- TOC entry 3864 (class 0 OID 0)
-- Dependencies: 239
-- Name: playservice_images_playadvisor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_images_playadvisor_id_seq', 203527, true);


--
-- TOC entry 3802 (class 0 OID 64379)
-- Dependencies: 218
-- Data for Name: playservice_location_accessibility; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_accessibility (location, accessibility) FROM stdin;
\.


--
-- TOC entry 3824 (class 0 OID 141372)
-- Dependencies: 241
-- Data for Name: playservice_location_accessibility_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_accessibility_playadvisor (location, accessibility) FROM stdin;
\.


--
-- TOC entry 3803 (class 0 OID 64382)
-- Dependencies: 219
-- Data for Name: playservice_location_agecategories; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_agecategories (location, agecategory, pa_id) FROM stdin;
\.


--
-- TOC entry 3825 (class 0 OID 141387)
-- Dependencies: 242
-- Data for Name: playservice_location_agecategories_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_agecategories_playadvisor (location, agecategory, pa_id) FROM stdin;
\.


--
-- TOC entry 3804 (class 0 OID 64385)
-- Dependencies: 220
-- Data for Name: playservice_location_categories; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_categories (location, category, pa_id, pm_guid) FROM stdin;
\.


--
-- TOC entry 3826 (class 0 OID 141402)
-- Dependencies: 243
-- Data for Name: playservice_location_categories_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_categories_playadvisor (location, category, pa_id) FROM stdin;
\.


--
-- TOC entry 3805 (class 0 OID 64388)
-- Dependencies: 221
-- Data for Name: playservice_location_equipment; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_equipment (id, location, equipment, pa_guid, longitude, latitude, type_, installeddate, pm_guid, name, priceindexation, priceinstallation, pricemaintenance, pricepurchase, pricereinvestment, depth, width, height, endoflifeyear, freefallheight, safetyzonelength, safetyzonewidth, manufacturer, material, product, productid, productvariantid, serialnumber, geom) FROM stdin;
\.


--
-- TOC entry 3811 (class 0 OID 97353)
-- Dependencies: 227
-- Data for Name: playservice_location_equipment_agecategories; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_equipment_agecategories (location_equipment, agecategory) FROM stdin;
\.


--
-- TOC entry 3818 (class 0 OID 141300)
-- Dependencies: 235
-- Data for Name: playservice_location_equipment_agecategories_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_equipment_agecategories_playadvisor (location_equipment, agecategory) FROM stdin;
\.


--
-- TOC entry 3865 (class 0 OID 0)
-- Dependencies: 222
-- Name: playservice_location_equipment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_location_equipment_id_seq', 349829, true);


--
-- TOC entry 3817 (class 0 OID 141276)
-- Dependencies: 234
-- Data for Name: playservice_location_equipment_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_equipment_playadvisor (id, location, equipment, pa_guid, longitude, latitude, type_, installeddate, pm_guid, name, priceindexation, priceinstallation, pricemaintenance, pricepurchase, pricereinvestment, depth, width, height, endoflifeyear, freefallheight, safetyzonelength, safetyzonewidth, manufacturer, material, product, productid, productvariantid, serialnumber, geom) FROM stdin;
\.


--
-- TOC entry 3866 (class 0 OID 0)
-- Dependencies: 233
-- Name: playservice_location_equipment_playadvisor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_location_equipment_playadvisor_id_seq', 34042, true);


--
-- TOC entry 3807 (class 0 OID 64393)
-- Dependencies: 223
-- Data for Name: playservice_location_facilities; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_facilities (location, facility, pa_id) FROM stdin;
\.


--
-- TOC entry 3819 (class 0 OID 141315)
-- Dependencies: 236
-- Data for Name: playservice_location_facilities_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_location_facilities_playadvisor (location, facility, pa_id) FROM stdin;
\.


--
-- TOC entry 3798 (class 0 OID 64363)
-- Dependencies: 214
-- Data for Name: playservice_locations; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_locations (id, title, content, summary, street, number, numberextra, postalcode, municipality, area, country, website, email, phone, longitude, latitude, pm_guid, pa_id, parking, geom, averagerating, pa_title) FROM stdin;
\.


--
-- TOC entry 3815 (class 0 OID 141260)
-- Dependencies: 232
-- Data for Name: playservice_locations_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_locations_playadvisor (id, title, content, summary, street, number, numberextra, postalcode, municipality, area, country, website, email, phone, longitude, latitude, pm_guid, pa_id, parking, geom, averagerating, pa_title) FROM stdin;
\.


--
-- TOC entry 3867 (class 0 OID 0)
-- Dependencies: 231
-- Name: playservice_locations_playadvisor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_locations_playadvisor_id_seq', 32190, true);


--
-- TOC entry 3868 (class 0 OID 0)
-- Dependencies: 225
-- Name: playservice_parking_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--

SELECT pg_catalog.setval('playservice_parking_id_seq', 3, true);


--
-- TOC entry 3808 (class 0 OID 64396)
-- Dependencies: 224
-- Data for Name: playservice_parking_list; Type: TABLE DATA; Schema: public; Owner: playbase
--

COPY playservice_parking_list (id, parking) FROM stdin;
1   ja - gratis
2   ja - betaald
3   nee
\.


--
-- TOC entry 3562 (class 2606 OID 97340)
-- Name: playmapping_type_group playmapping_type_group_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playmapping_type_group
    ADD CONSTRAINT playmapping_type_group_pkey PRIMARY KEY (id);


--
-- TOC entry 3564 (class 2606 OID 64653)
-- Name: playservice_accessibility_list playservice_accessibility_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_accessibility_list
    ADD CONSTRAINT playservice_accessibility_pkey PRIMARY KEY (id);


--
-- TOC entry 3566 (class 2606 OID 64655)
-- Name: playservice_agecategories_list playservice_agecategories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_agecategories_list
    ADD CONSTRAINT playservice_agecategories_pkey PRIMARY KEY (id);


--
-- TOC entry 3568 (class 2606 OID 64657)
-- Name: playservice_categories_list playservice_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_categories_list
    ADD CONSTRAINT playservice_categories_pkey PRIMARY KEY (id);


--
-- TOC entry 3614 (class 2606 OID 173772)
-- Name: playservice_comment playservice_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_comment
    ADD CONSTRAINT playservice_comment_pkey PRIMARY KEY (id);


--
-- TOC entry 3616 (class 2606 OID 173788)
-- Name: playservice_comment_playadvisor playservice_comment_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_comment_playadvisor
    ADD CONSTRAINT playservice_comment_playadvisor_pkey PRIMARY KEY (id);


--
-- TOC entry 3594 (class 2606 OID 97431)
-- Name: playservice_documents playservice_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_documents
    ADD CONSTRAINT playservice_documents_pkey PRIMARY KEY (id);


--
-- TOC entry 3604 (class 2606 OID 141340)
-- Name: playservice_documents_playadvisor playservice_documents_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_documents_playadvisor
    ADD CONSTRAINT playservice_documents_playadvisor_pkey PRIMARY KEY (id);


--
-- TOC entry 3572 (class 2606 OID 64659)
-- Name: playservice_equipment_list playservice_equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_equipment_list
    ADD CONSTRAINT playservice_equipment_pkey PRIMARY KEY (id);


--
-- TOC entry 3574 (class 2606 OID 64661)
-- Name: playservice_facilities_list playservice_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_facilities_list
    ADD CONSTRAINT playservice_facilities_pkey PRIMARY KEY (id);


--
-- TOC entry 3578 (class 2606 OID 64663)
-- Name: playservice_images playservice_images_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_images
    ADD CONSTRAINT playservice_images_pkey PRIMARY KEY (id);


--
-- TOC entry 3606 (class 2606 OID 141361)
-- Name: playservice_images_playadvisor playservice_images_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_images_playadvisor
    ADD CONSTRAINT playservice_images_playadvisor_pkey PRIMARY KEY (id);


--
-- TOC entry 3580 (class 2606 OID 64665)
-- Name: playservice_location_accessibility playservice_location_accessibility_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_accessibility
    ADD CONSTRAINT playservice_location_accessibility_pkey PRIMARY KEY (location, accessibility);


--
-- TOC entry 3608 (class 2606 OID 141376)
-- Name: playservice_location_accessibility_playadvisor playservice_location_accessibility_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_accessibility_playadvisor
    ADD CONSTRAINT playservice_location_accessibility_playadvisor_pkey PRIMARY KEY (location, accessibility);


--
-- TOC entry 3582 (class 2606 OID 64667)
-- Name: playservice_location_agecategories playservice_location_agecategories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_agecategories
    ADD CONSTRAINT playservice_location_agecategories_pkey PRIMARY KEY (location, agecategory);


--
-- TOC entry 3610 (class 2606 OID 141391)
-- Name: playservice_location_agecategories_playadvisor playservice_location_agecategories_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_agecategories_playadvisor
    ADD CONSTRAINT playservice_location_agecategories_playadvisor_pkey PRIMARY KEY (location, agecategory);


--
-- TOC entry 3584 (class 2606 OID 64669)
-- Name: playservice_location_categories playservice_location_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_categories
    ADD CONSTRAINT playservice_location_categories_pkey PRIMARY KEY (location, category);


--
-- TOC entry 3612 (class 2606 OID 141406)
-- Name: playservice_location_categories_playadvisor playservice_location_categories_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_categories_playadvisor
    ADD CONSTRAINT playservice_location_categories_playadvisor_pkey PRIMARY KEY (location, category);


--
-- TOC entry 3592 (class 2606 OID 97357)
-- Name: playservice_location_equipment_agecategories playservice_location_equipment_agecategories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_agecategories
    ADD CONSTRAINT playservice_location_equipment_agecategories_pkey PRIMARY KEY (location_equipment, agecategory);


--
-- TOC entry 3600 (class 2606 OID 141304)
-- Name: playservice_location_equipment_agecategories_playadvisor playservice_location_equipment_agecategories_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_agecategories_playadvisor
    ADD CONSTRAINT playservice_location_equipment_agecategories_playadvisor_pkey PRIMARY KEY (location_equipment, agecategory);


--
-- TOC entry 3586 (class 2606 OID 64671)
-- Name: playservice_location_equipment playservice_location_equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment
    ADD CONSTRAINT playservice_location_equipment_pkey PRIMARY KEY (id);


--
-- TOC entry 3598 (class 2606 OID 141284)
-- Name: playservice_location_equipment_playadvisor playservice_location_equipment_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_playadvisor
    ADD CONSTRAINT playservice_location_equipment_playadvisor_pkey PRIMARY KEY (id);


--
-- TOC entry 3588 (class 2606 OID 64673)
-- Name: playservice_location_facilities playservice_location_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_facilities
    ADD CONSTRAINT playservice_location_facilities_pkey PRIMARY KEY (location, facility);


--
-- TOC entry 3602 (class 2606 OID 141319)
-- Name: playservice_location_facilities_playadvisor playservice_location_facilities_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_facilities_playadvisor
    ADD CONSTRAINT playservice_location_facilities_playadvisor_pkey PRIMARY KEY (location, facility);


--
-- TOC entry 3576 (class 2606 OID 64675)
-- Name: playservice_locations playservice_locations_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_locations
    ADD CONSTRAINT playservice_locations_pkey PRIMARY KEY (id);


--
-- TOC entry 3596 (class 2606 OID 141268)
-- Name: playservice_locations_playadvisor playservice_locations_playadvisor_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_locations_playadvisor
    ADD CONSTRAINT playservice_locations_playadvisor_pkey PRIMARY KEY (id);


--
-- TOC entry 3590 (class 2606 OID 64677)
-- Name: playservice_parking_list playservice_parking_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_parking_list
    ADD CONSTRAINT playservice_parking_pkey PRIMARY KEY (id);


--
-- TOC entry 3570 (class 2606 OID 195958)
-- Name: playservice_categories_list sd; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_categories_list
    ADD CONSTRAINT sd UNIQUE (id);


--
-- TOC entry 3618 (class 2606 OID 195330)
-- Name: playmapping_type_group fk_category; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playmapping_type_group
    ADD CONSTRAINT fk_category FOREIGN KEY (locationcategory) REFERENCES playservice_categories_list(id);


--
-- TOC entry 3619 (class 2606 OID 195959)
-- Name: playmapping_type_group fk_location_equipment_type; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playmapping_type_group
    ADD CONSTRAINT fk_location_equipment_type FOREIGN KEY (locationcategory) REFERENCES playservice_categories_list(id);


--
-- TOC entry 3617 (class 2606 OID 195183)
-- Name: playmapping_type_group playmapping_equipment_pkey; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playmapping_type_group
    ADD CONSTRAINT playmapping_equipment_pkey FOREIGN KEY (equipment_type) REFERENCES playservice_equipment_list(id);


--
-- TOC entry 3656 (class 2606 OID 173773)
-- Name: playservice_comment playservice_comment_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_comment
    ADD CONSTRAINT playservice_comment_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3657 (class 2606 OID 173789)
-- Name: playservice_comment_playadvisor playservice_comment_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_comment_playadvisor
    ADD CONSTRAINT playservice_comment_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3636 (class 2606 OID 97432)
-- Name: playservice_documents playservice_documents_fk; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_documents
    ADD CONSTRAINT playservice_documents_fk FOREIGN KEY (equipment) REFERENCES playservice_location_equipment(id);


--
-- TOC entry 3637 (class 2606 OID 97437)
-- Name: playservice_documents playservice_documents_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_documents
    ADD CONSTRAINT playservice_documents_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3646 (class 2606 OID 141341)
-- Name: playservice_documents_playadvisor playservice_documents_playadvisor_fk; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_documents_playadvisor
    ADD CONSTRAINT playservice_documents_playadvisor_fk FOREIGN KEY (equipment) REFERENCES playservice_location_equipment_playadvisor(id);


--
-- TOC entry 3647 (class 2606 OID 141346)
-- Name: playservice_documents_playadvisor playservice_documents_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_documents_playadvisor
    ADD CONSTRAINT playservice_documents_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3622 (class 2606 OID 97416)
-- Name: playservice_images playserviceimages_equipment_fk; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_images
    ADD CONSTRAINT playserviceimages_equipment_fk FOREIGN KEY (equipment) REFERENCES playservice_location_equipment(id);


--
-- TOC entry 3648 (class 2606 OID 141362)
-- Name: playservice_images_playadvisor playserviceimages_equipment_playadvisor_fk; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_images_playadvisor
    ADD CONSTRAINT playserviceimages_equipment_playadvisor_fk FOREIGN KEY (equipment) REFERENCES playservice_location_equipment_playadvisor(id);


--
-- TOC entry 3621 (class 2606 OID 64688)
-- Name: playservice_images playserviceimages_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_images
    ADD CONSTRAINT playserviceimages_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3649 (class 2606 OID 141367)
-- Name: playservice_images_playadvisor playserviceimages_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_images_playadvisor
    ADD CONSTRAINT playserviceimages_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3623 (class 2606 OID 64693)
-- Name: playservice_location_accessibility playservicelocationaccessibility_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_accessibility
    ADD CONSTRAINT playservicelocationaccessibility_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3624 (class 2606 OID 64698)
-- Name: playservice_location_accessibility playservicelocationaccessibility_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_accessibility
    ADD CONSTRAINT playservicelocationaccessibility_fk2 FOREIGN KEY (accessibility) REFERENCES playservice_accessibility_list(id);


--
-- TOC entry 3650 (class 2606 OID 141377)
-- Name: playservice_location_accessibility_playadvisor playservicelocationaccessibility_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_accessibility_playadvisor
    ADD CONSTRAINT playservicelocationaccessibility_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3651 (class 2606 OID 141382)
-- Name: playservice_location_accessibility_playadvisor playservicelocationaccessibility_playadvisor_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_accessibility_playadvisor
    ADD CONSTRAINT playservicelocationaccessibility_playadvisor_fk2 FOREIGN KEY (accessibility) REFERENCES playservice_accessibility_list(id);


--
-- TOC entry 3625 (class 2606 OID 64703)
-- Name: playservice_location_agecategories playservicelocationagecategories_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_agecategories
    ADD CONSTRAINT playservicelocationagecategories_fk1 FOREIGN KEY (agecategory) REFERENCES playservice_agecategories_list(id);


--
-- TOC entry 3634 (class 2606 OID 97358)
-- Name: playservice_location_equipment_agecategories playservicelocationagecategories_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_agecategories
    ADD CONSTRAINT playservicelocationagecategories_fk1 FOREIGN KEY (agecategory) REFERENCES playservice_agecategories_list(id);


--
-- TOC entry 3626 (class 2606 OID 64708)
-- Name: playservice_location_agecategories playservicelocationagecategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_agecategories
    ADD CONSTRAINT playservicelocationagecategories_fk2 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3642 (class 2606 OID 141305)
-- Name: playservice_location_equipment_agecategories_playadvisor playservicelocationagecategories_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_agecategories_playadvisor
    ADD CONSTRAINT playservicelocationagecategories_playadvisor_fk1 FOREIGN KEY (agecategory) REFERENCES playservice_agecategories_list(id);


--
-- TOC entry 3652 (class 2606 OID 141392)
-- Name: playservice_location_agecategories_playadvisor playservicelocationagecategories_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_agecategories_playadvisor
    ADD CONSTRAINT playservicelocationagecategories_playadvisor_fk1 FOREIGN KEY (agecategory) REFERENCES playservice_agecategories_list(id);


--
-- TOC entry 3653 (class 2606 OID 141397)
-- Name: playservice_location_agecategories_playadvisor playservicelocationagecategories_playadvisor_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_agecategories_playadvisor
    ADD CONSTRAINT playservicelocationagecategories_playadvisor_fk2 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3627 (class 2606 OID 64713)
-- Name: playservice_location_categories playservicelocationcategories_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_categories
    ADD CONSTRAINT playservicelocationcategories_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3628 (class 2606 OID 64718)
-- Name: playservice_location_categories playservicelocationcategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_categories
    ADD CONSTRAINT playservicelocationcategories_fk2 FOREIGN KEY (category) REFERENCES playservice_categories_list(id);


--
-- TOC entry 3655 (class 2606 OID 141412)
-- Name: playservice_location_categories_playadvisor playservicelocationcategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_categories_playadvisor
    ADD CONSTRAINT playservicelocationcategories_fk2 FOREIGN KEY (category) REFERENCES playservice_categories_list(id);


--
-- TOC entry 3654 (class 2606 OID 141407)
-- Name: playservice_location_categories_playadvisor playservicelocationcategories_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_categories_playadvisor
    ADD CONSTRAINT playservicelocationcategories_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3629 (class 2606 OID 64723)
-- Name: playservice_location_equipment playservicelocationequipment_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment
    ADD CONSTRAINT playservicelocationequipment_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3630 (class 2606 OID 64728)
-- Name: playservice_location_equipment playservicelocationequipment_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment
    ADD CONSTRAINT playservicelocationequipment_fk2 FOREIGN KEY (equipment) REFERENCES playservice_equipment_list(id);


--
-- TOC entry 3639 (class 2606 OID 141285)
-- Name: playservice_location_equipment_playadvisor playservicelocationequipment_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_playadvisor
    ADD CONSTRAINT playservicelocationequipment_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3640 (class 2606 OID 141290)
-- Name: playservice_location_equipment_playadvisor playservicelocationequipment_playadvisor_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_playadvisor
    ADD CONSTRAINT playservicelocationequipment_playadvisor_fk2 FOREIGN KEY (equipment) REFERENCES playservice_equipment_list(id);


--
-- TOC entry 3635 (class 2606 OID 97363)
-- Name: playservice_location_equipment_agecategories playservicelocationequipmentagecategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_agecategories
    ADD CONSTRAINT playservicelocationequipmentagecategories_fk2 FOREIGN KEY (location_equipment) REFERENCES playservice_location_equipment(id);


--
-- TOC entry 3643 (class 2606 OID 141310)
-- Name: playservice_location_equipment_agecategories_playadvisor playservicelocationequipmentagecategories_playadvisor_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_agecategories_playadvisor
    ADD CONSTRAINT playservicelocationequipmentagecategories_playadvisor_fk2 FOREIGN KEY (location_equipment) REFERENCES playservice_location_equipment_playadvisor(id);


--
-- TOC entry 3631 (class 2606 OID 97348)
-- Name: playservice_location_equipment playservicelocationequipmenttype_fk; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment
    ADD CONSTRAINT playservicelocationequipmenttype_fk FOREIGN KEY (type_) REFERENCES playmapping_type_group(id);


--
-- TOC entry 3641 (class 2606 OID 141295)
-- Name: playservice_location_equipment_playadvisor playservicelocationequipmenttype_playadvisor_fk; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_equipment_playadvisor
    ADD CONSTRAINT playservicelocationequipmenttype_playadvisor_fk FOREIGN KEY (type_) REFERENCES playmapping_type_group(id);


--
-- TOC entry 3632 (class 2606 OID 64733)
-- Name: playservice_location_facilities playservicelocationfacilities_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_facilities
    ADD CONSTRAINT playservicelocationfacilities_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3633 (class 2606 OID 64738)
-- Name: playservice_location_facilities playservicelocationfacilities_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_facilities
    ADD CONSTRAINT playservicelocationfacilities_fk2 FOREIGN KEY (facility) REFERENCES playservice_facilities_list(id);


--
-- TOC entry 3644 (class 2606 OID 141320)
-- Name: playservice_location_facilities_playadvisor playservicelocationfacilities_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_facilities_playadvisor
    ADD CONSTRAINT playservicelocationfacilities_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3645 (class 2606 OID 141325)
-- Name: playservice_location_facilities_playadvisor playservicelocationfacilities_playadvisor_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_location_facilities_playadvisor
    ADD CONSTRAINT playservicelocationfacilities_playadvisor_fk2 FOREIGN KEY (facility) REFERENCES playservice_facilities_list(id);


--
-- TOC entry 3620 (class 2606 OID 64743)
-- Name: playservice_locations playservicelocations_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_locations
    ADD CONSTRAINT playservicelocations_fk1 FOREIGN KEY (parking) REFERENCES playservice_parking_list(id);


--
-- TOC entry 3638 (class 2606 OID 141269)
-- Name: playservice_locations_playadvisor playservicelocations_playadvisor_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER TABLE ONLY playservice_locations_playadvisor
    ADD CONSTRAINT playservicelocations_playadvisor_fk1 FOREIGN KEY (parking) REFERENCES playservice_parking_list(id);


-- Completed on 2017-10-17 12:24:45 CEST

--
-- PostgreSQL database dump complete
--

