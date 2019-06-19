/**生成日期：2019-06-19 16:52:47**/
package com.yz.webmanage.mapper;

import org.apache.ibatis.annotations.Param;
import com.yz.webmanage.model.ArchiveTransfer;

public interface ArchiveTransferMapper {

	//保存
	int saveArchiveTransfer(ArchiveTransfer archiveTransfer);
	//修改
	int updateArchiveTransfer(ArchiveTransfer archiveTransfer);
	//delete by primary key
	int delArchiveTransfer(@Param(value="transfer_no")String transfer_no);
	//find by primary key
	ArchiveTransfer loadArchiveTransfer(@Param(value="transfer_no")String transfer_no);

}