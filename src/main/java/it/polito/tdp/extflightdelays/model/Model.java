package it.polito.tdp.extflightdelays.model;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private ExtFlightDelaysDAO dao;
	
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private List<Airport> aeroporti = new ArrayList<>();
	private Map<Integer, Airport> aeroportiIdMap;
	
	
	public Model() {
		this.dao = new ExtFlightDelaysDAO();
		this.aeroportiIdMap = new HashMap<Integer, Airport>();
		this.aeroporti = dao.loadAllAirports(aeroportiIdMap);
	}


	public boolean creaGrafo(Integer n) throws RuntimeException{
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		List<Airport> allVertex = new ArrayList<>();
		if(this.aeroporti!=null) {
			for(Airport air : aeroporti) {
				if(dao.isVertex(air, n))
					allVertex.add(air);
			}
			Graphs.addAllVertices(this.grafo, allVertex);
			
		}
		else
			throw new RuntimeException("Error loading Airports from DataBase!");
		
		//Se arrivo qui è perché il meteodo non ha generato eccezioni e mi aspetto che abbia quindi costruito i vertici del grafo.
		//Posso dunque procedere a caricare gli archi
		List<Edge> allEdges = dao.getAllEdges(aeroportiIdMap);
		for(Edge edge : allEdges) {
			if(this.grafo.vertexSet().contains(edge.getV1()) && this.grafo.vertexSet().contains(edge.getV2())) {
				DefaultWeightedEdge e = this.grafo.getEdge(edge.getV1(), edge.getV2());
				if(e==null)
					Graphs.addEdgeWithVertices(this.grafo, edge.getV1(), edge.getV2(), edge.getPeso());
				else {
					int peso = (int)grafo.getEdgeWeight(e);
					peso+=edge.getPeso();
					grafo.setEdgeWeight(e, peso);
				}
			}
		}

		if(this.grafo.vertexSet().size()>0 && this.grafo.edgeSet().size()>0) {
			System.out.println("Grafo creato correttamente! Esso è composto da "+ this.grafo.vertexSet().size() + " vertici e " + this.grafo.edgeSet().size() + " archi.\n");
			return true;
		}
		else
			return false;
	}


	public Set<Airport> getAllVertex() {
		return this.grafo.vertexSet();
	}
	
	
	public List<Airport> trovaPercorso(Airport partenza, Airport arrivo) {

		//metodo con algoritmo di Dijkstra; immediato e compatto nel codice; non mi richiede la ricomposizione del percorso
		DijkstraShortestPath<Airport, DefaultWeightedEdge> sp = new DijkstraShortestPath<>(this.grafo) ;
		
		GraphPath<Airport, DefaultWeightedEdge> gp = sp.getPath(partenza, arrivo) ;
		if (gp==null)
			return null;
		else
			return gp.getVertexList() ;
	}
}



















