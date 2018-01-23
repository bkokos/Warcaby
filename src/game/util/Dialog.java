package game.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Dialog {
	
	public static final ButtonType yes = new ButtonType("Tak");
	public static final ButtonType no = new ButtonType("Nie");

	
	public static Alert creteYesNoAlert(AlertType alertType, String title, String header, String message){
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);		

		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(yes, no);
		
		return alert;
	}
	

}
