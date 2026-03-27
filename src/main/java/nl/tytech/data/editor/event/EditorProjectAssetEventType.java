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

import static nl.tytech.core.net.serializable.MapLink.PROJECT_ASSETS;
import java.util.Arrays;
import java.util.List;
import nl.tytech.core.event.Event.EventTypeEnum;
import nl.tytech.core.item.annotations.EventIDField;
import nl.tytech.core.item.annotations.EventParamData;
import nl.tytech.core.item.annotations.Linked;

/**
 * EditorProjectAssetEventType
 *
 * @author Maxim Knepfle
 */
@Linked(PROJECT_ASSETS)
public enum EditorProjectAssetEventType implements EventTypeEnum {

    @EventParamData(desc = "Upload a new image as a Project asset.", params = { "Directory to upload to",
            "Filename. This should a simple file name, including extension", "File data" }, response = "Project Asset ID")
    ADD(String.class, String.class, byte[].class),

    @EventParamData(desc = "Upload new Textures as a Project assets, auto rescaled to small, medium, large.", params = {
            "Directory to upload to", "Filename. This should a simple file name, including extension", "File data" })
    @EventIDField(sameLength = true)
    ADD_TEXTURE(String[].class, String[].class, byte[][].class);

    private final List<Class<?>> classes;

    private EditorProjectAssetEventType(Class<?>... classes) {
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
        return this == ADD ? Integer.class : null;
    }

    @Override
    public boolean isServerSide() {
        return true;
    }
}
