package solrcontroller;

import java.util.LinkedList;

/**
 *
 * @author lahiru
 */
public class SolrWildCardSinhalaWordParser {
    
    private static final String sinhalaChars[] = {"අ", "ආ", "ඇ", "ඈ", "ඉ", "ඊ", "උ", "ඌ", "ඍ", "ඎ", "ඏ",
                             "ඐ", "එ", "ඒ", "ඓ", "ඔ", "ඕ", "ඖ", "ක", "ඛ", "ග", "ඝ", "ඞ", "ඟ",
                             "ච", "ඡ", "ජ", "ඣ", "ඤ", "ඥ", "ඦ", "ට", "ඨ", "ඩ", "ඪ", "ණ", "ඬ", "ත", "ථ", "ද",
                             "ධ", "න", "ඳ", "ප", "ඵ", "බ", "භ", "ම", "ඹ", "ය", "ර", "ල", 
                             "ව", "ශ", "ෂ", "ස", "හ", "ළ", "ෆ", "ං" };
    
    private static final String sinhalaVowelSigns[] = {"්", "ා", "ැ", "ෑ", "ි", "ී", "ු", "ූ", "ෘ", "ෙ", "ේ", "ෛ", "ො", "ෝ",
                              "ෞ", "ෟ", "ෲ", "ෳ", "෴" };
    
    private static final String visargayaSign = "ඃ";
    
    //private static final int multiplier = 100;
    //private static final int addition = 1;
    public static int acceptedCount = 0;
    public static int rejectedCount = 0;
    
    public static void resetCounters() {
        acceptedCount = 0;
        rejectedCount = 0;
    }
    
    private static int  isASinhalaLetter(String c) {
        if(c.length() > 1) {
            System.out.println("char length should be 1 : " + c);
            System.exit(-1);
        }
        for(int i = 0; i < sinhalaChars.length; ++i) {
            if(c.equals(sinhalaChars[i])) {
                return i;
            }
        }
        return -1;
    }
    
    private static int  isASinhalaVowelLetter(String c) {
        if(c.length() > 1) {
            System.out.println("char length should be 1 : " + c);
            System.out.println("Exiting...");
            System.exit(-1);
        }
        for(int i = 0; i < sinhalaVowelSigns.length; ++i) {
            if(c.equals(sinhalaVowelSigns[i])) {
                return i;
            }
        }
        return -1;
    }
    
    private static boolean  isSinhalaVisargayaLetter(String c) {
        if(c.length() > 1) {
            System.out.println("char length should be 1 : " + c);
            System.out.println("Exiting...");
            System.exit(-1);
        }
        if(c.equals(visargayaSign)) return true;
        return false;
    }
    
    public static String encode(String str) {
        String parts[] = str.split("");
        LinkedList<SinhalaLetter> letterList = new LinkedList<SinhalaLetter>();
        
        for(int i = 1; i < parts.length; ++i) {
            String c = parts[i];
            
            int letter = isASinhalaLetter(c);
            if(letter >= 0) {
                letterList.addLast(new SinhalaLetter(letter));
                continue;
            }
            
            int vowelLetter = isASinhalaVowelLetter(c);
            if(vowelLetter >= 0) {
                SinhalaLetter last = letterList.getLast();
                last.setVowel(vowelLetter);
                continue;
            }
            
            else if(isSinhalaVisargayaLetter(c)) {
                SinhalaLetter last = letterList.getLast();
                last.setVisargayaSign(true);
                continue;
            }
            
            else {
                System.out.println("Error char :" + c);
                System.exit(-1);
            }
        }
        
        String encoded = "";
        for(SinhalaLetter letter : letterList) {
            encoded += letter.getValue() + ".";
        }
        return encoded;
    }
    
    public static String decode(String str) {
        String parts[] = str.split("\\.");
        String decoded = "";
        for(String s : parts) {
            boolean visargaya;
            int sinhalaLetter;
            int sinhalaVowelSign;
            int val = Integer.parseInt(s);
            
            sinhalaLetter = val / 1000;
            val = val % 1000;
            decoded += sinhalaChars[sinhalaLetter - 1];
            
            sinhalaVowelSign = val /  10;
            if(sinhalaVowelSign > 0) {
                decoded += sinhalaVowelSigns[sinhalaVowelSign - 1];
            }
            val = val % 10;
            
            if(val > 0) decoded += visargayaSign;
        }
        
        return decoded;
    }
    
    public static void checkEncodeNDecode(String s) {
        String encoded = encode(s);
        if(encoded == null) {
            ++rejectedCount;
            System.out.println("Error -> before: " + s + "      encoded: " + encoded);
            return;
        }
        String decoded = decode(encoded);
        if(!s.equals(decoded)) {
            System.out.println("Error -> before: " + s + "      encoded: " + encoded + "      decoded: " + decoded);
            ++rejectedCount;
        } else {
            ++acceptedCount;
        }
    }
    
    public static void main(String[] args) {
        String str = "විෙ";
        String encoded = encode(str);
        System.out.println(encoded);
        String decoded = decode(encoded);
        System.out.println(decoded);
        System.out.println(str.equals(decoded));
    }
}

class SinhalaLetter {
    private boolean visargaya;
    private int sinhalaLetter;
    private int sinhalaVowelSign;

    public SinhalaLetter(int sinhalaLetter) {
        visargaya = false;
        sinhalaVowelSign = 0;
        this.sinhalaLetter = sinhalaLetter + 1;
    }
    
    public void setVowel(int sinhalaVowelSignIndex) {
        this.sinhalaVowelSign = sinhalaVowelSignIndex + 1;
    }
    
    public void setVisargayaSign(boolean visargaya) {
        this.visargaya = visargaya;
    }
    
    public String getValue() {
        String val = "";
        int visargayaVal = visargaya ? 1 : 0;
        val = String.format("%02d", sinhalaLetter) + String.format("%02d", sinhalaVowelSign) + visargayaVal;
        return val;
    }
    
}
