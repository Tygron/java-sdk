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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.ListOfClass;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.GeometryItem;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Answer;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Source;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.Building.Layer;
import nl.tytech.data.engine.other.TextItem;
import nl.tytech.data.engine.other.TimeStateItem;
import nl.tytech.data.engine.serializable.PopupImageType;
import nl.tytech.data.engine.serializable.PopupModelType;
import nl.tytech.data.engine.serializable.TimeState;
import nl.tytech.util.JTSUtils;
import nl.tytech.util.PackageUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.ColorUtils;
import nl.tytech.util.color.TColor;
import nl.tytech.util.logger.TLogger;

/**
 * PopupData: popup info.
 *
 * @author Maxim Knepfle
 */
public class PopupData extends UniqueNamedItem implements GeometryItem<Point> {

    public enum Type {

        /**
         * Standard popup witch give the location of a stakeholder
         */
        STAKEHOLDER_STANDARD(false),
        /**
         * Intractable popup, with 3 buttons for answering
         */
        INTERACTION(true),
        /**
         * Intractable popup, with 3 buttons for answering and a long time object
         */
        INTERACTION_WITH_DATE(true),
        /**
         * Non-interactable popup, for waiting approval popups
         */
        INFORMATION(true),
        /**
         * A LABEL is a simple name time related text like "building houses here".
         */
        LABEL(true);

        private static final List<Type> timeStateTypes;

        static {
            timeStateTypes = new ArrayList<>();
            for (Type type : values()) {
                if (type.isTimeStateType()) {
                    timeStateTypes.add(type);
                }
            }
        }

        /**
         * Returns an array of only the time state related popup types.
         *
         * @return
         */
        public static List<Type> getTimeStateTypes() {
            return timeStateTypes;
        }

        private boolean timeStateType = false;

        private Type(boolean partOfNegotiation) {
            this.timeStateType = partOfNegotiation;
        }

        public boolean isTimeStateType() {
            return timeStateType;
        }
    }

    /** Generated serialVersionUID */
    private static final long serialVersionUID = -2523818277653091684L;

    public static final double MIN_SCALE = 0.5;

    public static final double MAX_SCALE = 2.0;

    @XMLValue
    @ItemIDField(MapLink.STAKEHOLDERS)
    private ArrayList<Integer> visibleForStakeholderIDs = new ArrayList<>();

    @XMLValue
    @ListOfClass(Answer.class)
    private ArrayList<Answer> answers = new ArrayList<>();

    @XMLValue
    private String title = StringUtils.EMPTY;

    @XMLValue
    private Long calendar = null;

    @XMLValue
    private String text = StringUtils.EMPTY;

    @XMLValue
    private Boolean overrideAttention = null;

    @XMLValue
    private Integer linkID = Item.NONE;

    @XMLValue
    private MultiPolygon polygons = null;

    @XMLValue
    private MapLink linkType = null;

    @XMLValue
    private Type type = Type.INFORMATION;

    @XMLValue
    private Point point = null;

    @JsonIgnore
    private transient int clientY;

    @XMLValue
    private PopupModelType modelType = PopupModelType.QUESTION_MARK;

    @ItemIDField(MapLink.MODEL_DATAS)
    @XMLValue
    private Integer modelDataID = Item.NONE;

    @XMLValue
    private boolean opensAutomatically = false;

    @XMLValue
    private TColor color = ColorUtils.COLOR_INTERFACE;

    @XMLValue
    private double scale = 1d;

    @XMLValue
    private int visibleTimeframe = 0;

    @XMLValue
    private PopupImageType imageType = PopupImageType.RECEIVE_REQUEST;

    public PopupData() {

    }

    public PopupData(Type type, PopupModelType modelType, PopupImageType imageType, MapLink linkType, Integer linkID,
            List<Integer> visibleForStakeholderIDs) {
        this.type = type;
        this.modelType = modelType;
        this.color = modelType.getDefaultColor();
        this.imageType = imageType;
        this.linkType = linkType;
        this.linkID = linkID;
        this.visibleForStakeholderIDs = new ArrayList<>(visibleForStakeholderIDs);
    }

    public void addAnswer(Answer answer) {
        answer.setID(this.answers.size());
        this.answers.add(answer);
    }

    public MapType getActiveMap() {

        Item content = getContentItem();
        if (content instanceof TimeStateItem tsi) {
            if (tsi.getTimeState().before(TimeState.READY)) {
                return MapType.MAQUETTE;
            } else {
                return MapType.CURRENT;
            }
        } else if (content instanceof ExcelPanel ep && JTSUtils.hasArea(ep.getPolygons())) {
            return MapType.MAQUETTE;
        }
        return null;
    }

    public Answer getAnswer(Integer answerID) {
        for (Answer answer : this.answers) {
            if (answer.getID().equals(answerID)) {
                return answer;
            }
        }
        return null;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public Point getCenterPoint() {
        return point;
    }

    public int getClientY() {
        return clientY;
    }

    private MultiPolygon getClusterBuildingMP(NetCluster cluster) {

        List<Building> buildings = cluster.getBuildings(null);
        if (buildings == null || buildings.isEmpty()) {
            return JTSUtils.EMPTY;
        }

        List<Geometry> geometries = new ArrayList<>();
        for (Building building : buildings) {
            geometries.add(building.getPolygons(null));
        }

        return JTSUtils.createMP(geometries);
    }

    public TColor getColor() {
        return color;
    }

    public <I extends Item> I getContentItem() {
        return this.getItem(linkType, linkID);
    }

    public Integer getContentLinkID() {
        return linkID;
    }

    public MapLink getContentMapLink() {
        return linkType;
    }

    public Long getDateMillis() {
        return calendar;
    }

    @Override
    public Map<String, Object> getExportAttributes(boolean inherited) {

        Map<String, Object> map = super.getExportAttributes(inherited);

        map.put(UniqueNamedItem.NAME, getTitle());
        map.put("icon", getModelType().getIconTextureLocation(false).replace(".dds", ".png"));
        map.put("color", getColor().toCSS());

        if (isPanel() && getContentItem() instanceof Panel panel) {
            map.put("width", "" + panel.getWidth());
            map.put("height", "" + panel.getHeight());
        }
        return map;
    }

    @Override
    public Geometry getExportGeometry() {
        return getCenterPoint();
    }

    public String getImageLocation() {
        return imageType.getImageLocation();
    }

    public String getLogoName() {
        return "popupIcon_questionmark";
    }

    public ModelData getModelData() {

        return getItem(MapLink.MODEL_DATAS, modelDataID);
    }

    public Integer getModelDataID() {
        return modelDataID;
    }

    public PopupModelType getModelType() {
        return modelType;
    }

    public GeometryCollection getMultiPolygon() {

        if (polygons != null) {
            return polygons;
        }

        if (this.getContentMapLink() == MapLink.BUILDINGS || this.getContentMapLink() == MapLink.UPGRADE_TYPES) {
            Building building = this.getItem(MapLink.BUILDINGS, this.getContentLinkID());
            if (building.getTimeState().before(TimeState.READY)) {
                return building.getPolygons(MapType.MAQUETTE);
            } else {
                return building.getPolygons(MapType.CURRENT);
            }

        } else if (this.getContentMapLink() == MapLink.MEASURES) {
            MapMeasure measure = this.getItem(MapLink.MEASURES, this.getContentLinkID());
            return measure.getPolygons(Layer.VALUES);

        } else if (this.getContentMapLink() == MapLink.PANELS) {
            Panel panel = this.getItem(MapLink.PANELS, this.getContentLinkID());
            if (panel instanceof ExcelPanel ep) {
                return ep.getPolygons();
            } else {
                return null;
            }

        } else if (this.getContentMapLink() == MapLink.SPECIAL_OPTIONS) {
            return null;

        } else if (this.getContentMapLink() == MapLink.NET_CLUSTERS) {
            return getClusterBuildingMP((NetCluster) this.getItem(MapLink.NET_CLUSTERS, this.getContentLinkID()));
        }
        TLogger.warning("This MapLink (" + getContentMapLink() + ") is not implemented yet for PopupData.getCoordinates()!");
        setMultiPolygon(JTSUtils.EMPTY);
        return polygons;
    }

    @Override
    public Point[] getQTGeometries() {
        return new Point[] { this.getCenterPoint() };
    }

    public double getScale() {
        return scale;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Source> getSources() {
        return Collections.EMPTY_LIST;
    }

    public String getText() {

        if (getContentMapLink() == MapLink.PANELS) {
            Panel panel = this.getItem(MapLink.PANELS, getContentLinkID());
            switch (panel.getType()) {
                case TEXT_PANEL:
                case EXCEL_PANEL:
                case TEMPLATE_EXCEL_PANEL:
                case TEMPLATE_TEXT_PANEL:
                    return ((TextItem) panel).getText();
                case WEB_PANEL:
                    return PackageUtils.getStringFromResource(((WebPanel) panel).getURL());
                default:
                    break;// do nothing
            }
        }
        return text;
    }

    public String getTitle() {
        return title;
    }

    public Type getType() {
        return type;
    }

    public List<Integer> getVisibleForStakeholderIDs() {
        return visibleForStakeholderIDs;
    }

    public int getVisibleTimeframe() {
        return visibleTimeframe;
    }

    /**
     * When true an answer is required to continue the session. when false this popup will disappear automatically.
     * @return
     */
    public boolean isAnswerRequired() {

        if (this.getContentMapLink() == MapLink.BUILDINGS) {
            Building building = this.getItem(MapLink.BUILDINGS, this.getContentLinkID());
            return building.getTimeState().isAnswerRequired();
        } else if (this.getContentMapLink() == MapLink.MEASURES) {
            Measure measure = this.getItem(MapLink.MEASURES, this.getContentLinkID());
            return measure.getTimeState().isAnswerRequired();
        }
        return true;
    }

    public boolean isOpeningAutomatically() {
        return opensAutomatically;
    }

    public boolean isPanel() {
        return getContentMapLink() == MapLink.PANELS;
    }

    public boolean isRequestMyAttention(Integer stakeholderID) {

        if (!getVisibleForStakeholderIDs().contains(stakeholderID)) {
            return false;
        }
        if (overrideAttention != null) {
            return overrideAttention;
        }
        if (this.type == Type.INTERACTION || type == Type.INTERACTION_WITH_DATE) {
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        super.reset();
        JTSUtils.clearUserData(point);
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = new ArrayList<>(answers);
    }

    public void setCenterPoint(Point center) {

        if (!JTSUtils.hasZ(center) && JTSUtils.hasZ(point)) {
            JTSUtils.setZ(center, point.getZ());
        }
        this.point = center;
    }

    public void setClientY(int clientY) {
        this.clientY = clientY;
    }

    public void setColor(TColor color) {
        this.color = color;
    }

    public void setDateMillis(Long timeMillis) {
        this.calendar = timeMillis;
    }

    public void setModelDataID(Integer id) {
        this.modelDataID = id;
    }

    public void setModelType(PopupModelType modelType) {
        this.modelType = modelType;
    }

    public void setMultiPolygon(MultiPolygon polygons) {
        this.polygons = polygons;
    }

    public void setOpenAutomitically(boolean opensAutomatically) {
        this.opensAutomatically = opensAutomatically;
    }

    public void setOverrideAttention(Boolean overrideAttention) {
        this.overrideAttention = overrideAttention;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setSubject(String title) {
        this.setTitle(title);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVisibleForStakeholderIDs(List<Integer> ids) {
        visibleForStakeholderIDs = new ArrayList<>(ids);
    }

    public void setVisibleTimeframe(int visibleTimeframe) {
        this.visibleTimeframe = visibleTimeframe;
    }

    @Override
    public String toString() {
        return Integer.toString(getID()) + ": " + point;
    }
}
