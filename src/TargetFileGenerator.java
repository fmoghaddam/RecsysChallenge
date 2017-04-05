import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class TargetFileGenerator {

	private static final Logger LOG = Logger.getLogger(TargetFileGenerator.class.getCanonicalName());
	private static final Map<String, String> userMap = new HashMap<>();
	private static final Map<String, String> itemMap = new HashMap<>();

	private static final List<String> userTargetList = new ArrayList<>();
	private static final List<String> itemTargetList = new ArrayList<>();

	private static String USER_FILE = "data/user_feats_userid-SVMlight";
	private static String ITEM_FILE = "data/item_feats-SVMlight";

	private static String USER_TARGET_FILE = "data/targetUsers.csv";
	private static String ITEM_TARGET_FILE = "data/targetItems.csv";
	private static String RESULT_FILE = "target_result";
	private static String RESULT_FOLDER = "result";
	private static int FRACTION_NUMBER = 3000000;
	private static int MAX_USER_FEATURE_INDEX = 13000;

	public static void main(String[] args) {
		if (args.length != 6) {
			throw new IllegalArgumentException("Not enough argument");
		}
		USER_FILE = args[0];
		ITEM_FILE = args[1];
		USER_TARGET_FILE = args[2];
		ITEM_TARGET_FILE = args[3];
		MAX_USER_FEATURE_INDEX = Integer.parseInt(args[4]);
		FRACTION_NUMBER = Integer.parseInt(args[5]);
		
		printParameters();

		readUserFile();
		readItemFile();

		readUserTargetFile();
		readItemTargetFile();

		writeResult();
	}

	private static void printParameters() {
		LOG.info("User file: "+USER_FILE);
		LOG.info("Item file: "+ITEM_FILE);
		LOG.info("Target user file: "+USER_TARGET_FILE);
		LOG.info("Target item file: "+ITEM_TARGET_FILE);
		LOG.info("Max user feature index: "+MAX_USER_FEATURE_INDEX);
		LOG.info("Fraction number: "+FRACTION_NUMBER);
	}

	private static void writeResult() {
		StringBuilder result = new StringBuilder();
		int lineNumber = 0;
		int fileCounter= 1;
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(RESULT_FOLDER + File.separator + RESULT_FILE+"_"+fileCounter, true));
			for (final String targetItem : itemTargetList) {				
				final String itemFeature = itemMap.get(targetItem);
				if (itemFeature == null) {
					throw new IllegalArgumentException("Item " + targetItem + " does not exist in item file");
				}
				final String itemFeatureConverted = convert(itemFeature, MAX_USER_FEATURE_INDEX);
				for (final String targetUser : userTargetList) {
					final String userFeature = userMap.get(targetUser);
					if (userFeature == null) {
						throw new IllegalArgumentException("User " + targetUser + " does not exist in user file");
					}
					lineNumber++;
					result.append("(").append(targetUser).append(",").append(targetItem).append(")").append(" ")
							.append(userFeature).append(" ").append(itemFeatureConverted).append("\n");

					if (lineNumber % FRACTION_NUMBER == 0) {
						bw.write(result.toString());
						bw.close();
						LOG.info("File "+fileCounter+" has been created");
						fileCounter++;
						result = new StringBuilder();
						bw = new BufferedWriter(
								new FileWriter(RESULT_FOLDER + File.separator + RESULT_FILE+"_"+fileCounter, true));
					}
				}
			}
			bw.close();
		} catch (Exception exception) {
			LOG.error(exception);
		}
	}

	static String convert(String itemFeature, int i) {
		final StringBuilder result = new StringBuilder();
		final String[] pairs = itemFeature.split(" ");
		for (String pair : pairs) {
			String key = pair.split(":")[0];
			String value = pair.split(":")[1];
			result.append(Long.valueOf(key) + i).append(":").append(value).append(" ");
		}
		return result.toString();
	}

	private static void readItemFile() {
		try (final BufferedReader br = new BufferedReader(new FileReader(ITEM_FILE));) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				final String key = sCurrentLine.split(" ")[0];
				final String value = sCurrentLine.replaceFirst(key + " ", "");
				itemMap.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readUserFile() {
		try (final BufferedReader br = new BufferedReader(new FileReader(USER_FILE));) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				final String key = sCurrentLine.split(" ")[0];
				final String value = sCurrentLine.replaceFirst(key + " ", "");
				userMap.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readItemTargetFile() {
		try (final BufferedReader br = new BufferedReader(new FileReader(ITEM_TARGET_FILE));) {
			String sCurrentLine;
			br.readLine();
			while ((sCurrentLine = br.readLine()) != null) {
				itemTargetList.add(sCurrentLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readUserTargetFile() {
		try (final BufferedReader br = new BufferedReader(new FileReader(USER_TARGET_FILE));) {
			String sCurrentLine;
			br.readLine();
			while ((sCurrentLine = br.readLine()) != null) {
				userTargetList.add(sCurrentLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
