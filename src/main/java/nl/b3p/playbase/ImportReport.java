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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Meine Toonen
 */
public class ImportReport {

    public enum ImportType{
        
        LOCATION ("Locatie"),
        ASSET("Asset");
        
        private final String type;
        
        ImportType(String type){
            this.type = type;
        }
        
        @Override
        public String toString(){
            return type;
        }
    }
    
    private Map<ImportType,List<String>> errors = new HashMap<>();
    private Map<ImportType, Integer> inserted = new HashMap<>();
    private Map<ImportType, Integer> updated = new HashMap<>();

    public ImportReport() {
        for (ImportType type : ImportType.values()) {
            errors.put(type, new ArrayList<String>());
            inserted.put(type,0);
            updated.put(type,0);
        }
    }
    
    public int getNumberInserted(ImportType type) {
        return inserted.get(type);
    }


    public int getNumberUpdated(ImportType type) {
        return updated.get(type);
    }
    
    public void increaseUpdated(ImportType type){
        updated.put(type, updated.get(type) +1);
    }
    
    public void increaseInserted(ImportType type){
        inserted.put(type, inserted.get(type) +1);
    }
    
    public void addError(String message,ImportType type){
        this.errors.get(type).add(message);
    }

    public List<String> getErrors(ImportType type) {
        return errors.get(type);
    }
    
    public Map<ImportType, List<String>> getAllErrors(){
        return errors;
    }
    
    
    public List<String> getErrors(){
        List<String> allErrors = new ArrayList<>();
        for (ImportType importType : errors.keySet()) {
            allErrors.addAll(errors.get(importType));
        }
        return allErrors;
    }
    
    

   /* public void setErrors(List<String> errors) {
        this.errors = errors;
    }*/
    
    
}
