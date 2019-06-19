/**生成日期：2019-06-19 16:52:47**/
package com.yz.webmanage.model;
import java.io.Serializable;
import java.util.Date;

public class ArchiveTransfer implements Serializable {

	private String transfer_no;//
	private Date opertime;//添加时间
	private String operid;//操作人id
	private String operprivilege;//操作人权限
	private String operdepid;//操作部门Id
	private String project_name;//项目名称
	private String unit;//份数
	private String piece;//张数
	private String dep_transfer;//移交部室项目负责人
	private String dep_receive;//接收部室
	private String name_transfer;//移交人
	private String name_receive;//接收人
	private String date_transfer;//移交日期
	private String date_receive;//接收日期
	private String is_confirm;//是否确认 1是 2否
	private Date confirm_time;//确认时间
	private String confirm_userid;//确认操作人id
	private String transfer_type;//类型 1文书档案 2工程档案
}