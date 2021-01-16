package com.github.lixiang2114.flow.thread;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.github.lixiang2114.flow.scheduler.ETLSchedulerPool;
import com.github.lixiang2114.flow.scheduler.SchedulerPool;
import com.github.lixiang2114.flow.scheduler.TRASchedulerPool;

/**
 * @author Lixiang
 * @description 平滑关闭LogCollector例程
 */
public class GracefulShutdown extends Thread{	
	
	@Override
	public void run() {
		TRASchedulerPool.gracefulStopAllTRAs();
		TRASchedulerPool.stopTRAScheduler();
		
		ETLSchedulerPool.gracefulStopAllETLs();
		ETLSchedulerPool.stopETLScheduler();
		
		ThreadPoolTaskExecutor taskExecutor=SchedulerPool.getTaskExecutor();
		if(null!=taskExecutor) taskExecutor.destroy();
		
		TRASchedulerPool.destoryTRAPool();
		ETLSchedulerPool.destoryETLPool();
	}
}
