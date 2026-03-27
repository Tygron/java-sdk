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

import static nl.tytech.core.net.serializable.MapLink.INDICATORS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.CoreStakeholder;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.other.ActiveItem;
import nl.tytech.data.engine.other.ImageItem;
import nl.tytech.data.engine.other.TQLItem;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 * Stakeholder: This class keeps track of the role of a Stakeholder
 *
 * @author Maxim Knepfle, Frank Baars
 */
public class Stakeholder extends CoreStakeholder implements TQLItem, ImageItem, ActiveItem {

    public enum Type {

        CIVILIAN(new TColor(250, 128, 114), 0, true),

        COMPANY(TColor.GRAY, 500000, true),

        EDUCATION(TColor.CYAN, 500000, false),

        EXPERT(TColor.BROWN, 0, false),

        FARMER(new TColor(154, 205, 50), 500000, false),

        HEALTHCARE(TColor.MAGENTA, 1000000, false),

        HOUSING_CORPORATION(new TColor(238, 221, 130), 1000000, true),

        MUNICIPALITY(TColor.LIGHT_GRAY, 1000000, true),

        MEDIA(TColor.PINK, 0, false),

        PROJECT_DEVELOPER(new TColor(178, 34, 34), 500000, true),

        UTILITY_CORPORATION(TColor.YELLOW, 1000000, false),

        WATER_AUTHORITY(TColor.BLUE, 1000000, false),

        OTHER_AUTHORITY(TColor.DARK_GRAY, 1000000, false),

        OTHER(TColor.DARK_GRAY, 0, false);

        public static final Type[] VALUES = values();

        public static Type[] getCommonStakeholders() {

            List<Type> types = new ArrayList<>();
            for (Type type : Type.values()) {
                if (type.common) {
                    types.add(type);
                }
            }
            return types.toArray(new Type[types.size()]);
        }

        private TColor defaultColor;
        private double defaultStartBudget;
        private boolean common;

        private Type(TColor defaultColor, int defaultStartBudget, boolean common) {
            this.defaultColor = defaultColor;
            this.defaultStartBudget = defaultStartBudget;
            this.common = common;
        }

        public TColor getDefaultColor() {
            return defaultColor;
        }

        public double getDefaultStartBudget() {
            return defaultStartBudget;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    private static final String STAKEHOLDER_TQL_NAME = MapLink.STAKEHOLDERS.getTQLName();

    public static final String STAKEHOLDER_IMAGE_LOCATION = "Gui/Images/Portraits/";

    private static final long serialVersionUID = 178838230577313380L;

    @XMLValue
    @AssetDirectory(STAKEHOLDER_IMAGE_LOCATION)
    private String imageName = "other.png";

    // local value used to check if asset was updated in editor
    private int imageVersion = 0;

    @XMLValue
    private double startBudget = 0d;

    @XMLValue
    private HashMap<Integer, Double> startBudgets = new HashMap<>();

    @XMLValue
    private HashMap<Integer, Integer> startCinematics = new HashMap<>();

    @XMLValue
    private Type type = Type.CIVILIAN;

    @XMLValue
    private Point startPoint = null;

    @XMLValue
    private String shortName = StringUtils.EMPTY;

    /**
     * Returns budget based on the finance indicator or NULL when there is no budget in the session (things are free)
     */
    public Double getBudget() {

        for (Indicator indicator : getVisibleIndicators()) {
            if (indicator.isFinance()) {
                // Note: maptype is not relevant for finance indicator values are the same.
                Double budget = indicator.getExactNumberValue(MapType.CURRENT);
                if (budget != null) {
                    return budget.doubleValue();
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {

        Map<String, Object> map = super.getExportAttributes(inherited);
        map.put("TYPE", this.getType().name());
        return map;
    }

    @Override
    public Geometry getExportGeometry() {

        List<Geometry> geoms = new ArrayList<>();
        ItemMap<Plot> plots = this.getMap(MapLink.PLOTS);

        // add all my plots
        for (Plot plot : plots) {
            if (getID().equals(plot.getOwnerID())) {
                geoms.add(plot.getExportGeometry());
            }
        }
        return JTSUtils.createCollection(geoms);
    }

    @Override
    public String getImageLocation() {
        return STAKEHOLDER_IMAGE_LOCATION + imageName;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public int getImageVersion() {
        return imageVersion;
    }

    @Override
    public String getMyParam() {
        return STAKEHOLDER_TQL_NAME;
    }

    public final double getScenarioStartBudget() {
        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_SCENARIO);
        return getScenarioStartBudget(setting.getIntegerValue());
    }

    public final double getScenarioStartBudget(Integer scenarioID) {
        return this.startBudgets.getOrDefault(scenarioID, 0.0);
    }

    public String getShortestName() {

        if (StringUtils.EMPTY.equals(shortName)) {
            return this.getName();
        } else {
            return this.shortName;
        }
    }

    public String getShortName() {
        return this.shortName;
    }

    public double getStartBudget() {
        return startBudget;
    }

    public CinematicData getStartCinematic() {

        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_SCENARIO);
        Integer scenarioID = setting.getIntegerValue();
        return getStartCinematic(scenarioID);
    }

    public CinematicData getStartCinematic(Integer scenarioID) {
        Integer cinematicID = this.startCinematics.get(scenarioID);
        return this.getItem(MapLink.CINEMATIC_DATAS, cinematicID);
    }

    public final Type getType() {
        return this.type;
    }

    public List<Indicator> getVisibleIndicators() {
        return this.<Indicator> getMap(INDICATORS).stream().filter(i -> i.isVisibleForStakeholder(getID())).sorted(Item.INDEX_NAME_SORT)
                .collect(Collectors.toList());
    }

    public List<Indicator> getVisibleParentIndicators() {
        return this.<Indicator> getMap(INDICATORS).stream().filter(i -> i.isVisibleForStakeholder(getID()) && i.getParent() == null)
                .sorted(Item.INDEX_NAME_SORT).collect(Collectors.toList());
    }

    public void removeStartBudget(Integer id) {
        this.startBudgets.remove(id);
    }

    public void removeStartingCinematic(Integer scenarioID) {
        this.startCinematics.remove(scenarioID);
    }

    @Override
    public void setImageName(String name) {
        imageName = name;
        imageVersion++;
    }

    public void setLevelStartBudget(double startBudget) {

        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_SCENARIO);
        this.setScenarioBudget(setting.getIntegerValue(), startBudget);
    }

    public void setScenarioBudget(Integer scenarioID, double startBudget) {
        this.startBudgets.put(scenarioID, startBudget);
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setStartBudget(double startBudget) {
        this.startBudget = startBudget;
    }

    public void setStartCinematic(Integer cinematicID) {

        Setting setting = this.getItem(MapLink.SETTINGS, Setting.Type.ACTIVE_SCENARIO);
        this.setStartCinematic(setting.getIntegerValue(), cinematicID);
    }

    public void setStartCinematic(Integer scenarioID, Integer cinematicID) {
        this.startCinematics.put(scenarioID, cinematicID);
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String validated(boolean startProject) {

        // fix missing image name from LTS 2024
        if (!StringUtils.containsData(imageName)) {
            imageName = "other.png";
        }

        for (Entry<Integer, ?> entry : new ArrayList<>(startBudgets.entrySet())) {
            if (entry.getValue() instanceof Integer) {
                startBudgets.put(entry.getKey(), Double.valueOf("" + entry.getValue()));
            }
        }
        return super.validated(startProject);
    }
}
