import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static final String VK_KEY = "1ccc8bad54a0d678f294539e448438e1d5d4d9b00773d2bf8defca61f50d86b4e99f16689efc92655d049";

    public static void main(String[] args) {

        Group group = new Group(165543634, VK_KEY);
        group.onSimpleTextMessage(message -> {
            List<String> infoList = new ArrayList<>();
            String info = "Город" + " &#128308; " + "Заболевшие" + " &#128309; " + "Выздровевшие" + " &#9899; " + "Умершие";
            info += "\n";
            int i = 1;
            for(Statistics statistics : getInfo()){
                info += "\n";
                info += statistics.getLocation() + " &#128308; " +  statistics.getStick() + " &#128309; " + statistics.getHealed() + " &#9899; " +  statistics.getDie();
                i++;
                if (i == 20){
                    infoList.add(info);
                    info = "";
                    i=1;
                }
            }
            infoList.add(info);
            for(String infoMessage : infoList) {
                new Message()
                        .from(group)
                        .to(message.authorId())
                        .text(infoMessage)
                        .send();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected static List<Statistics> getInfo(){
        JSONParser parser = new JSONParser();
        List<Statistics> statisticsList = new ArrayList<>();

        try {
            URL oracle = new URL("https://virusinfo.herokuapp.com/info"); // URL to Parse
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                JSONArray a = (JSONArray) parser.parse(inputLine);
                // Loop through each item
                for (Object o : a) {
                    Statistics statistics = new Statistics();
                    JSONObject jsonObject = (JSONObject) o;

                    statistics.setLocation((String)jsonObject.get("location"));
                    statistics.setHealed((String)jsonObject.get("healed"));
                    statistics.setStick((String)jsonObject.get("stick"));
                    statistics.setDie(((String)jsonObject.get("die")));

                    statisticsList.add(statistics);
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return statisticsList;
    }
}
