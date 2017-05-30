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
package nl.b3p.playbase;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Meine Toonen
 */
public class ImportReport {
    private int numberInserted;
    private int numberUpdated;
    private String type;
    
    private List<String> errors;

    public ImportReport(String type) {
        numberInserted = 0;
        numberUpdated = 0;
        errors = new ArrayList<>();
        this.type = type;
    }
    
    public int getNumberInserted() {
        return numberInserted;
    }

    public void setNumberInserted(int numberInserted) {
        this.numberInserted = numberInserted;
    }

    public int getNumberUpdated() {
        return numberUpdated;
    }

    public void setNumberUpdated(int numberUpdated) {
        this.numberUpdated = numberUpdated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void increaseUpdated(){
        this.numberUpdated++;
    }
    
    public void increaseInserted(){
        this.numberInserted++;
    }
    
    public void addError(String message){
        this.errors.add(message);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    
}
