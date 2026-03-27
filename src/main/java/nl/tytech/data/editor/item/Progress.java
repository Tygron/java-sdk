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
package nl.tytech.data.editor.item;

import nl.tytech.core.item.annotations.AssetDirectory;
import nl.tytech.data.core.item.Item;
import nl.tytech.util.StringUtils;

/**
 * Shows GEO progress on loading a new map in the Wizard
 * @author Maxim Knepfle
 *
 */
public class Progress extends Item {

    private static final long serialVersionUID = -4710120365118092729L;

    private static final String ASSET_LOC = "Gui/Images/Wizard/";

    private String name = StringUtils.EMPTY;

    private String source = StringUtils.EMPTY;

    @AssetDirectory(ASSET_LOC)
    private String iconName = "satellite.gif";

    private String failText = StringUtils.EMPTY;

    private double progress = 0;

    private int featureCount = 0;

    private long downloadTime = 0;

    private long processTime = 0;

    public Progress() {

    }

    public Progress(String name, String source, String icon) {
        this.name = name;
        this.source = source;
        this.iconName = icon;
    }

    public long getDownloadTime() {
        return downloadTime;
    }

    public int getFeatureCount() {
        return featureCount;
    }

    public String getFeedback() {

        if (this.isFailed()) {
            return failText;
        }

        if (StringUtils.containsData(source)) {
            return name + "\n(source: " + source + ")";
        } else {
            return name;
        }
    }

    public String getIconLocation() {
        return iconName == null ? null : ASSET_LOC + iconName;
    }

    public long getProcessTime() {
        return processTime;
    }

    public double getProgress() {
        return progress;
    }

    public boolean isFailed() {
        return StringUtils.containsData(failText);
    }

    public boolean isFinished() {
        return progress >= 1d;
    }

    public boolean isStarted() {
        return progress > 0;
    }

    public void setDownloadTime(long downloadTime, int featureCount) {
        this.downloadTime = downloadTime;
        this.featureCount = featureCount;
    }

    public void setFailText(String failText) {
        this.failText = failText;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    public void setProgress(double newProgress) {
        this.progress = newProgress;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.toPercentage(getProgress()));
        builder.append(" ");
        builder.append(name);

        if (featureCount > 0) {
            builder.append(": ");
            builder.append(StringUtils.toSI(featureCount));
            builder.append(" features");
        }
        if (downloadTime > 0 || processTime > 0) {
            builder.append(" (");
            builder.append(StringUtils.toSimpleTime(downloadTime));
            builder.append(" download, ");
            builder.append(StringUtils.toSimpleTime(processTime));
            builder.append(" processing)");
        }
        return builder.toString();
    }

}
