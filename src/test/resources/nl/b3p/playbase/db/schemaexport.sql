--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.2
-- Dumped by pg_dump version 9.6.2

-- Started on 2017-06-01 16:20:11 CEST

--
-- TOC entry 209 (class 1259 OID 64323)
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

--
-- TOC entry 210 (class 1259 OID 64329)
-- Name: playmapping_type_group; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playmapping_type_group (
    categorie character varying(255),
    assettype character varying(255),
    groep character varying(255),
    catasset character varying(255),
    id  bigint generated by default as identity (start with 1)
);



--
-- TOC entry 211 (class 1259 OID 64335)
-- Name: playservice_accessibility_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_accessibility_list (
    id   bigint generated by default as identity (start with 1),
    accessibility character varying(255) NOT NULL
);



--
-- TOC entry 213 (class 1259 OID 64340)
-- Name: playservice_agecategories_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_agecategories_list (
    id   bigint generated by default as identity (start with 1),
    agecategory character varying(255) NOT NULL
);


--
-- TOC entry 215 (class 1259 OID 64345)
-- Name: playservice_categories_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_categories_list (
    id   bigint generated by default as identity (start with 1),
    category character varying(255) NOT NULL,
    main character varying(255)
);

--

--
-- TOC entry 256 (class 1259 OID 97423)
-- Name: playservice_documents; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_documents (
    id   bigint generated by default as identity (start with 1),
    url varchar(255),
    caption character varying(255),
    location  integer not null,
    equipment integer,
    pm_guid character varying(255)
);

--
-- TOC entry 217 (class 1259 OID 64353)
-- Name: playservice_equipment_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_equipment_list (
    id   bigint generated by default as identity (start with 1),
    equipment character varying(255) NOT NULL
);

--
-- TOC entry 218 (class 1259 OID 64356)
-- Name: playservice_equipment_id_seq; Type: SEQUENCE; Schema: public; Owner: playbase
--
--
-- TOC entry 219 (class 1259 OID 64358)
-- Name: playservice_facilities_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_facilities_list (
    id   bigint generated by default as identity (start with 1),
    facility character varying(255) NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 64363)
-- Name: playservice_locations; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_locations (
    id   bigint generated by default as identity (start with 1),
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
    parking integer,
    geom clob
);



--
-- TOC entry 223 (class 1259 OID 64371)
-- Name: playservice_images; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_images (
    id  bigint generated by default as identity (start with 1),
    url varchar(255),
    caption character varying(255),
    location  integer not null,
    equipment integer,
    pm_guid character varying(255)
);



--
-- TOC entry 225 (class 1259 OID 64379)
-- Name: playservice_location_accessibility; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_accessibility (
    location   integer not null,
    accessibility  integer not null
);

--
-- TOC entry 226 (class 1259 OID 64382)
-- Name: playservice_location_agecategories; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_agecategories (
    location   integer not null,
    agecategory   integer not null
);

--
-- TOC entry 227 (class 1259 OID 64385)
-- Name: playservice_location_categories; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_categories (
    location  integer not null,
    category  integer not null
);

--
-- TOC entry 228 (class 1259 OID 64388)
-- Name: playservice_location_equipment; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_equipment (
    id  bigint generated by default as identity (start with 1),
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
    geom clob
);

--
-- TOC entry 254 (class 1259 OID 97353)
-- Name: playservice_location_equipment_agecategories; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_equipment_agecategories (
    location_equipment  integer not null,
    agecategory  integer not null
);



--
-- TOC entry 230 (class 1259 OID 64393)
-- Name: playservice_location_facilities; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_location_facilities (
    location  integer not null,
    facility  integer not null
);

--
-- TOC entry 231 (class 1259 OID 64396)
-- Name: playservice_parking_list; Type: TABLE; Schema: public; Owner: playbase
--

CREATE TABLE playservice_parking_list (
    id  bigint generated by default as identity (start with 1),
    parking character varying(255) NOT NULL
);



-- TOC entry 3573 (class 2606 OID 97340)
-- Name: playmapping_type_group playmapping_type_group_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playmapping_type_group
    ADD CONSTRAINT playmapping_type_group_pkey PRIMARY KEY (id);


--
-- TOC entry 3575 (class 2606 OID 64653)
-- Name: playservice_accessibility_list playservice_accessibility_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_accessibility_list
    ADD CONSTRAINT playservice_accessibility_pkey PRIMARY KEY (id);


--
-- TOC entry 3577 (class 2606 OID 64655)
-- Name: playservice_agecategories_list playservice_agecategories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_agecategories_list
    ADD CONSTRAINT playservice_agecategories_pkey PRIMARY KEY (id);


--
-- TOC entry 3579 (class 2606 OID 64657)
-- Name: playservice_categories_list playservice_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_categories_list
    ADD CONSTRAINT playservice_categories_pkey PRIMARY KEY (id);


--
-- TOC entry 3603 (class 2606 OID 97431)
-- Name: playservice_documents playservice_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_documents
    ADD CONSTRAINT playservice_documents_pkey PRIMARY KEY (id);


--
-- TOC entry 3581 (class 2606 OID 64659)
-- Name: playservice_equipment_list playservice_equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_equipment_list
    ADD CONSTRAINT playservice_equipment_pkey PRIMARY KEY (id);


--
-- TOC entry 3583 (class 2606 OID 64661)
-- Name: playservice_facilities_list playservice_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_facilities_list
    ADD CONSTRAINT playservice_facilities_pkey PRIMARY KEY (id);


--
-- TOC entry 3587 (class 2606 OID 64663)
-- Name: playservice_images playservice_images_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_images
    ADD CONSTRAINT playservice_images_pkey PRIMARY KEY (id);


--
-- TOC entry 3589 (class 2606 OID 64665)
-- Name: playservice_location_accessibility playservice_location_accessibility_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_accessibility
    ADD CONSTRAINT playservice_location_accessibility_pkey PRIMARY KEY (location, accessibility);


--
-- TOC entry 3591 (class 2606 OID 64667)
-- Name: playservice_location_agecategories playservice_location_agecategories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_agecategories
    ADD CONSTRAINT playservice_location_agecategories_pkey PRIMARY KEY (location, agecategory);


--
-- TOC entry 3593 (class 2606 OID 64669)
-- Name: playservice_location_categories playservice_location_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_categories
    ADD CONSTRAINT playservice_location_categories_pkey PRIMARY KEY (location, category);


--
-- TOC entry 3601 (class 2606 OID 97357)
-- Name: playservice_location_equipment_agecategories playservice_location_equipment_agecategories_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_equipment_agecategories
    ADD CONSTRAINT playservice_location_equipment_agecategories_pkey PRIMARY KEY (location_equipment, agecategory);


--
-- TOC entry 3595 (class 2606 OID 64671)
-- Name: playservice_location_equipment playservice_location_equipment_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_equipment
    ADD CONSTRAINT playservice_location_equipment_pkey PRIMARY KEY (id);


--
-- TOC entry 3597 (class 2606 OID 64673)
-- Name: playservice_location_facilities playservice_location_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_facilities
    ADD CONSTRAINT playservice_location_facilities_pkey PRIMARY KEY (location, facility);


--
-- TOC entry 3585 (class 2606 OID 64675)
-- Name: playservice_locations playservice_locations_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_locations
    ADD CONSTRAINT playservice_locations_pkey PRIMARY KEY (id);


--
-- TOC entry 3599 (class 2606 OID 64677)
-- Name: playservice_parking_list playservice_parking_pkey; Type: CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_parking_list
    ADD CONSTRAINT playservice_parking_pkey PRIMARY KEY (id);


--
-- TOC entry 3620 (class 2606 OID 97432)
-- Name: playservice_documents playservice_documents_fk; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_documents
    ADD CONSTRAINT playservice_documents_fk FOREIGN KEY (equipment) REFERENCES playservice_location_equipment(id);


--
-- TOC entry 3621 (class 2606 OID 97437)
-- Name: playservice_documents playservice_documents_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_documents
    ADD CONSTRAINT playservice_documents_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3606 (class 2606 OID 97416)
-- Name: playservice_images playserviceimages_equipment_fk; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_images
    ADD CONSTRAINT playserviceimages_equipment_fk FOREIGN KEY (equipment) REFERENCES playservice_location_equipment(id);


--
-- TOC entry 3605 (class 2606 OID 64688)
-- Name: playservice_images playserviceimages_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_images
    ADD CONSTRAINT playserviceimages_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3607 (class 2606 OID 64693)
-- Name: playservice_location_accessibility playservicelocationaccessibility_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_accessibility
    ADD CONSTRAINT playservicelocationaccessibility_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3608 (class 2606 OID 64698)
-- Name: playservice_location_accessibility playservicelocationaccessibility_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_accessibility
    ADD CONSTRAINT playservicelocationaccessibility_fk2 FOREIGN KEY (accessibility) REFERENCES playservice_accessibility_list(id);


--
-- TOC entry 3609 (class 2606 OID 64703)
-- Name: playservice_location_agecategories playservicelocationagecategories_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_agecategories
    ADD CONSTRAINT playservicelocationagecategories_fk1 FOREIGN KEY (agecategory) REFERENCES playservice_agecategories_list(id);



--
-- TOC entry 3610 (class 2606 OID 64708)
-- Name: playservice_location_agecategories playservicelocationagecategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_agecategories
    ADD CONSTRAINT playservicelocationagecategories_fk2 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3611 (class 2606 OID 64713)
-- Name: playservice_location_categories playservicelocationcategories_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_categories
    ADD CONSTRAINT playservicelocationcategories_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3612 (class 2606 OID 64718)
-- Name: playservice_location_categories playservicelocationcategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_categories
    ADD CONSTRAINT playservicelocationcategories_fk2 FOREIGN KEY (category) REFERENCES playservice_categories_list(id);


--
-- TOC entry 3613 (class 2606 OID 64723)
-- Name: playservice_location_equipment playservicelocationequipment_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_equipment
    ADD CONSTRAINT playservicelocationequipment_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3614 (class 2606 OID 64728)
-- Name: playservice_location_equipment playservicelocationequipment_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_equipment
    ADD CONSTRAINT playservicelocationequipment_fk2 FOREIGN KEY (equipment) REFERENCES playservice_equipment_list(id);


--
-- TOC entry 3619 (class 2606 OID 97363)
-- Name: playservice_location_equipment_agecategories playservicelocationequipmentagecategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_equipment_agecategories
    ADD CONSTRAINT playservicelocationequipmentagecategories_fk2 FOREIGN KEY (location_equipment) REFERENCES playservice_location_equipment(id);


--
-- TOC entry 3615 (class 2606 OID 97348)
-- Name: playservice_location_equipment playservicelocationequipmenttype_fk; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_equipment
    ADD CONSTRAINT playservicelocationequipmenttype_fk FOREIGN KEY (type_) REFERENCES playmapping_type_group(id);


--
-- TOC entry 3616 (class 2606 OID 64733)
-- Name: playservice_location_facilities playservicelocationfacilities_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_facilities
    ADD CONSTRAINT playservicelocationfacilities_fk1 FOREIGN KEY (location) REFERENCES playservice_locations(id);


--
-- TOC entry 3617 (class 2606 OID 64738)
-- Name: playservice_location_facilities playservicelocationfacilities_fk2; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_location_facilities
    ADD CONSTRAINT playservicelocationfacilities_fk2 FOREIGN KEY (facility) REFERENCES playservice_facilities_list(id);


--
-- TOC entry 3604 (class 2606 OID 64743)
-- Name: playservice_locations playservicelocations_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_locations
    ADD CONSTRAINT playservicelocations_fk1 FOREIGN KEY (parking) REFERENCES playservice_parking_list(id);


-- Completed on 2017-06-01 16:20:11 CEST

--
-- PostgreSQL database dump complete
--

