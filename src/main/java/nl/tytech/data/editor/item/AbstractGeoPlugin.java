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

import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Source;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.data.editor.serializable.GeoLinkType;
import nl.tytech.util.StringUtils;

/**
 * AbstractGeoPlugin
 * @author Frank Baars
 */
public abstract class AbstractGeoPlugin extends UniqueNamedItem {

    private static final long serialVersionUID = 1123274151118584588L;

    @XMLValue
    private String crs = StringUtils.EMPTY;

    @XMLValue
    private boolean forceXY = true;

    @XMLValue
    private boolean newProject = true;

    @XMLValue
    private GeoLinkType linkType = GeoLinkType.AREA;

    public AbstractGeoPlugin() {

    }

    public AbstractGeoPlugin(GeoLinkType linkType) {
        this.linkType = linkType;
    }

    public String getCrsName() {
        return crs;
    }

    public GeoLinkType getLinkType() {
        return linkType;
    }

    public abstract List<Integer> getSourceIDs();

    public List<Source> getSources() {
        return this.<Source> getItems(MapLink.SOURCES, getSourceIDs());
    }

    public abstract boolean hasSourceID(Integer sourceID);

    public boolean isForcedXY() {
        return forceXY;
    }

    public boolean isNewProject() {
        return newProject;
    }

    public abstract boolean removeSourceID(Integer sourceID);

    public void setCrsName(String crsName) {
        this.crs = crsName;
    }

    public void setForcedXY(boolean forcedXY) {
        this.forceXY = forcedXY;
    }

    public void setNewProject(boolean newProject) {
        this.newProject = newProject;
    }

    public abstract boolean setSourceID(Integer sourceID);

}
