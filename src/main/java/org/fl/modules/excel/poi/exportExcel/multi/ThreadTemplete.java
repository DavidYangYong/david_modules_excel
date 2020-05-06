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
 * # * ThreadTest.java Create on 2013-11-14 上午09:50:06 # * project Test # * Copyright 2013 by . #
 *
 * 文件名：ThreadTest.java 版本信息： 日期：2013-11-14 Copyright 足下 Corporation 2013 版权所有
 */
/**
 * 文件名：ThreadTest.java
 * 版本信息：
 * 日期：2013-11-14
 * Copyright 足下 Corporation 2013
 * 版权所有
 */
package org.fl.modules.excel.poi.exportExcel.multi;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.fl.modules.excel.poi.exportExcel.ISxssfWorkBookList;
import org.fl.modules.utils.RowSelect;

/**
 * @author David.Yang
 * @version 1.0
 *          CreateDate：2013-11-14 上午09:50:06
 *          类说明
 */

/**
 * 项目名称：Test
 * 类名称：ThreadTest
 * 类描述：
 * 创建人：David.Yang
 * 创建时间：2013-11-14 上午09:50:06
 * 修改人：David.Yang
 * 修改时间：2013-11-14 上午09:50:06
 * 修改备注：
 *
 * @version
 */
@Slf4j
class ThreadTemplete implements Runnable {

	private int pageSize;
	private RowSelect rowSelect;
	private ISxssfWorkBookList sxssfWorkBookList;

	private SXSSFWorkBookOperation sxssfWorkBookOperation;

	private CountDownLatch countDownLatch;

	public ThreadTemplete(
			SXSSFWorkBookOperation sxssfWorkBookOperation, RowSelect rowSelect,
			ISxssfWorkBookList sxssfWorkBookList, int pageSize, CountDownLatch countDownLatch) {
		this.sxssfWorkBookOperation = sxssfWorkBookOperation;
		this.rowSelect = rowSelect;
		this.sxssfWorkBookList = sxssfWorkBookList;
		this.pageSize = pageSize;
		this.countDownLatch = countDownLatch;
	}

	public List getList(RowSelect rowSelect, ISxssfWorkBookList sxssfWorkBookList) {
		if (sxssfWorkBookList != null) {
			return sxssfWorkBookList.doExecuteList(rowSelect);
		}
		return null;

	}

	public void run() {
		if (log.isDebugEnabled()) {
			log.debug("run() - " + Thread.currentThread().getName()
					+ " has been working!!!!");
		}
		try {
			List list = getList(rowSelect, sxssfWorkBookList);
			if (list == null || list.isEmpty()) {
				Thread.currentThread().interrupt();
			} else {
				sxssfWorkBookOperation.excute(
						sxssfWorkBookOperation.getSxIsxssfWorkBook(), list, pageSize);

				if (log.isDebugEnabled()) {
					log.debug("run() - " + Thread.currentThread().getName()
							+ " has been working end !!!!");
				}
			}
			countDownLatch.countDown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("run()", e);
			Thread.currentThread().interrupt();
		} finally {
			if (countDownLatch != null) {
				countDownLatch.countDown();
			}
		}
	}
}
