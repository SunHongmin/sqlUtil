package BuildUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlBuild
{
    private DBConnection dbConn;

    public MysqlBuild(){
        dbConn = new DBConnection();
    }
    
    public MysqlBuild(String driver,String url,String user,String pwd){
        dbConn = new DBConnection(driver,url,user,pwd);
    }
    
    public String getEntity(String datesource,String table) throws Exception
    {
        StringBuffer s = new StringBuffer();
        String comments = "";
        s.append("/**" + new Date() + "**/\n");
        s
                .append("\nimport java.math.BigDecimal;\nimport java.util.Date;\n\nimport com.tzsw.plat.util.entity.EntitySupport;\n\n");
        s.append("public class " + table.substring(0, 1).toUpperCase()
                 + table.substring(1, table.length()).toLowerCase() + " extends EntitySupport {\n");
        ResultSet rs2;
        ResultSet rs = dbConn.executeQuery("select * from " + table);
        ResultSetMetaData rsmd = rs.getMetaData();
        // System.out.println("table="+table+"\n");
        int cols = rsmd.getColumnCount();
        for (int i = 1; i <= cols; i++)
        {
            rs = dbConn.executeQuery("select * from " + table);
            rsmd = rs.getMetaData();
            String name = rsmd.getColumnName(i);// �ֶ���
            String type = rsmd.getColumnTypeName(i);// �ֶ�����
            type = getType(type, table, name);
            rs2 = dbConn
                    .executeQuery("select table_name,column_name,column_comment FROM Information_schema.columns  where table_schema='"+datesource+"' and table_name='"
                            + table
                            + "' and column_name='"
                            + name + "'");
            if (rs2 != null)
            {
                while (rs2.next())
                {
                    comments = rs2.getString(3);
                }
            }
            String s1 = "private " + type + " " + name.toLowerCase() + ";//"
                    + comments + "\n";
            // System.out.println(s1);
            s.append(s1);
        }
        s.append("}");
        return s.toString();
    }

    public String gethbdxml(String datesource,String table){
    	StringBuffer sb = new StringBuffer();
    	sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n\"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd\">\n");
    	sb.append("<hibernate-mapping>\n");
    	sb.append("\t<class name=\"\" table=\""+table+"\"  dynamic-insert=\"true\" dynamic-update=\"true\" select-before-update=\"true\">\n");
    	sb.append("\t  <id name=\"id\" type=\"java.lang.Integer\">\n");
    	sb.append("\t    <column name=\"id\" />\n");
		sb.append("\t    <generator class=\"identity\" />\n");
		sb.append("\t  </id> \n");
    	
    	
    	ResultSet rs;
		try {
			rs = dbConn.executeQuery("select table_name,column_name,column_comment,data_type FROM Information_schema.columns  where table_schema='"+datesource+"' and table_name='"
			        + table+"'");
			  while (rs.next())
              {
      	  String column_name = rs.getString(2);
      	  String type=getType( rs.getString(4));
                  sb.append("\t  <property name=\""+column_name+"\" column=\""+column_name+"\" type=\""+type+"\"/>\n");
              }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	sb.append("\t</class>\n");
    	sb.append("</hibernate-mapping>\n");
    	 return sb.toString();
    }
    
    
    
    
    
    /**
     * ���ݿ����ֶ�������ʵ��BEAN����ֱ�ӵ�ת�����ɸ�����Ҫ�����޸�
     * 
     * @author cb
     * @date 2006-11-29 9:33:17
     * @param type
     * @param table
     * @param name
     * @return
     */
    private String getType(String type, String table, String name)
    {
        if ("VARCHAR".equalsIgnoreCase(type) || "CHAR".equalsIgnoreCase(type))
        {
            type = "String";
        }
        else if ("DECIMAL".equalsIgnoreCase(type)||"DECIMAL UNSIGNED".equalsIgnoreCase(type))
        {
            type = "BigDecimal";
        }
        else if ("DATE".equalsIgnoreCase(type)||"DATETIME".equalsIgnoreCase(type))
        {
            type = "Date";
        }
        else if ("LONG".equalsIgnoreCase(type))  
        {
            type = "Long";
        }else if ("MEDIUMINT UNSIGNED".equalsIgnoreCase(type)||"INT".equalsIgnoreCase(type)||"INT UNSIGNED".equalsIgnoreCase(type)||"SMALLINT UNSIGNED".equalsIgnoreCase(type)||"TINYINT UNSIGNED".equalsIgnoreCase(type))
        {
            type = "Integer";
        }
        else
        {
            System.out.println(table + "�����δ�ܴ�����ֶ�" + name + "����Ϊ" + type);
        }
        return type;
    }
    private String getType(String type)
    {
        if ("VARCHAR".equalsIgnoreCase(type) || "CHAR".equalsIgnoreCase(type))
        {
            type = "java.lang.String";
        }
        else if ("DECIMAL".equalsIgnoreCase(type)||"DECIMAL UNSIGNED".equalsIgnoreCase(type))
        {
            type = "java.math.BigDecimal";
        }
        else if ("DATE".equalsIgnoreCase(type)||"DATETIME".equalsIgnoreCase(type))
        {
            type = "java.util.Date";
        }
        else if ("LONG".equalsIgnoreCase(type))  
        {
            type = "java.lang.Long";
        }else if ("MEDIUMINT".equalsIgnoreCase(type)||"INT".equalsIgnoreCase(type)||"INT".equalsIgnoreCase(type)||"SMALLINT".equalsIgnoreCase(type)||"TINYINT".equalsIgnoreCase(type))
        {
            type = "java.lang.Integer";
        }
        else
        {
            System.out.println("�����δ�ܴ�����ֶ�"+ type);
        }
        return type;
    }

    private void writeFile(String s, File file) throws IOException
    {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
                file)));
        out.println(s);
        out.close();

    }

    public void close(){
        dbConn.close();
    }
    
    public static void main(String[] s)
    {
        MysqlBuild t = new MysqlBuild();
        // String s2 = new String();
        // s2 = t.getEntity("ZD01");
        // System.out.println(s2);
        String sql = "select TABLE_NAME from TAB_EXPORT t where t.TYPE='04'";
        String file = "D:/1/";
        // t.createEntity(sql, file);
        System.out.println(new Date().toString());
    }
}
