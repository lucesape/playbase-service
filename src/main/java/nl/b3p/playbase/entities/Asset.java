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
import java.util.List;
import java.util.Map;

/**
 *
 * @author Meine Toonen
 */
public class Asset {

    private Integer id;
    private Integer location;
    private Integer equipment;
    private String pa_guid;
    private double longitude;
    private double latitude;
    private Integer type_;
    private String installeddate;
    private String pm_guid;
    private String name;
    private Integer priceindexation;
    private Integer priceinstallation;
    private Integer pricemaintenance;
    private Integer pricepurchase;
    private Integer pricereinvestment;
    private Integer depth;
    private Integer width;
    private Integer height;
    private Integer endoflifeyear;
    private Integer freefallheight;
    private Integer safetyzonelength;
    private Integer safetyzonewidth;
    private String manufacturer;
    private String material;
    private String product;
    private String productid;
    private String productvariantid;
    private String serialnumber;
    private Geometry geom;
    private Integer[] agecategories = new Integer[0];

    private List<Map<String, Object>> images;
    private List<Map<String, Object>> documents;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public Integer getEquipment() {
        return equipment;
    }

    public void setEquipment(Integer equipment) {
        this.equipment = equipment;
    }

    public String getPa_guid() {
        return pa_guid;
    }

    public void setPa_guid(String pa_guid) {
        this.pa_guid = pa_guid;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Integer getType_() {
        return type_;
    }

    public void setType_(Integer type_) {
        this.type_ = type_;
    }

    public String getInstalleddate() {
        return installeddate;
    }

    public void setInstalleddate(String installeddate) {
        this.installeddate = installeddate;
    }

    public String getPm_guid() {
        return pm_guid;
    }

    public void setPm_guid(String pm_guid) {
        this.pm_guid = pm_guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriceindexation() {
        return priceindexation;
    }

    public void setPriceindexation(Integer priceindexation) {
        this.priceindexation = priceindexation;
    }

    public Integer getPriceinstallation() {
        return priceinstallation;
    }

    public void setPriceinstallation(Integer priceinstallation) {
        this.priceinstallation = priceinstallation;
    }

    public Integer getPricemaintenance() {
        return pricemaintenance;
    }

    public void setPricemaintenance(Integer pricemaintenance) {
        this.pricemaintenance = pricemaintenance;
    }

    public Integer getPricepurchase() {
        return pricepurchase;
    }

    public void setPricepurchase(Integer pricepurchase) {
        this.pricepurchase = pricepurchase;
    }

    public Integer getPricereinvestment() {
        return pricereinvestment;
    }

    public void setPricereinvestment(Integer pricereinvestment) {
        this.pricereinvestment = pricereinvestment;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getEndoflifeyear() {
        return endoflifeyear;
    }

    public void setEndoflifeyear(Integer endoflifeyear) {
        this.endoflifeyear = endoflifeyear;
    }

    public Integer getFreefallheight() {
        return freefallheight;
    }

    public void setFreefallheight(Integer freefallheight) {
        this.freefallheight = freefallheight;
    }

    public Integer getSafetyzonelength() {
        return safetyzonelength;
    }

    public void setSafetyzonelength(Integer safetyzonelength) {
        this.safetyzonelength = safetyzonelength;
    }

    public Integer getSafetyzonewidth() {
        return safetyzonewidth;
    }

    public void setSafetyzonewidth(Integer safetyzonewidth) {
        this.safetyzonewidth = safetyzonewidth;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductvariantid() {
        return productvariantid;
    }

    public void setProductvariantid(String productvariantid) {
        this.productvariantid = productvariantid;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    public Integer[] getAgecategories() {
        return agecategories;
    }

    public void setAgecategories(Integer[] agecategories) {
        this.agecategories = agecategories;
    }

    public List<Map<String, Object>> getImages() {
        return images;
    }

    public void setImages(List<Map<String, Object>> images) {
        this.images = images;
    }

    public List<Map<String, Object>> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Map<String, Object>> documents) {
        this.documents = documents;
    }

    
}
