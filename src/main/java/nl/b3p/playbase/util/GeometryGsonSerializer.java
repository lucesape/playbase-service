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
package nl.b3p.playbase.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.vividsolutions.jts.geom.Geometry;
import java.lang.reflect.Type;

/**
 *
 * @author Meine Toonen
 */
public class GeometryGsonSerializer implements JsonSerializer<Geometry> {

    /**
     *
     * @param src Source geometry
     * @param typeOfSrc Type of geometry
     * @param context context 
     * @return JSON element of the geometry
     */
    @Override
    public JsonElement serialize(Geometry src, Type typeOfSrc, JsonSerializationContext context) {
         return new JsonPrimitive(src.toString());
    }
}
