package json;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.Statement;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class Driver {

	public static void main(String[] args) {
		String dbURL = "jdbc:sqlserver://161.31.4.49;user=cisa;password=Yw7LSLcajASptSQ7;encrypt=false;";
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(dbURL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Test.dbo.CAT");
//			while (rs.next()) {
//				System.out.println(rs.getString(1) + " " + rs.getString(2));
//			} // end while
			Gson gson = new Gson();
			JsonArray jsonArray = convertToJSON(rs);
			System.out.println(gson.toJson(jsonArray));
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} // end catch
		if (conn != null) {
		    System.out.println("Connected");
		} // end if
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
            // Not clear how you want dates to be represented in JSON.
            // Perhaps use SimpleDateFormat to convert them to a string?
            // I'll leave it up to you to finish this off.
        } else {
           // Some other type of value.  You can of course add handling
           // for extra types of values that you get, but it's worth
           // keeping this line at the bottom to ensure that if you do
           // get a value you are not expecting, you find out about it.
           throw new Exception("Unrecognised type of value: " + value.getClass().getName());
        }
    }
} // end Driver