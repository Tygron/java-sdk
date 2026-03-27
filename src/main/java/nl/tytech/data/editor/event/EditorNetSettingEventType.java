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

import static nl.tytech.core.net.serializable.MapLink.NET_SETTINGS;
import static nl.tytech.core.net.serializable.MapLink.STAKEHOLDERS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.Linked;
import nl.tytech.data.engine.item.NetLine.NetType;
import nl.tytech.data.engine.item.NetSetting;

/**
 *
 * @author Frank Baars
 *
 */
@Linked(NET_SETTINGS)
public enum EditorNetSettingEventType implements EventTypeEnum {

    SET_BOOLEAN(NetSetting.Type.class, Boolean.class),

    SET_CLUSTER_FRACTION_CONNECTED(Double.class),

    @EventIDField(links = { STAKEHOLDERS }, params = { 1 })
    SET_OWNER(NetType.class, Integer.class),

    SET_TRANSPORTED_TYPE_NAME(NetType.class, String.class);

    private final List<Class<?>> classes;

    private EditorNetSettingEventType(Class<?>... classes) {
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
        return null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

}
