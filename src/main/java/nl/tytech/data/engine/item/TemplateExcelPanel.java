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

import org.locationtech.jts.geom.Point;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.engine.item.Overlay.OverlayType;
import nl.tytech.data.engine.item.WaterOverlay.WaterKey;
import nl.tytech.data.engine.other.TemplatePanel;
import nl.tytech.data.engine.serializable.FunctionValue;
import nl.tytech.data.engine.serializable.PopupModelType;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.util.StringUtils;

/**
 * Panel that serves as a template for Excel panels.
 *
 * @author Maxim Knepfle
 */
public class TemplateExcelPanel extends ExcelPanel implements TemplatePanel {

    public enum PanelTemplate {

        TRAFFIC_NOISE(MapLink.NEIGHBORHOODS, 14, OverlayType.TRAFFIC_NOISE, PopupModelType.TRAFFIC_NOISE, StringUtils.EMPTY, false),

        DANGEROUS_OBJECTS(MapLink.BUILDINGS, 15, OverlayType.SAFETY_DISTANCE, PopupModelType.DECLINED,
                FunctionValue.SAFETY_DISTANCE_M.name(), false),

        NETWORK_INTERACTION(MapLink.NET_CLUSTERS, 16, OverlayType.NETWORK_OWNERSHIP, PopupModelType.CONNECT_OFF, StringUtils.EMPTY, false),

        WATER_AREAS(MapLink.AREAS, 21, OverlayType.RAINFALL, PopupModelType.ELEVATION, WaterKey.WATER_LEVEL.name(), false),

        WEIRS(MapLink.BUILDINGS, 22, OverlayType.RAINFALL, PopupModelType.WATER_WEIR, WaterKey.WEIR_HEIGHT.name(), false),

        ;

        private MapLink mapLink;
        private Integer excelID;
        private OverlayType overlayType;
        private PopupModelType modelType;
        private String attribute;
        private boolean attention;

        private PanelTemplate(MapLink mapLink, Integer excelID, OverlayType overlay, PopupModelType model, String attribute,
                boolean attention) {
            this.mapLink = mapLink;
            this.excelID = excelID;
            this.overlayType = overlay;
            this.modelType = model;
            this.attribute = attribute;
            this.attention = attention;
        }

        public String getAttribute() {
            return attribute;
        }

        public Integer getExcelID() {
            return excelID;
        }

        public MapLink getMapLink() {
            return mapLink;
        }

        public PopupModelType getModelType() {
            return modelType;
        }

        public String getName() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }

        public OverlayType getOverlayType() {
            return overlayType;
        }

        public boolean isAttention() {
            return attention;
        }
    }

    private static final long serialVersionUID = 2805451253961720701L;

    @XMLValue
    private MapLink mapLink = MapLink.NEIGHBORHOODS;

    @XMLValue
    private String attribute = StringUtils.EMPTY;

    @XMLValue
    private Relation relation = null;

    @XMLValue
    private boolean useOwner = false;

    @XMLValue
    private boolean autoApplied = false;

    public TemplateExcelPanel() {
        super(PanelType.TEMPLATE_EXCEL_PANEL);
    }

    public TemplateExcelPanel(MapLink mapLink, Integer excelID) {
        this();
        this.setMapLink(mapLink);
        this.setExcelID(excelID);
    }

    @Override
    public String getAttribute() {
        return attribute;
    }

    @Override
    public long getCalcTimeMS() {

        long totalCalcTimeMS = 0;
        ItemMap<Panel> panels = getMap(MapLink.PANELS);

        for (Panel panel : panels.values()) {
            if (panel.getType() == PanelType.EXCEL_PANEL) {
                ExcelPanel ePanel = (ExcelPanel) panel;
                if (this.getID().equals(ePanel.getParentID())) {
                    totalCalcTimeMS += ePanel.getCalcTimeMS();
                }
            }
        }
        return totalCalcTimeMS;
    }

    @Override
    public MapLink getMapLink() {
        return mapLink;
    }

    @Override
    public Point getPoint() {
        return null;
    }

    @Override
    public Relation getRelation() {
        return relation;
    }

    @Override
    public String getText() {
        return "Apply Template first to see contents.";
    }

    @Override
    public boolean hasRelation() {
        return relation != null;
    }

    @Override
    public boolean isAutoApplied() {
        return autoApplied;
    }

    @Override
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public void setAutoApplied(boolean autoApplied) {
        this.autoApplied = autoApplied;
    }

    @Override
    public void setMapLink(MapLink mapLink) {
        this.mapLink = mapLink;
    }

    @Override
    public void setPoint(Point point) {
        throw new UnsupportedOperationException("Template panels are not allow to have a Map Point!");
    }

    @Override
    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    @Override
    public void setUseOwner(boolean useOwner) {
        this.useOwner = useOwner;
    }

    @Override
    public boolean useOwner() {
        return useOwner;
    }
}
