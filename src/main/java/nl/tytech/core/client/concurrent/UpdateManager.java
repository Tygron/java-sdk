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
 * You can add random objects to this class that need to updated and do not belong in the OpenGL thread. Update freq is 60FPS by default.
 *
 * Note objects are weak referenced, thus removed when no one links!
 *
 * @author Maxim Knepfle
 */
public class UpdateManager {

    /**
     * Internal class that counts the frame rate for each thread (can be improved).
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
         * Max fps in MS at 30 fps
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

            // keep looping until the app dies (thread is daemon)
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

                // Limit the sleep time to max FPS and min 0.
                executionTime = System.currentTimeMillis() - start;
                sleepTime = MAX_TPF - executionTime + bonusTime;

                // when this frame was way too slow, add a bonus time to the next frame.
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
     * Execute the Runnable in the Parallel thread
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
     * Do not call this method except from main OpenGL loop update method.
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
            // Add the parallel thread
            fifo.addLast(runnable);
        }
    }

    private void _removeOpenGL(Object updatable) {
        synchronized (tweakOpenGLUpdatables) {
            for (WeakReference<OpenGLUpdatable> updatableReference : tweakOpenGLUpdatables) {
                // check for both objects!
                if (updatableReference.get() == updatable || updatableReference == updatable) {
                    tweakOpenGLUpdatables.remove(updatableReference);
                    break;
                }
            }
            openGLUpdatables = new ArrayList<WeakReference<OpenGLUpdatable>>(tweakOpenGLUpdatables);
        }
    }

    private void _removeParallel(Object updatable) {
        synchronized (tweakParallelUpdatables) {
            for (WeakReference<ParallelUpdatable> updatableReference : tweakParallelUpdatables) {
                // check for both objects!
                if (updatableReference.get() == updatable || updatableReference == updatable) {
                    tweakParallelUpdatables.remove(updatableReference);
                    break;
                }
            }
            parallelUpdatables = new ArrayList<>(tweakParallelUpdatables);
        }
    }

    private final boolean _shutdown(long waitMS) {

        long start = System.currentTimeMillis();
        // first wait 1 minute for jobs to finish
        while (!fifo.isEmpty() && System.currentTimeMillis() - start < waitMS) {
            Thread.yield();
        }
        parallelUpdater.stopThread();
        // second wait 1 minute for thread to die
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
                // remove and break, no problem skipping one frame
                _removeOpenGL(updatable);
                break;
            }
        }
    }

    private void updateParallel(float tpf) {

        // execute runners
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
                // remove and break, no problem skipping one frame
                _removeParallel(updatable);
                break;
            }
        }
    }
}
