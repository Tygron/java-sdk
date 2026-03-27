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
package nl.tytech.data.editor.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.ServiceSource;
import nl.tytech.data.core.item.Source;
import nl.tytech.data.core.item.Source.SourceInterface;
import nl.tytech.data.core.serializable.GeoFormat;
import nl.tytech.data.editor.serializable.GeoLinkType;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * GeoPlugin
 *
 * @author Frank Baars
 */
public class GeoPlugin extends AbstractGeoPlugin implements SourceInterface {

    private static final long serialVersionUID = 1123274150018584188L;

    public static boolean isSupported(GeoFormat geoFormat) {
        switch (geoFormat) {
            case WFS_GML:
            case WFS_JSON:
                return true;
            default:
                return false;
        }
    }

    @XMLValue
    @ItemIDField(MapLink.SOURCES)
    private Integer sourceID = Item.NONE;

    @XMLValue
    @ItemIDField(MapLink.GEO_LINKS)
    private ArrayList<Integer> geoLinkIDs = new ArrayList<>();

    @XMLValue
    private String layerName = StringUtils.EMPTY;

    @XMLValue
    private String nameAttribute = StringUtils.EMPTY;

    @XMLValue
    private String idAttribute = StringUtils.EMPTY;

    public GeoPlugin() {
        super();
    }

    public GeoPlugin(GeoLinkType linkType) {
        super(linkType);
    }

    public void addLink(Integer geoLinkID) {
        geoLinkIDs.add(geoLinkID);
    }

    @Override
    public boolean addSource(Integer sourceID) {
        return setSourceID(sourceID);
    }

    public String getIDAttribute() {
        return idAttribute;
    }

    public String getLayerName() {
        return layerName;
    }

    public Collection<GeoLink> getLinks() {
        return getItems(MapLink.GEO_LINKS, geoLinkIDs);
    }

    public String getNameAttribute() {
        return nameAttribute;
    }

    public Source getSource() {
        return getItem(MapLink.SOURCES, sourceID);
    }

    public Integer getSourceID() {
        return sourceID;
    }

    @Override
    public List<Integer> getSourceIDs() {
        List<Integer> sourceIDs = new ArrayList<>();
        sourceIDs.add(sourceID);
        return sourceIDs;
    }

    @Override
    public List<Source> getSources() {
        return getItems(MapLink.SOURCES, Arrays.asList(sourceID));
    }

    public boolean hasLink(Integer geoLinkID) {
        return geoLinkIDs.contains(geoLinkID);
    }

    public boolean hasSource() {
        return !Item.NONE.equals(sourceID) && getSource() != null;
    }

    @Override
    public boolean hasSourceID(Integer sourceID) {
        return this.sourceID.equals(sourceID);
    }

    public boolean removeLink(Integer geoLinkID) {
        return geoLinkIDs.remove(geoLinkID);
    }

    @Override
    public boolean removeSourceID(Integer sourceID) {
        if (!this.sourceID.equals(sourceID)) {
            return false;
        }
        this.sourceID = Item.NONE;
        return true;

    }

    public void setIDAttribute(String idAttribute) {
        this.idAttribute = idAttribute;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public void setNameAttribute(String nameAttribute) {
        this.nameAttribute = nameAttribute;
    }

    @Override
    public boolean setSourceID(Integer sourceID) {
        if (this.sourceID.equals(sourceID)) {
            return false;
        }
        this.sourceID = sourceID;
        return true;
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);

        if (getSource() != null && !(getSource() instanceof ServiceSource)) {
            TLogger.warning("Removing invalid Source: " + getSource() + " from Plugin: " + getName());
            setSourceID(Item.NONE);
            setLayerName(StringUtils.EMPTY);
            setIDAttribute(StringUtils.EMPTY);
            setNameAttribute(StringUtils.EMPTY);
        }

        return result;
    }
}
