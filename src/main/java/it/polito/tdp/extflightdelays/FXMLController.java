package it.polito.tdp.extflightdelays;

import java.net.URL;

import java.util.ResourceBundle;

import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.util.List;

public class FXMLController {

	private Model model;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML // fx:id="compagnieMinimo"
    private TextField compagnieMinimo; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBoxAeroportoPartenza"
    private ComboBox<Airport> cmbBoxAeroportoPartenza; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBoxAeroportoDestinazione"
    private ComboBox<Airport> cmbBoxAeroportoDestinazione; // Value injected by FXMLLoader

    @FXML // fx:id="btnAnalizza"
    private Button btnAnalizza; // Value injected by FXMLLoader

    @FXML // fx:id="btnConnessione"
    private Button btnConnessione; // Value injected by FXMLLoader

    @FXML
    void doAnalizzaAeroporti(ActionEvent event) {
    	
    	this.cmbBoxAeroportoPartenza.getItems().clear();
    	this.cmbBoxAeroportoDestinazione.getItems().clear();
    	
    	int x = 0;
    	
    	try {
    		x = Integer.parseInt(this.compagnieMinimo.getText());
    		if(x>0) {
    			if(this.model.creaGrafo(x)) {
    				this.txtResult.setText("Analisi riuscita, grafo correttamente implementato.\n");
    				this.cmbBoxAeroportoPartenza.getItems().addAll(this.model.getAllVertex());
    				this.cmbBoxAeroportoDestinazione.getItems().addAll(this.model.getAllVertex());
    				this.txtResult.appendText("Menu a tendina correttamente inizializzati.\n");
    			} else {
    				this.txtResult.setText("L'analisi con il numero minimo di compagnie da voi indicato non ha prodotto alcun risultato.\n");
    				this.txtResult.appendText("Se si desidera riprovare, inserire un valore numerico, maggiore di zero, nel campo '#compagnie minimo'.");
    			}
    			
    			
    		}
    		else
    			this.txtResult.setText("Inserire un valore numerico, maggiore di zero, nel campo '#compagnie minimo'.");

        	this.compagnieMinimo.clear();
    		
    	}catch (NumberFormatException ne) {
    		ne.printStackTrace();
    		this.txtResult.setText("Valore non accettabile!\nPrego, inserire un valore numerico, maggiore di zero, nel campo '#compagnie minimo'.");
    	}catch (RuntimeException re) {
    		re.printStackTrace();
    		this.txtResult.setText("Errore nella connessione al DB");
    		
    	}

    }

    @FXML
    void doTestConnessione(ActionEvent event) {
    	
    	Airport partenza = this.cmbBoxAeroportoPartenza.getValue();
    	Airport arrivo = this.cmbBoxAeroportoDestinazione.getValue();
    	if(partenza != null && arrivo != null)
    		if(!partenza.equals(arrivo)) {
    			List<Airport> percorso = this.model.trovaPercorso(partenza, arrivo);
    			if(percorso!=null) {
    				this.txtResult.setText("Percorso trovato!\n");
    				this.txtResult.appendText("L'aeroporto '" + arrivo.getAirportName() + "' è raggiungibile, partendo da '" + partenza.getAirportName() + "' seguendo il seguente itinerario:\n");
    				for(Airport a : percorso)
    					this.txtResult.appendText("- " + a.getAirportName()+"\n");
    			} else
    				this.txtResult.setText("Putroppo '" + arrivo.getAirportName() + "' non è raggiungibile, partendo da '" + partenza.getAirportName() +"'");
    				
    				
    		} else
    			this.txtResult.setText("Selezionare due aeroporti Partenza-->Destinazione diversi tra loro!");
    	else
    		this.txtResult.setText("Prima di effettuare la ricerca, si prega di selezionare due aeroporti Partenza-->Destinazione, diversi tra loro!");
    				

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert compagnieMinimo != null : "fx:id=\"compagnieMinimo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbBoxAeroportoPartenza != null : "fx:id=\"cmbBoxAeroportoPartenza\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbBoxAeroportoDestinazione != null : "fx:id=\"cmbBoxAeroportoDestinazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnAnalizza != null : "fx:id=\"btnAnalizza\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnConnessione != null : "fx:id=\"btnConnessione\" was not injected: check your FXML file 'Scene.fxml'.";

    }

    public void setModel(Model model) {
    	this.model = model;
    }
}