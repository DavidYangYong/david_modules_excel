package org.fl.modules.excel.poi.exportExcel.multi;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author david
 * @create 2018-08-09 13:26
 **/
public class NamedThreadFactory implements ThreadFactory {

	private static AtomicInteger tag = new AtomicInteger(0);

	private static String THREAD_NAME = "Export Thread";

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setName("resumption调度器线程：" + THREAD_NAME + tag.getAndIncrement());
		return thread;
	}

}
