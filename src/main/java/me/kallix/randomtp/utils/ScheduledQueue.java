package me.kallix.randomtp.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@SuppressWarnings("unused")
public final class ScheduledQueue<T> {

    private final Lock lock = new ReentrantLock();
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private final Queue<Pair<T, TinyFuture<T>>> pool = Lists.newLinkedList();

    private final Plugin plugin;
    private final int delay, period;

    private BukkitTask task;
    private boolean running = true;

    private ScheduledQueue(Plugin plugin,
                           int delay, int period,
                           boolean syncStart) {

        this.plugin = plugin;
        this.delay = delay;
        this.period = period;
        this.setRunning(true, syncStart);
    }

    public static <T> ScheduledQueue<T> newScheduledQueue(Plugin plugin, int delay, int period, boolean syncStart) {
        return new ScheduledQueue<>(plugin, delay, period, syncStart);
    }

    public static <T> ScheduledQueue<T> newScheduledQueue(Plugin plugin, int delay, boolean syncStart) {
        return new ScheduledQueue<>(plugin, delay, 1, syncStart);
    }

    public static <T> ScheduledQueue<T> newScheduledQueue(Plugin plugin, boolean syncStart) {
        return new ScheduledQueue<>(plugin, 0, 1, syncStart);
    }

    public static <T> ScheduledQueue<T> newScheduledQueue(Plugin plugin) {
        return new ScheduledQueue<>(plugin, 0, 1, true);
    }

    public boolean contains(T obj) {

        LinkedList<Pair<T, TinyFuture<T>>> handle = handle();

        for (Pair<T, TinyFuture<T>> pair : handle) {
            if (pair != null && pair.key().equals(obj)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(int index) {
        LinkedList<Pair<T, TinyFuture<T>>> handle = handle();
        return index < handle.size() && handle.get(index) != null;
    }

    public Queue<Pair<T, TinyFuture<T>>> pool() {
        return pool;
    }

    public void dispose(T key) {

        Set<Pair<T, TinyFuture<T>>> toRemove = Sets.newHashSet();

        pool.forEach(pair -> {
            if (pair.key().equals(key)) {
                toRemove.add(pair);
            }
        });

        toRemove.forEach(pool::remove);
    }

    public void flush() {
        pool.clear();
    }

    public int indexOf(T obj) {
        LinkedList<Pair<T, TinyFuture<T>>> handle = handle();
        int size = handle.size();

        for (int i = 0; i < size; i++) {
            Pair<T, TinyFuture<T>> pair = handle.get(i);
            if (pair != null && pair.key().equals(obj)) {
                return i;
            }
        }
        return -1;
    }

    public LinkedList<Pair<T, TinyFuture<T>>> handle() {
        return (LinkedList<Pair<T, TinyFuture<T>>>) pool;
    }

    public int positionOf(T obj) {
        int index = indexOf(obj);

        if (index == -1) {
            return -1;
        } else {
            int pos = 2;
            int all = pool().size();
            int offset = all - 1;

            for (int i = offset; i >= 0; i--) {
                if (contains(i)) {
                    pos--;
                }
            }
            if (pos == 2) {
                return -1;
            } else {
                return pos;
            }
        }
    }

    public TinyFuture<T> submit(T obj) {

        TinyFuture<T> future = new TinyFuture<>(plugin);

        lock.lock();

        try {
            pool.add(new Pair<>(obj, future));
        } finally {
            lock.unlock();
        }

        return future;
    }

    @SafeVarargs
    public final List<Pair<T, TinyFuture<T>>> submit(T... obj_s) {

        List<Pair<T, TinyFuture<T>>> result = Lists.newArrayListWithExpectedSize(obj_s.length);

        for (T obj : obj_s) {
            result.add(new Pair<>(obj, new TinyFuture<>(plugin)));
        }

        lock.lock();

        try {
            pool.addAll(result);
        } finally {
            lock.unlock();
        }

        return result;
    }

    public synchronized void setRunning(boolean running, boolean syncRoll) {
        this.running = running;
        Runnable runnable = () -> {
            if (this.running) {
                roll(syncRoll);
            } else {
                task.cancel();
            }
        };

        if (running && task == null) {
            if (syncRoll) {
                this.task = scheduler.runTaskTimer(plugin, runnable, delay, period);
            } else {
                this.task = scheduler.runTaskTimerAsynchronously(plugin, runnable, delay, period);
            }
        } else if (!running) {
            if (this.task != null) {
                this.task.cancel();
            }
        }
    }

    public void roll(boolean sync) {
        lock.lock();

        try {
            Pair<T, TinyFuture<T>> submitter = pool.poll();

            if (submitter != null) {
                if (sync) {
                    if (plugin.getServer().isPrimaryThread()) {
                        submitter.value().complete(submitter.key());
                    } else {
                        scheduler.runTask(plugin, () -> submitter.value().complete(submitter.key()));
                    }
                } else {
                    submitter.value().complete(submitter.key());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void destroy() {
        this.setRunning(false, false);
        this.pool.clear();
    }

    @RequiredArgsConstructor
    @Getter
    public static class TinyFuture<E> {

        private final Plugin plugin;

        private final ReentrantLock lock = new ReentrantLock();
        private final BukkitScheduler scheduler = plugin.getServer().getScheduler();

        private Runnable handler;
        private E value;

        private boolean sync;

        public TinyFuture<E> onCompleteSync(Runnable action, boolean force) {
            onComplete0(action, true, force);
            return this;
        }

        public TinyFuture<E> onComplete(Runnable action, boolean force) {
            onComplete0(action, false, force);
            return this;
        }

        private void onComplete0(Runnable action, boolean sync, boolean force) {

            if (force && value != null) {
                if (sync) {
                    scheduler.runTask(plugin, action);
                } else {
                    action.run();
                }
            }

            if (value != null || handler != null) {
                throw new IllegalStateException();
            }

            lock.lock();

            try {
                this.handler = action;
                this.sync = sync;
            } finally {
                lock.unlock();
            }
        }

        private synchronized void complete(E value) {

            if (this.value != null) {
                throw new IllegalStateException();
            } else {
                this.value = value;
            }

            if (sync) {
                scheduler.runTask(plugin, handler);
            } else {
                handler.run();
            }
        }
    }
}
