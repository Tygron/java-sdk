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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.NoDefaultText;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.other.IndexSortedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.ClientWord.ClientTerms;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.data.engine.other.ExcelItem;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;

/**
 * Indicator that shows a Stakeholder how he/she is doing.
 * @author Maxim Knepfle
 *
 */
public class Indicator extends AttributeItem implements ExcelItem, ActiveItem, IndexSortedItem, ImageItem {

    public enum IndicatorAttribute implements ReservedAttribute {

        FINANCE(Boolean.class, 0);

        private final Class<?> type;
        private final double[] defaultArray;

        private IndicatorAttribute(Class<?> type, double defaultValue) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
        }

        @Override
        public double[] defaultArray() {
            return defaultArray;
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }
    }

    public static final String DEFAULT_TARGET_DESCRIPTION = "Target (can be queried in Excel via TQL).";

    public static final Integer DEFAULT_EXCEL_ID = 12;

    public static final Integer DEFAULT_FINANCE_ID = 18;

    public static final Integer DEFAULT_PARENT_ID = 24;

    private static final String INDICATOR_TQL_NAME = MapLink.INDICATORS.getTQLName();

    /**
    *
    */
    private static final long serialVersionUID = -8776464666577232479L;

    public static final String IMAGE_LOCATION = "Gui/Images/Panels/TopMenu/Icons/";

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private Integer stakeholderID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.EXCEL_SHEETS)
    private Integer excelID = DEFAULT_EXCEL_ID;

    @XMLValue
    @ItemIDField(MapLink.INDICATORS)
    private Integer parentID = Item.NONE;

    // runtime setting do not save to XML
    private transient boolean excelUpdated = true;

    @XMLValue
    private HashMap<MapType, Double> mapTypeValues = new HashMap<>();

    @XMLValue
    private HashMap<MapType, Double> exactNumberValues = new HashMap<>();

    @XMLValue
    private HashMap<MapType, String> exactTextValues = new HashMap<>();

    @XMLValue
    private ArrayList<String> targetDescriptions = new ArrayList<>();

    @XMLValue
    @NoDefaultText
    private String description = StringUtils.EMPTY;

    @XMLValue
    @NoDefaultText
    private String currentExplanation = StringUtils.EMPTY;

    @XMLValue
    @DoNotSaveToInit
    private String maquetteExplanation = StringUtils.EMPTY;

    @XMLValue
    private int sortIndex = 50;

    @XMLValue
    private boolean isActive = true; // rename to active in 2024

    @XMLValue
    private boolean absolute = true;

    @XMLValue
    private String shortName = StringUtils.EMPTY;

    private long calcTimeMS = 0;

    @XMLValue
    private String imageName = StringUtils.EMPTY;

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    @XMLValue
    private HashMap<Integer, double[]> targets = new HashMap<>();

    @XMLValue
    private Timing updateTiming = Timing.BEFORE;

    @XMLValue
    @NoDefaultText
    private String warnings = StringUtils.EMPTY;

    public Indicator() {
    }

    public final double getAbsoluteValue(final MapType mapType) {

        Double value = mapTypeValues.get(mapType);
        if (value == null) {
            return 0.0;
        }
        return value;
    }

    private Integer getActiveScenarioID() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_SCENARIO);
        return setting.getIntegerValue();
    }

    public long getCalcTimeMS() {
        return calcTimeMS;
    }

    @Override
    public Integer getContentID() {
        return null;
    }

    @Override
    public MapLink getContentMapLink() {
        return null;
    }

    @Override
    public String getContentParam() {
        return null;
    }

    @Override
    protected ReservedAttribute[] getDefaultAttributes() {
        return IndicatorAttribute.values();
    }

    public final String getDescription() {
        return description;
    }

    public Double getExactNumberValue(MapType mapType) {
        if (exactNumberValues.containsKey(mapType)) {
            return exactNumberValues.get(mapType);
        }
        return null;
    }

    public String getExactTextValue(MapType mapType) {
        if (exactTextValues.containsKey(mapType)) {
            return exactTextValues.get(mapType);
        }
        return null;
    }

    @Override
    public Integer getExcelID() {
        return excelID;
    }

    @Override
    public ExcelSheet getExcelSheet() {
        return getItem(MapLink.EXCEL_SHEETS, getExcelID());
    }

    public final String getExplanation(MapType mapType) {

        if (mapType == MapType.MAQUETTE && StringUtils.containsData(maquetteExplanation)) {
            return maquetteExplanation;
        }
        return currentExplanation;
    }

    public final String getExplanationBody(MapType mapType) {

        StringBuilder builder = new StringBuilder();
        builder.append(getExplanation(mapType));
        builder.append("<p></p><br/><p>");
        builder.append(getWord(MapLink.CLIENT_WORDS, ClientTerms.STAKEHOLDER_INDICATOR_SCORE));
        builder.append(": ");
        builder.append(StringUtils.toPercentage(getValue(mapType)));
        builder.append("</p>");
        return builder.toString();
    }

    @Override
    public String getImageLocation() {
        return IMAGE_LOCATION + getImageName();
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public final int getImageVersion() {
        return imageVersion;
    }

    @Override
    public String getMyParam() {
        return INDICATOR_TQL_NAME;
    }

    public Indicator getParent() {
        return getItem(MapLink.INDICATORS, getParentID());
    }

    @Override
    public Integer getParentID() {
        return parentID;
    }

    @Override
    public Integer getRelationID(Relation relation) {

        if (relation == Relation.OWNER) {
            return getStakeholderID();
        }
        if (relation == Relation.PARENT) {
            return getParentID();
        }
        return Item.NONE;
    }

    private double getRelativeValue(double absoluteValue) {

        // relative value compared to current value as start
        double startValue = getAbsoluteValue(MapType.CURRENT);
        double distance = 1.0 - startValue;
        // when no distance, score 100%
        return distance == 0.0 ? 1.0 : (absoluteValue - startValue) / distance;
    }

    /**
     * @return The indicator s short name
     */
    public String getShortName() {
        return shortName == null || StringUtils.EMPTY.equals(shortName) ? getName() : shortName;
    }

    @Override
    public final int getSortIndex() {
        return sortIndex;
    }

    public final Stakeholder getStakeholder() {
        return this.getItem(MapLink.STAKEHOLDERS, getStakeholderID());
    }

    public final Integer getStakeholderID() {
        return this.stakeholderID;
    }

    public double getTarget() {

        double[] targets = this.getTargets();
        if (targets == null || targets.length == 0) {
            return 0;
        }
        return targets[0];
    }

    public String getTargetDescription(int index) {
        return index >= 0 && index < targetDescriptions.size() ? targetDescriptions.get(index) : DEFAULT_TARGET_DESCRIPTION;
    }

    public double[] getTargets() {
        Integer scenarioID = this.getActiveScenarioID();
        return this.getTargets(scenarioID);
    }

    public double[] getTargets(Integer scenarioID) {

        // fallback to previous scenario value when not available.
        if (!targets.containsKey(scenarioID)) {
            Collection<Scenario> previousScenarios = getPreviousSortedItems(MapLink.SCENARIOS, scenarioID);
            for (Scenario previousScenario : previousScenarios) {
                double[] previousTargets = targets.get(previousScenario.getID());
                if (previousTargets != null) {
                    targets.put(scenarioID, ObjectUtils.deepCopy(previousTargets));
                    break;
                }
            }
        }
        return targets.getOrDefault(scenarioID, new double[] { 0 });
    }

    public Timing getUpdateTiming() {
        return updateTiming;
    }

    /**
     * Get score value of this indicator.
     */
    public double getValue(final MapType mapType) {

        double absoluteValue = getAbsoluteValue(mapType);
        return absolute ? absoluteValue : getRelativeValue(absoluteValue);
    }

    public String getWarnings() {
        return warnings;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public boolean isFinance() {
        return getAttribute(IndicatorAttribute.FINANCE) > 0.0;
    }

    @Override
    public boolean isLogicUpdated() {
        return excelUpdated;
    }

    public boolean isPersonal() {
        return this.getStakeholder() != null;
    }

    public boolean isVisibleForStakeholder(Integer stakeholderID) {
        return isActive() && (this.stakeholderID.equals(Item.NONE) || this.stakeholderID.equals(stakeholderID));
    }

    public void removeExactNumberValues() {
        exactNumberValues.clear();
    }

    public void removeExactTextValues() {
        exactTextValues.clear();
    }

    public void removeScenarioTarget(Integer scenarioID) {
        targets.remove(scenarioID);
    }

    public void setAbsolute(boolean absolute) {
        this.absolute = absolute;
    }

    public final void setAbsoluteValues(Double[] values) {

        for (MapType mapType : MapType.VALUES) {
            if (values[mapType.ordinal()] != null) {
                mapTypeValues.put(mapType, values[mapType.ordinal()]);
            }
        }
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public void setCalcTimeMS(long calcTime) {
        this.calcTimeMS = calcTime;
    }

    public void setDescription(String text) {
        this.description = text;
    }

    public void setExactNumberValues(Double[] values) {

        for (MapType mapType : MapType.VALUES) {
            if (values[mapType.ordinal()] != null) {
                exactNumberValues.put(mapType, values[mapType.ordinal()]);
            }
        }
    }

    public void setExactTextValues(String[] values) {

        for (MapType mapType : MapType.VALUES) {
            if (values[mapType.ordinal()] != null) {
                exactTextValues.put(mapType, values[mapType.ordinal()]);
            }
        }
    }

    public void setExcelID(Integer excelID) {
        this.excelID = excelID;
        this.setCalcTimeMS(0);// reset calc time
        this.setLogicUpdated(true);
    }

    public final void setExplanations(final String[] explanations) {

        if (explanations[MapType.CURRENT.ordinal()] != null) {
            currentExplanation = explanations[MapType.CURRENT.ordinal()];
        }
        if (explanations[MapType.MAQUETTE.ordinal()] != null) {
            maquetteExplanation = explanations[MapType.MAQUETTE.ordinal()];
        }
    }

    @Override
    public void setImageName(String imageName) {
        this.imageName = imageName;
        this.imageVersion++;
    }

    @Override
    public void setLogicUpdated(boolean excelUpdated) {
        this.excelUpdated = excelUpdated;
    }

    public void setParentID(Integer parentIndicatorID) {
        this.parentID = parentIndicatorID;
    }

    public void setShortName(String text) {
        this.shortName = text;
    }

    @Override
    public void setSortIndex(int index) {
        sortIndex = index;
    }

    public void setStakeholderID(Integer stakeholderID) {
        this.stakeholderID = stakeholderID;
    }

    public void setTargets(double[] targets) {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_SCENARIO);
        this.setTargets(setting.getIntegerValue(), targets);
    }

    public void setTargets(Integer scenarioID, double[] targets) {
        this.targets.put(scenarioID, targets);
    }

    public void setUpdateTiming(Timing timing) {
        this.updateTiming = timing;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        if (!StringUtils.containsData(imageName)) {
            imageName = "excel.png";
        }

        this.description = StringUtils.removeHTMLTags(this.description);

        return result;
    }
}
