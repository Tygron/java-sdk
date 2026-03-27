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
 * Zoom Level definition for Tygron Client
 *
 * @author Maxim Knepfle
 *
 */
public enum ZoomLevel {

    REGION(new Vector3d(0, 600, 0.01), new Vector3d(0, 0, 0), 0, new Vector3d(0, 0, -1), false, 20000f, 0.0f, 0.86f, false, false),

    PLAN(new Vector3d(0, 500, 0.01), new Vector3d(0, 0, 0), 0, new Vector3d(0, 0, -1), false, 600f, 0, 0.86f, false, false),

    LOCAL(new Vector3d(0, 300, 0.01), new Vector3d(0, -30, 0), 0, new Vector3d(0, 0, -1), true, 400f, 250.0f, 0.86f, true, true),

    STREET(new Vector3d(0, 240, 230), new Vector3d(0, -30, 0), 60, new Vector3d(0, 1, 0), true, 200f, 150.0f, 0.7f, true, true),

    MEDIUMSTREET(new Vector3d(0, 130, 110), new Vector3d(0, -30, 0), 70, new Vector3d(0, 1, 0), true, 50f, 150.0f, 0.5f, true, true),

    CLOSESTREET(new Vector3d(0, 55, 110), new Vector3d(0, -10, 0), 80, new Vector3d(0, 1, 0), true, 10f, 150.0f, 0.45f, true, true),

    WALK(new Vector3d(0, 0, -0.01), new Vector3d(0, 1.5, 1), 85, new Vector3d(0, 1, 0), true, 0f, 1f, 0.45f, true, false),

    UNDERGROUND(new Vector3d(0, -5, 0), new Vector3d(0, 0, -5), 180, new Vector3d(0, 1, 0), true, 0f, 1f, 0.45f, true, false);

    public static final ZoomLevel[] VALUES = ZoomLevel.values();

    public static final ZoomLevel CAM2D = ZoomLevel.LOCAL;

    private static final ZoomLevel DEFAULT_START_LEVEL = ZoomLevel.STREET;

    public static ZoomLevel getBestZoomlevel(double objectSize, double mapSizeLargestM) {

        for (int i = VALUES.length - 2; i > 0; i--) {
            switch (VALUES[i]) {
                case PLAN:
                    if (objectSize < mapSizeLargestM) {
                        return PLAN;
                    }
                case LOCAL:
                    if (objectSize < mapSizeLargestM / 3.0) {
                        return LOCAL;
                    }
                    break;
                default:
                    if (objectSize < VALUES[i].visibleObjectSize) {
                        return VALUES[i];
                    }
                    break;
            }
        }
        return REGION;
    }

    private final Vector3d relativeLocation;

    private final Vector3d relativeLookAt;

    private final Vector3d defaultUp;

    private final double cameraMovementMultiplier;

    private final double inPlaneDeceleration;

    private final boolean boundToMap;

    private final double visibleObjectSize;

    private final boolean zoomToMouse;

    private final boolean showCursor;

    private final double maxPitch;

    private ZoomLevel(Vector3d relativeLocation, Vector3d relativeLookAt, double maxPitch, Vector3d up, boolean defaultBounds,
            double viewBoxHeight, double cameraMovementMultiplier, double inPlaneDeceleration, boolean zoomToMouse, boolean showCursor) {

        this.relativeLocation = relativeLocation;
        this.relativeLookAt = relativeLookAt;
        this.maxPitch = Math.toRadians(maxPitch);
        this.defaultUp = up;
        this.cameraMovementMultiplier = cameraMovementMultiplier;
        this.inPlaneDeceleration = inPlaneDeceleration;
        this.boundToMap = defaultBounds;
        this.visibleObjectSize = viewBoxHeight;
        this.zoomToMouse = zoomToMouse;
        this.showCursor = showCursor;
    }

    public final double getCameraMovementMultiplier() {
        return cameraMovementMultiplier;
    }

    public final Vector3d getDefaultUp() {
        return defaultUp;
    }

    public final double getInPlaneDeceleration() {
        return inPlaneDeceleration;
    }

    public final double getMaxPitch() {
        return maxPitch;
    }

    public final ZoomLevel getNextHigher() {
        return this.ordinal() > 0 ? ZoomLevel.VALUES[this.ordinal() - 1] : null;
    }

    public final ZoomLevel getNextLower() {
        return this.ordinal() < ZoomLevel.VALUES.length - 1 ? ZoomLevel.VALUES[this.ordinal() + 1] : null;
    }

    public final Vector3d getRelativeLocation() {
        return relativeLocation;
    }

    public final Vector3d getRelativeLookAt() {
        return relativeLookAt;
    }

    public final boolean isBiggerOrEqual(ZoomLevel target) {
        return this.ordinal() <= target.ordinal();
    }

    public final boolean isBiggerThan(ZoomLevel target) {
        return this.ordinal() < target.ordinal();
    }

    public final boolean isBoundToMap() {
        return boundToMap;
    }

    public final boolean isSmallerOrEqual(ZoomLevel target) {
        return this.ordinal() >= target.ordinal();
    }

    public final boolean isSmallerThan(ZoomLevel target) {
        return this.ordinal() > target.ordinal();
    }

    public final boolean isStartZoomLevel() {
        return this == DEFAULT_START_LEVEL;
    }

    public final boolean isZoomToMouse() {
        return zoomToMouse;
    }

    public final boolean showCursor() {
        return showCursor;
    }
}
