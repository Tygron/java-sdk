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
package nl.tytech.data.core.serializable;

/**
 * MapType: defines the map you are playing in. e.g. current or maquette.
 *
 * @author Maxim Knepfle
 */
public enum MapType {

    /**
     * This map show the actual situation at this moment in the simulation.
     */
    CURRENT,

    /**
     * This map shows also shows the planned building etc that are not yet in the actual/current map.
     */
    MAQUETTE;

    /**
     * Static reference to prevent creating new value arrays each time called. Best Practice
     */
    public static final MapType[] VALUES = MapType.values();

    /**
     * Name in web queries
     */
    public static final String QUERY = "maptype";

    public static final MapType[] CURRENT_ARRAY = new MapType[] { MapType.CURRENT };
    public static final MapType[] MAQUETTE_ARRAY = new MapType[] { MapType.MAQUETTE };

}
