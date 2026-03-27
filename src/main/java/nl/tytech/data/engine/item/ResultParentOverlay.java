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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.data.engine.serializable.Relation;

/**
 * Overlay that calculates results for children
 *
 * @author Maxim Knepfle
 */
public abstract class ResultParentOverlay<R extends ResultType, P extends PrequelType> extends GridOverlay<R, P> {

    private static final long serialVersionUID = -2572949484529750421L;

    @Override
    public Integer getRelationID(Relation relation) { // parent == myself
        return relation == Relation.RESULT_PARENT ? getID() : super.getRelationID(relation);
    }

    public ResultChildOverlay<R, P> getResultChild(R resultType) {
        return getResultChildrenStream().filter(c -> c.getResultType() == resultType).findAny().orElse(null);
    }

    public ResultChildOverlay<R, P> getResultChild(String resultType) {
        return getResultChildrenStream().filter(c -> c.getResultType().name().equals(resultType)).findAny().orElse(null);
    }

    public List<ResultChildOverlay<R, P>> getResultChildren() {
        return getResultChildrenStream().collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Stream<ResultChildOverlay<R, P>> getResultChildrenStream() {
        return (Stream<ResultChildOverlay<R, P>>) super.getChildren().stream().filter(o -> o instanceof ResultChildOverlay);
    }

    public GridOverlay<R, P> getResultOverlay(R selectType) {
        return this.getItem(MapLink.OVERLAYS, getResultOverlayID(selectType));
    }

    public Integer getResultOverlayID(R selectType) {

        // return parent ID
        if (selectType == this.getResultType()) {
            return this.getID();
        }

        // return child ID
        ResultChildOverlay<R, P> child = this.getResultChild(selectType);
        return child != null ? child.getID() : Item.NONE;
    }

    public List<R> getResultTypes() {

        Set<R> results = new LinkedHashSet<>();
        results.add(this.getResultType());
        getResultChildrenStream().forEach(c -> results.add(c.getResultType()));
        return new ArrayList<>(results);
    }

    @SuppressWarnings("unchecked")
    public boolean hasResult(R... typeArray) {

        for (R type : typeArray) {
            if (this.getResultType() == type || this.hasResultChild(type)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasResultChild(Integer overlayID) {
        return getResultChildrenStream().anyMatch(c -> c.getID().equals(overlayID));
    }

    public boolean hasResultChild(R type) {
        return getResultChildrenStream().anyMatch(c -> c.getResultType() == type);
    }

    @Override
    public boolean isActivePrequel() {

        boolean isPrequel = super.isActivePrequel();
        if (!isPrequel) {
            ItemMap<Overlay> overlays = this.getMap(MapLink.OVERLAYS);
            for (Overlay child : getChildren()) {
                if (child instanceof ResultChildOverlay) {
                    for (Overlay overlay : overlays) {
                        if (overlay instanceof GridOverlay<?, ?> go && go.hasPrequel(child.getID()) && overlay.isActive()) {
                            return true;
                        }
                    }
                }
            }
        }
        return isPrequel;
    }
}
