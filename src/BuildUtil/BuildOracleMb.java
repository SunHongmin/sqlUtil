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

public class BuildOracleMb
{
    private DBConnection dbConn;

    public BuildOracleMb(){
        dbConn = new DBConnection();
    }
    
    public BuildOracleMb(String driver,String url,String user,String pwd){
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
        int cols = rsmd.getColumnCount();
        for (int i = 1; i <= cols; i++)
        {
            rs = dbConn.executeQuery("select * from " + table);
            rsmd = rs.getMetaData();
            String name = rsmd.getColumnName(i);//
            String type = rsmd.getColumnTypeName(i);// 
            int scalenum=rsmd.getScale(i);
            type = getType(type, table, name,scalenum);
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
        String pk = dbConn.getPK(table.toUpperCase()).toLowerCase();
        //select语句字符
        StringBuffer select = new StringBuffer();
        //select字段字符集
        StringBuffer selectcol = new StringBuffer();
        //update语句字符
        StringBuffer update = new StringBuffer();
        //update大写语句字符
        StringBuffer updateUpperCase = new StringBuffer();
        //delete大写语句字符
        StringBuffer delete = new StringBuffer();
        //insert语句字符
        String insert = new String();
        String insertUpperCase = new String();
      //insert语句字段
        StringBuffer insert1 = new StringBuffer();
        //insert语句字段value
        StringBuffer insert2 = new StringBuffer();
        StringBuffer insert2UpperCase = new StringBuffer();
        //Mybatis返回集合
        StringBuffer resultMap = new StringBuffer();
        select.append("<select id=\"\" resultType=\"\">\n" + "select ");
        delete.append("<delete id=\"\" >\n" + "delete from " + table + " where " + pk
                + "=#{" + pk+"}\n</delete>");
        update.append("<update id=\"update\" parameterType=\"\" >\n" + "update " + table + " set ");
        updateUpperCase.append("<update id=\"update\" parameterType=\"\" >\n" + "update " + table + " set ");
        resultMap.append("<resultMap type=\"\" id=\"\">\n");
        ResultSet rs = dbConn.executeQuery("select * from " + table);
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        for (int i = 1; i <= cols; i++)
        {
            String name = rsmd.getColumnName(i).toLowerCase();// 
            String type = rsmd.getColumnTypeName(i);
            int scalenum=rsmd.getScale(i);
            if (i != cols)
            {
                insert1.append(name + ",");
                insert2.append("#{" + name + ",jdbcType="+getType(type,scalenum)+"},");
                insert2UpperCase.append("#{" + name.toUpperCase() + ",jdbcType="+getType(type,scalenum)+"},");
                if (!pk.equals(name))
                {
                    update.append(name + "=#{" + name + "},");
                    updateUpperCase.append(name + "=#{" + name.toUpperCase() + "},");
                }
                selectcol.append(name + ",");
                resultMap.append("<result property=\""+name+"\" column=\""+name.toUpperCase()+"\"  />\n");
            }
            else
            {
                insert1.append(name + ") values(");
                insert2.append("#{" + name + ",jdbcType="+getType(type,scalenum)+"})");
                insert2UpperCase.append("#{" + name.toUpperCase() + ",jdbcType="+getType(type,scalenum)+"})");
                selectcol.append(name);
                insert = "<insert id=\"save\" parameterType=\"\" >\n" + "insert into " + table + "("
                        + insert1.toString() + insert2.toString()+"\n</insert>";
                insertUpperCase= "<insert id=\"save\" parameterType=\"\" >\n" + "insert into " + table + "("
                        + insert1.toString() + insert2UpperCase.toString()+"\n</insert>";
                select.append(selectcol+" from " + table + " where " + pk
                        + "=#{" + pk+"}\n</select>");
                resultMap.append("</resultMap>\n");
                if (!pk.equals(name))
                {
                    update.append(name + "=#{" + name+"}");
                    updateUpperCase.append(name + "=#{" + name.toUpperCase()+"}");
                }
                else
                {
                    update.deleteCharAt(update.length() - 1);
                }
            }
        }
        update.append(" where " + pk + "=#{" + pk+"}\n</update>");
        updateUpperCase.append(" where " + pk + "=#{" + pk.toUpperCase()+"}\n</update>");
        
        
        
        
        
        
        return resultMap.toString()+"\n" + insert + "\n"+ insertUpperCase + "\n" + update.toString() + "\n" + updateUpperCase.toString() + "\n"
                + delete.toString() + "\n" + select.toString();
    }

    /**
     * 获取实体类-》数据库对象属性的类型
     * 
     * @author dyl
     * @date 2016-10-14 9:33:17
     * @param type
     * @param table
     * @param name
     * @return
     */
    private String getType(String type, String table, String name,int scalenum)
    {
        if ("VARCHAR2".equalsIgnoreCase(type) || "CHAR".equalsIgnoreCase(type))
        {
            type = "String";
        }
        else if ("NUMBER".equalsIgnoreCase(type))
        {
            if(scalenum==0){
            	 type = "Integer";
        	}else{
        		 type = "BigDecimal";
        	}
        }
        else if ("DATE".equalsIgnoreCase(type))
        {
            type = "Date";
        }
        else if ("LONG".equalsIgnoreCase(type))
        {
            type = "Long";
        }else if ("CLOB".equalsIgnoreCase(type))
        {
            type = "String";
        }
        else
        {
            System.out.println(table + "字段" + name + "不识别类型" + type);
        }
        return type;
    }
    /**
     * 获取sql-》数据库对象属性的类型
     * @param type
     * @return
     */
    private String getType(String type,int scalenum)
    {
        if ("VARCHAR2".equalsIgnoreCase(type) || "CHAR".equalsIgnoreCase(type))
        {
            type = "VARCHAR";
        }
        else if ("NUMBER".equalsIgnoreCase(type))
        {
        	if(scalenum==0){
        		type = "INTEGER";
        	}else{
        		type = "NUMERIC";
        	}
        }
        else if ("DATE".equalsIgnoreCase(type))
        {
            type = "DATE";
        }
        else if ("LONG".equalsIgnoreCase(type))
        {
            type = "Long";
        }else if ("CLOB".equalsIgnoreCase(type))
        {
            type = "VARCHAR";
        }
        else
        {
            System.out.println("不识别类型" + type);
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
    
    public void close(){
        dbConn.close();
    }
    
}
