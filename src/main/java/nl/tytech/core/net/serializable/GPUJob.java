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
import java.util.concurrent.ThreadLocalRandom;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import nl.tytech.data.core.item.Moment;
import nl.tytech.util.StringUtils;

/**
 *
 * Current state of a GPU Job executing
 *
 * @author Maxim Knepfle
 */
public class GPUJob implements Serializable, Comparable<GPUJob> {

    public static final long ERROR = -1;

    public static final long CANCELED = -2;

    public static final long TIMEOUT = -3;

    public static final long INSUFFICIENT_MEMORY = -4;

    public static final long INVALID_CLUSTER = -5;

    private static final long serialVersionUID = -1715752299974456885L;

    /**
     * When Job is finished for more then this fraction it cannot timeout
     */
    public static final double TIMEOUT_FRACTION = 0.75;

    public static final long TIMEOUT_LIMITED = 1l * Moment.HOUR;

    public static final long TIMEOUT_LONG = 7l * Moment.DAY;

    public static final String getWarningMessage(long code) {

        if (ERROR == code) {
            return "Error in Calculation!";
        }
        if (CANCELED == code) {
            return "Calculation was Canceled!";
        }
        if (TIMEOUT == code) {
            return "Calc too long forced Timeout!";
        }
        if (INSUFFICIENT_MEMORY == code) {
            return "Insufficient memory!";
        }
        if (INVALID_CLUSTER == code) {
            return "Invalid cluster index!";
        }
        return null;
    }

    private long id = ThreadLocalRandom.current().nextLong();

    private long timeMS = 0;

    private long timeoutMS = Long.MAX_VALUE;

    private double percentage = 0;

    private boolean cancel = false;

    /**
     * Variables below are not set on RMI GPU cluster (transient) only in client API (json)
     */

    @JsonSerialize
    @JsonDeserialize
    private String name = null;

    @JsonSerialize
    @JsonDeserialize
    private String type = null;

    @JsonSerialize
    @JsonDeserialize
    private transient int clusterID = GPUCluster.CLUSTER_NONE;

    public GPUJob() {
        // empty
    }

    public GPUJob(long id, long timeMS, long timeoutMS, double percentage, boolean cancel) {
        this.id = id;
        this.timeMS = timeMS;
        this.timeoutMS = timeoutMS;
        this.percentage = percentage;
        this.cancel = cancel;
    }

    @Override
    public int compareTo(GPUJob o) {
        return this.getPercentage() < o.getPercentage() ? 1 : -1;
    }

    public int getClusterID() {
        return clusterID;
    }

    public long getETA() {
        return (long) (percentage >= 0.0 ? (1.0 - percentage) * timeMS : timeMS);
    }

    public long getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPercentage() {
        return percentage;
    }

    public long getTimeMS() {
        return timeMS;
    }

    public long getTimeoutMS() {
        return timeoutMS;
    }

    public String getType() {
        return type;
    }

    public boolean isCancelable() {
        return true;
    }

    public boolean isCanceled() {
        return cancel;
    }

    public boolean isCPU() {
        return clusterID == GPUCluster.CPU_CLUSTER_ID;
    }

    public void setCanceled(boolean cancel) {
        this.cancel = cancel;
    }

    public void setClusterID(int clusterID) {
        this.clusterID = clusterID;
    }

    public void setName(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setTimeMS(long timeMS) {
        this.timeMS = timeMS;
    }

    @Override
    public String toString() {

        String name = getName();
        if (isCanceled()) {
            return name + ": Canceling";

        } else if (getPercentage() == 0.0) { // begin or unknown calculating
            return name;

        } else if (getPercentage() > 0.0) { // calculating
            return name + ": " + StringUtils.toPercentage(getPercentage()) + " (ETA: " + StringUtils.toSimpleTime(getETA()) + ")";

        } else { // waiting
            return name + ": Queuing";
        }
    }
}
