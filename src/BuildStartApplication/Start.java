package BuildStartApplication;

import BuildUtil.StartBuild;

/**
 * @author shm
 * @createdate 2019-02-19 12:00:00
 * 
 * 自动生成工具启动入口，启动步骤：
 * 1.首先在application.properties中配置数据库连接的相应属性及生成包名
 * 2.在本类主方法中配置表名
 * 3.启动项目，在控制台输入pojo类名，回车
 * 4.等待完成提示，然后刷新项目build文件夹
 * 
 * 待更新：
 * 支持ORACLE
 * 支持Hibernate
 */
public class Start {

	public static void main(String[] args) throws Exception{
		String tableName = "EAMS_ARCHIVE_TRANSFER";
		StartBuild.dataSourceMysqlMybatis(tableName);
	}
}
