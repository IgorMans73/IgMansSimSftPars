//package com.company;

import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.parser.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        MyBD myBD = new MyBD();

	    MyStream myStream = new MyStream(args);

	    MyParser myParser = new MyParser(DTD.getDTD("html"));
	    myParser.parse(myStream.getInputStreamReader());

	    MyReport myReport = new MyReport();
	    myReport.report();
    }
}
class MyStream{
    private static Logger log = Logger.getLogger(MyStream.class.getName());

    private InputStream inputStream;
    private InputStreamReader inputStreamReader;

    public MyStream(String[] args){
        String pathTXT = "";
        URL url;
        if (args.length == 0) {
            pathTXT ="https://www.simbirsoft.com/";
        }else {
            pathTXT = args[0];
        }
        try {
            url = new URL(pathTXT);
            setInputStreamReader(url);
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "MalformedURLException: ",e);
        }
    }
    private void setInputStreamReader(URL url){
        try {
            inputStream = url.openStream();
        } catch (IOException e) {
            log.log(Level.SEVERE, "IOException: ",e);
        }
        inputStreamReader = new InputStreamReader(inputStream);
        log.info("connecting to the site "
                .concat(url.toString())
                .concat(" took place")
        );
    }
    public InputStreamReader getInputStreamReader() {return inputStreamReader;}
}
class MyParser extends Parser {
    public MyParser(DTD dtd) { super(dtd);}
    private MyBD myBD = new MyBD();
    public void handleText(char[] data){
        String str = String.valueOf(data);
        Scanner sc = new Scanner(str).useDelimiter(
            " |- |/|, |: |!|;|\\?|\\'|\\. |[|]|\\(|\\)|\""
        );
        while (sc.hasNext()){
            String s = sc.next();
            if (s.length()<3) continue;

            if (s.charAt(0) == '-' ) s = s.substring(1);
            if (s.charAt(s.length() - 1) == '.') {
                s = s.substring(0, s.length() - 1);
            }
            s = s.toUpperCase();
            myBD.setBD(s);
        }
    }
}
class MyBD{
    static private HashMap<String, Integer> map = new HashMap<String, Integer>();;
    MyBD(){
        //map = new HashMap<String, Integer>();
    }
    public HashMap<String, Integer> getMap(){return map;}
    public void setBD(String key){
        Integer val = map.put(key, 1);
        if (val != null){
            map.put(key,val+1);
        }
    }
}
class MyReport{
    private static Logger log = Logger.getLogger(MyStream.class.getName());
    private MyBD myBD;
    private Map<String, Integer> mySortedMap;
    MyReport() {
        myBD = new MyBD();
        //System.out.println(myBD.getMap());

        mySortedMap = myBD.getMap().entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        log.info("parsing is over, there are "
                .concat(String.valueOf(mySortedMap.size()))
                .concat(" items in the collection")
        );
    }
    public void report(){
        //System.out.println(mySortedMap.toString());
        mySortedMap.forEach((k,v) -> {
            System.out.println(k + " : " + v);
                }
        );
    }
}
