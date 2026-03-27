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
package nl.tytech.data.engine.serializable;

import java.util.ArrayList;
import java.util.List;
import nl.tytech.data.core.other.IndexedEnum;
import nl.tytech.util.EnumUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 *
 * General purpose of the residence as registered by the Dutch government, linked to the internal FunctionCategory.
 *
 * @author Jurrian Hartveldt, Frank Baars
 * @specialization GIS
 */
public enum CadastralPurpose implements IndexedEnum {

    UNKNOWN(StringUtils.EMPTY, 0d, Category.values()), //
    LIVING("woonfunctie", 1d, Category.NORMAL, Category.SOCIAL, Category.LUXE, Category.SENIOR, Category.STUDENT), //
    SPORTS("sportfunctie", 2d, Category.LEISURE), //
    EDUCATIONAL("onderwijsfunctie", 3d, Category.EDUCATION), //
    HEALTHCARE("gezondheidszorgfunctie", 4d, Category.HEALTHCARE), //
    SHOPPING("winkelfunctie", 5d, Category.SHOPPING), //
    GATHERING("bijeenkomstfunctie", 6d, Category.SHOPPING), //
    PRISON("celfunctie", 7d, Category.OTHER), //
    INDUSTRIAL("industriefunctie", 8d, Category.INDUSTRY), //
    OFFICE("kantoorfunctie", 9d, Category.OFFICES), //
    LODGING("logiesfunctie", 10d, Category.SHOPPING), //
    OTHER("overige gebruiksfunctie", 11d, Category.OTHER), //
    MULTIPLE("meervoudige functie", 12d, Category.values()), //
    ;

    public static final String BAG_TAG = "gebruiksdoel";

    public static final CadastralPurpose[] VALUES = values();

    public static final CadastralPurpose[] UNKNOWNS = new CadastralPurpose[] { CadastralPurpose.UNKNOWN };

    public static CadastralPurpose get(double value) {
        return EnumUtils.get(VALUES, value, UNKNOWN);
    }

    public static CadastralPurpose[] get(double[] values) {
        return EnumUtils.get(VALUES, values, UNKNOWN);
    }

    public static CadastralPurpose get(String value) {

        // trim
        value = value.trim();

        // try names
        for (CadastralPurpose type : CadastralPurpose.VALUES) {
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

    public static final CadastralPurpose[] getDutch(String dutchCadastralKey) {

        if (!StringUtils.containsData(dutchCadastralKey)) {
            return UNKNOWNS;
        }

        String[] keys = dutchCadastralKey.split(",");
        if (keys.length == 0) {
            return UNKNOWNS;
        }

        CadastralPurpose[] result = new CadastralPurpose[keys.length];
        keyloop: for (int i = 0; i < keys.length; ++i) {
            String key = keys[i].trim();
            for (CadastralPurpose type : VALUES) {
                if (type.getDutch().equals(key)) {
                    result[i] = type;
                    continue keyloop;
                }
            }
            TLogger.warning("Couldn't interpret key: " + dutchCadastralKey + ", passing default.");
            result[i] = OTHER;
        }
        return result;
    }

    public static final CadastralPurpose[] getEnglish(String englishCadastralKey) {

        if (!StringUtils.containsData(englishCadastralKey)) {
            return UNKNOWNS;
        }

        String[] keys = englishCadastralKey.split(",");
        if (keys.length == 0) {
            return UNKNOWNS;
        }

        CadastralPurpose[] result = new CadastralPurpose[keys.length];
        keyloop: for (int i = 0; i < keys.length; ++i) {
            String key = keys[i].trim();
            for (CadastralPurpose type : VALUES) {
                if (type.getEnglish().equals(key)) {
                    result[i] = type;
                    continue keyloop;
                }
            }
            TLogger.warning("Couldn't interpret key: " + englishCadastralKey + ", passing default.");
            result[i] = OTHER;
        }
        return result;
    }

    public static final CadastralPurpose[] getNY(String buildingClass) {

        List<CadastralPurpose> possible = new ArrayList<>();
        if (!StringUtils.containsData(buildingClass)) {
            possible.add(CadastralPurpose.OTHER);
        } else if (buildingClass.startsWith("A") || buildingClass.startsWith("B") || buildingClass.startsWith("C")
                || buildingClass.startsWith("D")) {
            possible.add(CadastralPurpose.LIVING);
        } else if (buildingClass.startsWith("E") || buildingClass.startsWith("F") || buildingClass.startsWith("G")) {
            possible.add(CadastralPurpose.INDUSTRIAL);
        } else if (buildingClass.startsWith("H")) {
            possible.add(CadastralPurpose.LODGING);
        } else if (buildingClass.startsWith("I")) {
            possible.add(CadastralPurpose.HEALTHCARE);
        } else if (buildingClass.startsWith("J")) {
            possible.add(CadastralPurpose.GATHERING);
        } else if (buildingClass.startsWith("K")) {
            possible.add(CadastralPurpose.SHOPPING);
        } else if (buildingClass.startsWith("L")) {
            possible.add(CadastralPurpose.LIVING);
        } else if (buildingClass.startsWith("M") || buildingClass.startsWith("N")) {
            possible.add(CadastralPurpose.GATHERING);
        } else if (buildingClass.startsWith("O")) {
            possible.add(CadastralPurpose.OFFICE);
        } else if (buildingClass.startsWith("P")) {
            possible.add(CadastralPurpose.GATHERING);
        } else if (buildingClass.startsWith("Q")) {
            possible.add(CadastralPurpose.SPORTS);
        } else if (buildingClass.startsWith("R")) {
            possible.add(CadastralPurpose.LIVING);
        } else if (buildingClass.startsWith("S")) {
            possible.add(CadastralPurpose.MULTIPLE);
        } else if (buildingClass.startsWith("T") || buildingClass.startsWith("U")) {
            possible.add(CadastralPurpose.OTHER);
        } else if (buildingClass.startsWith("V")) {
            /**
             * Vacant land, skip building
             *
             */
            return null;
        } else if (buildingClass.startsWith("W")) {
            possible.add(CadastralPurpose.EDUCATIONAL);
        } else if (buildingClass.startsWith("Y") || buildingClass.startsWith("Z")) {
            possible.add(CadastralPurpose.OTHER);
        } else {
            TLogger.severe("Couldn't interpret key: " + buildingClass + ", passing null.");
            return null;
        }

        return possible.toArray(new CadastralPurpose[possible.size()]);
    }

    private final List<Category> functionCategories = new ArrayList<>();
    private final double value;
    private final String dutch;
    private final String english;

    private CadastralPurpose(String dutchCadastralKey, double value, Category... functionCategories) {
        this.dutch = dutchCadastralKey;
        this.english = StringUtils.capitalizeWithSpacedUnderScores(this);
        addFunctionCategories(functionCategories);
        this.value = value;
    }

    private void addFunctionCategories(Category... functionCategories) {
        for (Category fc : functionCategories) {
            this.functionCategories.add(fc);
        }
    }

    public String getDutch() {
        return dutch;
    }

    public String getEnglish() {
        return english;
    }

    public List<Category> getFunctionCategories() {
        return functionCategories;
    }

    @Override
    public double getValue() {
        return value;
    }
}
