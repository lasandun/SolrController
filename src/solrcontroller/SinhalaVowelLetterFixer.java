package solrcontroller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

/**
 *
 * @author lahiru
 */
public class SinhalaVowelLetterFixer {
    
    private String fixedText;
    private String lastLetter;
    private String lastVowelSign;
    
    private static final String sinhalaChars[] = {"අ", "ආ", "ඇ", "ඈ", "ඉ", "ඊ", "උ", "ඌ", "ඍ", "ඎ", "ඏ",
                             "ඐ", "එ", "ඒ", "ඓ", "ඔ", "ඕ", "ඖ", "ක", "ඛ", "ග", "ඝ", "ඞ", "ඟ",
                             "ච", "ඡ", "ජ", "ඣ", "ඤ", "ඥ", "ඦ", "ට", "ඨ", "ඩ", "ඪ", "ණ", "ඬ", "ත", "ථ", "ද",
                             "ධ", "න", "ඳ", "ප", "ඵ", "බ", "භ", "ම", "ඹ", "ය", "ර", "ල", 
                             "ව", "ශ", "ෂ", "ස", "හ", "ළ", "ෆ", "ං", "෴", "ඃ" , "\u200d"};
    
    private static final String sinhalaVowelSigns[] = {"්", "ා", "ැ", "ෑ", "ි", "ී", "ු", "ූ", "ෘ", "ෙ", "ේ", "ෛ", "ො", "ෝ",
                              "ෞ", "ෟ", "ෲ", "ෳ" };
    
    private final Hashtable<String, String> vowelSignMap;
    
    // Default - false. Will be enabled for tokenizing for
    // wildcard search using solr
    private boolean appendUnresolvedConsecutiveVowelChars; 

    public SinhalaVowelLetterFixer() {
        fixedText = "";
        lastVowelSign = "";
        lastLetter = "";
        vowelSignMap = new Hashtable<String, String>();
        initVowelSignMap();
        appendUnresolvedConsecutiveVowelChars = true;
    }
    
    private void initVowelSignMap() {
        vowelSignMap.put("ෙ" + "්", "ේ");
        vowelSignMap.put("්" + "ෙ", "ේ");
        
        vowelSignMap.put("ෙ" + "ා", "ො");
        vowelSignMap.put("ා" + "ෙ", "ො");
        
        vowelSignMap.put("ේ" + "ා", "ෝ");
        vowelSignMap.put("ො" + "්", "ෝ");
        
        vowelSignMap.put("ෙෙ", "ෛ");
        
        vowelSignMap.put("ෘෘ", "ෲ");
        
        vowelSignMap.put("ෙ" + "ෟ", "ෞ");
        vowelSignMap.put("ෟ" + "ෙ", "ෞ");
        
        vowelSignMap.put("ි" + "ී", "ී");
        vowelSignMap.put("ී" + "ි", "ී");
        
        
        // duplicating same symbol
        vowelSignMap.put("ේ" + "්", "ේ");
        vowelSignMap.put("ේ" + "ෙ", "ේ");
        
        vowelSignMap.put("ො" + "ා", "ො");
        vowelSignMap.put("ො" + "ෙ", "ො");
        
        vowelSignMap.put("ෝ" + "ා", "ෝ");
        vowelSignMap.put("ෝ" + "්", "ෝ");
        vowelSignMap.put("ෝ" + "ෙ", "ෝ");
        vowelSignMap.put("ෝ" + "ේ", "ෝ");
        vowelSignMap.put("ෝ" + "ො", "ෝ");
        
        vowelSignMap.put("ෞ" + "ෟ", "ෞ");
        vowelSignMap.put("ෞ" + "ෙ", "ෞ");
        
        
        // special cases - may be typing mistakes
        //ො + ෟ
        vowelSignMap.put("ො" + "ෟ", "ෞ");
        vowelSignMap.put("ෟ" + "ො", "ෞ");
    }
    
    private boolean isSinhalaLetter(String c) {
        for(String s : sinhalaChars) {
            if(s.equals(c)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSinhalaVowelSign(String c) {
        for(String s : sinhalaVowelSigns) {
            if(s.equals(c)) {
                return true;
            }
        }
        return false;
    }
    
    private void appendChar(String c) {
        if(c.length() > 1) {
            System.out.println("Char length should be 1 : " + c);
            System.exit(-1);
        }
        
        if(isSinhalaLetter(c)) {
            fixedText += lastLetter + lastVowelSign;
            lastLetter = c;
            lastVowelSign = "";
        }
        else if(isSinhalaVowelSign(c)) {
            if(lastLetter.equals("")) {
                System.out.println("Error : First letter can't be a vowel sign : " + c);
                return;
            }
            if(lastVowelSign.equals("")) {
                lastVowelSign = c;
            }
            else {
                 String fixedVowel = addVoewlSigh(c);
                 if(fixedVowel == null) {
                     if(c.equals(lastVowelSign)) { // consecutive 2 same vowel symbol
                         return;
                     }
                     else {
                         System.out.println("Error : can't fix " + lastVowelSign + " + " + c);
                         if(appendUnresolvedConsecutiveVowelChars) {
                             lastVowelSign += c;
                         }
                         return;
                     }
                 }
                 lastVowelSign = fixedVowel;
            }
        } else {
            fixedText += lastLetter + lastVowelSign + c;
            lastVowelSign = "";
            lastLetter = "";
        }
    }
    
    private String addVoewlSigh(String c) {
        String connected = lastVowelSign + c;
        return vowelSignMap.get(connected);
    }
    
    public void appendText(String str) {
        for(int i = 0; i < str.length(); ++i) {
            String c = str.charAt(i) + "";
            appendChar(c);
        }
        flush();
    }
    
    private void flush() {
        fixedText += lastLetter + lastVowelSign;
        lastLetter = "";
        lastVowelSign = "";
    }
    
    public String getFixedText() {
        flush();
        return fixedText;
    }
    
    public void clear() {
        fixedText = "";
        lastVowelSign = "";
        lastLetter = "";
    }
    
    public void setAppendUnresolvedConsecutiveVowelChars(boolean val) {
        appendUnresolvedConsecutiveVowelChars = val;
    }
    
    public void TestFixer() throws IOException{
        SinhalaVowelLetterFixer fixer = new SinhalaVowelLetterFixer();
        BufferedReader br = new BufferedReader(new FileReader("/home/lahiru/Desktop/word.csv"));
        String line;
        
        while((line = br.readLine()) != null) {
            fixer.appendText(line);
            String fixed = fixer.getFixedText();
            fixer.clear();
            if(!line.equals(fixed)) {
                System.out.println(line);
                Util.printUnicodeElements(line);
                Util.printUnicodeElements(fixed);
                System.out.println("----------------");
            }
        }
        br.close();
    }
    
    public static void main(String[] args) throws IOException {
        String word = "යෝක්‍රෝපෝ";
        SinhalaVowelLetterFixer x = new SinhalaVowelLetterFixer();
        x.TestFixer();
    }
    
}
