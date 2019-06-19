/**生成日期：2019-06-19 16:52:47**/
package com.yz.webmanage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import com.yz.webmanage.service.ArchiveTransferService;

@Controller
@RequestMapping("/admin/archivetransfer")
public class ArchiveTransferController {

	@Resource
	private ArchiveTransferService archiveTransferSerivce;

	}