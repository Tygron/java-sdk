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

import nl.tytech.core.net.serializable.MapLink;

/**
 *
 * Generic relation between objects, lines, stakleholders, areas, etc.
 *
 * @author Frank Baars & Maxim Knepfle
 *
 */
public enum Relation {

    OWNER(MapLink.STAKEHOLDERS),

    CONSTRUCTOR(MapLink.STAKEHOLDERS),

    DEMOLISHER(MapLink.STAKEHOLDERS),

    PERMITTER(MapLink.STAKEHOLDERS),

    SENDER(MapLink.STAKEHOLDERS),

    RECEIVER(MapLink.STAKEHOLDERS),

    NETOWNER(MapLink.STAKEHOLDERS),

    BORDER_A(MapLink.AREAS),

    BORDER_B(MapLink.AREAS),

    BUILDING(MapLink.BUILDINGS),

    WEATHER(MapLink.WEATHERS),

    PARENT(MapLink.INDICATORS),

    RESULT_PARENT(MapLink.OVERLAYS),

    ;

    public static final Relation[] VALUES = values();

    private final MapLink mapLink;

    private Relation(MapLink mapLink) {
        this.mapLink = mapLink;
    }

    public MapLink getMapLink() {
        return mapLink;
    }
}
