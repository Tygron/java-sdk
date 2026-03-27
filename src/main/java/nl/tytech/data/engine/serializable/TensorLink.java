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

import static nl.tytech.util.StringUtils.COLON;
import static nl.tytech.util.StringUtils.EMPTY;
import static nl.tytech.util.StringUtils.UNDER_SCORE;
import static nl.tytech.util.StringUtils.WHITESPACE;
import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.InferenceOverlay.InferencePrequel;
import nl.tytech.data.engine.item.InferenceOverlay.InferenceResult;
import nl.tytech.util.StringUtils;

/**
 * Link between Neural Network and Tensor
 *
 * @author Frank Baars
 *
 */
public class TensorLink implements Serializable {

    public static enum ValueType {

        DEFAULT, R, G, B, A;

    }

    private static final long serialVersionUID = -6898950516772476132L;

    public static final String NORMALIZED = "normalized";

    private static final Pattern PATTERN = Pattern.compile("_([a-z]{1}(:[rgba]+)?)+");

    public static String generateName(String name, int[] coordinate) {
        return (name + " " + StringUtils.arrayToHumanString(coordinate, COLON)).trim();
    }

    @XMLValue
    private String tensorName = StringUtils.EMPTY;

    @XMLValue
    private String prequel = null;

    @XMLValue
    private String result = null;

    @XMLValue
    private boolean normalized = false;

    @XMLValue
    private ValueType valueType = ValueType.DEFAULT;

    @XMLValue
    private int[] coordinate = new int[0];

    @XMLValue
    private Integer id = Item.NONE;

    public TensorLink() {

    }

    public TensorLink(Integer id, boolean input, String tensorName, int[] coordinate, int[] parentDimensions) {
        this.id = id;
        this.tensorName = tensorName;
        this.coordinate = coordinate;

        if (input) {
            setInputParameters(parentDimensions);
        } else {
            setOutputParameters();
        }
    }

    private int getChannelDimension(int[] parentDimensions, CharSequence valueTypeChars) {
        int parentDim = Arrays.stream(parentDimensions).filter(parentDimension -> parentDimension == valueTypeChars.length()).findFirst()
                .orElse(Arrays.stream(parentDimensions).filter(parentDimension -> parentDimension >= valueTypeChars.length()).findFirst()
                        .orElse(-1));
        if (parentDim < 0) {
            return -1;
        }
        int coord = -1;
        for (int p = 0; p < parentDimensions.length && p < coordinate.length; p++) {
            if (parentDim == parentDimensions[p]) {
                coord = coordinate[p];
                break;
            }
        }
        return coord;

    }

    public int[] getCoordinate() {
        return coordinate;
    }

    public Integer getID() {
        return id;
    }

    public String getName() {
        return (tensorName + WHITESPACE + StringUtils.arrayToHumanString(coordinate, COLON)).trim();
    }

    public InferencePrequel getPrequel() {
        for (InferencePrequel preq : InferencePrequel.VALUES) {
            if (preq.name().equals(prequel)) {
                return preq;
            }
        }
        return null;
    }

    public InferenceResult getResult() {
        for (InferenceResult result : InferenceResult.VALUES) {
            if (result.name().equals(this.result)) {
                return result;
            }
        }
        return null;
    }

    public String getTensorName() {
        return tensorName;
    }

    public ValueType getValueType() {
        return valueType;
    }

    private ValueType getValueType(int channelCoord, String valueTypeChars) {

        if (channelCoord >= 0 && channelCoord < valueTypeChars.length()) {
            for (ValueType type : ValueType.values()) {
                if (valueTypeChars.charAt(channelCoord) == type.name().toLowerCase().charAt(0)) {
                    return type;
                }
            }
        }
        return ValueType.DEFAULT;
    }

    public boolean isNormalized() {
        return normalized;
    }

    private String parsePrequel(String lowerCaseName, int coord, String[] patterns) {

        if (patterns.length == 0) {
            return InferencePrequel.A.name();
        } else if (patterns.length == 1 && patterns[0].length() > 1 && !patterns[0].contains(COLON)) {
            return InferencePrequel.A.name();
        }

        int patternIndex = 0;
        String[] pattern = patterns[patternIndex].split(COLON);

        int charIndex = 0;
        for (int c = 0; c <= coord; c++, charIndex++) {
            if (pattern.length == 1 || charIndex >= pattern[1].length()) {
                if (patternIndex < pattern.length - 1) {
                    pattern = patterns[++patternIndex].split(COLON);
                    charIndex = 0;
                } else {
                    break;
                }
            }
        }
        return pattern[0].toUpperCase();
    }

    public void setID(int id) {
        this.id = id;
    }

    private void setInputParameters(int[] parentDimensions) {

        String lowerCaseName = tensorName.toLowerCase().replace(UNDER_SCORE + NORMALIZED, StringUtils.EMPTY);

        String[] patterns = PATTERN.matcher(lowerCaseName).results().map(match -> lowerCaseName.substring(match.start() + 1, match.end()))
                .toArray(String[]::new);

        String valueTypeChars = Arrays.stream(patterns)
                .map(pattern -> pattern.contains(COLON) ? pattern.split(COLON)[1] : pattern.length() > 1 ? pattern : EMPTY)
                .reduce((s1, s2) -> s1 + s2).orElse(EMPTY);

        int channelCoord = getChannelDimension(parentDimensions, valueTypeChars);

        this.valueType = getValueType(channelCoord, valueTypeChars);

        this.normalized = tensorName.toLowerCase().contains(NORMALIZED);

        this.prequel = parsePrequel(lowerCaseName, channelCoord, patterns);
    }

    public void setNormalized(boolean normalize) {
        this.normalized = normalize;
    }

    private void setOutputParameters() {

        for (InferenceResult result : InferenceResult.VALUES) {
            if (result.hasTensorName(tensorName)) {
                this.result = result.name();
            }
        }
    }

    public void setPrequel(InferencePrequel prequel) {
        this.prequel = prequel == null ? null : prequel.name();
    }

    public void setResult(InferenceResult result) {
        this.result = result == null ? null : result.name();
    }

    public void setTensorName(String name) {
        this.tensorName = name;
    }

    public void setValueType(ValueType type) {
        this.valueType = type;
    }

    @Override
    public String toString() {
        return getName();
    }
}
