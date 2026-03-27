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
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Source.SourceInterface;
import nl.tytech.data.core.serializable.GeoFormat;
import nl.tytech.data.editor.serializable.GeoLinkType;

/**
 * GeoPlugin
 * @author Frank Baars
 */
public class HeightPlugin extends AbstractGeoPlugin implements SourceInterface {

    private static final long serialVersionUID = 1123274150018584588L;

    public static boolean isSupported(GeoFormat geoFormat) {
        switch (geoFormat) {
            case GEOTIFF:
                return true;
            default:
                return false;
        }
    }

    @XMLValue
    @ItemIDField(MapLink.SOURCES)
    private ArrayList<Integer> sourceIDs = new ArrayList<>();

    public HeightPlugin() {
        this(GeoLinkType.TERRAIN_HEIGHT);
    }

    public HeightPlugin(GeoLinkType type) {
        super(type);
    }

    @Override
    public boolean addSource(Integer sourceID) {
        return setSourceID(sourceID);
    }

    @Override
    public List<Integer> getSourceIDs() {
        return sourceIDs;
    }

    @Override
    public boolean hasSourceID(Integer sourceID) {
        return sourceIDs.contains(sourceID);
    }

    @Override
    public boolean removeSourceID(Integer sourceID) {
        return sourceIDs.remove(sourceID);
    }

    @Override
    public boolean setSourceID(Integer sourceID) {
        if (sourceIDs.contains(sourceID)) {
            return false;
        }
        sourceIDs.add(sourceID);
        return true;
    }

}
