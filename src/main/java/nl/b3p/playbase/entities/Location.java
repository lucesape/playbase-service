/*
 * Copyright (C) 2017 B3Partners B.V.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.b3p.playbase.entities;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Meine Toonen
 */
public class Location {

    private Integer id;
    private String title;
    private String pa_title;
    private String content;
    private String summary;
    private String street;
    private String number;
    private String numberextra;
    private String postalcode;
    private String municipality;
    private String area;
    private String country;
    private String website;
    private String email;
    private String phone;
    private Double longitude = null;
    private Double latitude = null;
    private String pm_guid;
    private String pa_id;
    private Integer parking;
    private Integer averagerating;
    private Geometry geom;
    private List<Map<String, Object>> images = new ArrayList<>();
    private List<Map<String, Object>> documents = new ArrayList<>();
    private Integer[] agecategories = new Integer[0];

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNumberextra() {
        return numberextra;
    }

    public void setNumberextra(String numberextra) {
        this.numberextra = numberextra;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getPm_guid() {
        return pm_guid;
    }

    public void setPm_guid(String pm_guid) {
        this.pm_guid = pm_guid;
    }

    public String getPa_id() {
        return pa_id;
    }

    public void setPa_id(String pa_id) {
        this.pa_id = pa_id;
    }

    public Integer getParking() {
        return parking;
    }

    public void setParking(Integer parking) {
        this.parking = parking;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    public List<Map<String, Object>> getImages() {
        return images;
    }

    public void setImages(List<Map<String, Object>> images) {
        this.images = images;
    }

    public Integer[] getAgecategories() {
        return agecategories;
    }

    public void setAgecategories(Integer[] agecategories) {
        this.agecategories = agecategories;
    }

    public List<Map<String, Object>> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Map<String, Object>> documents) {
        this.documents = documents;
    }

    public Integer getAveragerating() {
        return averagerating;
    }

    public void setAveragerating(Integer averagerating) {
        this.averagerating = averagerating;
    }

    public String getPa_title() {
        return pa_title;
    }

    public void setPa_title(String pa_title) {
        this.pa_title = pa_title;
    }
    
}
