import javafx.*;
import javafx.application.Application;
import javafx.scene.*;
//import javafx.scene.Group;
//import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UI extends Application {
	//do gui things
	
	
	public void start(Stage firststage) throws Exception {
		// TODO Auto-generated method stub
		firststage.setTitle("何???");
		firststage.setX(250);
		firststage.setY(50);
		firststage.setWidth(1000);
		firststage.setHeight(700);
		firststage.initStyle(StageStyle.DECORATED);
		
		MenuItem Item0 = new MenuItem("Blood");
		MenuItem Item1 = new MenuItem("for");
		MenuItem Item2 = new MenuItem("the");
		MenuItem Item3 = new MenuItem("BloodGod");
		MenuButton dropdown1 = new MenuButton("File", null, Item0, Item1, Item2, Item3);
		HBox hBox = new HBox(dropdown1);
		
		VBox  vBox  = new VBox();
		Scene scene1 = new Scene(hBox);
		firststage.setScene(scene1);
		
		firststage.show();
	}
	
	
	public static void main(String[] args){
		Application.launch(args);	
	}
}
