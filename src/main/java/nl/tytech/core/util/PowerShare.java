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
package nl.tytech.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.CPUJob;
import nl.tytech.core.util.PowerShare.MultiTaskList.ListTask;
import nl.tytech.util.MathUtils;
import nl.tytech.util.StringUtils;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.concurrent.ThreadPriorities;
import nl.tytech.util.logger.TLogger;

/**
 * Share calculation power between threads using one Thread Pool or unlimited virtual threads (max parallel).
 *
 * @author Maxim Knepfle
 */
public final class PowerShare {

    public enum Bound {

        /**
         * Server and Client: the runnable is ONLY limited by the amount of available CPU power (pool 1) and maybe killed.
         */
        MAX_CPU,

        /**
         * Server side second pool: the runnable is ONLY limited by the amount of available CPU power (pool 2) and maybe killed.
         */
        MAX_CPU2,

        /**
         * Virtual Threads: unlimited amount of runnables that area limited by the amount of e.g. available Internet speed, disk IO or
         * thread sleeps a lot.
         */
        MAX_PARRALISM,

        /**
         * Max threads dedicated to a special purpose e.g. Geo Sources, Backup, cannot be to much to prevent source server overloading.
         */
        MAX_PURPOSE,

        ;

        public final String getToken(PoolGroup group) {
            return group == null ? CPU_SHARE : group.getPoolGroupID() + "-" + this.name();
        }

        private boolean isKillable() {
            return this == Bound.MAX_CPU || this == Bound.MAX_CPU2;
        }
    }

    @FunctionalInterface
    public static interface ExceptionHandler {

        public abstract void run(Exception exp);
    }

    /**
     * Helper object to simplify common multi threading operations
     */
    public static class MultiTask {

        private AtomicInteger unfinishedTasks = new AtomicInteger(0);

        protected AtomicInteger totalTasks = new AtomicInteger(0);

        private long start = System.currentTimeMillis();

        private final PoolGroup group;

        private final ExecutorService service;

        private Exception exception = null;

        private MultiTask(PoolGroup group, Bound bound, String purpose, int purposeThreads) {
            this.group = group;

            // select matching pool
            service = switch (bound) {
                case MAX_CPU, MAX_CPU2 -> getCPUPool(bound.getToken(group));
                case MAX_PURPOSE -> getPurposePool(purpose, purposeThreads);
                case MAX_PARRALISM -> maxParallelService;
                default -> maxParallelService;
            };
        }

        public void execute(Runnable runnable) {

            unfinishedTasks.incrementAndGet();
            totalTasks.incrementAndGet();

            try {
                service.execute(() -> {
                    try {
                        // only execute when pool group is not shutdown
                        if (group == null || !group.isShutdown()) {
                            runnable.run();
                        }

                    } catch (ShutdownException e) {
                        TLogger.warning(e.getMessage() + ": stopped task.");

                    } catch (Exception e) {
                        if (group != null) {
                            group.exception(e);
                        } else {
                            TLogger.exception(e);
                        }
                    } finally {
                        unfinishedTasks.decrementAndGet();
                    }
                });
            } catch (RejectedExecutionException e) {
                throw new ShutdownException(getShutdownMessage());
            }
        }

        public Exception getException() {
            return exception;
        }

        public String getExecutionTime() {
            return StringUtils.toSimpleTime(getExecutionTimeMS());
        }

        public long getExecutionTimeMS() {
            return System.currentTimeMillis() - start;
        }

        public double getFractionFinished() {

            int total = totalTasks.get();
            return total > 0 ? (total - unfinishedTasks.get()) / (double) total : 0.0;
        }

        private String getShutdownMessage() {

            String message = "Unknown Shutdown";
            if (group != null && group.isShutdown()) {
                message = group.toString() + " Shutdown, remaining are: " + unfinishedTasks.get() + "/" + totalTasks.get() + " tasks.";

            } else if (service.isShutdown()) {
                message = service.toString() + " Shutdown, remaining are: " + unfinishedTasks.get() + "/" + totalTasks.get() + " tasks.";
            }
            TLogger.warning(message);
            return message;
        }

        public String getTaskAmount() {
            return StringUtils.toSI(getTaskNumber());
        }

        public int getTaskNumber() {
            return totalTasks.get();
        }

        public int getUnfinishedTaskAmount() {
            return unfinishedTasks.get();
        }

        public boolean isEmpty() {
            return getTaskNumber() == 0;
        }

        public void reset() {

            unfinishedTasks.set(0);
            totalTasks.set(0);
            start = System.currentTimeMillis();
            exception = null;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public void throwException() throws Exception {
            if (exception != null) {
                throw exception;
            }
        }

        public boolean waitUntilAllFinished() {
            return waitUntilAllFinished(null, null);
        }

        public boolean waitUntilAllFinished(final CPUJob job) {
            return waitUntilAllFinished(job, null);
        }

        public boolean waitUntilAllFinished(final CPUJob job, final ExceptionHandler handler) {

            int maxSleep = 0;// start very low for fast exec
            while (unfinishedTasks.get() > 0) {

                // sleep on it depending on counter how long
                if (maxSleep > 0) {
                    ThreadUtils.sleepInterruptible(MathUtils.clamp(unfinishedTasks.get() * 4, 0, maxSleep));
                }
                // build up max sleep time to max of 40ms
                if (maxSleep < 40) {
                    maxSleep++;
                }

                // check for shutdowns when Pool Group is present.
                if (group != null && group.isShutdown()) {
                    throw new ShutdownException(getShutdownMessage());
                }
                if (service.isShutdown()) {
                    throw new ShutdownException(getShutdownMessage());
                }

                // with handler handle exception directly and reset
                if (handler != null && exception != null) {
                    Exception e = exception;
                    exception = null;
                    handler.run(e);
                }

                // update job state
                if (job != null) {
                    job.setProgress(getExecutionTimeMS(), getFractionFinished());
                }
            }
            return true;
        }

        public boolean waitUntilAllFinished(final ExceptionHandler handler) {
            return waitUntilAllFinished(null, handler);
        }

    }

    public static class MultiTaskList<R> extends MultiTask {

        public interface ListTask<T, R> {

            public List<R> run(Collection<T> list);
        }

        private List<R> result = new ArrayList<>();

        private MultiTaskList(PoolGroup group) {
            super(group, Bound.MAX_CPU, null, 1);
        }

        public List<R> getResult() {
            return result;
        }
    }

    public interface PoolGroup {

        public void exception(Throwable exp);

        public String getPoolGroupID();

        public boolean isShutdown();

    }

    public static class ThreadPool extends ForkJoinPool {

        private final String name;

        public ThreadPool(int threads, String name) {
            super(threads, pool -> new Worker(pool, name), null, true);
            this.name = name;
        }

        public ThreadPool(String name) {
            this(CPU_THREADS, name);
        }

        private String getName() {
            return name;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    private static class Worker extends ForkJoinWorkerThread {

        private Worker(ForkJoinPool pool, String name) {
            super(pool);
            this.setName(name + "-" + counter.getAndIncrement() + "-" + pool.getActiveThreadCount());
            this.setPriority(ThreadPriorities.LOW);
        }
    }

    private static final String CPU_SHARE = "CPUShare";
    private static final String PURPOSE_SHARE = "PurposeShare";
    private static final String PARALLEL_SHARE = "ParallelShare";
    public static final int PARALLEL_THREADS = 512;

    private static final int MAX_THREADS = 64; // limit for large amount of e.g. excels
    private static final int MIN_THREADS = 4;
    private static final float CPU_MAX_USAGE = 0.95f;
    private static final AtomicInteger counter = new AtomicInteger(1);
    public static final int CPU_THREADS;
    private static int cpuParallelism;

    private static final ExecutorService maxParallelService;
    private static final Map<String, ThreadPool> purposeServiceMap = new ConcurrentHashMap<>();
    private static final Map<String, ThreadPool> cpuServiceMap = new ConcurrentHashMap<>();
    static {
        Network.AppType appType = SettingsManager.getAppType();
        int cores = Runtime.getRuntime().availableProcessors();
        CPU_THREADS = MathUtils.clamp((int) (cores * CPU_MAX_USAGE), MIN_THREADS, MAX_THREADS);
        cpuParallelism = CPU_THREADS;
        maxParallelService = new ThreadPool(PARALLEL_THREADS, PARALLEL_SHARE);
        // XXX: Virtual Threads can have synchronized deadlocks in RMI in Java 21, possible fixed in 25
        // See: https://surfingcomplexity.blog/2024/08/01/reproducing-a-java-21-virtual-threads-deadlock-scenario-with-tla/
        // maxParallelService = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name(PARALLEL_SHARE + "-V").factory());
        TLogger.info("Setup PowerShare: " + appType + " with: " + cores + " cores to use: " + CPU_THREADS + " CPU threads.");
    }

    public static final void execute(Bound bound, Runnable runnable) {
        execute(null, bound, runnable);
    }

    private static final void execute(PoolGroup group, Bound bound, Runnable runnable) {

        // select matching service
        ExecutorService service = switch (bound) {
            case MAX_CPU, MAX_CPU2 -> getCPUPool(bound.getToken(group));
            case MAX_PURPOSE -> throw new IllegalArgumentException("Purpose only allowed as MultiTask.");
            case MAX_PARRALISM -> maxParallelService;
            default -> maxParallelService;
        };
        // execute in service
        service.execute(runnable);
    }

    /**
     * Execute in Pool Group specific MAX_CPU thread pool
     */
    public static final void execute(PoolGroup group, Runnable runnable) {
        execute(group, Bound.MAX_CPU, runnable);
    }

    /**
     * Execute in generic MAX_PARRALISM thread pool
     */
    public static final void execute(Runnable runnable) {
        execute(null, Bound.MAX_PARRALISM, runnable);
    }

    public static long getActiveCPUThreadCount() {
        return getCPUPool(CPU_SHARE).getActiveThreadCount();
    }

    public static final int getActiveThreadCount(PoolGroup group) {

        int count = 0;
        if (group != null) {
            for (Bound b : Bound.values()) {
                ThreadPool pool = cpuServiceMap.get(b.getToken(group));
                if (pool != null) {
                    count += pool.getActiveThreadCount();
                }
            }
        }
        return count;
    }

    public static final int getCPUParallelism() {
        return cpuParallelism;
    }

    private static final ThreadPool getCPUPool(String key) {
        return cpuServiceMap.computeIfAbsent(key, f -> new ThreadPool(cpuParallelism, key));
    }

    public static long getCPUPoolSize() {
        return getCPUPool(CPU_SHARE).getPoolSize();
    }

    public static long getCPUTaskCount() {
        ThreadPool pool = getCPUPool(CPU_SHARE);
        return pool.getQueuedSubmissionCount() + pool.getActiveThreadCount();
    }

    private static final ThreadPool getPurposePool(String purpose, int purposeThreads) {

        if (!StringUtils.containsData(purpose)) {
            throw new IllegalArgumentException("Missing pool purpose.");
        } else {
            return purposeServiceMap.computeIfAbsent(purpose, f -> new ThreadPool(purposeThreads, PURPOSE_SHARE + "-" + purpose));
        }
    }

    public static String htmlState() {

        StringBuilder builder = new StringBuilder();
        builder.append("<br>Platform Threads: ");
        builder.append(Thread.activeCount());
        builder.append(" (");
        builder.append(cpuParallelism);
        builder.append(" parallelism)");

        /**
         * Print brain shares
         */
        int activeTotal = 0;
        int poolSizeTotal = 0;
        for (Entry<String, ThreadPool> entry : cpuServiceMap.entrySet()) {
            ThreadPool pool = entry.getValue();
            int active = pool.getActiveThreadCount();
            int poolSize = pool.getPoolSize();
            activeTotal += pool.getActiveThreadCount();
            poolSizeTotal += pool.getPoolSize();

            if (active > 0) {// print active source
                builder.append("<br>" + pool.getName() + ": " + active + "/" + poolSize);
            }
        }
        if (activeTotal == 0) { // non active, print pool size
            builder.append("<br>" + CPU_SHARE + ": " + activeTotal + "/" + poolSizeTotal + "/" + cpuServiceMap.size());
        }

        /**
         * Print purpose shares
         */
        activeTotal = 0;
        poolSizeTotal = 0;
        for (Entry<String, ThreadPool> entry : purposeServiceMap.entrySet()) {
            ThreadPool pool = entry.getValue();
            int active = pool.getActiveThreadCount();
            int poolSize = pool.getPoolSize();
            poolSizeTotal += poolSize;
            activeTotal += active;

            if (active > 0) {// print active source
                builder.append("<br>" + pool.getName() + ": " + active + "/" + poolSize);
            }
        }
        if (activeTotal == 0) { // non active, print pool size
            builder.append("<br>" + PURPOSE_SHARE + ": " + activeTotal + "/" + poolSizeTotal + "/" + purposeServiceMap.size());
        }

        return builder.toString();
    }

    public static final void killPools(PoolGroup group) {

        if (group != null) {
            for (Bound b : Bound.values()) {
                if (b.isKillable()) {
                    // create if it was not created jet, and shutdown
                    ThreadPool pool = getCPUPool(b.getToken(group));
                    shutdown(pool, true);
                }
            }
        }
    }

    public static final MultiTask multiTask(Bound bound) {
        return multiTask(null, bound);
    }

    public static final MultiTask multiTask(PoolGroup group, Bound bound) {
        return multiTask(group, bound, null, 1);
    }

    public static final MultiTask multiTask(PoolGroup group, Bound bound, String purpose, int purposeThreads) {
        return new MultiTask(group, bound, purpose, purposeThreads);
    }

    public static final <T, R> MultiTaskList<R> multiTaskListAndWait(Collection<T> completeList, ListTask<T, R> run) {
        return multiTaskListAndWait(null, completeList, run);
    }

    public static final <T, R> MultiTaskList<R> multiTaskListAndWait(PoolGroup group, Collection<T> completeList, ListTask<T, R> run) {
        // Parallelization is twice amount of available thread (optimal load balancing in most cases)
        return multiTaskListAndWait(group, completeList, run, 2 * CPU_THREADS);
    }

    public static final <T, R> MultiTaskList<R> multiTaskListAndWait(PoolGroup group, Collection<T> completeList, ListTask<T, R> run,
            int parallelization) {

        // create list task
        MultiTaskList<R> mt = new MultiTaskList<>(group);
        if (completeList == null) {
            return mt;
        }
        List<R> resultList = mt.getResult();

        // make sure always at least 1 task
        int listTaskSize = Math.max(1, (int) Math.ceil(completeList.size() / (double) parallelization));

        // small lists skip, no MULTI usefully
        if (completeList.size() <= listTaskSize) {

            List<R> result = run.run(completeList);
            if (result != null) {
                resultList.addAll(result);
            }
            // count as 1 task done
            if (!completeList.isEmpty()) {
                mt.totalTasks.set(1);
            }
            return mt;
        }

        Object[] completeArray = completeList.toArray();

        for (int i = 0; i < completeList.size(); i += listTaskSize) {
            int startIndex = Math.min(completeList.size() - 1, i);
            int endIndex = Math.min(completeList.size(), i + listTaskSize);
            int lenght = endIndex - startIndex;
            if (lenght == 0) {
                continue;
            }

            mt.execute(() -> {

                // copy from complete array into sub array
                Object[] subArray = new Object[lenght];
                System.arraycopy(completeArray, startIndex, subArray, 0, lenght);

                @SuppressWarnings("unchecked") List<T> subList = (List<T>) Arrays.asList(subArray);

                // execute the sublist
                List<R> result = run.run(subList);
                if (result != null) {
                    synchronized (resultList) {
                        resultList.addAll(result);
                    }
                }
            });
        }
        mt.waitUntilAllFinished();
        return mt;
    }

    public static final void setCPUParallelism(int threads) {

        // make sure values is safe
        threads = MathUtils.clamp(threads, MIN_THREADS, CPU_THREADS);
        if (threads == cpuParallelism) {
            return;
        }

        // update parallelism for CPU pools
        cpuParallelism = threads;
        for (ThreadPool cpuPool : cpuServiceMap.values()) {
            cpuPool.setParallelism(cpuParallelism);
        }
        TLogger.warning("Updated CPU PowerShare: " + cpuParallelism + " parallel active threads.");
    }

    public static final void shutdown() {

        List<ExecutorService> services = new ArrayList<>(purposeServiceMap.values());
        services.addAll(cpuServiceMap.values());
        services.add(maxParallelService);

        for (ExecutorService service : services) {
            shutdown(service, false);
        }
    }

    private static final void shutdown(ExecutorService service, boolean force) {

        try {
            if (force) {
                String log = "Service: " + service.toString() + " was FORCED to shutdown.";
                if (service instanceof ThreadPool tp) {
                    log += " With " + tp.getActiveThreadCount() + " threads still active!";
                }
                TLogger.warning(log);
                service.shutdownNow();
                return;
            }

            TLogger.info("Shutdown Service: " + service.toString() + "... (Please wait up to 5 minutes to terminate)");
            long start = System.currentTimeMillis();
            service.shutdown();
            if (service.awaitTermination(5, TimeUnit.MINUTES)) {
                TLogger.info("Service: " + service.toString() + " terminated gracefully in: " + StringUtils.toSimpleTimePast(start) + ".");
            } else {
                shutdown(service, true);
            }
        } catch (Exception e) {
            TLogger.exception(e);
        }
    }

    public static final void shutdown(PoolGroup group) {

        if (group != null) {
            for (Bound b : Bound.values()) {
                ThreadPool pool = cpuServiceMap.remove(b.getToken(group));
                if (pool != null) {
                    shutdown(pool, false);
                }
            }
        }
    }
}
