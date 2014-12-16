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
                             "ව", "ශ", "ෂ", "ස", "හ", "ළ", "ෆ", "෴" };
    
    private static final String sinhalaSubChar[] = {"ං", "ඃ", "්", "ා", "ැ", "ෑ", "ි", "ී", "ු", "ූ", "ෘ", "ෙ", "ේ", "ෛ", "ො", "ෝ",
                              "ෞ", "ෟ", "ෲ", "ෳ" };
    
    private static final int multiplyer = 100;
    private static final int constant = 1;
    public static int acceptedCount = 0;
    public static int rejectedCount = 0;
    
    public static void resetCounters() {
        acceptedCount = 0;
        rejectedCount = 0;
    }
    
    private static int  isASinhalaChar(String c) {
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
    
    private static int  isASinhalaSubChar(String c) {
        if(c.length() > 1) {
            System.out.println("char length should be 1 : " + c);
            System.out.println("Exiting...");
            System.exit(-1);
        }
        for(int i = 0; i < sinhalaSubChar.length; ++i) {
            if(c.equals(sinhalaSubChar[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public static String encode(String str) {
        String parts[] = str.split("");
        LinkedList<Integer> codePoints = new LinkedList<Integer>();
        for(int i = 1; i < parts.length; ++i) {
            String c = parts[i];
            //System.out.println(c);
            int charCodePoint = isASinhalaChar(c);
            int subCharCodePoint;
            
            // special events - these can occur even when the previous letter is a sub character
            if(c.equals("ං")) {
                codePoints.addLast(1);
                continue;
            } else if(c.equals("ඃ")) {
                codePoints.addLast(2);
                continue;
            }
            
            
            if(charCodePoint != -1) {
                codePoints.addLast(charCodePoint * multiplyer);
                //System.out.println("inserting " + (charCodePoint * multiplyer));
            }
            else if((subCharCodePoint = isASinhalaSubChar(c)) != -1) {
                if(codePoints.isEmpty()) {
                    System.out.println("Error -> Invalid word format(may be starting word): " + str);
                    return null;
                }
                int cp = codePoints.pollLast();
                if((cp  % multiplyer) > 0) {
                    System.out.println("Error -> Invalid format(may be 2 consecutive sub-chars): " + str);
                    return null;
                }
                codePoints.addLast(cp + subCharCodePoint + constant);
                //System.out.println("adding subchar : " + subCharCodePoint);
                //System.out.println("final value : " + (cp + subCharCodePoint));
            }
            else {
                System.out.println("Invalid sinhala character(code point) : " + c.codePointAt(0));
                System.out.println("ratio :" +WildCardWordListCreator.getSinhalaOnlyRatio(str));
                System.out.println(str);
                System.out.println("Exiting...");
                System.exit(-1);
            }
        }
        String encoded = "";
        for(Integer cp : codePoints) {
            if(cp > 100) encoded += cp + ".";
            else         encoded += "000" + cp + ".";
        }
        return encoded;
    }
    
    public static String decode(String str) {
        String parts[] = str.split("[\u002E]");
        String decodedString = "";
        for(String s : parts) {
            //System.out.println(s);
            int val = Integer.parseInt(s);
            // handling ං & ඃ
            if(val < 100) {
               if(val == 1) {
                   decodedString += "ං";
                   continue;
               } else if(val == 2) {
                   decodedString += "ඃ";
                   continue;
               }
            }
            
            int charIndex = val / multiplyer;
            int subCharIndex = (val % multiplyer) - constant;
            //System.out.print(sinhalaChars[charIndex]);
            decodedString += sinhalaChars[charIndex];
            if(subCharIndex >= 0) {
                //System.out.print(sinhalaSubChar[subCharIndex]);
                decodedString += sinhalaSubChar[subCharIndex];
            }
            //System.out.println("------------");
        }
        
        return decodedString;
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
    
    public static void printElements(String str) {
        String parts[] = str.split("");
        for(String s : parts) {
            System.out.println(s);
        }
    }
    
    public static void main(String[] args) {
//        SorlWildCardSinhalaWordParser x = new SorlWildCardSinhalaWordParser();
//        String s = x.encode("විවෘතය");
//        System.out.println(s);
//        s = x.decode(s);
//        System.out.println(s);
        
        checkEncodeNDecode("කලාංගයන්");
    }
}
