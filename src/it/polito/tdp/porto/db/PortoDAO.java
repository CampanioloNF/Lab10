package it.polito.tdp.porto.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import it.polito.tdp.porto.model.Author;
import it.polito.tdp.porto.model.Paper;

public class PortoDAO {

	/*
	 * Dato l'id ottengo l'autore.
	 */
	public Author getAutore(int id) {

		final String sql = "SELECT * FROM author where id=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {

				Author autore = new Author(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname"));
				return autore;
			}

			return null;

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}

	/*
	 * Dato l'id ottengo l'articolo.
	 */
	public Paper getArticolo(int eprintid) {

		final String sql = "SELECT * FROM paper where eprintid=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, eprintid);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				Paper paper = new Paper(rs.getInt("eprintid"), rs.getString("title"), rs.getString("issn"),
						rs.getString("publication"), rs.getString("type"), rs.getString("types"));
				return paper;
			}

			return null;

		} catch (SQLException e) {
			 e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}
	
	public void loadAuthor(Map<Integer, Author> idAuthorMap){
		
		final String sql = "SELECT * FROM author";

		
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
		 
			ResultSet rs = st.executeQuery();

			while(rs.next()) {
				
				if(!idAuthorMap.containsKey(rs.getInt("id")))
					idAuthorMap.put(rs.getInt("id"), new Author(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname")));
				
			}

			conn.close();

		} catch (SQLException e) {
			 e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
		
	}

	public void getArchi(Graph<Author, DefaultEdge> grafo, Map<Integer, Author> idAuthorMap) {
		
		final String sql = "SELECT c1.authorid, c2.authorid " + 
				"FROM creator c1, creator c2 " + 
				"WHERE c1.eprintid = c2.eprintid AND NOT (c1.authorid = c2.authorid) AND c2.authorid > c1.authorid " + 
				"GROUP BY c2.authorid ORDER BY c1.authorid";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
		 
			ResultSet rs = st.executeQuery();

			while(rs.next()) {
				
				if(idAuthorMap.containsKey(rs.getInt("c1.authorid")) && idAuthorMap.containsKey(rs.getInt("c2.authorid")))
				    grafo.addEdge(idAuthorMap.get(rs.getInt("c1.authorid")), idAuthorMap.get(rs.getInt("c2.authorid")));
				
			}

			conn.close();

		} catch (SQLException e) {
			 e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
		
	}

	public void getPapers(List<Paper> result, Author a1, Author a2) {
		
		final String sql = "SELECT p.eprintid, p.title, p.issn, p.publication, p.`type`, p.types " + 
				"FROM creator c1, creator c2, paper p " + 
				"WHERE c1.eprintid = c2.eprintid AND c1.eprintid = p.eprintid AND c1.authorid = ? AND c2.authorid = ?";
	
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
		    st.setInt(1, a1.getId());
		    st.setInt(2, a2.getId());
			ResultSet rs = st.executeQuery();

			while(rs.next()) {
				
				Paper paper = new Paper(rs.getInt("eprintid"), rs.getString("title"), rs.getString("issn"),
						rs.getString("publication"), rs.getString("type"), rs.getString("types"));
				paper.addAuthorsPair(a1, a2);
				result.add(paper);
				
			}

			conn.close();

		} catch (SQLException e) {
			 e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
		
	}
}