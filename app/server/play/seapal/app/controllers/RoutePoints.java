package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.data.DynamicForm;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;

public class RoutePoints extends Controller {

	public static Result get(String tripId) {
		return getRoutePoints(tripId);
	}
	
	public static Result updateCreate() {
		final DynamicForm form = form().bindFromRequest();
		final String dataId = form.get("routepointId");
		
		String query = "";
		
		if (dataId == null) {
			return badRequest("Missing parameter [routepointId]");
		} else if (dataId.equals("NULL") == false) {
			query = "UPDATE `seapal`.`routepoint` " +
					"SET " +
					"tripID='" + form.get("tripId") + "', " +
					"name='" + form.get("name") + "', " +
					"notes='" + form.get("notes") + "', " +
					"position='" + form.get("position") + "' " +
					"WHERE " +
					"routepointID='" + dataId + "'";
			
			try {
				Connection connection = DB.getConnection();
				Statement select = connection.createStatement();
				select.executeUpdate(query);
			} catch (SQLException e) {
				e.printStackTrace();
				return badRequest(e.getMessage());
			}
			
			return ok("");
		} else {
			query = "INSERT INTO `seapal`.`routepoint` ( " +
							"`routepointID`, `tripID`, `name`, `notes`, `position` " +
							") VALUES ( " +
							"NULL, " +
							"'" + form.get("tripId") + "', " +
							"'" + form.get("name") + "', " +
							"'" + form.get("notes") + "', " +
							"'" + form.get("position") + "' " +
							")";
			
			try {
				Connection connection = DB.getConnection();
				Statement select = connection.createStatement();
				int tId = select.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
				
				ObjectNode tJsonReturn = play.libs.Json.newObject();
				tJsonReturn.put("routepointId", tId);
				
				return ok(tJsonReturn);
			} catch (SQLException e) {
				e.printStackTrace();
				return badRequest(e.getMessage());
			}
		}
	}
	
	public static Result deleteForTrip() {
		final DynamicForm form = form().bindFromRequest();
		final String removeId = form.get("removeId");
		
		String query;
		if (removeId == null) {
			return badRequest("Missing parameter [removeId]");
		} 
		query = "" + 
			"DELETE FROM `seapal`.`routepoint` " +
			"WHERE tripID=" + removeId;
		
		try {
			Connection connection = DB.getConnection();
			Statement select = connection.createStatement();
			select.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		}
		return ok("");
	}
	
	public static Result delete() {
		final DynamicForm form = form().bindFromRequest();
		final String removeId = form.get("removeId");
		
		String query;
		if (removeId == null) {
			return badRequest("Missing parameter [removeId]");
		} 
		query = "" + 
			"DELETE FROM `seapal`.`routepoint` " +
			"WHERE routepointID=" + removeId;
		
		try {
			Connection connection = DB.getConnection();
			Statement select = connection.createStatement();
			select.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		}
		return ok("");
	}
	
	private static Result getRoutePoints(String tripId) {
		ArrayNode tJsonResult = JsonNodeFactory.instance.arrayNode();
		
		String Query = "SELECT * FROM routepoint WHERE tripID=" + tripId + " ORDER BY routepointID";
		
		try {
			Connection connection = DB.getConnection();
			Statement select = connection.createStatement();
			ResultSet result = select.executeQuery(Query);

			while (result.next()) {
				ObjectNode tJsonLogBook = play.libs.Json.newObject();
				tJsonLogBook.put("routepointId", result.getString("routepointID"));
				tJsonLogBook.put("name", result.getString("name"));
				tJsonLogBook.put("notes", result.getString("notes"));
				tJsonLogBook.put("tripId", result.getString("tripID"));
				tJsonLogBook.put("position", result.getString("position"));
				
				tJsonResult.add(tJsonLogBook);
			}
			connection.close();
			return ok(tJsonResult);
		} catch (SQLException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		}
	}
}