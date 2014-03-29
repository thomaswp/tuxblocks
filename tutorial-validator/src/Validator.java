import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.json.JSONObject;
import org.json.JSONTokener;


public class Validator {
	
	private final static String INPUT_PATH = "../assets/src/main/resources/assets/text";
	private final static PrintStream log = System.out;
	private final static String TUTORIAL_ID = "tutorial_id";
	
	public void validate() throws FileNotFoundException {
		File textDir = new File(INPUT_PATH);
		if (textDir.exists()) {
			String[] langDirs = textDir.list();
			for (String dir : langDirs) {
				File langDir = new File(dir);
				if (!langDir.isDirectory()) continue;
				String[] files = langDir.list();
				for (String file : files) {
					if (!file.toLowerCase().endsWith(".json")) continue;
					JSONTokener tokenizer = new JSONTokener(new FileInputStream(file));
					JSONObject object = new JSONObject(tokenizer);
					object.get("tutorial_id");
				}
			}
		} else {
			log.println("Cannot open text assets directory: " + textDir.getAbsolutePath());
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		new Validator().validate();;
	}
}
