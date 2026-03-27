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
import java.util.ArrayList;
import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.StringUtils;

/**
 * Neural Network Tensor
 *
 * @author Frank Baars
 *
 */
public class Tensor implements Serializable, Comparable<Tensor> {

    public enum DataType {

        FLOAT, DOUBLE, INT8, INT16, INT32, INT64, BOOL, STRING, UINT8, FLOAT16, BFLOAT16, UNKNOWN;

        public static final DataType[] VALUES = values();

        public static DataType get(String dataType) {
            if (!StringUtils.containsData(dataType)) {
                return null;
            }
            for (DataType type : VALUES) {
                if (dataType.equals(type.name())) {
                    return type;
                }
            }
            return null;
        }
    }

    private static final long serialVersionUID = -6603771027093609509L;

    @XMLValue
    private String name = "";

    @XMLValue
    private int[] dimensions = {};

    @XMLValue
    private DataType type = DataType.UNKNOWN;

    @XMLValue
    private boolean input = true;

    @XMLValue
    private boolean normalize = true;

    public Tensor() {

    }

    public Tensor(String name, boolean input) {
        this.name = name;
        this.input = input;
    }

    @Override
    public int compareTo(Tensor o) {
        return this.name.compareTo(o.name);
    }

    public int getDimension(int i) {
        return i >= 0 && i < dimensions.length ? dimensions[i] : 0;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public String getDimInfo() {
        String result = dimensions.length == 0 ? "" : dimensions[0] + "";
        for (int i = 1; i < dimensions.length; i++) {
            result += " x " + dimensions[i];
        }
        return result;
    }

    public int getHeight() {
        return dimensions.length > 1 ? dimensions[dimensions.length - 2] : 0;
    }

    public List<int[]> getInputCoordinates() {

        int width = getDimension(dimensions.length - 1);
        int height = getDimension(dimensions.length - 2);
        if (width == 0 || height == 0) {
            return new ArrayList<>();
        }
        ArrayList<int[]> result = new ArrayList<>();
        if (dimensions.length == 2) {
            result.add(new int[] { 0 });
            return result;
        } else {
            result.add(new int[] {});
        }

        for (int i = dimensions.length - 3; i >= 0; i--) {
            List<int[]> previous = result;
            result = new ArrayList<>();
            for (int d = 0; d < dimensions[i]; d++) {
                for (int r = 0; r < previous.size(); r++) {
                    int[] array = new int[previous.get(r).length + 1];
                    array[0] = d;
                    for (int a = 0; a < previous.get(r).length; a++) {
                        array[a + 1] = previous.get(r)[a];
                    }
                    result.add(array);
                }
            }
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public int getNumDimensions() {
        return dimensions.length;
    }

    public DataType getType() {
        return type;
    }

    public int getWidth() {
        return dimensions.length > 0 ? dimensions[dimensions.length - 1] : 0;
    }

    public boolean isInput() {
        return input;
    }

    public boolean isNormalize() {
        return normalize;
    }

    public boolean isOutput() {
        return !input;
    }

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
    }

    public void setInput(boolean isInput) {
        this.input = isInput;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNormalize(boolean normalize) {
        this.normalize = normalize;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getName();
    }
}
