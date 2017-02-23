/* 
 * Copyright 2017 Faissal Elamraoui.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.tempora.concurrent;

import com.github.tempora.svc.GmailDataProvider;
import com.google.common.util.concurrent.*;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * {@link BackgroundTask} executor
 *
 */
public class BackgroundTaskExecutor {

    @Autowired
    GmailDataProvider gmailDataProvider;

    private BackgroundTaskExecutor() {
    }

    /**
     * Execute task using separate thread pool
     *
     * @param task task to execute
     * @param <T>  task result type
     */
    public static <T> void run(final BackgroundTask<T> task) {
        execute(task, false);
    }

    /**
     * Execute task using separate thread pool and wait for it completion
     *
     * @param task task to execute
     * @param <T>  task result type
     */
    public static <T> void runAndWait(final BackgroundTask<T> task) {
        execute(task, true);
    }

    private static <T> void execute(final BackgroundTask<T> task, boolean wait) {
        final ListeningExecutorService service = MoreExecutors.listeningDecorator(
                wait ?
                        MoreExecutors.sameThreadExecutor() :
                        Executors.newSingleThreadExecutor());
        try {
            if (task instanceof LifecycleAware) {
                ((LifecycleAware) task).beforeCall();
            }
            final ListenableFuture<T> resultFuture = service.submit(task);
            task.relatedFuture = resultFuture;
            Futures.addCallback(resultFuture, task);
            if (task instanceof LifecycleAware) {
                Futures.addCallback(resultFuture, new FutureCallback<T>() {
                    @Override
                    public void onSuccess(T result) {
                        ((LifecycleAware) task).afterCall();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ((LifecycleAware) task).afterCall();
                    }
                });
            }
            if (wait) {
                Futures.getUnchecked(resultFuture);
            }
        } finally {
            service.shutdown();
        }
    }

    /**
     * Base class for background tasks.
     * <p><b>NOTE:</b> Any UI updates in the task class
     * need to use {@link #updateUI(UIUpdateOperation)} method. This causes proper
     * user application object updates.</p>
     *
     * @author michal.swiatowy@txtr.com
     */
    public abstract static class BackgroundTask<T> implements Callable<T>, FutureCallback<T>, Cancellable {
        private Future<T> relatedFuture;
        private final UI UILock;

        /**
         * Creates background task instance
         *
         * @param ui current application instance
         */
        protected BackgroundTask(UI ui) {
            UILock = ui;
        }

        /**
         * Wrap exception in runtime exception and rethrow
         */
        protected RuntimeException wrap(Throwable throwable) {
            return new RuntimeException(throwable);
        }

        /**
         * Check if task has been cancelled
         */
        protected boolean isCancelled(Throwable throwable) {
            return throwable instanceof CancellationException;
        }

        /**
         * Update UI element in thread safe (application instance aware) manner
         */
        protected final void updateUI(UIUpdateOperation operation) {
            synchronized (UILock) {
                operation.run();
            }
        }

        @Override
        public void cancel() {
            relatedFuture.cancel(true);
            relatedFuture = null;
        }

        protected static interface UIUpdateOperation {
            public void run();
        }
    }

    private static interface Cancellable {
        public void cancel();
    }

}
