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

import static nl.tytech.data.core.serializable.MapType.CURRENT;
import static nl.tytech.data.core.serializable.MapType.MAQUETTE;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.DoNotSaveToInit;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.item.Moment;
import nl.tytech.data.core.other.LargeCloneItem;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.Setting.Size;
import nl.tytech.data.engine.serializable.GridData;
import nl.tytech.util.MathUtils;
import nl.tytech.util.ObjectUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Inner Grid Overlay that caches GridData lists.
 *
 * @author Maxim Knepfle
 */
sealed public class GridInnerOverlay extends Overlay implements LargeCloneItem permits GridOverlay {

    private static final long serialVersionUID = -9114259465814386364L;

    public static final long MIN_LOG_TIME = Moment.SECOND;

    private static final GridData getUnused(MapType mapType, Item unusedItem, int index, int width, int height, int blockSize) {

        if (unusedItem instanceof GridInnerOverlay unusedGrid) {
            List<GridData> list = unusedGrid._getGridList(mapType);
            if (list != null && index >= 0 && index < list.size()) {
                GridData unusedArray = list.get(index);
                if (MathUtils.isDimension(unusedArray, width, height, blockSize)) {
                    return unusedArray;
                }
            }
        }
        return null;
    }

    /**
     * 32-bit accurate version, store only on server
     */
    @JsonIgnore
    @XMLValue
    private transient ArrayList<GridData> current = new ArrayList<>();

    /**
     * 32-bit accurate version, store only on server
     */
    @JsonIgnore
    @XMLValue
    @DoNotSaveToInit
    private transient ArrayList<GridData> maquette = new ArrayList<>();

    private final List<GridData> _getGridList(MapType mapType) {

        if (mapType == MAQUETTE) {
            return maquette;
        }

        // current may have been stored
        int count = 0;
        long start = System.currentTimeMillis();
        for (int i = 0; i < current.size(); i++) {
            if (current.get(i)._storage(getLord(), getID(), i, false)) {
                count++;
            }
        }
        if (count > 0 && System.currentTimeMillis() - start > MIN_LOG_TIME) {
            TLogger.info("Grid Storage: Loaded Overlay: " + getID() + " (" + count + " timeframes) in: "
                    + StringUtils.toSimpleTimePast(start) + ".");
        }
        return current;
    }

    private final void _validateLord() {

        if (getLord() != null && !getLord().isServerSide()) {
            throw new IllegalArgumentException("Only the Server contains grid data.");
        }
    }

    @Override
    public final GridInnerOverlay cloneItem(Item unusedItem) {

        // clone base first: note do not allow clone item method
        GridInnerOverlay cloneOverlay = ObjectUtils.deepCopy(this, false);

        // copy grid list while re-using unused objects
        for (MapType mapType : MapType.VALUES) {

            List<GridData> orginalList = _getGridList(mapType);
            ArrayList<GridData> cloneList = new ArrayList<>();

            for (int i = 0; i < orginalList.size(); i++) {
                GridData original = orginalList.get(i);
                GridData clone = getUnused(mapType, unusedItem, i, original.getWidth(), original.getHeight(), original.getBlockSize());
                if (clone != null) {
                    // recycle old Data Object
                    original.toGridData(clone);
                } else {
                    // create clone of the original
                    clone = original.toGridData();
                }
                cloneList.add(clone);
            }

            // store new cloned list
            if (mapType == MAQUETTE) {
                cloneOverlay.maquette = cloneList;
            } else {
                cloneOverlay.current = cloneList;
            }
        }
        return cloneOverlay;
    }

    /**
     * Get grid data
     */
    public GridData getRawData(MapType mapType, int timeframe) {

        _validateLord();

        List<GridData> list = mapType == MAQUETTE ? maquette : current;
        if (list.size() <= timeframe) {
            return new GridData.Zero();
        }
        GridData data = list.get(timeframe);

        // current may have been stored
        if (mapType == CURRENT) {
            long start = System.currentTimeMillis();
            if (data._storage(getLord(), getID(), timeframe, false) && System.currentTimeMillis() - start > MIN_LOG_TIME) {
                TLogger.info("Grid Storage: Loaded Overlay: " + getID() + " (timeframe " + timeframe + ") in: "
                        + StringUtils.toSimpleTimePast(start) + ".");
            }
        }
        return data;
    }

    /**
     * Get total grid byte size in memory
     */
    public long getTotalByteCount() {

        long total = 0;
        for (GridData data : current) {
            total += data.getCount(true);
        }
        for (GridData data : maquette) {
            total += data.getCount(true);
        }
        return total * Float.BYTES;
    }

    /**
     * Thread safe storage or raw grid data
     */
    public synchronized void setRawData(MapType mapType, int timeframe, Size gridSize, GridData data) {

        _validateLord();

        // multiple threads can read/write data from this object
        List<GridData> dataList = _getGridList(mapType);

        // remove remainder timeframes
        while (dataList.size() > getTimeframes()) {
            dataList.removeLast();
        }
        // override existing timeframe?
        if (dataList.size() > timeframe) {
            dataList.set(timeframe, data);
            return;
        }
        // fill with empty arrays when needed
        while (dataList.size() < timeframe) {
            dataList.add(new GridData.Zero(gridSize));
        }
        // set frame
        dataList.add(data);
    }

    /**
     * Store all current grids
     */
    public final int store() {

        _validateLord();

        int count = 0;
        for (int i = 0; i < current.size(); i++) {
            if (current.get(i)._storage(getLord(), getID(), i, true)) {
                count++;
            }
        }
        return count;
    }
}
