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

import nl.tytech.util.logger.TLogger;

/**
 * CameraSpeedEnum: This class defines speed of the camera.
 *
 * @author Marijn van Zanten, Frank Baars
 */
public enum CameraSpeedEnum {

    CUSTOM(20), FAST(0.6), NORMAL(1.0), INSTANT(0.0001);

    private double interpolationTime;

    private CameraSpeedEnum(double interpolation) {
        this.interpolationTime = interpolation;
    }

    public double getInterpolationTime() {
        return this.interpolationTime;
    }

    public void setInterpolationTime(float interpolationTime) {
        if (this == CUSTOM) {
            this.interpolationTime = interpolationTime;
        } else {
            TLogger.severe("You can only override the custom cam speed enum!");
        }
    }
}
