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
import java.util.TreeMap;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.data.core.item.Item;
import nl.tytech.data.core.serializable.MapType;
import nl.tytech.data.engine.item.AttributeItem.ReservedAttribute;
import nl.tytech.data.engine.other.AttributeQueryInterface;
import nl.tytech.data.engine.serializable.LegendEntry;
import nl.tytech.data.engine.serializable.Relation;
import nl.tytech.data.engine.serializable.Tensor;

/**
 * Trained Neural Network for Inference
 *
 * @author Maxim Knepfle & Frank Baars
 *
 */
public class NeuralNetwork extends DataItem implements AttributeQueryInterface {

    public interface Format {

        public String getExtension();

        public String name();

    }

    public enum NeuralNetworkAttribute implements ReservedAttribute {

        MIN_CELL_SIZE_M(Double.class, 0.25),

        MAX_CELL_SIZE_M(Double.class, 0.25),

        MAX_GT_INSTANCES(Double.class, 100),

        VERSION(Long.class, 0),

        ;

        private final Class<?> type;
        private final double[] defaultArray;

        private NeuralNetworkAttribute(Class<?> type, double defaultValue) {
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
    private String description;

    @XMLValue
    private String producer;

    @XMLValue
    private ArrayList<Tensor> tensors = new ArrayList<Tensor>();

    @XMLValue
    private TreeMap<String, double[]> attributes = new TreeMap<>();

    @XMLValue
    private ArrayList<LegendEntry> legendEntries = new ArrayList<>();

    @Override
    public double getAttribute(MapType mapType, String key) {
        return getAttribute(mapType, key, 0);
    }

    @Override
    public double getAttribute(MapType mapType, String key, int index) {
        double[] array = getAttributeArray(key);
        return index >= 0 && array.length > index ? array[index] : AttributeItem.DEFAULT_VALUE;
    }

    public double getAttribute(ReservedAttribute key) {
        return getAttribute(key.name());
    }

    @Override
    public double getAttribute(String key) {
        return getAttribute(MapType.CURRENT, key, 0);
    }

    @Override
    public double[] getAttributeArray(MapType mapType, String key) {
        return getAttributeArray(key);
    }

    @Override
    public double[] getAttributeArray(String key) {
        return attributes.getOrDefault(key, AttributeItem.ZERO);
    }

    @Override
    public Collection<String> getAttributes() {
        return this.attributes.keySet();
    }

    @Override
    public Collection<String> getAttributes(MapType mapType) {
        return getAttributes();
    }

    public String getDescription() {
        return description;
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

    @Override
    public List<Item> getLinks() {

        List<Item> links = new ArrayList<>();
        for (Item item : getMap(MapLink.OVERLAYS)) {
            if (item instanceof InferenceOverlay io && this.getID().equals(io.getNeuralNetworkID())) {
                links.add(item);
            }
        }
        return links;
    }

    public List<Tensor> getOutputTensors() {
        return getTensors().stream().filter(t -> t.isOutput()).toList();
    }

    public String getProducer() {
        return producer;
    }

    @Override
    public AttributeQueryInterface getRelationAttribute(Relation relation) {
        return null;
    }

    public Tensor getTensor(String tensorName) {
        return this.tensors.stream().filter(t -> t.getName().equals(tensorName)).findFirst().orElse(null);
    }

    public List<Tensor> getTensors() {
        return tensors;
    }

    @Override
    public boolean hasAttribute(MapType mapType, String attribute) {
        return hasAttribute(attribute);
    }

    public final boolean hasAttribute(ReservedAttribute attribute) {
        return hasAttribute(attribute.name());
    }

    @Override
    public boolean hasAttribute(String attribute) {
        return attribute != null && !attributes.isEmpty() && attributes.containsKey(attribute);
    }

    public final void setAttribute(String key, double value) {
        setAttributeArray(key, new double[] { value });
    }

    public final void setAttributeArray(String key, double[] values) {
        if (key != null) {
            this.attributes.put(key, values);
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public final void setLegendEntries(Collection<LegendEntry> entries) {
        this.legendEntries.clear();
        this.legendEntries.addAll(entries);
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public void setTensors(Collection<Tensor> tensors) {
        this.tensors.clear();
        this.tensors.addAll(tensors);
    }
}
