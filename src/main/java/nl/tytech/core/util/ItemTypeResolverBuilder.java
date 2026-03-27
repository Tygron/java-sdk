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
package nl.tytech.core.util;

import java.io.IOException;
import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nl.tytech.core.structure.ItemNamespace;

/**
 *
 * Type resolver that behaves as a normal class resolver except for known Tygron Item. They use the same naming as ItemNameSpace.
 *
 * @author Maxim Knepfle
 */
public class ItemTypeResolverBuilder extends DefaultTypeResolverBuilder {

    public class ItemTypeIdResolver extends ClassNameIdResolver {

        private JavaType baseType;

        public ItemTypeIdResolver(JavaType baseType, TypeFactory factory) {
            super(baseType, factory, LaissezFaireSubTypeValidator.instance);
            this.baseType = baseType;
        }

        @Override
        public String idFromValue(Object value) {

            if (ItemNamespace.containsClass(value.getClass())) {
                return ItemNamespace.getSimpleName(value.getClass());
            }
            return super.idFromValue(value);
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> type) {

            if (ItemNamespace.containsClass(type)) {
                return ItemNamespace.getSimpleName(type);
            }
            return super.idFromValueAndType(value, type);
        }

        @Override
        public JavaType typeFromId(DatabindContext context, String simpleName) throws IOException {

            if (ItemNamespace.containsSimpleName(simpleName)) {
                Class<?> classz = ItemNamespace.getClass(simpleName);
                return this._typeFactory.constructSpecializedType(baseType, classz);
            }
            return super.typeFromId(context, simpleName);
        }
    }

    private static final long serialVersionUID = -11025605395210958L;

    /**
     * Constructor with custom configuration included
     */
    public ItemTypeResolverBuilder() {

        super(DefaultTyping.NON_CONCRETE_AND_ARRAYS, LaissezFaireSubTypeValidator.instance);

        init(JsonTypeInfo.Id.CLASS, null);

        inclusion(As.WRAPPER_OBJECT);
    }

    @Override
    protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator subtypeValidator,
            Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
        return new ItemTypeIdResolver(baseType, config.getTypeFactory());
    }
}
