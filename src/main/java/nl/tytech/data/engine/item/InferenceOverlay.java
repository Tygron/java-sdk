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

import static nl.tytech.data.engine.item.NeuralNetwork.NeuralNetworkAttribute.MAX_CELL_SIZE_M;
import static nl.tytech.data.engine.item.NeuralNetwork.NeuralNetworkAttribute.MIN_CELL_SIZE_M;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.item.annotations.ItemIDField;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.engine.item.InferenceOverlay.InferencePrequel;
import nl.tytech.data.engine.item.InferenceOverlay.InferenceResult;
import nl.tytech.data.engine.other.PrequelType;
import nl.tytech.data.engine.other.ResultType;
import nl.tytech.data.engine.serializable.Tensor;
import nl.tytech.data.engine.serializable.TensorLink;
import nl.tytech.util.StringUtils;

/**
 * AI Inference Overlay
 *
 * @author Maxim Knepfle
 */
public class InferenceOverlay extends ResultParentOverlay<InferenceResult, InferencePrequel> {

    public enum InferenceAttribute implements ReservedAttribute {

        MASK_THRESHOLD(Double.class, 0.7),

        SCORE_THRESHOLD(Double.class, 0.95),

        INFERENCE_MODE(InferenceMode.class, 0),

        STRIDE_FRACTION(Double.class, 0.5),

        BOX_OVERLAP(Double.class, 1.0),

        PRIORITIZE_LABELS(Double.class, 1.0),

        GPU_MEMORY_MB(Double.class, 3000.0), // default 3GB mem

        ;

        public static final InferenceAttribute[] VALUES = InferenceAttribute.values();

        private final Class<?> type;
        private final double[] defaultArray;

        private InferenceAttribute(Class<?> type, double defaultValue) {
            this.type = type;
            this.defaultArray = new double[] { defaultValue };
        }

        @Override
        public double[] defaultArray() {
            return defaultArray;
        }

        @Override
        public double defaultValue() {
            return defaultArray()[0];
        }

        @Override
        public Class<?> getType() {
            return type;
        }

    }

    public enum InferenceMode {

        CLASSIFICATION, // classifies a picture, predicts probability of object
        // LOCALIZATION, // detects an object in a picture, predicts probability of object and where it is located
        BBOX_DETECTION, // detects up to several objects in picture, predicts probabilities of objects and where they are located
        // LANDMARK_DETECTION //Detects a shape or characteristics of an object (e.g. eyes)
        ;

        public static InferenceMode fromValue(double attribute) {

            for (InferenceMode b : InferenceMode.values()) {
                if (b.ordinal() == attribute) {
                    return b;
                }
            }
            return InferenceMode.CLASSIFICATION;
        }

        public double getValue() {
            return ordinal();
        }

    }

    public enum InferencePrequel implements PrequelType {

        A, B, C;

        public static final InferencePrequel[] VALUES = InferencePrequel.values();
    }

    public enum InferenceResult implements ResultType {

        LABELS(0, "labels"), //

        SCORES(1, "scores"), //

        BOXES(2, "boxes"), //

        MASKS(3, "masks"), //

        DEBUG_GT_INSTANCES(4), //

        CENTER_POINTS(5),//

        ;

        public static final InferenceResult[] VALUES = InferenceResult.values();

        public static final InferenceResult valueOf(byte index) {
            for (InferenceResult result : VALUES) {
                if (result.index == index) {
                    return result;
                }
            }
            return null;
        }

        private final String[] tensorNames;
        private final byte index;

        private InferenceResult(int index, String... tensorNames) {
            this.tensorNames = tensorNames;
            this.index = (byte) index;
        }

        @Override
        public byte getIndex() {
            return index;
        }

        public boolean hasTensorName(String tensorName) {

            for (String name : tensorNames) {
                if (name.equalsIgnoreCase(tensorName)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isStatic() {
            return false;
        }

        @Override
        public String toString() {
            return StringUtils.capitalizeWithSpacedUnderScores(this);
        }
    }

    public static final double MAX_GPU_MEMORY_MB = 5000;

    private static final long serialVersionUID = -409762925418107164L;

    @XMLValue
    private ArrayList<TensorLink> tensorLinks = new ArrayList<>();

    @XMLValue
    @ItemIDField(MapLink.NEURAL_NETWORKS)
    private Integer neuralNetworkID = Item.NONE;

    @Override
    protected InferenceResult getDefaultResult() {
        return InferenceResult.LABELS;
    }

    public NeuralNetwork getNeuralNetwork() {
        return getItem(MapLink.NEURAL_NETWORKS, getNeuralNetworkID());
    }

    public Integer getNeuralNetworkID() {
        return neuralNetworkID;
    }

    @Override
    public String getPrequelDescription(InferencePrequel prequelType) {

        for (TensorLink link : tensorLinks) {
            if (link.getPrequel() == prequelType) {
                return StringUtils.capitalizeFirstLetter(link.getTensorName()).replace(TensorLink.NORMALIZED, "")
                        .replace(StringUtils.UNDER_SCORE, " ").trim();
            }
        }
        return tensorLinks.isEmpty() ? null : "Inactive";
    }

    @Override
    public InferencePrequel[] getPrequelTypes() {
        return InferencePrequel.VALUES;
    }

    @Override
    protected Class<InferenceResult> getResultClass() {
        return InferenceResult.class;
    }

    public final TensorLink getTensorLink(Integer id) {

        for (TensorLink link : tensorLinks) {
            if (link.getID().equals(id)) {
                return link;
            }
        }
        return null;
    }

    public final TensorLink getTensorLink(String tensorName, int[] coordinate) {

        for (TensorLink link : tensorLinks) {
            if (link.getTensorName().equals(tensorName)
                    && (link.getCoordinate().length == 0 && coordinate == null || Arrays.equals(link.getCoordinate(), coordinate))) {
                return link;
            }
        }
        return null;
    }

    public List<TensorLink> getTensorLinks() {
        return tensorLinks;
    }

    public List<TensorLink> getTensorLinks(String tensorName) {
        return tensorLinks.stream().filter(tl -> tl.getTensorName().equals(tensorName)).toList();
    }

    @Override
    public String getWarnings() {

        NeuralNetwork nn = this.getNeuralNetwork();
        if (nn != null && !StringUtils.containsData(super.getWarnings())) {
            if (nn.hasAttribute(MIN_CELL_SIZE_M)) {
                double min = nn.getAttribute(MIN_CELL_SIZE_M);
                if (this.getCellSizeM() < min) {
                    return "Inaccurate: requires min " + StringUtils.toSI(min) + " m cell size.";
                }
            }
            if (nn.hasAttribute(MAX_CELL_SIZE_M)) {
                double max = nn.getAttribute(MAX_CELL_SIZE_M);
                if (this.getCellSizeM() > max) {
                    return "Inaccurate: requires max " + StringUtils.toSI(max) + " m cell size.";
                }
            }
        }
        return super.getWarnings();
    }

    public void setNeuralNetworkID(Integer neuralNetworkID) {
        this.neuralNetworkID = neuralNetworkID;
    }

    public void updateLinks(NeuralNetwork network) {

        tensorLinks.removeIf(t -> network.getTensor(t.getTensorName()) == null);
        for (int t = 0; t < tensorLinks.size(); t++) {
            tensorLinks.get(t).setID(t);
        }

        for (Tensor tensor : network.getTensors()) {

            List<int[]> coordinates = tensor.getInputCoordinates();
            if (coordinates.isEmpty()) {
                coordinates.add(new int[0]);
            }

            for (int[] coordinate : coordinates) {
                TensorLink link = getTensorLink(tensor.getName(), coordinate);
                if (link == null) {
                    tensorLinks.add(
                            new TensorLink(tensorLinks.size(), tensor.isInput(), tensor.getName(), coordinate, tensor.getDimensions()));
                } else if (tensor.isInput()) {
                    link.setResult(null);
                } else if (tensor.isOutput()) {
                    link.setPrequel(null);
                }
            }
        }

        String bbox = InferenceResult.BOXES.tensorNames[0];
        InferenceMode mode = InferenceMode.fromValue(getOrDefault(InferenceAttribute.INFERENCE_MODE));

        if (tensorLinks.stream().filter(l -> l.getTensorName().toLowerCase().contains(bbox)).findAny().isPresent()
                && mode != InferenceMode.BBOX_DETECTION) {
            setAttribute(InferenceAttribute.INFERENCE_MODE, InferenceMode.BBOX_DETECTION.getValue());
        }

        for (InferenceAttribute attribute : InferenceAttribute.VALUES) {
            if (network.hasAttribute(attribute.name())) {
                setAttributeArray(attribute, network.getAttributeArray(attribute.name()));
            }
        }
    }

    @Override
    public String validated(boolean startSession) {

        String result = super.validated(startSession);
        for (TensorLink link : this.tensorLinks) {
            if (Item.NONE.equals(link.getID())) {
                link.setID(tensorLinks.indexOf(link));
            }
        }
        return result;
    }
}
