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

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Meine Toonen
 */
public enum Status {
    
    @SerializedName("Gepubliceerd")
    PUBLISHED("Gepubliceerd"),
    @SerializedName("In behandeling")
    UNDER_REVIEW("In behandeling"),
    @SerializedName("Niet gepubliceerd")
    UNPUBLISHED("Niet gepubliceerd");
    
    private String type;
    
    Status(String type) {
        this.type = type;
    }
    
    @Override
    public String toString(){
        return type;
    }
    
    public boolean equals(String s){
        return false;
    }
}
