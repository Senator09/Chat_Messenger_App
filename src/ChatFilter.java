import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * [Add your documentation here]
 *
 * @author your name and section
 * @version date
 */


public class ChatFilter {

//    public static void main(String [] args) throws IOException {
//        ChatFilter newFilter = new ChatFilter("/Users/alyekaba/Downloads/Project5/src/badwords");
//        System.out.println(newFilter.filter("Iu is a sleepy sch"));
//    }
    ArrayList<String> badwords = new ArrayList<>();
    public ChatFilter(String badWordsFileName) throws IOException {
        try {
            File file = new File(badWordsFileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;

            while ((st = br.readLine()) != null) {
                badwords.add(st);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public String filter(String msg) {
        for (int i = 0; i < badwords.size(); i++) {
            if (msg.toLowerCase().contains(badwords.get(i).toLowerCase())) {
                msg = msg.toLowerCase().replaceAll(badwords.get(i).toLowerCase(), wordLength(badwords.get(i).length()));
            }
        }
        return msg;
    }

    public String wordLength(int lengthWord) {
        String wordLength = "";
        for (int i = 0; i < lengthWord; i++) {
            wordLength += "*";
        }

        return wordLength;
    }
}
