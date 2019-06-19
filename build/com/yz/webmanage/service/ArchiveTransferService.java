/**生成日期：2019-06-19 16:52:47**/
package com.yz.webmanage.service;

import com.yz.webmanage.model.ArchiveTransfer;

public interface ArchiveTransferService {

	//保存
	boolean saveArchiveTransfer(ArchiveTransfer archiveTransfer);
	//修改
	boolean updateArchiveTransfer(ArchiveTransfer archiveTransfer);
	//delete by primary key
	boolean delArchiveTransfer(String transfer_no);
	//find by primary key
	ArchiveTransfer loadArchiveTransfer(String transfer_no);

}