package BuildUtil;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author cb
 * 
 */
public class DBConnection
{
    // for Oracle
    static private String strDriver = "oracle.jdbc.OracleDriver";

    static private String strUrl = "jdbc:oracle:thin:@192.2.2.116:1521:tzsw";

    static private String strUser = "zjdqld_user";

    static private String strPwd = "oracle";

    private Connection conn = null;

    private Statement stmt = null;

    private ResultSet rs = null;
    
    public DBConnection(){
        
    }
    
    public DBConnection(String driver,String url,String user,String pwd){
        strDriver = driver;
        strUrl = url;
        strUser = user;
        strPwd = pwd;
    }

    static
    {
        try
        {
            Class.forName(strDriver);
        }
        catch (ClassNotFoundException ex)
        {
            System.out.println("Error load" + strDriver);
        }
    }

    private Connection getConnection()
    {
        try
        {
            if (conn == null || conn.isClosed())
                conn = DriverManager.getConnection(strUrl, strUser, strPwd);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
        return conn;
    }

    public ResultSet executeQuery(String sql) throws Exception
    {
        try
        {
            rs = getStatement().executeQuery(sql);
        }
        catch (SQLException ex)
        {
            System.err.println("query error:" + ex.getMessage());
            throw new SQLException();
        }
        return rs;
    }

    public void close()
    {
        try
        {
            if (rs != null)
            {
                rs.close();
                rs = null;
            }
            if (stmt != null)
            {
                stmt.close();
                stmt = null;
            }
            if (conn != null)
            {
                conn.close();
                conn = null;
            }
        }
        catch (Exception ex)
        {
            System.err.println("close error:" + ex.getMessage());
        }
    }

    private Statement getStatement()
    {
        try
        {
            if (stmt == null)
                stmt = getConnection().createStatement();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
        return stmt;
    }

    public String getPK(String table) throws SQLException
    {
        String s = "eorr";
        DatabaseMetaData dbMeta = getConnection().getMetaData();
        ResultSet rst = dbMeta.getPrimaryKeys(null,strUser.toUpperCase(), table);
        while (rst.next())
        {
            s = rst.getString(4);
        }
        return s;
    }

    public static void main(String[] s) throws Exception
    {
        DBConnection con = new DBConnection();
        con.getPK("CB80");
        con.close();
    }

}
