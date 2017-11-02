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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Meine Toonen
 */
public class ImportReport {

    private final Map<ImportType, String> importedstring = new HashMap<>();
    private final Map<ImportType, Set<String>> errorsMessages = new HashMap<>();
    private final Map<ImportType, Integer> errors = new HashMap<>();
    private final Map<ImportType, Integer> inserted = new HashMap<>();
    private final Map<ImportType, Integer> updated = new HashMap<>();

    public ImportReport() {
        for (ImportType type : ImportType.values()) {
            errorsMessages.put(type, new HashSet<>());
            inserted.put(type, 0);
            updated.put(type, 0);
            errors.put(type, 0);
        }
    }

    public int getNumberInserted(ImportType type) {
        return inserted.get(type);
    }

    public int getNumberUpdated(ImportType type) {
        return updated.get(type);
    }

    public void increaseUpdated(ImportType type) {
        updated.put(type, updated.get(type) + 1);
    }

    public void increaseInserted(ImportType type) {
        inserted.put(type, inserted.get(type) + 1);
    }

    public void addError(String message, ImportType type) {
        errors.put(type, errors.get(type) + 1);
        this.errorsMessages.get(type).add(message);
    }

    public Set<String> getErrors(ImportType type) {
        return errorsMessages.get(type);
    }

    public Map<ImportType, Set<String>> getAllErrors() {
        return errorsMessages;
    }

    public int getNumErrors(ImportType type) {
        return errors.get(type);
    }

    public List<String> getErrors() {
        List<String> allErrors = new ArrayList<>();
        for (ImportType importType : errorsMessages.keySet()) {
            allErrors.addAll(errorsMessages.get(importType));
        }
        return allErrors;
    }

    public Map<ImportType, String> getImportedstring() {
        return importedstring;
    }

    public void setImportedstring(ImportType type, String importedstring) {
        this.importedstring.put(type, importedstring);
    }

    public String toLog() {
        String ls = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("Import on ");
        sb.append(new Date());
        sb.append(ls);
        sb.append("*******************");

        sb.append(ls);
        sb.append("Er zijn ").append(getNumberInserted(ImportType.ASSET)).append(" ").append(ImportType.ASSET.toString()).append(" weggeschreven.");
        sb.append(ls);
        sb.append("Er zijn ").append(getNumberInserted(ImportType.LOCATION)).append(" ").append(ImportType.LOCATION.toString()).append(" weggeschreven.");
        sb.append(ls);
        sb.append("Er zijn ").append(getNumberUpdated(ImportType.ASSET)).append(" ").append(ImportType.ASSET.toString()).append(" geupdatet.");
        sb.append(ls);
        sb.append("Er zijn ").append(getNumberUpdated(ImportType.LOCATION)).append(" ").append(ImportType.LOCATION.toString()).append(" geupdatet.");
        sb.append(ls);

        if (getErrors().size() > 0) {
            sb.append("Er zijn ").append(getErrors(ImportType.ASSET).size()).append(" ").append(ImportType.ASSET.toString()).append(" mislukt:");
            sb.append(ls);
            sb.append("Er zijn ").append(getErrors(ImportType.LOCATION).size()).append(" ").append(ImportType.LOCATION.toString()).append(" mislukt:");
            sb.append(ls);

            for (ImportType importType : getAllErrors().keySet()) {
                Set<String> errs = getAllErrors().get(importType);
                for (String error : errs) {
                    sb.append(importType.toString()).append(": ").append(error);
                    sb.append(ls);
                }
            }
        }

        return sb.toString();
    }

    public enum ImportType {

        GENERAL("Algemeen"),
        LOCATION("Locatie"),
        ASSET("Asset"),
        COMMENT("Commentaar");

        private final String type;

        ImportType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}
