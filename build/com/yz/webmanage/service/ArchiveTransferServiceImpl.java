/**生成日期：2019-06-19 16:52:47**/
package com.yz.webmanage.service;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.yz.webmanage.mapper.ArchiveTransferMapper;
import com.yz.webmanage.model.ArchiveTransfer;

@Service("ArchiveTransferService")
public class ArchiveTransferServiceImpl implements ArchiveTransferService{

	@Resource
	private ArchiveTransferMapper archiveTransferMapper;

	@Override
	public boolean saveArchiveTransfer(ArchiveTransfer archiveTransfer){
		if(archiveTransferMapper.saveArchiveTransfer(archiveTransfer)>0){
			return true;
		}
		return false;
	}

	@Override
	public boolean updateArchiveTransfer(ArchiveTransfer archiveTransfer){
		if(archiveTransferMapper.updateArchiveTransfer(archiveTransfer)>0){
			return true;
		}
		return false;
	}

	@Override
	public boolean delArchiveTransfer(String transfer_no){
		if(archiveTransferMapper.delArchiveTransfer(transfer_no)>0){
			return true;
		}
		return false;
	}

	@Override
	public ArchiveTransfer loadArchiveTransfer(String transfer_no){
		return archiveTransferMapper.loadArchiveTransfer(transfer_no);
	}

	}

