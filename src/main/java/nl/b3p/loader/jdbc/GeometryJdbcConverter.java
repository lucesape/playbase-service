
package nl.b3p.loader.jdbc;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.sql.SQLException;

/**
 *
 * @author Matthijs Laan
 */
public abstract class GeometryJdbcConverter {
    
    protected GeometryFactory gf = new GeometryFactory();
    protected final WKTReader wkt= new WKTReader();
    //definieer placeholder als ? wanneer object naar native geometry wordt 
    //geconverteerd
    //defineer placeholder via native wkt-import functie als geometry als 
    //wkt-string wordt doorgegeven
    public abstract Object convertToNativeGeometryObject(Geometry param) throws SQLException, ParseException;
    public abstract Object convertToNativeGeometryObject(Geometry param, int srid) throws SQLException, ParseException;
    public abstract Geometry convertToJTSGeometryObject(Object nativeObj);
    public abstract String createPSGeometryPlaceholder() throws SQLException;
    public abstract boolean isPmdKnownBroken();
    
    public abstract String getGeomTypeName();
    
    public Object createNativePoint(double lat, double lon, int srid) throws SQLException, ParseException{
        if(lat == 0 || lon == 0){
            return null;
        }
        Point p = gf.createPoint(new Coordinate(lon, lat));
        return convertToNativeGeometryObject(p, srid);
    }

}
