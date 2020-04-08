package com.ola.travel.camera.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Create by wyman
 * Email  piitw@qq.com
 * Create at 2019-05-17 16:41
 * Desc : 创建线程池 配合ScheduledExecutorService使用 替换Timer
 */
public class ThreadPoolManager {

    private volatile static ThreadPoolManager manager = null;
    /**
     * 任务缓存队列，用来存放等待执行的任务
     */
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>();

    public ThreadPoolManager() {
    }

    public static ThreadPoolManager getInstance() {
        if (manager == null) {
            synchronized (ThreadPoolManager.class) {
                if (manager == null) {
                    manager = new ThreadPoolManager();
                }
            }
        }
        return manager;
    }

    private static ThreadPoolExecutor threadPoolExecutor = null;
    private static ScheduledExecutorService scheduledExecutorService = null;

    private ThreadPoolExecutor getThreadPoolExecutor() {
        //核心池的大小（即线程池中的线程数目大于这个参数时，提交的任务会被放进任务缓存队列）
        int corePoolSize = 2;
        //线程池最大能容忍的线程数
        int maximumPoolSize = 5;
        //线程存货时间
        long keepAliveTime = 0L;
        //时间单位
        TimeUnit unit = TimeUnit.MICROSECONDS;
        if (!isThreadServiceEnable()) {
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize
                    , keepAliveTime, unit, workQueue, new ThreadFactoryBuilder()
                    .setNameFormat("olayc-pool-%d").build());
        }

        return threadPoolExecutor;
    }

    private static final int SCHE_THREAD_SIZE = 5;

    public ScheduledExecutorService getScheduledExecutorService() {

        if (!isScheduledServiceEnable()) {
            scheduledExecutorService = new ScheduledThreadPoolExecutor(SCHE_THREAD_SIZE
                    , new ThreadFactoryBuilder().setNameFormat("olayc-pool-%d").build());
        }

        return scheduledExecutorService;
    }

    /**
     * 循环执行任务
     *
     * @param timerTask
     * @param initialDelay Initial delay time
     * @param period       the period between successive executions
     * @param timeUnit     unit
     * @return
     */
    public ScheduledFuture<?> addScheduledExecutor(TimerTask timerTask, long initialDelay, long period, TimeUnit timeUnit) {
        return getScheduledExecutorService().scheduleAtFixedRate(timerTask, initialDelay, period, timeUnit);
    }

    /**
     * 执行单项任务
     *
     * @param runnable
     * @param delay
     * @param timeUnit
     * @return
     */
    public ScheduledFuture<?> addDelayScheduledExecutor(Runnable runnable, long delay, TimeUnit timeUnit) {
        return getScheduledExecutorService().schedule(runnable, delay, timeUnit);
    }


    /**
     * 添加任务
     *
     * @param runnable
     */
    public void addThreadExecutor(Runnable runnable) {
        getThreadPoolExecutor().submit(runnable);
    }

    /**
     * 优雅停掉当前任务，已添加的任务会继续执行完毕，新的线程将会被拒绝。
     * 这个方法不会等待提交的任务执行完，可以用awaitTermination来等待任务执行完。
     */
    public void shutDownScheduledExecutor() {
        if (!isScheduledServiceEnable()) {
            getScheduledExecutorService().shutdown();
        }
    }

    /**
     * 停止当前和即将执行的任务并不接受新任务
     */
    public void shutDownNowScheduledExecutor() {
        if (!isScheduledServiceEnable()) {
            getScheduledExecutorService().shutdownNow();
        }
    }

    /**
     * 线程池将变成shutdown状态，此时不接收新任务，但会处理完正在运行的 和 在阻塞队列中等待处理的任务
     */
    public void shutDownThreadPool() {
        if (!isThreadServiceEnable()) {
            getThreadPoolExecutor().shutdown();
        }
    }

    /**
     * 线程池将变成stop状态，此时不接收新任务，不再处理在阻塞队列中等待的任务，还会尝试中断正在处理中的工作线程
     */
    public void shutDownNowThreadPool() {
        if (!isThreadServiceEnable()) {
            getThreadPoolExecutor().shutdownNow();
        }
    }

    /**
     * 线程池将变成stop状态，此时不接收新任务，不再处理在阻塞队列中等待的任务，还会尝试中断正在处理中的工作线程
     */
    public void cancelSingleThread(ScheduledFuture scheduledFuture) {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

    /**
     * 判断任务池是否启动
     *
     * @return
     */
    private boolean isScheduledServiceEnable() {
        return !(scheduledExecutorService == null
                || scheduledExecutorService.isShutdown()
                || scheduledExecutorService.isTerminated());
    }

    /**
     * 判断线程池是否启动
     *
     * @return
     */
    private boolean isThreadServiceEnable() {
        return !(threadPoolExecutor == null
                || threadPoolExecutor.isShutdown()
                || threadPoolExecutor.isTerminated());
    }

}