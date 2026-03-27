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
package nl.tytech.core.net.serializable;

import java.io.Serializable;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;

/**
 * Sub Domain contains a collection of Sub Users that share projects an rights.
 *
 * @author Maxim Knepfle
 */
@Deprecated
public class SubDomain implements Serializable {

    private static final long serialVersionUID = 5352851174976194971L;

    private String name = StringUtils.EMPTY;

    private int maxArea = 0; // km2

    @Deprecated
    private int maxSaves = 0;

    private long maxProjectArea = 1000 * 1000;

    @Deprecated
    public SubDomain() {

    }

    @Deprecated
    public SubDomain(String name, int maxAreaKM2, long maxProjectArea) {
        this.name = name;
        this.maxArea = maxAreaKM2;
        this.maxProjectArea = maxProjectArea;
    }

    @Deprecated
    public int getMaxArea() {
        return maxArea;
    }

    @Deprecated
    public long getMaxProjectArea() {
        return maxProjectArea;
    }

    /**
     * Max available project area M2 within my Sub Domain.
     */
    @Deprecated
    public long getMaxProjectArea(Integer licenseUsedAreaKM2) {

        if (licenseUsedAreaKM2 == null) {
            return maxProjectArea;
        }
        long availableM2 = Math.max(0, maxArea * MathUtils.KM2 - licenseUsedAreaKM2 * MathUtils.KM2);
        return Math.min(availableM2, maxProjectArea);
    }

    @Deprecated
    public int getMaxSaves() {
        return maxSaves;
    }

    @Deprecated
    public String getName() {
        return name;
    }

    @Deprecated
    public void setMaxArea(int maxArea) {
        this.maxArea = maxArea;
    }

    @Deprecated
    public void setMaxProjectArea(long maxProjectArea) {
        this.maxProjectArea = maxProjectArea;
    }

    @Deprecated
    public void setMaxSaves(int maxSaves) {
        this.maxSaves = maxSaves;
    }

    @Deprecated
    public void setName(String name) {
        this.name = name;
    }
}
