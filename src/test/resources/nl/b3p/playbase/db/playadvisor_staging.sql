

CREATE TABLE playservice_locations_playadvisor
(
  id int generated by default as identity (start with 1),
  title character varying(255) NOT NULL,
  content character varying(255),
  summary character varying(255),
  street character varying(255),
  "number" character varying(255),
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
  geom clob,
  CONSTRAINT playservice_locationsplayadvisor_pkey PRIMARY KEY (id),
  CONSTRAINT playservicelocationsplayadvisor_fk1 FOREIGN KEY (parking)
      REFERENCES playservice_parking_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);



--
-- TOC entry 223 (class 1259 OID 64371)
-- Name: playservice_images; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_images_playadvisor (
    id int generated by default as identity (start with 1),
    url character varying(255),
    caption character varying(255),
    location integer NOT NULL,
    pm_guid character varying(255),
    equipment integer
);


--
-- TOC entry 225 (class 1259 OID 64379)
-- Name: playservice_location_accessibility; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_location_accessibility_playadvisor (
    location integer NOT NULL,
    accessibility integer NOT NULL
);


--
-- TOC entry 226 (class 1259 OID 64382)
-- Name: playservice_location_agecategories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_location_agecategories_playadvisor (
    location integer NOT NULL,
    agecategory integer NOT NULL
);


--
-- TOC entry 227 (class 1259 OID 64385)
-- Name: playservice_location_categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_location_categories_playadvisor (
    location integer NOT NULL,
    category integer NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 64388)
-- Name: playservice_location_equipment; Type: TABLE; Schema: public; Owner: -
--


CREATE TABLE playservice_location_equipment_playadvisor 
(
  id int generated by default as identity (start with 1),
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
  geom clob,
  CONSTRAINT playservice_location_equipment_playadvisor_pkey PRIMARY KEY (id),
  CONSTRAINT playservicelocationequipment_playadvisor_fk1 FOREIGN KEY (location)
      REFERENCES playservice_locations_playadvisor  (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationequipment_playadvisor_fk2 FOREIGN KEY (equipment)
      REFERENCES playservice_equipment_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationequipmenttype_playadvisor_fk FOREIGN KEY (type_)
      REFERENCES playmapping_type_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


--
-- TOC entry 230 (class 1259 OID 64393)
-- Name: playservice_location_facilities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE playservice_location_facilities_playadvisor (
    location integer NOT NULL,
    facility integer NOT NULL
);


-- Table: public.playservice_location_equipment_agecategories

-- DROP TABLE public.playservice_location_equipment_agecategories;

CREATE TABLE public.playservice_location_equipment_agecategories_playadvisor
(
  location_equipment integer NOT NULL,
  agecategory integer NOT NULL,
  CONSTRAINT playservice_location_equipment_agecategories_playadvisor_pkey PRIMARY KEY (location_equipment, agecategory),
  CONSTRAINT playservicelocationagecategories_playadvisor_fk1 FOREIGN KEY (agecategory)
      REFERENCES public.playservice_agecategories_list (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT playservicelocationequipmentagecategories_playadvisor_fk2 FOREIGN KEY (location_equipment)
      REFERENCES public.playservice_location_equipment_playadvisor (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);


CREATE TABLE playservice_documents_playadvisor (
    id int generated by default as identity (start with 1),
    url varchar(255),
    caption character varying(255),
    location  integer not null,
    equipment integer,
    pm_guid character varying(255)
);

ALTER table playservice_documents_playadvisor
    ADD CONSTRAINT playservice_documents_playadvisor_pkey PRIMARY KEY (id);



ALTER table playservice_documents_playadvisor
    ADD CONSTRAINT playservice_documents_playadvisor_fk FOREIGN KEY (equipment) REFERENCES playservice_location_equipment_playadvisor(id);


--
-- TOC entry 3621 (class 2606 OID 97437)
-- Name: playservice_documents playservice_documents_fk1; Type: FK CONSTRAINT; Schema: public; Owner: playbase
--

ALTER table playservice_documents_playadvisor
    ADD CONSTRAINT playservice_documents_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3609 (class 2604 OID 64451)
-- Name: playservice_images id; Type: DEFAULT; Schema: public; Owner: -
--


--
-- TOC entry 3608 (class 2604 OID 64453)
-- Name: playservice_locations id; Type: DEFAULT; Schema: public; Owner: -



ALTER TABLE playservice_images_playadvisor
    ADD CONSTRAINT playservice_images_playadvisor_pkey PRIMARY KEY (id);



--
-- TOC entry 3643 (class 2606 OID 64665)
-- Name: playservice_location_accessibility playservice_location_accessibility_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE playservice_location_accessibility_playadvisor
    ADD CONSTRAINT playservice_location_accessibility_playadvisor_pkey PRIMARY KEY (location, accessibility);


--
-- TOC entry 3645 (class 2606 OID 64667)
-- Name: playservice_location_agecategories playservice_location_agecategories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE playservice_location_agecategories_playadvisor
    ADD CONSTRAINT playservice_location_agecategories_playadvisor_pkey PRIMARY KEY (location, agecategory);


--
-- TOC entry 3647 (class 2606 OID 64669)
-- Name: playservice_location_categories playservice_location_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE playservice_location_categories_playadvisor
    ADD CONSTRAINT playservice_location_categories_playadvisor_pkey PRIMARY KEY (location, category);




--
-- TOC entry 3651 (class 2606 OID 64673)
-- Name: playservice_location_facilities playservice_location_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE playservice_location_facilities_playadvisor
    ADD CONSTRAINT playservice_location_facilities_playadvisor_pkey PRIMARY KEY (location, facility);


--
-- TOC entry 3639 (class 2606 OID 64675)
-- Name: playservice_locations playservice_locations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



ALTER TABLE playservice_images_playadvisor
    ADD CONSTRAINT playserviceimages_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3672 (class 2606 OID 64693)
-- Name: playservice_location_accessibility playservicelocationaccessibility_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE playservice_location_accessibility_playadvisor
    ADD CONSTRAINT playservicelocationaccessibility_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);




ALTER TABLE playservice_location_accessibility_playadvisor
    ADD CONSTRAINT playservicelocationaccessibility_playadvisor_fk2 FOREIGN KEY (accessibility) REFERENCES playservice_accessibility_list(id);

--
-- TOC entry 3675 (class 2606 OID 64708)
-- Name: playservice_location_agecategories playservicelocationagecategories_fk2; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE playservice_location_agecategories_playadvisor
    ADD CONSTRAINT playservicelocationagecategories_playadvisor_fk2 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);


--
-- TOC entry 3676 (class 2606 OID 64713)
-- Name: playservice_location_categories playservicelocationcategories_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE playservice_location_categories_playadvisor
    ADD CONSTRAINT playservicelocationcategories_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);



--
-- TOC entry 3680 (class 2606 OID 64733)
-- Name: playservice_location_facilities playservicelocationfacilities_fk1; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE playservice_location_facilities_playadvisor
    ADD CONSTRAINT playservicelocationfacilities_playadvisor_fk1 FOREIGN KEY (location) REFERENCES playservice_locations_playadvisor(id);





ALTER TABLE playservice_location_categories_playadvisor
    ADD CONSTRAINT playservicelocationcategories_playadvisor_fk2 FOREIGN KEY (category) REFERENCES playservice_categories_list(id);

    
ALTER TABLE playservice_location_facilities_playadvisor
    ADD CONSTRAINT playservicelocationfacilities_playadvisor_fk2 FOREIGN KEY (facility) REFERENCES playservice_facilities_list(id);