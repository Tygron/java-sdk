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

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.GridOverlay;
import nl.tytech.data.engine.item.Setting.Size;
import nl.tytech.data.engine.other.Grid;
import nl.tytech.util.MathUtils;
import nl.tytech.util.logger.TLogger;

/**
 * GridData V4 that stores data as floats and can have initial compression outside data window
 *
 * @author Maxim Knepfle
 *
 */
public class GridData extends Grid implements Serializable {

    public enum Block {
        SINGLE, BYTE, SHORT, FLOAT;
    }

    /**
     * Note: To be replaced with VarHandle and MethodHandles in future?
     */
    public static class UnsafeAccess {

        public byte getByte(float[] array, int byteIndex) {
            throw new IllegalArgumentException("Not supported for Client");
        }

        public short getShort(float[] array, int shortIndex) {
            throw new IllegalArgumentException("Not supported for Client");
        }

        public float[] putByte(float[] array, int byteIndex, byte value) {
            throw new IllegalArgumentException("Not supported for Client");
        }

        public float[] putShort(float[] array, int shortIndex, short value) {
            throw new IllegalArgumentException("Not supported for Client");
        }
    }

    public static class Zero extends GridData {

        private static final long serialVersionUID = 8497833153394924616L;

        public Zero() {
            this(0, 0);
        }

        public Zero(int width, int height) {

            super();
            super.width = width;
            super.height = height;
            super.blockSize = Math.max(1, Math.max(width, height));

            // create valid range
            while (outsideRange(super.blockSize, super.blockSize)) {
                super.blockSize /= 2;
            }

            // store valid
            super.blockWidth = (int) Math.ceil(width / (double) super.blockSize);
            super.blockHeight = (int) Math.ceil(height / (double) super.blockSize);

            // full compression to zero
            super.data = new float[super.blockWidth * super.blockHeight][];
            for (int i = 0; i < super.data.length; i++) {
                super.data[i] = createArray(0);
            }
        }

        public Zero(Size size) {
            this(size.x, size.y);
        }
    }

    private static final double BLOCK_SIZE_M = 10.0;

    private static final long RESET = -1;

    private static final long serialVersionUID = -2892497163069078807L;

    public static UnsafeAccess unsafe = new UnsafeAccess();

    private static final float[] NO_DATA = new float[] { GridOverlay.NO_DATA };
    private static final float[][] SHORTCUTS = new float[Byte.MAX_VALUE][];
    static {
        for (int i = 0; i < SHORTCUTS.length; i++) {
            SHORTCUTS[i] = new float[] { i };
        }
    }

    public static final float FLOAT_SHORT_FACTOR = 100;

    private static final float FLOAT_NO_DATA = GridOverlay.NO_DATA;

    private static final short SHORT_NO_DATA = -Short.MAX_VALUE;

    private static final byte BYTE_NO_DATA = -Byte.MAX_VALUE;

    private static final float[] createArray(float value) {

        // no data array
        if (value == GridOverlay.NO_DATA) {
            return NO_DATA;
        }
        // get shortcut value
        if (value >= 0 && value < SHORTCUTS.length && (int) value == value) {
            return SHORTCUTS[(int) value];
        }
        // create new array object
        return new float[] { value };
    }

    public static int getDefaultBlockSize(Size grid, double cellSizeM) {

        int blockSize = MathUtils.clamp((int) Math.round(BLOCK_SIZE_M / cellSizeM), 2, 10);
        while (outsideRange((int) Math.ceil(grid.x / (double) blockSize), (int) Math.ceil(grid.y / (double) blockSize))
                && !outsideRange(blockSize, blockSize)) {
            blockSize++;
        }
        return blockSize;
    }

    private static final boolean hasNaN(float[] array) {

        for (int i = 0; i < array.length; i++) {
            if (Float.isNaN(array[i])) {
                return true;
            }
        }
        return false;
    }

    private static final boolean outsideRange(int x, int y) {
        return (long) x * (long) y >= Integer.MAX_VALUE;
    }

    public static final byte toByte(float value) {
        return value == FLOAT_NO_DATA ? BYTE_NO_DATA : (byte) value;
    }

    public static final float toFloat(byte value) {
        return value == BYTE_NO_DATA ? FLOAT_NO_DATA : value;
    }

    public static final float toFloat(short value) {
        return value == SHORT_NO_DATA ? FLOAT_NO_DATA : value / FLOAT_SHORT_FACTOR;
    }

    public static final short toShort(float value) {
        return value == FLOAT_NO_DATA ? SHORT_NO_DATA : (short) Math.round(FLOAT_SHORT_FACTOR * value);
    }

    @XMLValue
    private int width = 0;

    @XMLValue
    private int height = 0;

    @XMLValue
    private int blockWidth = 1;

    @XMLValue
    private int blockHeight = 1;

    @XMLValue
    private int blockSize = 1;

    @XMLValue
    private float[][] data;

    @XMLValue
    private long count = RESET;

    @JsonIgnore
    private transient boolean logError = true;

    @JsonIgnore
    private transient int byteLength = 0;

    @JsonIgnore
    private transient int shortLength = 0;

    public GridData() {

    }

    public GridData(float[][] matrix, int blockSize) {

        this(MathUtils.getDimX(matrix), MathUtils.getDimY(matrix), blockSize);

        // set initial matrix values
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                set(x, y, matrix[y][x]);
            }
        }

        // all set, thus compress
        compress();
    }

    public GridData(int width, int height, int blockSize) {
        this(width, height, blockSize, 0, 0, width, height);
    }

    public GridData(int width, int height, int blockSize, int windowX, int windowY, int windowWidth, int windowHeight) {

        if (blockSize <= 0) {
            throw new IllegalArgumentException("Block dimensions cannot be zero or negative");
        }
        // prevent out of addressing range
        if (outsideRange(blockSize, blockSize)) {
            throw new IllegalArgumentException("Too large block, use smaller blockSize");
        }
        this.width = width;
        this.height = height;
        this.blockSize = blockSize;

        // use ceiling to get borders
        this.blockWidth = (int) Math.ceil(width / (double) blockSize);
        this.blockHeight = (int) Math.ceil(height / (double) blockSize);
        int blockArea = blockSize * blockSize;

        // prevent out of addressing range
        if (outsideRange(blockWidth, blockHeight)) {
            throw new IllegalArgumentException("Too large grid, use bigger blockSize");
        }

        // data set without initial compression when data window is entire area
        if (windowX == 0 && windowY == 0 && width == windowWidth && height == windowHeight) {
            this.data = new float[blockWidth * blockHeight][blockArea];
            return;
        }

        // create data window offsets
        this.data = new float[blockWidth * blockHeight][];
        int minX = (int) (windowX / (double) blockSize);
        int minY = (int) (windowY / (double) blockSize);
        int maxX = (int) Math.ceil((windowX + windowWidth) / (double) blockSize);
        int maxY = (int) Math.ceil((windowY + windowHeight) / (double) blockSize);

        // fill outside data window with NO DATA
        for (int by = 0; by < blockHeight; by++) {
            for (int bx = 0; bx < blockWidth; bx++) {
                if (bx >= minX && by >= minY && bx < maxX && by < maxY) {
                    this.data[by * blockWidth + bx] = new float[blockArea];
                } else {
                    this.data[by * blockWidth + bx] = NO_DATA;
                }
            }
        }
    }

    /**
     * Call to trigger compression, note: faster to to this at the end then on the fly
     *
     * WARNING: You may read from multiple threads, but never set or compress data from multiple threads simultaneously (use synchronized)
     */
    public void compress() {

        count = 0; // start compression counter
        loop: for (int b = 0; b < data.length; b++) {

            if (b > 0) { // store previous row length
                count += data[b - 1].length;
            }

            float[] array = data[b];
            if (array.length == 1) {
                continue loop;
            }

            // check for identical values
            float first = array[0];
            int blockLength = blockSize * blockSize;

            // float
            if (array.length == blockLength) {
                boolean convert = getShortLength() > 1; // cannot interfere with single
                boolean identical = true;
                for (int i = 0; i < blockLength; i++) {
                    float value = array[i];
                    identical &= first == value;
                    convert &= value == toFloat(toShort(value));
                    if (!convert && !identical) {
                        continue loop;
                    }
                }
                if (identical) {
                    data[b] = createArray(first);
                    continue loop;

                } else if (convert) {
                    data[b] = new float[getShortLength()];
                    for (int i = 0; i < blockLength; i++) {
                        unsafe.putShort(data[b], i, toShort(array[i]));
                    }
                    if (hasNaN(data[b])) {
                        data[b] = array; // flip back, NaN is evil
                    } else {
                        array = data[b]; // set and maybe compress more
                    }
                }
            }

            // short
            if (array.length == getShortLength()) {
                boolean convert = getByteLength() > 1; // cannot interfere with single
                boolean identical = true;
                for (int i = 0; i < blockLength; i++) {
                    float value = toFloat(unsafe.getShort(array, i));
                    identical &= first == value;
                    convert &= value == toFloat(toByte(value));
                    if (!convert && !identical) {
                        continue loop;
                    }
                }
                if (identical) {
                    data[b] = createArray(first);
                    continue loop;

                } else if (convert) {
                    data[b] = new float[getByteLength()];
                    for (int i = 0; i < blockLength; i++) {
                        unsafe.putByte(data[b], i, toByte(toFloat(unsafe.getShort(array, i))));
                    }
                    if (hasNaN(data[b])) {
                        data[b] = array; // flip back, NaN is evil
                    } else {
                        array = data[b]; // set and maybe compress more
                    }
                }
            }

            // byte
            if (array.length == getByteLength()) {
                for (int i = 0; i < blockLength; i++) {
                    if (first != toFloat(unsafe.getByte(array, i))) {
                        continue loop;
                    }
                }
                // indentical: compress to single value
                data[b] = createArray(first);
            }
        }

        if (data.length > 0) { // store last row length
            count += data[data.length - 1].length;
        }
    }

    @Override
    public float get(int x, int y) {

        // get block X Y
        int bx = x / blockSize;
        int by = y / blockSize;
        float[] block = data[by * blockWidth + bx];
        if (block.length == 1) {
            return block[0];
        }

        // get block cell Index X Y
        int cx = x - bx * blockSize;
        int cy = y - by * blockSize;
        int index = cy * blockSize + cx;
        return getBlockValue(block, index);
    }

    public final int getBlockCount(Block type) {

        int count = 0;
        int length = switch (type) {
            case SINGLE -> 1;
            case BYTE -> getByteLength();
            case SHORT -> getShortLength();
            case FLOAT -> blockSize * blockSize;
        };
        for (int i = 0; i < data.length; i++) {
            if (data[i].length == length) {
                count++;
            }
        }
        return count;
    }

    public final int getBlockHeight() {
        return blockHeight;
    }

    public final int getBlockSize() {
        return blockSize;
    }

    private float getBlockValue(float[] array, int index) {

        int blockLength = blockSize * blockSize;

        if (array.length == getByteLength()) {
            return toFloat(unsafe.getByte(array, index));

        } else if (array.length == getShortLength()) {
            return toFloat(unsafe.getShort(array, index));

        } else if (array.length == blockLength) {
            return array[index];

        } else {
            if (logError) { // single log to prevent over doing it
                TLogger.severe("ERROR getting block index: " + index + " from array length: " + array.length);
                logError = false;
            }
            return 0f;// invalid
        }
    }

    public final int getBlockWidth() {
        return blockWidth;
    }

    private final int getByteLength() {

        if (byteLength == 0) { // cache expensive value
            byteLength = (int) Math.ceil(blockSize * blockSize / 4.0);
        }
        return byteLength;
    }

    public double getCompressionRatio() {
        return getCount(false) / (double) getCount(true);
    }

    public double getCompressionSavings() {
        return (getCount(false) - getCount(true)) / (double) getCount(false);
    }

    public long getCount(boolean compressed) {

        if (compressed) {
            if (count <= RESET) {
                count = 0;
                for (int i = 0; i < data.length; i++) {
                    count += data[i].length;
                }
            }
            return count;
        }
        return (long) width * (long) height;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private final int getShortLength() {

        if (shortLength == 0) { // cache expensive value
            shortLength = (int) Math.ceil(blockSize * blockSize / 2.0);
        }
        return shortLength;
    }

    public final Float getSingleBlockValue(int bx, int by) {

        // returns either a single value for entire block or null
        float[] b = data[by * blockWidth + bx];
        return b.length == 1 ? b[0] : null;
    }

    @Override
    public final Size getSize() {
        return new Size(width, height);
    }

    public final String getStats() {

        if (isEmpty()) {
            return "Empty";
        }
        return MathUtils.round(getCompressionRatio(), 1) + "x (" + getBlockCount(Block.SINGLE) + " " + getBlockCount(Block.BYTE) + " "
                + getBlockCount(Block.SHORT) + " " + getBlockCount(Block.FLOAT) + ")";
    }

    @Override
    public int getWidth() {
        return width;
    }

    public boolean isEmpty() {
        return width == 0 || height == 0;
    }

    public final void resetCount() {
        count = RESET;
    }

    /**
     * Set value in grid data matrix
     *
     * WARNING: You may read from multiple threads, but never set or compress data from multiple threads simultaneously (use synchronized)
     */
    @Override
    public void set(int x, int y, float value) {

        // get block X Y
        int bx = x / blockSize;
        int by = y / blockSize;
        int blockID = by * blockWidth + bx;
        float[] array = data[blockID];
        int blockLength = blockSize * blockSize;
        resetCount(); // reset on changes

        float single = array[0];
        if (array.length == 1) {
            // decompression: change to multi value block
            if (single != value) {
                array = new float[blockLength];

                // get block cell X Y for new value
                int cx = x - bx * blockSize;
                int cy = y - by * blockSize;
                int cellID = cy * blockSize + cx;

                // fill with previous or new value at cellID
                for (int i = 0; i < blockLength; i++) {
                    array[i] = i == cellID ? value : single;
                }
                data[blockID] = array;
            }
            return;
        }

        if (array.length == getByteLength()) { // convert byte to float
            data[blockID] = new float[blockLength];
            for (int i = 0; i < blockLength; i++) {
                data[blockID][i] = toFloat(unsafe.getByte(array, i));
            }
            array = data[blockID];

        } else if (array.length == getShortLength()) {// convert short to float
            data[blockID] = new float[blockLength];
            for (int i = 0; i < blockLength; i++) {
                data[blockID][i] = toFloat(unsafe.getShort(array, i));
            }
            array = data[blockID];
        }

        // must be float array now
        if (array.length == blockLength) {
            // get block cell X Y for new value
            int cx = x - bx * blockSize;
            int cy = y - by * blockSize;
            int cellID = cy * blockSize + cx;
            array[cellID] = value;

        } else if (logError) { // single log to prevent over doing it
            TLogger.severe("ERROR setting: " + x + " " + y + " value: " + value + " block array length: " + array.length);
            logError = false;
        }
    }

    public final GridData toGridData() {

        // copy object
        GridData clone = new GridData();
        clone.width = this.width;
        clone.height = this.height;
        clone.blockWidth = this.blockWidth;
        clone.blockHeight = this.blockHeight;
        clone.blockSize = this.blockSize;
        clone.count = this.count;
        clone.data = new float[this.data.length][];

        // copy data
        for (int i = 0; i < data.length; i++) {
            clone.data[i] = new float[this.data[i].length];
            System.arraycopy(this.data[i], 0, clone.data[i], 0, this.data[i].length);
        }
        return clone;
    }

    public void toGridData(GridData other) {

        if (!MathUtils.isDimension(other, width, height, blockSize)) {
            throw new IllegalArgumentException("Can only copy to same dimension matrix");
        }

        for (int i = 0; i < data.length; i++) {
            if (this.data[i].length == 1) {
                other.data[i] = this.data[i]; // static value
            } else {
                // convert other when not the same length
                if (other.data[i].length != this.data[i].length) {
                    other.data[i] = new float[this.data[i].length];
                }
                // copy array
                System.arraycopy(this.data[i], 0, other.data[i], 0, this.data[i].length);
            }
        }
        // update others counter
        other.count = this.count;
    }

    @Override
    public float[][] toMatrix() {

        float[][] matrix = new float[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                matrix[y][x] = get(x, y);
            }
        }
        return matrix;
    }

    @Override
    public String toString() {
        return width + "x" + height + " (block: " + blockSize + ")";
    }
}
