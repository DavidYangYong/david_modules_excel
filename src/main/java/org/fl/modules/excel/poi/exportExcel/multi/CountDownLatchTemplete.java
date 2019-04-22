/*
 * Copyright (C) 2013 Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * # * CountDownLatchDemo1.java Create on 2013-11-14 上午09:51:16 # * project Test # * Copyright 2013 by . #
 *
 * 文件名：CountDownLatchDemo1.java 版本信息： 日期：2013-11-14 Copyright 足下 Corporation 2013 版权所有
 */
/**
 * 文件名：CountDownLatchDemo1.java
 * 版本信息：
 * 日期：2013-11-14
 * Copyright 足下 Corporation 2013
 * 版权所有
 */
package org.fl.modules.excel.poi.exportExcel.multi;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.fl.modules.excel.poi.exportExcel.ISxssfWorkBookList;
import org.fl.modules.utils.RowSelect;

/**
 * @author David.Yang
 * @version 1.0
 *          CreateDate：2013-11-14 上午09:51:16
 *          类说明
 */

/**
 * 项目名称：Test
 * 类名称：CountDownLatchDemo1
 * 类描述：
 * 创建人：David.Yang
 * 创建时间：2013-11-14 上午09:51:16
 * 修改人：David.Yang
 * 修改时间：2013-11-14 上午09:51:16
 * 修改备注：
 *
 * @version
 */
@Slf4j
class CountDownLatchTemplete {
	private boolean isClose;



	/***
	 * 多线程主方法
	 *
	 * @param int
	 *            totalRows
	 * @param SXSSFWorkBookOpTemplete
	 *            sxssfWorkBookOperation
	 * @param ISxssfWorkBookList
	 *            sxssfWorkBookList
	 */

	public void countDownLatch(int totalRows, final SXSSFWorkBookOperation sxssfWorkBookOperation,
			ISxssfWorkBookList sxssfWorkBookList)
			throws IOException, NullPointerException, RuntimeException {
		if (sxssfWorkBookOperation == null) {
			throw new NullPointerException(
					"sxssfWorkBookOperation is not null");
		}
		int pageSize = 0;
		if (sxssfWorkBookOperation.getPageSize() == 0) {
			sxssfWorkBookOperation.setPageSize(PageSizeUtils.pageSize);
		}
		pageSize = sxssfWorkBookOperation.getPageSize();
		sxssfWorkBookOperation.setTotalRows(totalRows);
		int pageNo = (totalRows + (pageSize - 1)) / pageSize; // 页数
		sxssfWorkBookOperation.setPageNo(pageNo);
		if (log.isDebugEnabled()) {
			log.debug(
					"countDownLatch(int, SXSSFWorkBookUtil, ISxssfWorkBookList) - CountDownLatchDemo1:"

							+ new Date().toString());
		}

		CountDownLatch doneCdl = new CountDownLatch(pageNo);// 连接的总数为 pageNo 闸门
		sxssfWorkBookOperation.setSheetNum(pageNo);
		// 设置特定的线程池，大小为pageNo
		ExecutorService exe = Executors.newFixedThreadPool(pageNo, new NamedThreadFactory());
		for (int i = 1; i <= pageNo; i++) {
			RowSelect rowSelect = new RowSelect(i, pageSize, totalRows);

			ThreadTemplete threadTemplete = new ThreadTemplete(
					sxssfWorkBookOperation, rowSelect, sxssfWorkBookList,
					sxssfWorkBookOperation.getPageSize());
			Future future = exe.submit(threadTemplete);
			doneCdl.countDown();
		}
		long start = 0;
		if (log.isDebugEnabled()) {
			// 记录所有连接线程的开始时间
			start = System.currentTimeMillis();
		}

		// 所有线程虽然都已建立，并 start。但只有等闸门打开才都开始运行。
		try {
			doneCdl.await();// 主线程等待所有连接结束
			// 连接达到峰值后，执行一些测试逻辑代码
			exe.shutdown();
			boolean loop = true;
			do { // 等待所有任务完成
				loop = !exe.awaitTermination(30, TimeUnit.SECONDS);
			} while (loop);
			isClose = true;
		} catch (InterruptedException e) {
			log.error(
					"countDownLatch(int, SXSSFWorkBookUtil, ISxssfWorkBookList)",
					e);
			exe.shutdownNow();
		}

		if (log.isDebugEnabled()) {
			// 记录所有连接线程的结束时间
			long end = System.currentTimeMillis();
			log.debug(
					"countDownLatch(int, SXSSFWorkBookUtil, ISxssfWorkBookList) - The task takes time(ms): "
							+ ((end - start) / 1000));
			log.debug(
					"countDownLatch(int, SXSSFWorkBookUtil, ISxssfWorkBookList) - CountDownLatchDemo1:"
							+ new Date().toString());
		}

	}

	public boolean isClose() {
		return isClose;
	}
}
