import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;


public class Validator {
	
	private final static String INPUT_PATH = "../assets/src/main/resources/assets/text";
	private final static String OUTPUT_PATH = "../core/src/main/java";
	private final static PrintStream log = System.out;
	private final static String TUTORIAL_ID = "tutorial_id";
	private final static String DEFAULT_LANG = "en";
	private final static String PACKAGE = "tuxkids.tuxblocks.core.tutorial.gen";
	
	private HashMap<String, JSONObject> objects = new HashMap<String, JSONObject>();
	private JCodeModel codeModel = new JCodeModel();
	
	public void validate() throws IOException {
		File textDir = new File(INPUT_PATH);
		if (textDir.exists()) {
			analyzeLangDir(textDir, DEFAULT_LANG, true);
			String[] langDirs = textDir.list();
			for (String dir : langDirs) {
				if (DEFAULT_LANG.equals(dir)) continue;
				analyzeLangDir(textDir, dir, false);
			}
		} else {
			log.println("Cannot open text assets directory: " + textDir.getAbsolutePath());
		}
		
		codeModel.build(new File(OUTPUT_PATH));
	}

	private void analyzeLangDir(File textDir, String dir, boolean master) throws FileNotFoundException {
		File langDir = new File(textDir, dir);
		if (!langDir.isDirectory()) return;
		String[] files = langDir.list();
		for (String file : files) {
			if (!file.toLowerCase().endsWith(".json")) continue;
			JSONTokener tokenizer = new JSONTokener(new FileInputStream(new File(langDir, file)));
			JSONObject object = new JSONObject(tokenizer);
			if (!object.has(TUTORIAL_ID)) continue;
			if (master) {
				objects.put(file, object);
				createInterface(object);
			} else {
				JSONObject oldObject = objects.get(file);
				if (oldObject == null) {
					log.printf("WARNING: Non-default language %s has tutorial %s but default language %s does not!\n",
							dir, file, DEFAULT_LANG);
				}
				List<String> oldKeys = Arrays.asList(JSONObject.getNames(oldObject));
				List<String> newKeys = Arrays.asList(JSONObject.getNames(object));
				
				for (String key : oldKeys) {
					if (!newKeys.contains(key)) {
						throw new RuntimeException(String.format(
								"Tutorial %s for lang '%s' is missing key '%s', which exists in default lang '%s'!",
								file, dir, key, DEFAULT_LANG));
					}
				}
				for (String key : newKeys) {
					if (!oldKeys.contains(key)) {
						throw new RuntimeException(String.format(
								"Tutorial %s for default lang '%s' is missing key '%s', which exists in lang '%s'!",
								file, DEFAULT_LANG, key, dir));
					}
				}
			}
		}
	}
	
	private void createInterface(JSONObject object) {
		String id = object.getString(TUTORIAL_ID);
		
		try {
			JDefinedClass tutorial = codeModel._class(PACKAGE + "." + id + "_Base", ClassType.INTERFACE);
			String[] keys = JSONObject.getNames(object);
			for (String key : keys) {
				String value = object.getString(key);
				JFieldVar field = tutorial.field(JMod.FINAL | JMod.STATIC, String.class, key);
				field.javadoc().add(value);
				field.init(JExpr.lit(key));
			}
		} catch( Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error building code");
		}
	}
	
	public static void main(String[] args) throws IOException {
		new Validator().validate();;
	}
}
