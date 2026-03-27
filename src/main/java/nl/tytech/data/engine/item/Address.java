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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.data.core.other.IndexedEnum;
import nl.tytech.data.engine.serializable.CadastralPurpose;
import nl.tytech.util.EnumUtils;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;

/**
 * Address: Address info of addresses in a building
 *
 * @author Frank Baars
 */
public class Address extends SourcedAttributeItem implements GeometryItem<Point> {

    public enum AddressAttribute implements ReservedAttribute {

        ENERGY_LABEL(Double.class, EnergyLabel.UNKNOWN.value), //
        CADASTRAL_PURPOSE(Double.class, CadastralPurpose.UNKNOWN.getValue()), //
        RESIDENCE_TYPE(Double.class, ResidenceType.UNKNOWN.value), //
        INHABITANTS(Double.class, 0), //
        FLOOR_SPACE_M2(Double.class, 0);

        private final Class<?> type;
        private final double[] array;

        private AddressAttribute(Class<?> type, double defaultValue) {
            this.type = type;
            this.array = new double[] { defaultValue };
        }

        @Override
        public double[] defaultArray() {
            return array;
        }

        @Override
        public double defaultValue() {
            return array[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    public enum EnergyLabel implements IndexedEnum {

        UNKNOWN("", 0),

        G("G", 1),

        F("F", 2),

        E("E", 3),

        D("D", 4),

        C("C", 5),

        B("B", 6),

        A0("A", 7),

        A1("A+", 8),

        A2("A++", 9),

        A3("A+++", 10),

        A4("A++++", 11),

        A5("A+++++", 12);

        public static final EnergyLabel[] VALUES = values();

        public static EnergyLabel get(double value) {
            return EnumUtils.get(VALUES, value, UNKNOWN);
        }

        public static EnergyLabel get(String value) {

            // trim
            value = value.trim();

            // try names
            for (EnergyLabel type : EnergyLabel.VALUES) {
                if (type.name().equalsIgnoreCase(value) || type.getLabel().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            // fallback to values
            try {
                return get(Double.parseDouble(value));
            } catch (Exception e) {
                return UNKNOWN;
            }
        }

        private String label;

        private double value;

        private EnergyLabel(String label, Integer id) {
            this.label = label;
            this.value = id;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public double getValue() {
            return value;
        }
    }

    public enum ResidenceType implements IndexedEnum {

        UNKNOWN("", 0), //
        DETACHED("Vrijstaande woning", 1), //
        SEMI_DETACHED("Twee-onder-één-kap", 2), //

        PORCH("Portiekwoning", 3), //
        TERRACED_BETWEEN("Rijwoning tussen", 4), //
        TERRACED_CORNER("Rijwoning hoek", 5), //
        TINY("Maisonnette", 6), //
        GALLERY_HOUSE("Galerijwoning", 7), //
        FLAT("Flatwoning (overig)", 8), //
        APARTMENT("Appartement", 9), //

        CARAVAN("Woonwagen", 10), //
        HOUSE_BOAT_BERTH("Woonboot bestaande ligplaats", 11), //
        HOUSE_BOAT_NEW_BERTH("Woonboot nieuwe ligplaats", 12), //
        SHARED_HOUSE("Woongebouw met niet-zelfstandige woonruimte", 13), //
        ACCOMMODATION("Logieswoning", 14),//

        ;

        public static final ResidenceType[] VALUES = values();

        public static ResidenceType get(double value) {
            return EnumUtils.get(VALUES, value, UNKNOWN);
        }

        public static ResidenceType get(String value) {

            // trim
            value = value.trim();

            // try names
            for (ResidenceType type : ResidenceType.VALUES) {
                if (type.getEnglish().equalsIgnoreCase(value) || type.getDutch().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            // fallback to values
            try {
                return get(Double.parseDouble(value));
            } catch (Exception e) {
                return UNKNOWN;
            }
        }

        private final String dutch;
        private final String english;
        private final double value;

        private ResidenceType(String dutch, double value) {

            this.english = StringUtils.capitalizeWithSpacedUnderScores(name());
            this.dutch = dutch;
            this.value = value;
        }

        public final String getDutch() {
            return dutch;
        }

        public final String getEnglish() {
            return english;
        }

        @Override
        public double getValue() {
            return value;
        }
    }

    private static final long serialVersionUID = 2761206928331755843L;

    @XMLValue
    private String zipCode = StringUtils.EMPTY;

    @XMLValue
    private String street = StringUtils.EMPTY;

    @XMLValue
    private Integer number = null;

    @XMLValue
    private String letter = StringUtils.EMPTY;

    @XMLValue
    private String addition = StringUtils.EMPTY;

    @XMLValue
    private Point point = null;

    @XMLValue
    private String type = StringUtils.EMPTY;

    @XMLValue
    private double surfaceSize = 0;

    public Address() {

    }

    public Address(Point point, double surfaceSize, String zipCode, String street, int number, String letter, String addition) {
        setPoint(point);
        setSurfaceSize(surfaceSize);
        setZipCode(zipCode);
        setStreet(street);
        setNumber(number);
        setLetter(letter);
        setAddition(addition);
    }

    public boolean equalsAddress(Address address) {
        if (address == null) {
            return false;
        }
        if (this.number == null && address.number != null) {
            return false;
        } else if (!this.number.equals(address.number)) {
            return false;
        }

        return street.equals(address.getStreet()) && addition.equals(address.addition) && letter.equals(address.letter)
                && zipCode.equals(address.zipCode);
    }

    public String getAddition() {
        return addition;
    }

    @Override
    public Point getCenterPoint() {
        return point;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return AddressAttribute.values();
    }

    public String getExportCode() {
        String addressText = getZipCode() + StringUtils.WHITESPACE + getNumber() + StringUtils.WHITESPACE + getLetter()
                + StringUtils.WHITESPACE + getAddition();
        addressText = addressText.trim().toUpperCase();
        return addressText;
    }

    @Override
    public Geometry getExportGeometry() {
        return point == null ? JTSUtils.EMPTY : point;
    }

    public double getFloorSpaceM2() {
        return getOrDefault(AddressAttribute.FLOOR_SPACE_M2);
    }

    public String getLetter() {
        return letter;
    }

    public Integer getNumber() {
        return number;
    }

    public Point getPoint() {
        return point;
    }

    @Override
    public Point[] getQTGeometries() {
        return new Point[] { point };
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(point);
    }

    public void setAddition(String newValue) {
        this.addition = newValue;
    }

    public void setLetter(String newValue) {
        this.letter = newValue;
    }

    public void setNumber(Integer newValue) {
        this.number = newValue;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setStreet(String newValue) {
        this.street = newValue;
    }

    public void setSurfaceSize(double area) {
        setAttribute(AddressAttribute.FLOOR_SPACE_M2, area);
    }

    public void setZipCode(String newValue) {
        this.zipCode = newValue;
    }

    @Override
    public String toString() {
        return (getStreet() + StringUtils.WHITESPACE + getNumber() + StringUtils.WHITESPACE + getLetter() + StringUtils.WHITESPACE
                + getAddition()).trim();
    }

    public String toZipString() {
        return (getZipCode() + StringUtils.WHITESPACE + getNumber() + StringUtils.WHITESPACE + getLetter() + StringUtils.WHITESPACE
                + getAddition()).trim();
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        if (StringUtils.containsData(type)) {
            double[] array = EnumUtils.get(CadastralPurpose.getDutch(type), CadastralPurpose.UNKNOWN);
            setAttributeArray(AddressAttribute.CADASTRAL_PURPOSE, array);
            type = StringUtils.EMPTY;
        }
        if (surfaceSize != 0) {
            setAttribute(AddressAttribute.FLOOR_SPACE_M2, surfaceSize);
            surfaceSize = 0;
        }
        return result;
    }
}
