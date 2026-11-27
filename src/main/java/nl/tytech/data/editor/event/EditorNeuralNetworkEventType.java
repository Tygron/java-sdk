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
package nl.tytech.data.editor.event;

import static nl.tytech.core.net.serializable.MapLink.NEURAL_NETWORKS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event;
import nl.tytech.core.event.Event.IndicatorEventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.naming.GeoNC;

/**
 *
 * @author Maxim Knepfle
 *
 */
@Linked(NEURAL_NETWORKS)
public enum EditorNeuralNetworkEventType implements IndicatorEventTypeEnum {

    @EventParamData(desc = "Add a new RCNN via a byte array of a " + GeoNC.ONNX, params = { "Name", "Byte array content",
            "Uploader Name" }, response = "Neural Network ID")
    ADD(String.class, byte[].class, String.class),

    @EventParamData(desc = "Export the RCCN of a Neural Network as a byte array", params = {
            "Neural Network" }, response = "Byte array content")
    @EventIDField(links = { NEURAL_NETWORKS }, params = { 0 })
    EXPORT(Integer.class),

    @EventParamData(desc = "Remove a Neural Network", params = { "Neural Networks" }, response = "Byte array content")
    @EventIDField(links = { NEURAL_NETWORKS }, params = { 0 })
    REMOVE(Integer[].class),

    @EventParamData(desc = "Set the RCNN for a Neural Network via a byte array of a " + GeoNC.ONNX, params = { "Neural Network",
            "Byte array content", "Uploader Name" }, response = "Neural Network ID")
    @EventIDField(links = { NEURAL_NETWORKS }, params = { 0 })
    SET_NEURAL_NETWORK(Integer.class, byte[].class, String.class);

    private final List<Class<?>> classes;

    private EditorNeuralNetworkEventType(Class<?>... classes) {
        this.classes = Arrays.asList(classes);
    }

    @Override
    public boolean canBePredefined() {
        return false;
    }

    @Override
    public List<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Class<?> getResponseClass(Object[] args) {

        return switch (this) {

            case ADD -> Integer.class;

            case EXPORT -> byte[].class;

            case REMOVE -> Boolean.class;

            default -> null;
        };
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public MapLink triggerUpdate(Event event) {
        return NEURAL_NETWORKS;
    }
}
