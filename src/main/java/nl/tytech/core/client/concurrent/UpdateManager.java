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
package nl.tytech.core.client.concurrent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import nl.tytech.util.StringUtils;
import nl.tytech.util.ThreadUtils;
import nl.tytech.util.logger.TLogger;

/**
 * Adds objects that require updates outside of the OpenGL thread. The default update frequency is 60 FPS.
 *
 * Objects are weakly referenced and will be removed when no longer strongly referenced.
 *
 * @author Maxim Knepfle
 */
public class UpdateManager {

    /**
     * Internal class that counts the frame rate for each thread.
     */
    public static class FPSCounter implements OpenGLUpdatable, ParallelUpdatable, Updatable {

        private static final long NANO_SECOND = 1_000_000_000;
        private volatile int fps = 0;
        private int frameCounter = 0;
        private long time = System.nanoTime();

        public int getFps() {
            return fps;
        }

        @Override
        public void update(float tpf) {
            updateInner();
        }

        public void updateInner() {
            updateInner(System.nanoTime());
        }

        public void updateInner(long now) {

            if (now - time > NANO_SECOND) {
                fps = frameCounter;
                frameCounter = 0;
                time = now;
            }
            frameCounter++;
        }

        @Override
        public void updateOpenGL(float tpf) {
            updateInner();
        }

        @Override
        public void updateParallel(float tpf) {
            updateInner();
        }
    }

    private class ParallelUpdater extends Thread {

        /**
         * Maximum time per frame in milliseconds for 30 FPS
         */
        private static final float MAX_TPF = 1000f / 30f;

        private volatile boolean active = true;

        private ParallelUpdater() {
            this.setName("Client-" + ParallelUpdater.class.getSimpleName());
            this.setDaemon(true);
        }

        @Override
        public final void run() {

            long start = System.currentTimeMillis();
            float executionTime = 1;
            float sleepTime = 0;
            float tpf = MAX_TPF;
            // extra time to make up for the delay of a slow previous frame.
            float bonusTime = 0;

            // loop until the application terminates (thread is daemon)
            while (active) {
                tpf = System.currentTimeMillis() - start;
                start = System.currentTimeMillis();
                try {
                    // call update with TPF in seconds
                    this.update(tpf / 1000f);
                } catch (Exception exp) {
                    TLogger.exception(exp);
                } catch (OutOfMemoryError error) {
                    TLogger.exception(error);
                    System.gc();
                }

                // Limit the sleep time between 0 and the target frame time.
                executionTime = System.currentTimeMillis() - start;
                sleepTime = MAX_TPF - executionTime + bonusTime;

                // If the current frame exceeded the target time, compensate in the next frame.
                bonusTime = sleepTime < 0 ? sleepTime : 0;

                if (sleepTime > 0) {
                    ThreadUtils.sleepInterruptible((long) sleepTime);
                }
            }
        }

        public void stopThread() {
            active = false;
        }

        public void update(float tpf) {
            updateParallel(tpf);
        }
    };

    private static final class SingletonHolder {

        private static final UpdateManager INSTANCE = new UpdateManager();
    }

    public static Thread PARALLELTHREAD = null;

    public static void addOpenGL(OpenGLUpdatable updatable) {
        SingletonHolder.INSTANCE._addOpenGL(updatable);
    }

    public static void addParallel(ParallelUpdatable updatable) {
        SingletonHolder.INSTANCE._addParallel(updatable);
    }

    /**
     * Executes the Runnable in the Parallel thread.
     *
     * @param runnable
     */
    public static void exec(final Runnable runnable) {
        SingletonHolder.INSTANCE._exec(runnable);
    }

    public static int getOpenGLFPS() {
        return SingletonHolder.INSTANCE.openGLCounter.getFps();
    }

    public static int getParallelFPS() {
        return SingletonHolder.INSTANCE.parallelCounter.getFps();
    }

    public static void removeOpenGL(Object updatable) {
        SingletonHolder.INSTANCE._removeOpenGL(updatable);
    }

    public static void removeParallel(Object updatable) {
        SingletonHolder.INSTANCE._removeParallel(updatable);
    }

    public static boolean shutdown(long waitMS) {
        return SingletonHolder.INSTANCE._shutdown(waitMS);
    }

    /**
     * This method should only be called from the main OpenGL loop update method.
     * @param tpf
     */
    public static void updateOpenGL(float tpf) {
        SingletonHolder.INSTANCE._updateOpenGL(tpf);
    }

    private List<WeakReference<ParallelUpdatable>> parallelUpdatables = new ArrayList<>();

    private List<WeakReference<OpenGLUpdatable>> openGLUpdatables = new ArrayList<>();

    private final List<WeakReference<ParallelUpdatable>> tweakParallelUpdatables = new ArrayList<>();

    private final List<WeakReference<OpenGLUpdatable>> tweakOpenGLUpdatables = new ArrayList<>();

    private final ParallelUpdater parallelUpdater;

    private final FPSCounter openGLCounter, parallelCounter;

    private final LinkedBlockingDeque<Runnable> fifo = new LinkedBlockingDeque<>();

    private UpdateManager() {

        parallelUpdater = new ParallelUpdater();
        parallelUpdater.start();
        PARALLELTHREAD = parallelUpdater;

        openGLCounter = new FPSCounter();
        this._addOpenGL(openGLCounter);

        parallelCounter = new FPSCounter();
        this._addParallel(parallelCounter);

    }

    private void _addOpenGL(OpenGLUpdatable updatable) {
        synchronized (tweakOpenGLUpdatables) {
            tweakOpenGLUpdatables.add(new WeakReference<OpenGLUpdatable>(updatable));
            openGLUpdatables = new ArrayList<>(tweakOpenGLUpdatables);
        }
    }

    private void _addParallel(ParallelUpdatable updatable) {
        synchronized (tweakParallelUpdatables) {
            tweakParallelUpdatables.add(new WeakReference<ParallelUpdatable>(updatable));
            parallelUpdatables = new ArrayList<>(tweakParallelUpdatables);
        }
    }

    private void _exec(final Runnable runnable) {

        if (Thread.currentThread() == PARALLELTHREAD) {
            try {
                runnable.run();
            } catch (Exception exp) {
                TLogger.exception(exp);
            }
        } else {
            // Queue the runnable for execution by the Parallel thread
            fifo.addLast(runnable);
        }
    }

    private void _removeOpenGL(Object updatable) {
        synchronized (tweakOpenGLUpdatables) {
            tweakOpenGLUpdatables.removeIf(ref -> ref.get() == updatable || ref == updatable);
            openGLUpdatables = new ArrayList<WeakReference<OpenGLUpdatable>>(tweakOpenGLUpdatables);
        }
    }

    private void _removeParallel(Object updatable) {
        synchronized (tweakParallelUpdatables) {
            tweakParallelUpdatables.removeIf(ref -> ref.get() == updatable || ref == updatable);
            parallelUpdatables = new ArrayList<>(tweakParallelUpdatables);
        }
    }

    private final boolean _shutdown(long waitMS) {

        long start = System.currentTimeMillis();
        // Wait for jobs to finish within the specified timeout.
        while (!fifo.isEmpty() && System.currentTimeMillis() - start < waitMS) {
            Thread.yield();
        }
        parallelUpdater.stopThread();
        // Wait for the thread to terminate within the specified timeout.
        while (parallelUpdater.isAlive() && System.currentTimeMillis() - start < waitMS) {
            Thread.yield();
        }
        TLogger.info("Terminated " + parallelUpdater.getName() + " in: " + StringUtils.toSimpleTimePast(start));
        return System.currentTimeMillis() - start < waitMS;
    }

    private void _updateOpenGL(float tpf) {
        for (int i = 0; i < openGLUpdatables.size(); i++) {
            WeakReference<OpenGLUpdatable> updatableReference = openGLUpdatables.get(i);
            OpenGLUpdatable updatable = updatableReference.get();
            if (updatable != null) {
                updatable.updateOpenGL(tpf);
            } else {
                // Remove reference and stop processing for this frame.
                _removeOpenGL(updatable);
                break;
            }
        }
    }

    private void updateParallel(float tpf) {

        // Execute queued Runnables
        Runnable runnable = fifo.pollFirst();
        while (runnable != null) {
            try {
                runnable.run();
            } catch (Exception e) {
                TLogger.exception(e);
            }
            runnable = fifo.pollFirst();
        }

        for (int i = 0; i < parallelUpdatables.size(); i++) {
            WeakReference<ParallelUpdatable> updatableReference = parallelUpdatables.get(i);
            ParallelUpdatable updatable = updatableReference.get();
            if (updatable != null) {
                updatable.updateParallel(tpf);
            } else {
                // Remove reference and stop processing for this frame.
                _removeParallel(updatable);
                break;
            }
        }
    }
}
