import java.io.File;
import java.io.PrintStream;


public class Validator {
	
	private final static String PATH = "../assets/src/main/resources/assets/text";
	private final static PrintStream log = System.out;
	
	public void validate() {
		File textDir = new File(PATH);
		if (textDir.exists()) {
			
		} else {
			log.println("Cannot open text assets directory: " + textDir.getAbsolutePath());
		}
	}
	
	public static void main(String[] args) {
		new Validator().validate();;
	}
}
