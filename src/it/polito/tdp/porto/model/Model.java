package it.polito.tdp.porto.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


import it.polito.tdp.porto.db.PortoDAO;

public class Model {

	private Map<Integer, Author> idAuthorMap;
	private PortoDAO dao;
	private Graph<Author, DefaultEdge> grafo;
	private List<Author> shortestPath; 
	
	public Model() {
		dao=new PortoDAO();
		this.idAuthorMap = new HashMap<>();
		dao.loadAuthor(idAuthorMap);
		creaGrafo();
	}
	
	

	public List<Author> getListAuthor(){
		 List<Author> result = new LinkedList<>(this.idAuthorMap.values());
		 Collections.sort(result);
		 return result;
	}
	
    private void creaGrafo() {
		
		grafo = new SimpleGraph<Author, DefaultEdge>(DefaultEdge.class);
		
		//carico i vertici
		
		Graphs.addAllVertices(grafo, idAuthorMap.values());
		
		//carico gli archi
		dao.getArchi(grafo, idAuthorMap);
		
				
	}

	public List<Author> getCoAuthors(Author value) {
		
		Set <DefaultEdge> coEdge = grafo.edgesOf(value);
		
		List <Author> result = new LinkedList<>();
		
		for(DefaultEdge de : coEdge) {
			if(grafo.getEdgeSource(de).equals(value))
				result.add(grafo.getEdgeTarget(de));
			else
				result.add(grafo.getEdgeSource(de));
		}
		
		return result;
	}



	public List<Paper> getSmallPath(Author primo, Author secondo) {
	
		if(grafo!=null) {
			
			this.shortestPath = new LinkedList<>();
			List<Paper> result = new ArrayList<>();
			
			DijkstraShortestPath<Author,DefaultEdge> dijkstra =  new DijkstraShortestPath<>(this.grafo);
			GraphPath <Author,DefaultEdge> cammino = dijkstra.getPath(primo, secondo);
			
			if(cammino!=null) {
			
			//questa è la lista degli autori nel cammino
			this.shortestPath = cammino.getVertexList();
			
			/*
			 * Il problema ci richiede la lista dei Paper che ciascuno di questi autori ha in comune 
			 * con il successivo quindi;
			 * 
			 * primo con a1
			 * a1 con a2
			 * ....
			 * an con secondo
			 */
			
			//tali Paper possono essere salvati in una lista che verrà mandata direttamente al controller
			
			for(int i=0; i<shortestPath.size()-1;i++) 
			      dao.getPapers(result, shortestPath.get(i),  shortestPath.get(i+1));	
			
			return result;
			
			}
		
		}
		
		return null;
	}
    
    
}
