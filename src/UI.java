import javafx.*;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UI extends Application {
	//do gui things
	
	
	public void start(Stage firststage) throws Exception {
		firststage.setTitle("何???");
		firststage.setX(250);
		firststage.setY(50);
		firststage.setWidth(1000);
		firststage.setHeight(700);
		firststage.initStyle(StageStyle.DECORATED);
		
		TextField courseCode = new TextField("Input Course Code");
		Button addCourse = new Button("Add Course");
		addCourse.setOnAction(action -> {System.out.println(courseCode.getText());});
		
		HBox hBox = new HBox(courseCode, addCourse);
		
		VBox  vBox  = new VBox();
		Scene scene1 = new Scene(hBox, 200, 100);
		firststage.setScene(scene1);
		
		firststage.show();
	}
	
	
	public static void main(String[] args){
		Application.launch(args);	
	}
}
