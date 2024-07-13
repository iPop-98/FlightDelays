package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Edge;
import it.polito.tdp.extflightdelays.model.Flight;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports(Map<Integer, Airport> aeroportiIdMap) {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("ID");
				if(aeroportiIdMap.get(id)==null) {
					Airport airport = new Airport(id, rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
							rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
							rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
					
					result.add(airport);
					aeroportiIdMap.put(id, airport);
				}
				else
					result.add(aeroportiIdMap.get(id));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Airport> getAllVertex(Map<Integer, Airport> aeroportiIdMap, Integer n){
		
		String sqlTMP="SELECT tmp.ID, COUNT(*) as N" 
					+ "FROM (SELECT a.ID, a.IATA_CODE, f.AIRLINE_ID, COUNT(*) as n "
					+ 		"FROM flights f, airports a "
					+ 		"WHERE f.ORIGIN_AIRPORT_ID = a.ID OR f.DESTINATION_AIRPORT_ID = a.ID "
					+ 		"GROUP BY a.ID, a.IATA_CODE, f.AIRLINE_ID) tmp "
					+ "GROUP BY tmp.ID "
					+ "HAVING N >= ? ";
		
		String sql = "SELECT a.ID "
				+ 	 "FROM flights f, airports a "
				+ 	 "WHERE f.ORIGIN_AIRPORT_ID = a.ID OR f.DESTINATION_AIRPORT_ID = a.ID "
				+ 	 "GROUP BY a.ID "
				+ 	 "HAVING COUNT(DISTINCT f.AIRLINE_ID) >= ? ";
		
		List<Airport> allVertex = new ArrayList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, n);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				allVertex.add(aeroportiIdMap.get(rs.getInt("ID")));
					
			}

			st.close();
			rs.close();
			conn.close();
			return allVertex;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
 
	public boolean isVertex(Airport air, Integer n) {
		String sql = "SELECT COUNT(Distinct flights.AIRLINE_ID) AS nAirlines "
				+ "FROM flights "
				+ "WHERE flights.ORIGIN_AIRPORT_ID = ? OR flights.DESTINATION_AIRPORT_ID = ? ";
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, air.getId());
			st.setInt(2, air.getId());
			ResultSet rs = st.executeQuery();
			
			//per come è scritta la query, il risultato è sempre composto da una riga, in cui è contenuto il risultato atteso, dunque procedo direttamente alla lettura
			rs.first();
			int valore = rs.getInt("nAirlines");
			
			//letto il valore, chiudo tutto
			st.close();
			rs.close();
			conn.close();
			
			if(valore>=n)
				return true;
			else
				return false;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Edge> getAllEdges(Map<Integer, Airport> aeroportiIdMap) {
		String sql = "SELECT ORIGIN_AIRPORT_ID, DESTINATION_AIRPORT_ID, COUNT(f.ID) AS nFlights "
				+ "FROM flights f "
				+ "GROUP BY ORIGIN_AIRPORT_ID, f.DESTINATION_AIRPORT_ID ";
		
		List<Edge> edges = new ArrayList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Edge e = new Edge(aeroportiIdMap.get(rs.getInt("ORIGIN_AIRPORT_ID")),
								aeroportiIdMap.get(rs.getInt("DESTINATION_AIRPORT_ID")), rs.getInt("nFlights"));
				edges.add(e);
					
			}

			st.close();
			rs.close();
			conn.close();
			return edges;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}