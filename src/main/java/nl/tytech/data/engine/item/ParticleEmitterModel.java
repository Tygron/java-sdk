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

import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.data.core.item.UniqueNamedItem;
import nl.tytech.util.StringUtils;

/**
 * Particle emitter: This item encapsulates the available models, this are NOT the individual models on the map.
 *
 * @author Christian Paping
 */
public class ParticleEmitterModel extends UniqueNamedItem {

    public static final String EFFECT_DIR = ModelData.MODEL_DIR + "Effects/";

    /** Generated serialVersionUID */
    private static final long serialVersionUID = 5059475154747750949L;

    @XMLValue
    private String fileName = StringUtils.EMPTY;

    @XMLValue
    private boolean wind = false;

    @XMLValue
    private boolean zBuffer = false;

    @XMLValue
    private boolean hidden = false;

    /**
     * Empty constructor.
     */
    public ParticleEmitterModel() {

    }

    /**
     * Returns the file name of the particle emitter.
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return the zBuffer for this particle emitter.
     */
    public final boolean getZBuffer() {
        return this.zBuffer;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isWindDirected() {
        return wind;
    }

    @Override
    public String toString() {
        return getName();
    }
}
