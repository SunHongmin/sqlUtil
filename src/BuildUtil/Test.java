package BuildUtil;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Test{
    /**
     * @author dyl
     * @date 2016-10-14 10:10:23
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
    	//dataSourceMysql();//生成mysql hibernate配置文件
    	//dataSourceOracle();//生成oracle hibernate配置文件
    	//dataSourceOracleMybatis();//生成oracle mybatis配置文件
    	dataSourceMysqlMybatis();//生成mysql mybatis配置文件
    }
   
   /**
    * 生成mysql mybatis配置文件
    * @throws Exception
    */
   private static void dataSourceMysqlMybatis() throws Exception{
	   String strDriver = "com.mysql.jdbc.Driver";
       String strUrl = "jdbc:mysql://192.168.0.60:3306/eams";
       String strUser = "root";
       String strPwd = "123456";
       
       BuildMysqlMb build = new BuildMysqlMb(strDriver,strUrl,strUser,strPwd);
       String tableName = "eams_contract_pay";
       String datesource = "eams";
       Scanner sc = new Scanner(System.in);
   	   System.out.println("pojo class name:(table name is "+tableName+")");
   	   String cname = sc.next();
   	//父文件夹   
   	String relativelyPath=System.getProperty("user.dir");
	File file2 = new File(relativelyPath+"\\com\\yz\\webmanage");
	file2.mkdirs();
	//子文件夹
	String ClassName =  cname.substring(0, 1).toUpperCase()+cname.substring(1);
	String pojofn = file2.getPath()+File.separator+"model"+File.separator+ClassName+".java";
	File pojof = new File(pojofn);
	if(!pojof.getParentFile().exists()){
		pojof.getParentFile().mkdirs();
	}
	
	   FileWriter pojoFw = new FileWriter(pojof);
	   /******pojoFw.write(build.getEntity(datesource,tableName,cname));//*******/
	   pojoFw.close();
       //fw.write("\r\n##########华丽的分割线(pojo类结束，以下是Mapper接口)#########\r\n");
//	   FileWriter mapperFw = new FileWriter(file2.getPath()+File.separator+"mapper"+File.separator+ClassName+"Mapper.java");
//	   mapperFw.write(build.getMapperInter(tableName, cname));
//	   mapperFw.close();
      // fw.write("\r\n##########华丽的分割线(Mapper接口结束，以下是Mapper.xml)#########\r\n");
//	   FileWriter xmlFw = new FileWriter(file2.getPath()+File.separator+"mapper"+File.separator+ClassName+"Mapper.xml");
//	   xmlFw.write(build.getSql(datesource,tableName,cname));
//	   xmlFw.close();
       build.close();
   }
   /**
    * 生成mysql hibernate配置文件
    * @throws Exception
    */
  private static void dataSourceMysql() throws Exception{
	   String strDriver = "com.mysql.jdbc.Driver";
      String strUrl = "jdbc:mysql://192.168.0.60:3306/cm_dy";
      String strUser = "root";
      String strPwd = "123456";
      
      MysqlBuild build = new MysqlBuild(strDriver,strUrl,strUser,strPwd);
      String tableName = "dy";
      String datesource = "cm_dy";
      System.out.println(build.getEntity(datesource,tableName));
      System.out.println("\n##########华丽的分割线#########\n");
      System.out.println(build.gethbdxml(datesource,tableName));
      
      build.close();
  }
   /**
    * 生成oracle hibernate配置文件
    * @throws Exception
    */
   private static void dataSourceOracle() throws Exception{
	   String strDriver = "oracle.jdbc.driver.OracleDriver";
       String strUrl = "jdbc:oracle:thin:@192.168.0.60:1521:orcl";
       String strUser = "axh";
       String strPwd = "oracle";
       
       Build build = new Build(strDriver,strUrl,strUser,strPwd);
       
       String tableName = "SYSLOGINLOG";
       System.out.println(build.getEntity(tableName));
       System.out.println("\n##########华丽的分割线#########\n");
       System.out.println(build.gethbdxml(tableName));
       
       build.close();
   }
   /**
    * 生成oracle mybatis配置文件
    * @throws Exception
    */
   private static void dataSourceOracleMybatis() throws Exception{
	   String strDriver = "oracle.jdbc.driver.OracleDriver";
       String strUrl = "jdbc:oracle:thin:@192.168.0.60:1521:orcl";
       String strUser = "yyxyreport";
       String strPwd = "oracle";
       
       BuildOracleMb build = new BuildOracleMb(strDriver,strUrl,strUser,strPwd);
       
       String tableName = "GXQT0501";
       System.out.println(build.getEntity(tableName));
       System.out.println("\n##########华丽的分割线#########\n");
       System.out.println(build.getSql(tableName));
       
       build.close();
   }
  
}
