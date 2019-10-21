import javafx.*;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



/**
 * @author Alex Grant, Coleman Clarstein
 *
 */
public class UI extends Application {


	/**
	 * this function builds the GUI and displays it to the user once everything
	 * has been initialized
	 *
	 * @param firststage - a pre-made stage created by Application.launch
	 * @return none
	 */
	public void start(Stage firststage) throws Exception {
		// TODO Auto-generated method stub
		firststage.setTitle("Wicked Scheduler");
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


	/**
	 * @param args
	 */
	public static void main(String[] args){
		Application.launch(args);
	}
}
