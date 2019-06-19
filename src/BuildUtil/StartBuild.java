package BuildUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;


/**
 * @author Sunhongmin
 * 获取配置信息，获取mysql连接，为生成语句准备必要资源
 * 生成文件
 * 更新时间：20190619 
 */
public class StartBuild {
	
	private static String pojoName;//自定义实体类名称
	private static String relativelyPath;//项目根目录
	private String tableName;//数据表名称
	private static Map<String,String> propertymap;//参数集合
	
	/**
	 * 初始化表名称，生成文件路径
	 * @param tableName
	 */
	public StartBuild(String tableName){
		this.tableName = tableName;
		propertymap = getProperties();
	   	relativelyPath=System.getProperty("user.dir");
	   	relativelyPath = relativelyPath+"\\build\\"+propertymap.get("packagename");
		File file2 = new File(relativelyPath);
		file2.mkdirs();
		Scanner sc = new Scanner(System.in);
	   	System.out.println(">>>pojo class name:(table name is "+tableName+")");
	   	pojoName = sc.next();
	   	sc.close();
	}
	
   /**
    * 创建并返回File
	 * @param moudleName 包模块名称
	 * @param fileName 文件名称
	 * @return
	 */
	private static File createFile(String moudleName,String fileName){
	   fileName = fileName.substring(0,1).toUpperCase()+fileName.substring(1);
	   String fn = relativelyPath+File.separator+moudleName+File.separator+fileName;
	   File file = new File(fn);
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		return file;
   }
   
    /**
     * （main）调用生成方法生成相应文件
     * @param tableName
     * @throws Exception
     */
    public static void dataSourceMysqlMybatis(String tableName) throws Exception{
	   StartBuild stab = new StartBuild(tableName);
	   stab.fileToEmpty();
	   
	   BuildMysqlMb build = new BuildMysqlMb(propertymap.get("strDriver"),propertymap.get("strUrl"),propertymap.get("strUser"),propertymap.get("strPwd"));
       String datesource = propertymap.get("strUrl").substring(propertymap.get("strUrl").lastIndexOf("/")+1);

       //生成实体类
       FileWriter pojoFw = new FileWriter(createFile("model",pojoName+".java"));
	   pojoFw.write(build.getEntity(datesource,tableName,pojoName,propertymap.get("packagename")));
	   pojoFw.close();
	   //生成mapper接口类
	   FileWriter mapperFw = new FileWriter(createFile("mapper",pojoName+"Mapper.java"));
	   mapperFw.write(build.getMapperInter());
	   mapperFw.close();
	   //生成mapper.xml文件
	   FileWriter xmlFw = new FileWriter(createFile("mapper",pojoName+"Mapper.xml"));
	   xmlFw.write(build.getSql());
	   xmlFw.close();
	   //生成service接口
	   FileWriter serviceFw = new FileWriter(createFile("service",pojoName+"Service.java"));
	   serviceFw.write(build.getServiceStatement());
	   serviceFw.close();
	   //生成serviceImpl默认实现类
	   FileWriter serviceImplFw = new FileWriter(createFile("service",pojoName+"ServiceImpl.java"));
	   serviceImplFw.write(build.getServiceImplStatement());
	   serviceImplFw.close();
	   //controller
	   FileWriter controllerFw = new FileWriter(createFile("controller",pojoName+"Controller.java"));
	   controllerFw.write(build.getControllerStatement());
	   controllerFw.close();
	   
	   System.out.println(">>>Created successfully, please refresh the project");
       build.close();
   }
   
	/**
	 * 获取文件信息
	 * @return
	 */
	public static Map<String,String> getProperties(){
	    Properties props = new Properties();
		try {
			props.load(StartBuild.class.getClassLoader().getResourceAsStream("application.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String,String> map = new HashMap<>();
		map.put("strDriver", props.getProperty("jdbc.driver"));
		map.put("strUrl", props.getProperty("jdbc.url"));
		map.put("strUser", props.getProperty("jdbc.username"));
		map.put("strPwd", props.getProperty("jdbc.password"));
		map.put("packagename", props.getProperty("parent.packagename").replace(".", "\\\\"));
		return map;
   }
   
	/**
	 * 调用deleteAllFiles删除build文件夹下所有文件
	 */
    private void fileToEmpty(){
	   String delPath = relativelyPath;
		File file = new File(delPath);
		deleteAllFiles(file);
    }
   
	/**
	* 递归删除根路径root下所有文件及文件夹
	* @param root
	*/
	private void deleteAllFiles(File root) {  
	       File files[] = root.listFiles();  
	       if (files != null)  
	           for (File f : files) {
	               if (f.isDirectory()) { 
	            	   // 判断是否为文件夹  
	                   deleteAllFiles(f);  
	                   f.delete();  
	               } else {  
	                   if (f.exists()) { 
	                	   // 判断是否存在  
	                       deleteAllFiles(f);  
	                       f.delete();  
	                   }  
	               } 
	           }  
	   }  
	
}
