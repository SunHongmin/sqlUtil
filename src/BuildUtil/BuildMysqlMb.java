package BuildUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author sunhongmin
 * springmvc+mysql模板代码
 * 持久层，业务层，控制层相关文件的自动生成
 * 生成重用率高的模板代码 数据增删改查的相应方法及sql语句
 * 更新时间：20190619 
 */
public class BuildMysqlMb{
	
    private DBConnection dbConn;//数据库连接
    
    private String pk;//主键
    private String pkType;//主键类型
    
    private String createTime;//创建时间  yyyy-MM-dd HH:mm:ss
    
    private String packagename;//文件包名   eg：com.yz.webmanage
    private String javaFileHeadInfo;//java文件头公用信息
    private String datasource;
    private String table;
    
    private String classname;//全小写形式，一般只有xml文件中用到
    private String className;//驼峰命名形式
    private String ClassName;//全大写类名形式
    
    public BuildMysqlMb(){
        dbConn = new DBConnection();
    }
    
    public BuildMysqlMb(String driver,String url,String user,String pwd){
        dbConn = new DBConnection(driver,url,user,pwd);
    }
    
    /**
     * 初始化公用参数
     * @param datasource
     * @param table
     * @param cname
     * @param packagename
     * @throws SQLException
     */
    private void initInnerInfo(String datasource,String table,String cname,String packagename) throws SQLException{
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	this.createTime = sdf.format(new Date());
    	this.datasource = datasource;
        this.javaFileHeadInfo = "/**生成日期：" + createTime + "**/\r\n";
        this.packagename = packagename.replace("\\\\",".");
        this.ClassName = cname.substring(0, 1).toUpperCase()+cname.substring(1);
        this.className = cname.substring(0,1).toLowerCase()+cname.substring(1);
        this.classname = cname.toLowerCase();
        this.pk = dbConn.getPK(table).toLowerCase();
        this.table = table;
    }
    
    /**
     * mapper接口文件内容
     * @return
     * @throws SQLException
     */
    public String getMapperInter() throws SQLException{
    	StringBuilder mapperStr = new StringBuilder();
    	mapperStr.append(this.javaFileHeadInfo);
    	mapperStr.append("package "+packagename+".mapper;\r\n\r\n");
    	mapperStr.append("import org.apache.ibatis.annotations.Param;\r\n");
    	mapperStr.append("import "+packagename+".model."+this.ClassName+";\r\n\r\n");
    	mapperStr.append("public interface "+this.ClassName+"Mapper {\r\n\r\n");
    	mapperStr.append("\t//保存\r\n");
    	mapperStr.append("\tint save"+this.ClassName+"("+this.ClassName+" "+this.className+");\r\n");
    	mapperStr.append("\t//修改\r\n");
    	mapperStr.append("\tint update"+this.ClassName+"("+this.ClassName+" "+this.className+");\r\n");
    	if(pkType==null || pkType.equals("")){
    		pkType = "String";
    	}
    	mapperStr.append("\t//delete by primary key\r\n");
    	mapperStr.append("\tint del"+this.ClassName+"(@Param(value=\""+this.pk+"\")"+pkType+" "+this.pk+");\r\n");
    	mapperStr.append("\t//find by primary key\r\n");
    	mapperStr.append("\t"+this.ClassName+" load"+this.ClassName+"(@Param(value=\""+this.pk+"\")"+pkType+" "+this.pk+");\r\n\r\n}");
    	return mapperStr.toString();
    }
    
    /**
     * 实体类文件内容
     * @param datesource
     * @param table
     * @param cname
     * @param packagename
     * @return
     * @throws Exception
     */
    public String getEntity(String datesource,String table,String cname,String packagename) throws Exception{
    	this.initInnerInfo(datasource,table,cname,packagename);
    	StringBuilder s = new StringBuilder();
        String comments = "";
        s.append(this.javaFileHeadInfo);
        s.append("package "+this.packagename+".model;\r\n");
        s.append("import java.io.Serializable;\r\n");
        StringBuilder s2 = new StringBuilder();
        s2.append("public class " + this.ClassName + " implements Serializable {\r\n\r\n");
        ResultSet rs2;
        ResultSet rs = dbConn.executeQuery("select * from " + table);
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        String pk = dbConn.getPK(table).toLowerCase();
        boolean dateImport = false;
        boolean BigDeciImport = false;
        for (int i = 1; i <= cols; i++){
            rs = dbConn.executeQuery("select * from " + table);
            rsmd = rs.getMetaData();
            String name = rsmd.getColumnName(i);// 属性名
            String type = rsmd.getColumnTypeName(i);// 属性类型
            type = getType(type, table, name);
            if(type.equals("BigDecimal") && !BigDeciImport){
            	s.append("import java.math.BigDecimal;\r\n");
            	BigDeciImport = true;
            }else if(type.equals("Date") && !dateImport){
            	s.append("import java.util.Date;\r\n\r\n");
            	dateImport = true;
            }
            rs2 = dbConn.executeQuery("select table_name,column_name,column_comment FROM Information_schema.columns  where table_schema='"+datesource+"' and table_name='"
                            + table
                            + "' and column_name='"
                            + name + "'");
            if (rs2 != null){
                while (rs2.next()){
                    comments = rs2.getString(3);
                }
            }
            String s1 = "\tprivate " + type + " " + name.toLowerCase() + ";//"
                    + comments + "\r\n";
            if(name.equalsIgnoreCase(pk)){
            	pkType = type;
            }
            s2.append(s1);
        }
        s2.append("}");
        return s.toString()+s2.toString();
    }
    
    /**
     * xml sql语句
     * @param datesource
     * @param table
     * @param cname
     * @param packagename
     * @return
     * @throws Exception
     */
    public String getSql() throws Exception{
        //table = table.toLowerCase();
        //select语句字符
        StringBuffer select = new StringBuffer();
        //select字段字符集
        StringBuffer selectcol = new StringBuffer();
        //update语句字符
        StringBuffer update = new StringBuffer();
        //delete大写语句字符
        StringBuffer delete = new StringBuffer();
        //insert语句字符
        String insert = new String();
        //insert语句字段
        StringBuffer insert1 = new StringBuffer();
        //insert语句字段value
        StringBuffer insert2 = new StringBuffer();
        //insert语句字符包括主键
        String insertkey = new String();
        //insert语句字段包括主键
        StringBuffer insertkey1 = new StringBuffer();
        //insert语句字段包括主键value
        StringBuffer insertkey2 = new StringBuffer();
        //Mybatis返回集合
        StringBuffer resultMap = new StringBuffer();
        select.append("<select id=\"load"+this.ClassName+"\" resultMap=\""+this.classname+"\">\r\n" + "select ");
        delete.append("<delete id=\"del"+this.ClassName+"\" >\r\n" + "delete from " + this.table + " where " + this.pk
                + "=#{" + this.pk+"}\r\n</delete>");
        update.append("<update id=\"update"+this.ClassName+"\" parameterType=\""+packagename+".model."+this.ClassName+"\" >\r\n" + "update " + this.table + " set ");
        resultMap.append("<resultMap type=\""+packagename+".model."+this.ClassName+"\" id=\""+this.classname+"\">\r\n");
        ResultSet rs = dbConn.executeQuery("select * from " + this.table);
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        for (int i = 1; i <= cols; i++){
            String name = rsmd.getColumnName(i).toLowerCase();//sql字段 
            String type = rsmd.getColumnTypeName(i);//sql字段类型
            //int scalenum=rsmd.getScale(i);
            if (i != cols){
            	if(!name.equals(this.pk)){
            		 insert1.append(name + ",");
                     insert2.append("#{" + name + ",jdbcType="+getType(type)+"},");
            	}
            	insertkey1.append(name + ",");
            	insertkey2.append("#{" + name + ",jdbcType="+getType(type)+"},");
                if (!this.pk.equals(name)){
                    update.append(name + "=#{" + name + "},");
                }
                selectcol.append(name + ",");
                resultMap.append("<result property=\""+name+"\" column=\""+name+"\"  />\r\n");
            }else{
            	if(!name.equals(this.pk)){
            		insert1.append(name + ") values(");
                    insert2.append("#{" + name + ",jdbcType="+getType(type)+"})");
            	}
            	insertkey1.append(name + ") values(");
            	insertkey2.append("#{" + name + ",jdbcType="+getType(type)+"})");
                selectcol.append(name);
                insert = "<insert id=\"save"+this.ClassName+"\" parameterType=\""+packagename+".model."+this.ClassName+"\" >\r\n" + "insert into " + table + "("
                        + insert1.toString() + insert2.toString()+"\r\n</insert>";
                insertkey = "<!--<insert id=\"save"+this.ClassName+"\" parameterType=\""+packagename+".model."+this.ClassName+"\" >\r\n" + "insert into " + table + "("
                        + insertkey1.toString() + insertkey2.toString()+"\r\n</insert>-->";
                select.append(selectcol+" from " + table + " where " + pk
                        + "=#{" + pk +"}\r\n</select>");
                resultMap.append("<result property=\""+name+"\" column=\""+name+"\"  />\r\n");
                resultMap.append("</resultMap>\r\n");
                if(!this.pk.equals(name)){
                    update.append(name + "=#{" + name+"}");
                }else{
                    update.deleteCharAt(update.length() - 1);
                }
            }
        }
        update.append(" where " + this.pk + "=#{" + this.pk+"}\r\n</update>");
        String MapperDoc1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<!DOCTYPE mapper\r\nPUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\r\n\"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\r\n\r\n<mapper namespace=\""+packagename+".mapper."+this.ClassName+"Mapper\">\r\n\r\n";
        String MapperDoc2 = "\r\n\r\n</mapper>";
        String insertAnno = "\r\n<!-- insert (No primary key parameters required) -->\r\n";
        String insertKeyAnno = "\r\n\r\n<!-- insert (Need primary key parameter) -->\r\n";
        String updateAnno = "\r\n\r\n<!-- update  -->\r\n";
        String delAnno = "\r\n\r\n<!-- delete by primary key-->\r\n";
        String loadAnno = "\r\n\r\n<!-- load by primary key -->\r\n";
        return MapperDoc1 + resultMap.toString() + insertAnno+insert +  insertKeyAnno+insertkey + updateAnno+update.toString()
        	   + delAnno+ delete.toString() + loadAnno+select.toString() + MapperDoc2;
    }
    
    /**
     * service 接口文件内容
     * @param table
     * @param cname
     * @param packagename
     * @return
     * @throws SQLException
     */
    public String getServiceStatement() throws SQLException{
    	StringBuilder fileBody = new StringBuilder();
    	fileBody.append(this.javaFileHeadInfo);
    	fileBody.append("package "+packagename+".service;\r\n\r\n");
    	fileBody.append("import "+packagename+".model."+this.ClassName+";\r\n\r\n");
    	fileBody.append("public interface "+this.ClassName+"Service {\r\n\r\n");
    	fileBody.append("\t//保存\r\n");
    	fileBody.append("\tboolean save"+this.ClassName+"("+this.ClassName+" "+this.className+");\r\n");
    	fileBody.append("\t//修改\r\n");
    	fileBody.append("\tboolean update"+this.ClassName+"("+this.ClassName+" "+this.className+");\r\n");
    	if(pkType==null || pkType.equals("")){
    		pkType = "String";
    	}
    	fileBody.append("\t//delete by primary key\r\n");
    	fileBody.append("\tboolean del"+this.ClassName+"("+pkType+" "+pk+");\r\n");
    	fileBody.append("\t//find by primary key\r\n");
    	fileBody.append("\t"+this.ClassName+" load"+this.ClassName+"("+pkType+" "+pk+");\r\n\r\n}");
    	return fileBody.toString();
    }
    
    /**
     * service实现类内容
     * @return
     * @throws SQLException
     */
    public String getServiceImplStatement() throws SQLException{
    	StringBuilder fileBody = new StringBuilder();
    	fileBody.append(this.javaFileHeadInfo);
    	fileBody.append("package "+this.packagename+".service;\r\n\r\n");
    	fileBody.append("import javax.annotation.Resource;\r\n");
    	fileBody.append("import org.springframework.stereotype.Service;\r\n");
    	fileBody.append("import "+this.packagename+".mapper."+this.ClassName+"Mapper;\r\n");
    	fileBody.append("import "+this.packagename+".model."+this.ClassName+";\r\n\r\n");
    	
    	fileBody.append("@Service(\""+this.ClassName+"Service\")\r\n");
    	fileBody.append("public class "+this.ClassName+"ServiceImpl implements "+this.ClassName+"Service{\r\n\r\n");
    	
    	fileBody.append("\t@Resource\r\n");
    	fileBody.append("\tprivate "+this.ClassName+"Mapper "+this.className+"Mapper;\r\n\r\n");
    	
    	fileBody.append("\t@Override\r\n");
    	fileBody.append("\tpublic boolean save"+this.ClassName+"("+this.ClassName+" "+this.className+"){\r\n");
    	fileBody.append("\t\tif("+this.className+"Mapper.save"+this.ClassName+"("+this.className+")>0){\r\n");
    	fileBody.append("\t\t\treturn true;\r\n");
    	fileBody.append("\t\t}\r\n");
    	fileBody.append("\t\treturn false;\r\n");
    	fileBody.append("\t}\r\n\r\n");
    	
    	fileBody.append("\t@Override\r\n");
    	fileBody.append("\tpublic boolean update"+this.ClassName+"("+this.ClassName+" "+this.className+"){\r\n");
    	fileBody.append("\t\tif("+this.className+"Mapper.update"+this.ClassName+"("+this.className+")>0){\r\n");
    	fileBody.append("\t\t\treturn true;\r\n");
    	fileBody.append("\t\t}\r\n");
    	fileBody.append("\t\treturn false;\r\n");
    	fileBody.append("\t}\r\n\r\n");
    	
    	fileBody.append("\t@Override\r\n");
    	fileBody.append("\tpublic boolean del"+this.ClassName+"("+this.pkType+" "+this.pk+"){\r\n");
    	fileBody.append("\t\tif("+this.className+"Mapper.del"+this.ClassName+"("+this.pk+")>0){\r\n");
    	fileBody.append("\t\t\treturn true;\r\n");
    	fileBody.append("\t\t}\r\n");
    	fileBody.append("\t\treturn false;\r\n");
    	fileBody.append("\t}\r\n\r\n");
    	
    	fileBody.append("\t@Override\r\n");
    	fileBody.append("\tpublic "+this.ClassName+" load"+this.ClassName+"("+this.pkType+" "+this.pk+"){\r\n");
    	fileBody.append("\t\treturn "+this.className+"Mapper.load"+this.ClassName+"("+this.pk+");\r\n");
    	fileBody.append("\t}\r\n\r\n");
    	
    	
    	fileBody.append("}");
    	
    	
    	return fileBody.toString();
    }
    
    /**
     * Controller文件内容
     * @return
     */
    public String getControllerStatement(){
    	StringBuilder fileBody = new StringBuilder();
    	fileBody.append(this.javaFileHeadInfo);
    	fileBody.append("package "+this.packagename+".controller;\r\n\r\n");
    	fileBody.append("import org.springframework.stereotype.Controller;\r\n");
    	fileBody.append("import org.springframework.web.bind.annotation.RequestMapping;\r\n");
    	fileBody.append("import javax.annotation.Resource;\r\n");
    	fileBody.append("import "+this.packagename+".service."+this.ClassName+"Service;\r\n\r\n");
    	fileBody.append("@Controller\r\n");
    	fileBody.append("@RequestMapping(\"/admin/"+this.classname+"\")\r\n");
    	fileBody.append("public class "+this.ClassName+"Controller {\r\n\r\n");
    	fileBody.append("\t@Resource\r\n");
    	fileBody.append("\tprivate "+this.ClassName+"Service "+this.className+"Serivce;\r\n\r\n");
    	fileBody.append("}");
    	return fileBody.toString();
    }
    
    /**
     * 获取实体类->数据库对象属性的类型
     * @param type
     * @param table
     * @param name
     * @return
     */
    private String getType(String type, String table, String name){
        if ("VARCHAR".equalsIgnoreCase(type) || "CHAR".equalsIgnoreCase(type) || "LONGVARCHAR".equalsIgnoreCase(type)){
            type = "String";
        }else if ("DECIMAL".equalsIgnoreCase(type)||"DECIMAL UNSIGNED".equalsIgnoreCase(type) || "NUMERIC".equalsIgnoreCase(type)){
            type = "BigDecimal";
        }else if ("DATE".equalsIgnoreCase(type)||"DATETIME".equalsIgnoreCase(type)){
            type = "Date";
        }else if ("LONG".equalsIgnoreCase(type)){
            type = "Long";
        }else if ("MEDIUMINT UNSIGNED".equalsIgnoreCase(type)||"INT".equalsIgnoreCase(type)||"INT UNSIGNED".equalsIgnoreCase(type)||"SMALLINT UNSIGNED".equalsIgnoreCase(type)||"TINYINT UNSIGNED".equalsIgnoreCase(type)){
            type = "Integer";
        }else{
            System.out.println(table + "不识别类型 column:" + name + ",jdbcType:" + type);
        }
        return type;
    }
    
    /**
     * 获取sql字段对应java的数据类型
     * @param type
     * @return
     */
    private String getType(String type){
    	if(type.indexOf("UNSIGNED")>=0){
    		type=type.replaceAll(" UNSIGNED", "");
    	}
        if ("VARCHAR".equalsIgnoreCase(type) || "CHAR".equalsIgnoreCase(type))
        {
            type = "VARCHAR";
        }
        else if ("DECIMAL".equalsIgnoreCase(type)||"DECIMAL UNSIGNED".equalsIgnoreCase(type))
        {
            type = "DECIMAL";
        }
        else if ("DATE".equalsIgnoreCase(type))
        {
            type = "DATE";
        } else if ("DATETIME".equalsIgnoreCase(type))
        {
            type = "TIMESTAMP";
        }
        else if ("LONG".equalsIgnoreCase(type))  
        {
            type = "LONG";
        }else if ("MEDIUMINT".equalsIgnoreCase(type)||"INT".equalsIgnoreCase(type)||"SMALLINT".equalsIgnoreCase(type)||"TINYINT".equalsIgnoreCase(type))
        {
            type = "INTEGER";
        }
        else
        {
            System.out.println("不识别类型"+ type);
        }
        return type;
    }

    /**
     * 关闭连接 
     */
    public void close(){
        dbConn.close();
    }
    
    /*
    private void writeFile(String s, File file) throws IOException{
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
                file)));
        out.println(s);
        out.close();
    }*/
}
