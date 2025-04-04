package json;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.Statement;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import servlets.JSONServlet;

public class Driver {

	public static void main(String[] args) {
		
		Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(JSONServlet.class.getName(), "/hello");
        server.setHandler(context);
        try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		String dbURL = "jdbc:sqlserver://161.31.4.49;user=cisa;password=Yw7LSLcajASptSQ7;encrypt=false;";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(dbURL);
			if (conn != null) {
			    System.out.println((conn != null? "Connected":"Not Connected"));
			} // end if
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stmt.executeQuery("SELECT * FROM Test.dbo.CAT");
			while (rs.next()) {
				int total_columns = rs.getMetaData().getColumnCount();
				for (int i=0; i < total_columns; i++) {
                    System.out.print(rs.getMetaData().getColumnLabel(i + 1) + ": " + rs.getObject(i + 1) + " ");
                } // end for
				System.out.println();
			} // end while
			rs.beforeFirst();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonArray jsonArray = convertToJSON(rs);
			String prettyJSON = gson.toJson(jsonArray);
			System.out.println(prettyJSON);
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} // end catch

	} // end main
	
    public static JsonArray convertToJSON(ResultSet resultSet) throws Exception {
    	JsonArray jsonArray = new JsonArray();
        while (resultSet.next()) {
            int total_columns = resultSet.getMetaData().getColumnCount();
            JsonObject obj = new JsonObject();
            for (int i = 0; i < total_columns; i++) {
            	addValueToJSON(obj, resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(), resultSet.getObject(i + 1));
            } // end for
          jsonArray.add(obj);
        } // end while
        return jsonArray;
    } // end convertToJSON
    
    private static void addValueToJSON(JsonObject obj, String propertyName, Object value) throws Exception {
        if (value == null) {
            obj.add(propertyName, JsonNull.INSTANCE);
        } else if (value instanceof Number) {
            obj.addProperty(propertyName, (Number)value);
        } else if (value instanceof String) {
            obj.addProperty(propertyName, (String)value);
        } else if (value instanceof java.sql.Date) {
            obj.addProperty(propertyName, ((java.sql.Date)value).toString());
        } else if (value instanceof java.sql.Time) {
            obj.addProperty(propertyName, ((java.sql.Time)value).toString());
        } else if (value instanceof java.sql.Timestamp) {
            obj.addProperty(propertyName, ((java.sql.Timestamp)value).toString());
        } else {
           throw new Exception("Unrecognised type of value: " + value.getClass().getName());
        }
    }
} // end Driver