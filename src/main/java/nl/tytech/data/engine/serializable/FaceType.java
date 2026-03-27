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

/**
 * Main face of a building, e.g. groundlevel wall or roof
 *
 * @author Maxim Knepfle
 */
public enum FaceType {

    BASEMENT(FunctionValue.BASEMENT_COLOR, FunctionValue.BASEMENT_HEIGHT_M, "Ba"),

    GROUND(FunctionValue.GROUND_COLOR, FunctionValue.FLOOR_HEIGHT_M, "Gr"),

    EXTRA(FunctionValue.EXTRA_COLOR, FunctionValue.FLOOR_HEIGHT_M, "Ex"),

    TOP(FunctionValue.TOP_COLOR, FunctionValue.FLOOR_HEIGHT_M, "Tl"),

    ROOF(FunctionValue.ROOF_COLOR, FunctionValue.SLANTING_ROOF_HEIGHT, "Roof");

    public static final String SOLAR_PANELS_TEXTURE = "solar_panels";

    public static final String TRANSPARENT_TEXTURE = "transparent";

    public static final FaceType[] VALUES = FaceType.values();

    private String extension;
    private FunctionValue colorValue;
    private FunctionValue heightValue;

    private FaceType(FunctionValue value, FunctionValue heightValue, String extension) {
        this.colorValue = value;
        this.heightValue = heightValue;
        this.extension = extension;
    }

    public FunctionValue getColorFunctionValue() {
        return colorValue;
    }

    public String getExtension() {
        return extension;
    }

    public FunctionValue getHeightFunctionValue() {
        return heightValue;
    }
}
