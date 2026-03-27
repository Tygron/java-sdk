/*******************************************************************************************************************************************
 * Copyright 2006-2026 TyTech B.V., Lange Vijverberg 4, 2513 AC, The Hague, The Netherlands. All rights reserved under the copyright laws of
 * The Netherlands and applicable international laws, treaties, and conventions. TyTech B.V. is a subsidiary company of Tygron Group B.V..
 *
 * This software is proprietary information of TyTech B.V.. You may freely redistribute and use this SDK code, with or without modification,
 * provided you include the original copyright notice and use it in compliance with your Tygron Platform License Agreement.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************************************************************************/
package nl.tytech.util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import com.bedatadriven.jackson.datatype.jts.GeometryDeserializer;
import com.bedatadriven.jackson.datatype.jts.GeometrySerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import nl.tytech.core.structure.ItemNamespace;
import nl.tytech.util.RestManager.Format;

/**
 * Maps object to different JSON formats
 *
 * @author Maxim Knepfle
 */
public class JsonMapper {

    private static final Value IGNORE_DEFAULTS = Value.construct(Include.NON_DEFAULT, Include.NON_DEFAULT);

    private static DefaultTypeResolverBuilder resolver = null;

    private static final ObjectMapper[] localMappers = new ObjectMapper[Format.values().length];

    private static final SimpleModule deserialModule = new SimpleModule();

    static {
        deserialModule.addDeserializer(Geometry.class, new GeometryDeserializer<>(Geometry.class));
        deserialModule.addDeserializer(Point.class, new GeometryDeserializer<>(Point.class));
        deserialModule.addDeserializer(LineString.class, new GeometryDeserializer<>(LineString.class));
        deserialModule.addDeserializer(MultiLineString.class, new GeometryDeserializer<>(MultiLineString.class));
        deserialModule.addDeserializer(Polygon.class, new GeometryDeserializer<>(Polygon.class));
        deserialModule.addDeserializer(MultiPolygon.class, new GeometryDeserializer<>(MultiPolygon.class));
        deserialModule.addDeserializer(GeometryCollection.class, new GeometryDeserializer<>(GeometryCollection.class));

        SimpleModule serialModule = new SimpleModule();
        serialModule.addSerializer(Geometry.class, new GeometrySerializer<>(Geometry.class));
        serialModule.addSerializer(Point.class, new GeometrySerializer<>(Point.class));
        serialModule.addSerializer(LineString.class, new GeometrySerializer<>(LineString.class));
        serialModule.addSerializer(MultiLineString.class, new GeometrySerializer<>(MultiLineString.class));
        serialModule.addSerializer(Polygon.class, new GeometrySerializer<>(Polygon.class));
        serialModule.addSerializer(MultiPolygon.class, new GeometrySerializer<>(MultiPolygon.class));
        serialModule.addSerializer(GeometryCollection.class, new GeometrySerializer<>(GeometryCollection.class));

        for (Format format : Format.values()) {
            localMappers[format.ordinal()] = createMapper(format, serialModule);
        }
    }

    public static final ObjectMapper createMapper(Format format, SimpleModule serialModule) {

        ObjectMapper mapper = new ObjectMapper(format.isSmile() ? new SmileFactory() : null);
        mapper.enable(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);

        if (format.isPrettified()) {
            mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        /**
         * For typed format (linked to JAVA types) ignore default values. These are already set by the Java Object
         */
        if (format.isTyped()) {
            JacksonAnnotationIntrospector jsonIntrospector = new JacksonAnnotationIntrospector() {

                private static final long serialVersionUID = 7126909959294625550L;

                @Override
                public Value findPropertyInclusion(Annotated ann) {
                    // keep values for map, others ignore default
                    if (ann.getType().isMapLikeType()) {
                        return super.findPropertyInclusion(ann);
                    } else {
                        return IGNORE_DEFAULTS;
                    }
                }
            };
            mapper.setAnnotationIntrospector(jsonIntrospector);

            if (resolver != null) {
                mapper.setDefaultTyping(resolver);
            }
        }

        mapper.registerModule(serialModule);
        mapper.registerModule(deserialModule);
        return mapper;
    }

    public static final ObjectMapper getLocalMapper(Format format) {
        return localMappers[format.ordinal()];
    }

    public static final void setDefaultTyping(DefaultTypeResolverBuilder r) {

        // set for new mappers
        resolver = r;

        // also set for default local mappers
        for (Format format : Format.values()) {
            localMappers[format.ordinal()].registerSubtypes(ItemNamespace.getClasses());
            if (format.isTyped()) {
                localMappers[format.ordinal()].setDefaultTyping(resolver);
            }
        }
    }
}
