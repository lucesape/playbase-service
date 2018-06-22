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

import java.util.Date;

/**
 *
 * @author Meine Toonen
 */
public class Project {
    private Integer id;
    private ProjectType type_;
    private String cronexpressie;
    private String username;
    private String password;
    private String name;
    private String authkey;
    private String baseurl;
    private String imagepath;
    private Date lastrun;
    private String log;
    private String importedstring;
    private String mailaddress;
    private Status status;

    public Project() {
    }

    
    public Project(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCronexpressie() {
        return cronexpressie;
    }

    public void setCronexpressie(String cronexpressie) {
        this.cronexpressie = cronexpressie;
    }

    public ProjectType getType_() {
        return type_;
    }

    public void setType_(ProjectType type_) {
        this.type_ = type_;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getLastrun() {
        return lastrun;
    }

    public void setLastrun(Date lastrun) {
        this.lastrun = lastrun;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getImportedstring() {
        return importedstring;
    }

    public void setImportedstring(String importedstring) {
        this.importedstring = importedstring;
    }

    public String getMailaddress() {
        return mailaddress;
    }

    public void setMailaddress(String mailaddress) {
        this.mailaddress = mailaddress;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public String getAuthkey() {
        return authkey;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
