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

import nl.tytech.core.net.serializable.SettingType;
import nl.tytech.data.core.item.AbstractSetting;
import nl.tytech.data.engine.item.NetLine.NetLineAttribute;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.other.ActiveItem;

/**
 * NetSetting: Setting related to networks
 *
 * @author Maxim Knepfle
 */
public class NetSetting extends AbstractSetting<NetSetting.Type> implements ActiveItem {

    public enum Type implements SettingType {

        CLUSTER_FRACTION_CONNECTED(Double.class, "1.0"),

        REQUIRE_UTILITY_CORPORATION_APPROVAL(Boolean.class, "true"),

        CLUSTER_MODELS_ENABLED(Boolean.class, "true"),

        LOAD_TO_NODE_LINES_ENABLED(Boolean.class, "true"),

        FIRST_CONNECT_ACCEPT(Boolean.class, "true"),

        HEAT_NETWORK_OWNER_ID(Integer.class, "-1"), //
        ELECTRICITY_NETWORK_OWNER_ID(Integer.class, "-1"), //
        GAS_NETWORK_OWNER_ID(Integer.class, "-1"), //
        INTERNET_NETWORK_OWNER_ID(Integer.class, "-1"), //
        SEWER_NETWORK_OWNER_ID(Integer.class, "-1"), //

        ELECTRICITY_FLOW_ATTRIBUTE(String.class, NetLineAttribute.FLOW.name()), //
        GAS_FLOW_ATTRIBUTE(String.class, NetLineAttribute.FLOW.name()), //
        HEAT_FLOW_ATTRIBUTE(String.class, NetLineAttribute.FLOW.name()), //
        INTERNET_FLOW_ATTRIBUTE(String.class, NetLineAttribute.FLOW.name()), //
        SEWER_FLOW_ATTRIBUTE(String.class, NetLineAttribute.FLOW.name()), //

        ELECTRICITY_ACTIVE(Boolean.class, "false"), //
        GAS_ACTIVE(Boolean.class, "false"), //
        HEAT_ACTIVE(Boolean.class, "false"), //
        INTERNET_ACTIVE(Boolean.class, "false"), //
        SEWER_ACTIVE(Boolean.class, "false"), //

        RESTRICT_TO_NET_OVERLAY(Boolean.class, "false"),

        ;

        public static Type getActiveForNetType(NetType netType) {
            return netType != null ? Type.valueOf(netType.name() + "_ACTIVE") : null;
        }

        public static Type getAttributeTypeForNetType(NetType netType) {
            return netType != null ? Type.valueOf(netType.name() + "_FLOW_ATTRIBUTE") : null;
        }

        public static Type getNetOwnerTypeForNetType(NetType netType) {
            return netType != null ? Type.valueOf(netType.name() + "_NETWORK_OWNER_ID") : null;
        }

        private String defaultValue;

        private Class<?> valueType;

        private Type(Class<?> valueType, String defaultValue) {
            this.valueType = valueType;
            this.defaultValue = defaultValue;
        }

        @Override
        public String getDefaultValue() {
            return getDefaultValue(null);
        }

        @Override
        public String getDefaultValue(Boolean detailed) {
            return this.defaultValue;
        }

        public NetType getNetType() {
            switch (this) {
                case ELECTRICITY_ACTIVE:
                case ELECTRICITY_FLOW_ATTRIBUTE:
                case ELECTRICITY_NETWORK_OWNER_ID:
                    return NetType.ELECTRICITY;
                case GAS_ACTIVE:
                case GAS_FLOW_ATTRIBUTE:
                case GAS_NETWORK_OWNER_ID:
                    return NetType.GAS;
                case HEAT_ACTIVE:
                case HEAT_FLOW_ATTRIBUTE:
                case HEAT_NETWORK_OWNER_ID:
                    return NetType.HEAT;
                case INTERNET_ACTIVE:
                case INTERNET_FLOW_ATTRIBUTE:
                case INTERNET_NETWORK_OWNER_ID:
                    return NetType.INTERNET;
                case SEWER_ACTIVE:
                case SEWER_FLOW_ATTRIBUTE:
                case SEWER_NETWORK_OWNER_ID:
                    return NetType.SEWER;
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getValueType() {
            return this.valueType;
        }
    }

    private static final long serialVersionUID = 3730370813278282986L;

    @Override
    public Type[] getEnumValues() {
        return Type.values();
    }

    @Override
    public boolean isActive() {
        switch (getType()) {
            case ELECTRICITY_ACTIVE:
            case GAS_ACTIVE:
            case HEAT_ACTIVE:
            case INTERNET_ACTIVE:
            case SEWER_ACTIVE:
                return getBooleanValue();
            default:
                return true;
        }
    }
}
