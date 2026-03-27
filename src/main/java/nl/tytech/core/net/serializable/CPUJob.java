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

import org.locationtech.jts.geom.Geometry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.tytech.data.engine.item.Overlay;
import nl.tytech.util.StringUtils;

/**
 *
 * Current state of a CPU Job executing
 *
 * @author Maxim Knepfle
 */
public class CPUJob extends GPUJob {

    public static class Excel extends CPUJob {

        private static final long serialVersionUID = -7012269378411887345L;

        public Excel(int amount) {
            super(Task.EXCEL, StringUtils.toSI(amount) + " Excel" + (amount == 1 ? "" : "s"));
        }
    }

    public static class Import extends CPUJob {

        private static final long serialVersionUID = -7012269378411887348L;

        public Import(Geometry g) {
            this(g.getNumGeometries());
        }

        public Import(int count) {
            super(Task.IMPORT, StringUtils.toSI(count) + " " + (count == 1 ? "Geometry" : "Geometries"));
        }
    }

    public static class Iteration extends CPUJob {

        private static final long serialVersionUID = -7012269478411887337L;

        public Iteration() {
            super(Task.ITERATION, Task.ITERATION.getText() + ": 0");
        }
    }

    public static class Parametric extends CPUJob {

        private static final long serialVersionUID = -7012269478411887347L;

        public Parametric() {
            super(Task.PARAMETRIC, "Design");
        }
    }

    public static class Query extends CPUJob {

        private static final long serialVersionUID = -7012269378411887346L;

        public Query(String text, int amount) {
            super(Task.QUERY, StringUtils.toSI(amount) + " TQL " + (StringUtils.containsData(text) ? text + " " : "")
                    + (amount == 1 ? "Query" : "Queries"));
        }
    }

    public static class Scenario extends CPUJob {

        private static final long serialVersionUID = -7012269478411117347L;

        public Scenario(String name) {
            super(Task.SCENARIO,
                    (name.startsWith(Scenario.class.getSimpleName()) ? name.replaceFirst(Scenario.class.getSimpleName(), "").trim() : name)
                            + " starting");
        }
    }

    /**
     * Special CPU Task, never Overlay calculation
     */
    public enum Task {

        RASTER("Rasterizing"),

        IMPORT("Importing"),

        PROCESSING("Processing"),

        PARAMETRIC("Parametric"),

        ITERATION("Iteration"),

        TRIGGER("Triggering"),

        EXCEL("Calculating"),

        QUERY("Executing"),

        UPDATE("Updating"),

        WAITING("Waiting"),

        SCENARIO("Scenario"),

        ;

        private final String text;

        private Task(String text) {
            this.text = text;
        }

        public final String getText() {
            return text;
        }

        public final boolean isCancelable() {
            return this != PROCESSING;
        }
    }

    private static final long serialVersionUID = -1215752299974456895L;

    public static final boolean isIteration(GPUJob job) {
        return CPUJob.Task.ITERATION.getText().equals(job.getType());
    }

    public static final boolean isTask(GPUJob job) {

        for (Task task : Task.values()) {
            if (task.getText().equals(job.getType())) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    private final long start = System.currentTimeMillis();

    public CPUJob(Overlay overlay) {

        super();
        setClusterID(GPUCluster.CPU_CLUSTER_ID);
        setName(overlay.getType().name(), overlay.getName());
    }

    public CPUJob(Task type, String details) {

        super();
        setClusterID(GPUCluster.CPU_CLUSTER_ID);
        setName(type.getText(), type.getText() + " " + details);
    }

    public long getStartMS() {
        return start;
    }

    public boolean isActive() {
        return !isCanceled();
    }

    @Override
    public boolean isCancelable() {

        for (Task task : Task.values()) {
            if (task.getText().equals(getType())) {
                return task.isCancelable();
            }
        }
        return true;
    }

    public void setProgress(double fraction) {
        setProgress(System.currentTimeMillis() - start, fraction);
    }

    public void setProgress(long executionTimeMS, double fraction) {
        setTimeMS(fraction > 0.0 ? (long) (executionTimeMS / fraction) : executionTimeMS);
        setPercentage(fraction);
    }
}
