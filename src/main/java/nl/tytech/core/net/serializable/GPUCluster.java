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
import nl.tytech.util.MemoryUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.color.TColor;

/**
 *
 * Current state of a GPU Cluster
 *
 * @author Maxim Knepfle
 */
public class GPUCluster implements Serializable, Comparable<GPUCluster> {

    public enum Lane {

        SPRINT(0),

        REGULAR(32),

        HEAVY(64),

        SUPER(128);

        public static final Lane[] VALUES = values();

        public static Lane getLaneForJob(long maxJobMemorySize) {

            // try faster lanes first
            for (int i = VALUES.length - 1; i >= 0; i--) {
                if (maxJobMemorySize >= VALUES[i].maxJobMemorySize) {
                    return VALUES[i];
                }
            }
            return Lane.SPRINT;
        }

        private final long maxJobMemorySize;

        private Lane(long gb) {
            this.maxJobMemorySize = gb * MemoryUtils.GB;
        }

        public final float getScore() {

            return switch (this) {
                case SUPER -> 4f;
                case HEAVY -> 2f;
                default -> 0f;
            };
        }
    }

    public enum PeerAccess {

        STANDALONE(0.5), PCI(1.0), NVLINK(1.5);

        private final double score;

        private PeerAccess(double score) {
            this.score = score;
        }

        public double getPerformanceScore() {
            return score;
        }
    }

    public static final int CPU_CLUSTER_ID = -1;

    public static final int CLUSTER_NONE = -2;

    private static final long serialVersionUID = -1715752299974456886L;

    private long waitMS = 0;

    private int activeJobs = 0;

    private boolean free = true;

    private boolean busy = false;

    private String location = "";

    private int id;

    private int cores;

    private boolean timeLimited;

    private Lane lane;

    public GPUCluster() {
        // empty
    }

    public GPUCluster(int id, Lane lane, boolean timeLimited, boolean free, boolean busy, String location, int cores, int activeJobs,
            long waitMS) {

        this.id = id;
        this.lane = lane;
        this.timeLimited = timeLimited;
        this.free = free;
        this.busy = busy;
        this.location = location;
        this.cores = cores;
        this.activeJobs = activeJobs;
        this.waitMS = waitMS;
    }

    @Override
    public int compareTo(GPUCluster o) {
        return this.lane.compareTo(o.lane);
    }

    public int getActiveJobs() {
        return activeJobs;
    }

    public TColor getColor() {

        if (isCPU()) {
            return TColor.GREEN;
        } else if (isBusy()) {
            return TColor.ORANGE;
        } else if (isFree()) {
            return TColor.GREEN;
        } else {// failed
            return TColor.RED;
        }
    }

    public int getCores() {
        return cores;
    }

    public int getID() {
        return id;
    }

    public Lane getLane() {
        return lane;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {

        if (isCPU()) {
            return "Shared";
        } else if (isFree()) {
            return "Free";
        } else if (isBusy()) {
            return "Busy (~" + StringUtils.toSimpleTime(getWaitMS()) + " with " + getActiveJobs() + " job"
                    + (getActiveJobs() == 1 ? "" : "s") + ")";
        } else {
            return "Offline";
        }
    }

    public long getWaitMS() {
        return waitMS;
    }

    public boolean isBusy() {
        return busy;
    }

    public boolean isCPU() {
        return lane == null;
    }

    public boolean isFree() {
        return free;
    }

    public boolean isTimeLimited() {
        return timeLimited;
    }

    @Override
    public String toString() {
        return (isCPU() ? "CPU" : StringUtils.capitalizeWithSpacedUnderScores(getLane().name()) + " GPU") + " Cluster is " + getStatus();
    }
}
