import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ParallelTask implements Runnable{
	private final int startIndex;
	private final int length;
	private int lineNumber = 0;

	private static Set<String> duplicateUsers = new HashSet<>();
	private static Set<String> duplicateItems = new HashSet<>();

	public ParallelTask(int startIndex, int length) {
		super();
		this.startIndex = startIndex;
		this.length = length;
	}

	@Override
	public void run() {
		int fileCounter = 1;
		try(final BufferedReader br = new BufferedReader(new FileReader(Main.INTERACTION_FILE));){
			BufferedWriter bw =  new BufferedWriter(new FileWriter(Main.RESULT_FOLDER+File.separator+Main.RESULT_FILE+"_"+fileCounter,true));
			String sCurrentLine;
			StringBuilder result = new StringBuilder();
			br.readLine();
			while(lineNumber<startIndex){
				br.readLine();
			}
			lineNumber++;
			while(lineNumber<startIndex+length){
				sCurrentLine = br.readLine();
				if(sCurrentLine==null){
					break;
				}
				String[] split = sCurrentLine.split("\t");
				String userId = split[0];
				String itemId = split[1];
				String interactionType = split[2];

				String userFeature = Main.userMap.get(userId);
				String itemFeature = Main.itemMap.get(itemId);

				if(userFeature==null){
					duplicateUsers.add(userId);
					System.err.println("userId "+userId);
				}
				if(itemFeature==null){
					duplicateItems.add(itemId);
				}
				if(userFeature !=null && itemFeature!=null){
					final String itemFeatureConverted = Main.convert(itemFeature,Main.MAX_USER_FEATURE_INDEX);
					result.append(interactionType).append(" ").append(userFeature).append(" ").append(itemFeatureConverted).append("\n");
					bw.write(result.toString());
					result = new StringBuilder();
					lineNumber++;
				}

			}
			bw.close();
			for (Iterator iterator = duplicateItems.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				//itemLog.info(string);
			}
			for (Iterator iterator = duplicateUsers.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				//userLog.info(string);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}