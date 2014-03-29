import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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


public class LanguageValidator {
	
	private final static String INPUT_PATH = TutorialValidator.INPUT_PATH;
	private final static String OUTPUT_PATH = TutorialValidator.OUTPUT_PATH;
	private final static String DEFAULT_LANG = TutorialValidator.DEFAULT_LANG;
	private final static PrintStream log = System.out;
	private final static String LANGUAGE_FILE = "Strings.json";
	private final static String PACKAGE = "tuxkids.tuxblocks.core.lang";
	
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
			if (!file.equals(LANGUAGE_FILE)) {
				if (master) throw new RuntimeException(String.format("Default language %s does not have %s!",
						DEFAULT_LANG, file));
				continue;
			}
			JSONTokener tokenizer = new JSONTokener(new FileInputStream(new File(langDir, file)));
			JSONObject object = new JSONObject(tokenizer);
			
			if (master) {
				objects.put(file, object);
				createInterface(object, file);
			} else {
				JSONObject oldObject = objects.get(file);

				List<String> oldKeys = createKeyList(oldObject);
				List<String> newKeys = createKeyList(object);
				
				for (String key : oldKeys) {
					if (!newKeys.contains(key)) {
						log.println(String.format(
								"WARNING! %s for lang '%s' is missing key '%s', which exists in default lang '%s'!",
								file, dir, key, DEFAULT_LANG));
					}
				}
				for (String key : newKeys) {
					if (!oldKeys.contains(key)) {
						throw new RuntimeException(String.format(
								"%s for default lang '%s' is missing key '%s', which exists in lang '%s'!",
								file, DEFAULT_LANG, key, dir));
					}
				}
			}
		}
	}
	
	private List<String> createKeyList(JSONObject object) {
		List<String> keys = new ArrayList<String>();
		for (String domain : JSONObject.getNames(object)) {
			JSONObject list = object.getJSONObject(domain);
			for (String key : JSONObject.getNames(list)) {
				keys.add(domain + "." + key);
			}
		}
		return keys;
	}
	
	private static String capitalize(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	private void createInterface(JSONObject object, String filename) {
		try {
			for (String domain : JSONObject.getNames(object)) {
				JDefinedClass domainBase = codeModel._class(PACKAGE + ".Strings_" + capitalize(domain), ClassType.INTERFACE);
				String[] keys = JSONObject.getNames(object);
				for (String key : keys) {
					String value = object.getString(key);
					JFieldVar field = domainBase.field(JMod.FINAL | JMod.STATIC, String.class, key);
					field.javadoc().add(value);
					field.init(JExpr.lit(key));
				}
			}
		} catch( Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error building code");
		}
	}
	
	public static void main(String[] args) throws IOException {
		new LanguageValidator().validate();;
	}
}
