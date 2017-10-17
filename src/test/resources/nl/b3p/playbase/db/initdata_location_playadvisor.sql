--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

-- Started on 2017-07-07 15:56:25 CEST


INSERT INTO playservice_locations_playadvisor (id, title, pa_title,pa_content, summary, street, number, numberextra, postalcode, municipality, area, country, website, email, phone, longitude, latitude, pm_guid, pa_id, parking, geom) VALUES (1, 'Speeltuin Assendorp','Speeltuin Assendorp', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '10952', NULL, NULL);

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

-- Started on 2017-07-10 10:04:38 CEST
--
-- TOC entry 3756 (class 0 OID 141353)
-- Dependencies: 268
-- Data for Name: playservice_images_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--
INSERT INTO playservice_location_equipment_playadvisor (id, location, equipment, pa_guid, longitude, latitude, type_, installeddate, pm_guid, name, priceindexation, priceinstallation, pricemaintenance, pricepurchase, pricereinvestment, depth, 
	width, height, endoflifeyear, freefallheight, safetyzonelength, safetyzonewidth, manufacturer, material, product, productid, productvariantid, serialnumber, geom) VALUES (1, 1, NULL, NULL, 0, 0, NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


INSERT INTO playservice_images_playadvisor (id, url, caption, location, equipment, pm_guid) VALUES (1, '', '', 1, NULL, NULL);
INSERT INTO playservice_images_playadvisor (id, url, caption, location, equipment, pm_guid) VALUES (2, 'http://playadvisor.b3p.nl/wp-content/uploads/2016/04/001.jpg', NULL, 1, NULL, NULL);
INSERT INTO playservice_images_playadvisor (id, url, caption, location, equipment, pm_guid) VALUES (3, 'http://playadvisor.b3p.nl/wp-content/uploads/2016/04/006.jpg', NULL, 1, NULL, NULL);


--
-- TOC entry 3765 (class 0 OID 0)
-- Dependencies: 267
-- Name: playservice_images_playadvisor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: playbase
--



--
-- TOC entry 3757 (class 0 OID 141372)
-- Dependencies: 269
-- Data for Name: playservice_location_accessibility_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

INSERT INTO playservice_location_accessibility_playadvisor (location, accessibility) VALUES (1, 4);


--
-- TOC entry 3758 (class 0 OID 141387)
-- Dependencies: 270
-- Data for Name: playservice_location_agecategories_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

INSERT INTO playservice_location_agecategories_playadvisor (location, agecategory) VALUES (1, 1);
INSERT INTO playservice_location_agecategories_playadvisor (location, agecategory) VALUES (1, 2);


--
-- TOC entry 3759 (class 0 OID 141402)
-- Dependencies: 271
-- Data for Name: playservice_location_categories_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

INSERT INTO playservice_location_categories_playadvisor (location, category) VALUES (1, 17);
INSERT INTO playservice_location_categories_playadvisor (location, category) VALUES (1, 23);



--
-- TOC entry 3753 (class 0 OID 141300)
-- Dependencies: 263
-- Data for Name: playservice_location_equipment_agecategories_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

INSERT INTO playservice_location_equipment_agecategories_playadvisor (location_equipment, agecategory) VALUES (1, 1);



--
-- TOC entry 3754 (class 0 OID 141315)
-- Dependencies: 264
-- Data for Name: playservice_location_facilities_playadvisor; Type: TABLE DATA; Schema: public; Owner: playbase
--

INSERT INTO playservice_location_facilities_playadvisor (location, facility) VALUES (1, 10);
INSERT INTO playservice_location_facilities_playadvisor (location, facility) VALUES (1, 7);
