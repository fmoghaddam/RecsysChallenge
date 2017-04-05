//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.apache.log4j.Logger;
//
//public class NewMain {
//
//	private static ExecutorService executor = Executors.newCachedThreadPool();
//	
//	private static final Logger userLog = Logger.getLogger("debugLogger");
//	private static final Logger itemLog = Logger.getLogger("reportsLogger");
//	static final Map<String,String> userMap = new HashMap<>();
//	static final Map<String,String> itemMap = new HashMap<>();
//
//	private static String USER_FILE = "user_feats_userid-SVMlight";
//	private static String ITEM_FILE = "item_feats-SVMlight";
//	static String INTERACTION_FILE = "interactions.csv";
//	static String RESULT_FILE = "result";
//	static String RESULT_FOLDER = "result";
//	private static int FRACTION_NUMBER=1;
//	static int MAX_USER_FEATURE_INDEX=100;
//	
//	private static Set<String> duplicateUsers = new HashSet<>();
//	private static Set<String> duplicateItems = new HashSet<>();
//
//	private static long lineNumber = 0;
//	
//	public static void main(String[] args) {
//		if(args.length<5){
//			throw new IllegalArgumentException("Not enough argument");
//		}
//		USER_FILE = args[0];
//		ITEM_FILE = args[1];
//		INTERACTION_FILE = args[2];
//		MAX_USER_FEATURE_INDEX = Integer.parseInt(args[3]);
//		FRACTION_NUMBER = Integer.parseInt(args[4]);
//				
//		while(lineNumber<320000000){
//			new ParallelTask(lineNumber, length)
//			
//		}
//
//	}
//
//}
