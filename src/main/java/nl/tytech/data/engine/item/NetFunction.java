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
package nl.tytech.data.engine.item;

import java.util.Map;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.other.NetItem;
import nl.tytech.util.StringUtils;

/**
 * Defines a Net Line's cost, diameter, etc.
 *
 * @author Maxim Knepfle
 */
public class NetFunction extends SourcedAttributeItem implements NetItem {

    public enum NetLineFunctionAttribute implements ReservedAttribute {

        PRICE_M(Double.class),

        VISUALIZATION_DIAMETER_M(Double.class),

        DIAMETER_M(Double.class),

        COLOR(Double.class),

        ;

        public static final NetLineFunctionAttribute[] VALUES = values();
        private final Class<?> type;

        private NetLineFunctionAttribute(Class<?> type) {
            this.type = type;
        }

        @Override
        public double[] defaultArray() {
            return AttributeItem.ZERO; // always zero
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        public double[] defaultValue(NetType netType) {

            switch (this) {
                case COLOR:
                    return netType.getDefaultColor().toArray();
                case VISUALIZATION_DIAMETER_M:
                    return new double[] { 10.0 * netType.getDefaultDiameter() };
                case DIAMETER_M:
                    return new double[] { netType.getDefaultDiameter() };
                case PRICE_M:
                    return new double[] { netType.getDefaultPrice() };
                default:
                    return AttributeItem.EMPTY;
            }
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    private static final long serialVersionUID = -6091749457747835078L;

    @XMLValue
    private Double priceM = null;

    @XMLValue
    private Double diameterM = null;

    @XMLValue
    private NetType netType = null;

    @XMLValue
    private String groupName = StringUtils.EMPTY;

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return NetLineFunctionAttribute.values();
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {
        Map<String, Object> map = super.getExportAttributes(inherited);
        map.put(NetType.NET_TYPE, getNetType().name());
        return map;
    }

    public String getGroupName() {
        return groupName;
    }

    public NetType getNetType() {
        return netType;
    }

    @Override
    public boolean isPartOf(NetType netType) {
        return this.netType == netType;
    }

    public void setGroupName(String heatGroup) {
        this.groupName = heatGroup;
    }

    public void setNetType(NetType netType) {
        this.netType = netType;
    }

    @Override
    public String validated(boolean startNewSession) {

        if (!hasAttribute(NetLineFunctionAttribute.PRICE_M) && this.priceM == null) {
            this.priceM = 1d;
        }
        if (this.priceM != null) {
            this.setAttribute(NetLineFunctionAttribute.PRICE_M, priceM);
            this.priceM = null;
        }

        if (!hasAttribute(NetLineFunctionAttribute.DIAMETER_M) && this.diameterM == null) {
            this.diameterM = 1d;
        }
        if (this.diameterM != null) {
            this.setAttribute(NetLineFunctionAttribute.DIAMETER_M, diameterM);
            this.diameterM = null;
        }
        if (this.netType == null) {
            this.netType = NetType.HEAT;
        }
        return StringUtils.EMPTY;
    }
}
