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

public class Build
{
    private DBConnection dbConn;

    public Build(){
        dbConn = new DBConnection();
    }
    
    public Build(String driver,String url,String user,String pwd){
        dbConn = new DBConnection(driver,url,user,pwd);
    }
    
    public String getEntity(String table) throws Exception
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
                    .executeQuery("select t.comments from user_col_comments t where t.table_name='"
                            + table.toUpperCase()
                            + "' and t.column_name='"
                            + name.toUpperCase() + "'");
            if (rs2 != null)
            {
                while (rs2.next())
                {
                    comments = rs.getString(1);
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

    public String getSql(String table) throws Exception
    {
        table = table.toLowerCase();
        String pk = dbConn.getPK(table).toLowerCase();
        StringBuffer select = new StringBuffer();
        StringBuffer update = new StringBuffer();
        StringBuffer delete = new StringBuffer();
        String insert = new String();
        StringBuffer insert1 = new StringBuffer();
        StringBuffer insert2 = new StringBuffer();
        select.append(table + "_select=select * from " + table + " where " + pk
                + "=:" + pk);
        delete.append(table + "_delete=delete from " + table + " where " + pk
                + "=:" + pk);
        update.append(table + "_update=update " + table + " set ");
        ResultSet rs = dbConn.executeQuery("select * from " + table);
        ResultSetMetaData rsmd = rs.getMetaData();
        // System.out.println("table="+table+"\n");
        int cols = rsmd.getColumnCount();
        for (int i = 1; i <= cols; i++)
        {
            //String name = table + '.' + rsmd.getColumnName(i).toLowerCase();// �ֶ��� fan
            String name = rsmd.getColumnName(i).toLowerCase();// �ֶ���
            if (i != cols)
            {
                insert1.append(name + ",");
                insert2.append(":" + name + ",");
                if (!pk.equals(name))
                {
                    update.append(name + "=:" + name + ",");
                }
            }
            else
            {
                insert1.append(name + ") values(");
                insert2.append(":" + name + ")");
                insert = table + "_insert=insert into " + table + "("
                        + insert1.toString() + insert2.toString();
                if (!pk.equals(name))
                {
                    update.append(name + "=:" + name);
                }
                else
                {
                    update.deleteCharAt(update.length() - 1);
                }
            }
        }
        update.append(" where " + pk + "=:" + pk);
        return "\n" + insert + "\n" + update.toString() + "\n"
                + delete.toString() + "\n" + select.toString();
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
        if ("VARCHAR2".equalsIgnoreCase(type) || "CHAR".equalsIgnoreCase(type))
        {
            type = "String";
        }
        else if ("NUMBER".equalsIgnoreCase(type))
        {
            type = "BigDecimal";
        }
        else if ("DATE".equalsIgnoreCase(type))
        {
            type = "Date";
        }
        else if ("LONG".equalsIgnoreCase(type))
        {
            type = "Long";
        }
        else
        {
            System.out.println(table + "�����δ�ܴ�����ֶ�" + name + "����Ϊ" + type);
        }
        return type;
    }
    private String getType(String type)
    {
        if ("VARCHAR2".equalsIgnoreCase(type) || "CHAR".equalsIgnoreCase(type))
        {
            type = "java.lang.String";
        }
        else if ("NUMBER".equalsIgnoreCase(type))
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
        }
        else
        {
            System.out.println("�����δ�ܴ�����ֶ�"+ type);
        }
        return type;
    }
    private Map getEntityList(String sql) throws Exception
    {
        List list = new ArrayList();
        List list2 = new ArrayList();
        Map map = new HashMap();
        ResultSet rs = dbConn.executeQuery(sql);
        // System.out.println(sql);
        if (rs != null)
        {
            while (rs.next())
            {
                String table = rs.getString(1);
                table = table.substring(0, 1).toUpperCase()
                        + table.substring(1, table.length()).toLowerCase();
                // String entity = getEntity(table);
                // list.add(entity);
                list2.add(table);
            }
        }
        for (int i = 0; i < list2.size(); i++)
        {
            String entity = getEntity((String) list2.get(i));
            list.add(entity);
        }
        map.put("entity", list);
        map.put("table", list2);
        return map;
    }

    public void createEntity(String sql, String file)
    {
        new File(file).mkdirs();
        try
        {
            Map map = getEntityList(sql);
            List list = (List) map.get("entity");
            List list2 = (List) map.get("table");
            for (int i = 0; i < list.size(); i++)
            {
                String entity = (String) list.get(i);
                String table = (String) list2.get(i);
                writeFile(entity, new File(file + table + ".java"));
                // writeFile(getSql(table),new File(file+table+".sql"));
                System.out.println(getSql(table));
            }
            System.out.println("\n\n������ϣ�δ�����κ��쳣,��׼��5��Ǯ��");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            dbConn.close();
        }
    }

    private void writeFile(String s, File file) throws IOException
    {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
                file)));
        out.println(s);
        out.close();

    }
    public String gethbdxml(String table){
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
			rs = dbConn.executeQuery("select t.column_name,t.data_type from user_tab_columns t where t.table_name='"
                    + table.toUpperCase()+ "'");
			  while (rs.next())
              {
      	  String column_name = rs.getString(1).toLowerCase();
      	  String type=getType( rs.getString(2));
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
    
    public void close(){
        dbConn.close();
    }
    
    public static void main(String[] s)
    {
        Build t = new Build();
        // String s2 = new String();
        // s2 = t.getEntity("ZD01");
        // System.out.println(s2);
        String sql = "select TABLE_NAME from TAB_EXPORT t where t.TYPE='04'";
        String file = "D:/1/";
        // t.createEntity(sql, file);
        System.out.println(new Date().toString());
    }
}
