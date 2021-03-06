package com.github.lixiang2114.flow.scheduler;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.github.lixiang2114.flow.thread.SpringThreadPool;

/**
 * @author Lixiang
 * @description 流程控制调度器
 */
@SuppressWarnings("unchecked")
public abstract class SchedulerPool {
	/**
	 * Spring线程池执行器
	 */
	private static ThreadPoolTaskExecutor taskExecutor;
	
	/**
	 * 获取Spring线程池执行器
	 * @return 线程池执行器
	 */
	public synchronized static final ThreadPoolTaskExecutor getTaskExecutor(){
		if(null!=taskExecutor) return taskExecutor;
		return taskExecutor=SpringThreadPool.getSpringThreadPool();
	}
}
