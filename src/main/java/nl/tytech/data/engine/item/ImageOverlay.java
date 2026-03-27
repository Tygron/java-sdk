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
import java.util.Map;
import cjava.FrameVar;
import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.core.item.annotations.XMLValue;
import nl.tytech.util.MathUtils;

/**
 * Special overlay overlaying a given image
 *
 * @author Maxim Knepfle
 */
public class ImageOverlay extends Overlay {

    public enum ImageAttribute implements ReservedAttribute {

        ALPHA(Double.class, 0.75);

        private final Class<?> type;
        private final double[] defaultArray;

        private ImageAttribute(Class<?> type, double defaultValue) {
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

    public static final String OVERLAY_IMAGES = "Overlays/";

    private static final String DEFAULT_IMAGE = "empty.png";

    private static final long serialVersionUID = 3137271350161025012L;

    @XMLValue
    @AssetDirectory(OVERLAY_IMAGES)
    private ArrayList<String> imageNames = new ArrayList<>();

    public void addOverlayImageName(String imageName) {
        this.imageNames.add(imageName);
        this.incrementImageVersion();
    }

    @Override
    protected int calcTimeframes(Map<Integer, Integer> cache) {
        return MathUtils.clamp(imageNames.size(), 1, FrameVar.MAX_FRAMES);
    }

    public int getNumOverlayImages() {
        return imageNames.size();
    }

    public String getOverlayImageLocation(int timeframe) {
        return OVERLAY_IMAGES + getOverlayImageName(timeframe);
    }

    public String getOverlayImageName(int timeframe) {
        return timeframe >= 0 && timeframe < imageNames.size() ? imageNames.get(timeframe) : DEFAULT_IMAGE;
    }

    public void removeAllOverlayImages() {

        if (!imageNames.isEmpty()) {
            this.imageNames.clear();
            this.incrementImageVersion();
        }
    }

    public void removeOverlayImageName(String imageName) {

        if (this.imageNames.remove(imageName)) {
            this.incrementImageVersion();
        }
    }

    public void setOverlayImageName(int i, String imageName) {

        if (i < getNumOverlayImages() && !this.imageNames.get(i).equals(imageName)) {
            this.imageNames.set(i, imageName);
            this.incrementImageVersion();
        }
    }
}
