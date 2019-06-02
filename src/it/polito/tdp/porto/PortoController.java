package it.polito.tdp.porto;

import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.porto.model.Author;
import it.polito.tdp.porto.model.Model;
import it.polito.tdp.porto.model.Paper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class PortoController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Author> boxPrimo;

    @FXML
    private ComboBox<Author> boxSecondo;

    @FXML
    private TextArea txtResult;

	private Model model;

    @FXML
    void handleCoautori(ActionEvent event) {
    	
    	txtResult.clear();
    	
    	if(this.boxPrimo.getValue()!=null) {
    		
    		List<Author> result = model.getCoAuthors(this.boxPrimo.getValue());
    		Collections.sort(result);
    		txtResult.appendText("List of Co-Author\n");
    		for(Author a : result) {
    			txtResult.appendText(a+"\n");
    		}
    		
    		List<Author> carica = new LinkedList<>(model.getListAuthor());
    		carica.remove(this.boxPrimo.getValue());
    		carica.removeAll(result);
    		
    		this.boxSecondo.setItems(FXCollections.observableList(carica));
    		
    	}
    	else
    		txtResult.appendText("Please, select an author");
    	
    	

    }

    @FXML
    void handleSequenza(ActionEvent event) {
    	
    	txtResult.clear();

    	Author primo = this.boxPrimo.getValue();
    	Author secondo = this.boxSecondo.getValue();
    	
    	if(primo!=null && secondo!=null) {
    	
    	List<Paper> path = model.getSmallPath(primo, secondo);
    	
    	if(path!=null) {
    	    
    	  for(Paper p : path) 
    		   txtResult.appendText(p.toString()+"\n");
    		
    	}
    	else 
    		txtResult.appendText("There is no bound between this two authors");
    	
    			
    	}
    	else
    		txtResult.appendText("Please, select two author");
    		
    }

    @FXML
    void initialize() {
        assert boxPrimo != null : "fx:id=\"boxPrimo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert boxSecondo != null : "fx:id=\"boxSecondo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Porto.fxml'.";

    }

	public void setModel(Model model) {
		this.model = model;
		
	}

	public void caricaBox1() {
		
		boxPrimo.setItems(FXCollections.observableList(model.getListAuthor()));
		
	}
}
