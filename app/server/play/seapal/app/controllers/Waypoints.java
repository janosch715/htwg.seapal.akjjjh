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

public class Waypoints extends Controller {

	public static Result get(String tripId) {
		return getWaypoints(tripId);
	}
	
	public static Result edit() {
		final DynamicForm form = form().bindFromRequest();
		final String dataId = form.get("dataId");
		
		String query = "";
		if (dataId == null) {
			return badRequest("Missing parameter [dataId]");
		} else if (dataId.equals("NULL") == false) {
			query = "UPDATE `seapal`.`waypoint` " +
					"SET " +
					"waypoint_name='" + form.get("waypointtitle") + "', " +
					"maneuverID='" + form.get("maneuverId") + "', " +
					"headsailID='" + form.get("headsailId") + "', " +
					"mainsailID='" + form.get("mainsailId") + "' " +
					"WHERE " +
					"waypointID='" + dataId + "'";
		} else {
			return badRequest("No parameter [dataId]");
		}
		
		try {
			Connection connection = DB.getConnection();
			Statement select = connection.createStatement();
			select.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		}
		
		
		String Query = "SELECT * " +
				"FROM `seapal`.`waypoint` " +
				"WHERE waypointID = " + dataId;
		
		try {
			Connection connection = DB.getConnection();
			Statement select = connection.createStatement();
			ResultSet result = select.executeQuery(Query);

			result.next();
			
			boolean tHasData = false;
			tHasData |= result.getString("waypoint_name") != null;
			tHasData |= result.getString("maneuverID").equals("1") == false;
			tHasData |= result.getString("headsailID").equals("1") == false;
			tHasData |= result.getString("mainsailID").equals("1") == false;
			
			ObjectNode tItem = play.libs.Json.newObject();
			
			tItem.put("has_data", tHasData ? 1 : 0);
	
			connection.close();
			return ok(tItem);
		} catch (SQLException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		}
	}
	
	public static Result getDetails(String waypointId) {
		ObjectNode tJsonResult = play.libs.Json.newObject();
		
		String Query = "SELECT * FROM waypoint WHERE waypointID=" + waypointId;
		
		try {
			Connection connection = DB.getConnection();
			Statement select = connection.createStatement();
			ResultSet result = select.executeQuery(Query);

			result.next();
			tJsonResult.put("dataId", result.getString("waypointID"));
			tJsonResult.put("tripId", result.getString("tripID"));
			tJsonResult.put("waypointtitle", result.getString("waypoint_name"));
			tJsonResult.put("position", result.getString("position"));
			tJsonResult.put("btm", result.getString("btm"));
			tJsonResult.put("dtm", result.getString("dtm"));
			tJsonResult.put("cog", result.getString("cog"));
			tJsonResult.put("sog", result.getString("sog"));
			tJsonResult.put("maneuverId", result.getString("maneuverID"));
			tJsonResult.put("headsailId", result.getString("headsailID"));
			tJsonResult.put("mainsailId", result.getString("mainsailID"));
	
			connection.close();
			return ok(tJsonResult);
		} catch (SQLException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		}
	}
	
	
	private static Result getWaypoints(String tripId) {
		ArrayNode tJsonResult = JsonNodeFactory.instance.arrayNode();
		
		String Query = "SELECT * FROM waypoint WHERE tripID=" + tripId + " ORDER BY waypointID";
		
		try {
			Connection connection = DB.getConnection();
			Statement select = connection.createStatement();
			ResultSet result = select.executeQuery(Query);

			while (result.next()) {
				ObjectNode tItem = play.libs.Json.newObject();
				
				boolean tHasData = false;
				tHasData |= result.getString("waypoint_name") != null;
				tHasData |= result.getString("maneuverID").equals("1") == false;
				tHasData |= result.getString("headsailID").equals("1") == false;
				tHasData |= result.getString("mainsailID").equals("1") == false;
				
				tItem.put("waypointId", result.getString("waypointID"));
				tItem.put("tripId", result.getString("tripId"));
				tItem.put("position", result.getString("position"));
				tItem.put("has_data", tHasData ? 1 : 0);
				
				tJsonResult.add(tItem);
			}
			connection.close();
			return ok(tJsonResult);
		} catch (SQLException e) {
			e.printStackTrace();
			return badRequest(e.getMessage());
		}
	}
}