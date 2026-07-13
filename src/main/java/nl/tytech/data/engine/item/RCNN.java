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
import java.util.Collection;
import java.util.List;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.engine.item.AttributeItem.ReservedAttribute;
import nl.tytech.data.engine.serializable.LegendEntry;
import nl.tytech.data.engine.serializable.Tensor;

/**
 * Trained Region-based Convolutional Neural Network (RCNN) for Inference Overlays
 *
 * @author Maxim Knepfle & Frank Baars
 *
 */
public class RCNN extends NeuralNetwork {

    public enum RCNNAttribute implements ReservedAttribute {

        MIN_CELL_SIZE_M(Double.class, 0.25),

        MAX_CELL_SIZE_M(Double.class, 0.25),

        MAX_GT_INSTANCES(Double.class, 100),

        VERSION(Long.class, 0),

        ;

        private final Class<?> type;
        private final double[] defaultArray;

        private RCNNAttribute(Class<?> type, double defaultValue) {
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

    public static final String ONNX_EXTENSION = "onnx";

    private static final long serialVersionUID = 5371401657101259581L;

    @XMLValue
    private ArrayList<Tensor> tensors = new ArrayList<>();

    @XMLValue
    private ArrayList<LegendEntry> legendEntries = new ArrayList<>();

    public RCNN() {
        super(Type.RCNN);
    }

    @Override
    public String getExtension() {
        return ONNX_EXTENSION;
    }

    public List<Tensor> getInputTensors() {
        return getTensors().stream().filter(t -> t.isInput()).toList();
    }

    public List<LegendEntry> getLegendEntries() {
        return legendEntries;
    }

    public List<Tensor> getOutputTensors() {
        return getTensors().stream().filter(t -> t.isOutput()).toList();
    }

    public Tensor getTensor(String tensorName) {
        return this.tensors.stream().filter(t -> t.getName().equals(tensorName)).findFirst().orElse(null);
    }

    public List<Tensor> getTensors() {
        return tensors;
    }

    public final void setLegendEntries(Collection<LegendEntry> entries) {
        this.legendEntries.clear();
        this.legendEntries.addAll(entries);
    }

    public void setTensors(Collection<Tensor> tensors) {
        this.tensors.clear();
        this.tensors.addAll(tensors);
    }
}
