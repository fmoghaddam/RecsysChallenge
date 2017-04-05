import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class Main {

	//private static final Logger LOG = Logger.getLogger(Main.class.getCanonicalName());
	private static final Logger userLog = Logger.getLogger("debugLogger");
	private static final Logger itemLog = Logger.getLogger("reportsLogger");
	static final Map<String,String> userMap = new HashMap<>();
	static final Map<String,String> itemMap = new HashMap<>();

	private static String USER_FILE = "user_feats_userid-SVMlight";
	private static String ITEM_FILE = "item_feats-SVMlight";
	static String INTERACTION_FILE = "interactions.csv";
	static String RESULT_FILE = "result";
	static String RESULT_FOLDER = "result";
	private static int FRACTION_NUMBER=1;
	static int MAX_USER_FEATURE_INDEX=100;
	
	private static Set<String> duplicateUsers = new HashSet<>();
	private static Set<String> duplicateItems = new HashSet<>();

	private static long lineNumber = 0;

	public static void main(String[] args) {
		if(args.length<5){
			throw new IllegalArgumentException("Not enough argument");
		}
		USER_FILE = args[0];
		ITEM_FILE = args[1];
		INTERACTION_FILE = args[2];
		MAX_USER_FEATURE_INDEX = Integer.parseInt(args[3]);
		FRACTION_NUMBER = Integer.parseInt(args[4]);
		
		readUserFile();
		readItemFile();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					System.err.println(lineNumber);
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.setDaemon(true);
		t.start();
		
		writeResult();
	}

	private static void writeResult() {
		int fileCounter = 1;
		try(final BufferedReader br = new BufferedReader(new FileReader(INTERACTION_FILE));){
			BufferedWriter bw =  new BufferedWriter(new FileWriter(RESULT_FOLDER+File.separator+RESULT_FILE+"_"+fileCounter,true));
			String sCurrentLine;
			StringBuilder result = new StringBuilder();
			br.readLine();
			lineNumber++;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] split = sCurrentLine.split("\t");
				String userId = split[0];
				String itemId = split[1];
				String interactionType = split[2];

				String userFeature = userMap.get(userId);
				String itemFeature = itemMap.get(itemId);
				if(lineNumber%FRACTION_NUMBER==0){
					fileCounter++;
					bw.close();
					bw =  new BufferedWriter(new FileWriter(RESULT_FOLDER+File.separator+RESULT_FILE+"_"+fileCounter,true));
				}
				if(userFeature==null){
					duplicateUsers.add(userId);
					System.err.println("userId "+userId);
				}
				if(itemFeature==null){
					duplicateItems.add(itemId);
				}
				if(userFeature !=null && itemFeature!=null){
					final String itemFeatureConverted = convert(itemFeature,MAX_USER_FEATURE_INDEX);
					result.append(interactionType).append(" ").append(userFeature).append(" ").append(itemFeatureConverted).append("\n");
					bw.write(result.toString());
					result = new StringBuilder();
					lineNumber++;
				}
			}
			
			for (Iterator iterator = duplicateItems.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				itemLog.info(string);
			}
			for (Iterator iterator = duplicateUsers.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				userLog.info(string);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	static String convert(String itemFeature, int i) {
		final StringBuilder result = new StringBuilder();
		final String[] pairs = itemFeature.split(" ");
		for(String pair: pairs){
			String key = pair.split(":")[0];
			String value = pair.split(":")[1];
			result.append(Long.valueOf(key)+i).append(":").append(value).append(" ");
		}
		return result.toString();
	}

	private static void readItemFile() {
		try(final BufferedReader br = new BufferedReader(new FileReader(ITEM_FILE));){
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				final String key = sCurrentLine.split(" ")[0];
				final String value = sCurrentLine.replaceFirst(key+" ","");
				itemMap.put(key, value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void readUserFile() {
		try(final BufferedReader br = new BufferedReader(new FileReader(USER_FILE));){
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				final String key = sCurrentLine.split(" ")[0];
				final String value = sCurrentLine.replaceFirst(key+" ","");
				userMap.put(key, value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
