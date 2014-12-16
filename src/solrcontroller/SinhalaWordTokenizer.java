package solrcontroller;

import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author lahiru
 */
public class SinhalaWordTokenizer {
    
    public LinkedList<String> ignoringCharList;
    
    public final String punctuationDelims[] = {".", ",", "\n", " ", "¸", "‚",
                                    "\"", "/", "-", "|", "\\", "—", "¦",
                                    "”", "‘", "'", "“", "’", "´", "´",
                                    "!", "@", "#", "$", "%", "^", "&", "\\*", "+", "\\-", "£", "\\?", "˜",
                                    "\\(", "\\)", "\\[", "\\]", "{", "}",
                                    ":", ";",
                                    "¹", "Ê",
                                    "\u2013", "\u2022", "\u00a0", "\u2003", "\u200f",
                                    "\ufffd", "\uf020", "\uf073", "\uf06c", "\uf190", // unknown or invalid unicode chars
                                    "", "É", "¯", "Ò", // check these
                                    "\u202a", "\u202c" //  direction control chars (for arabic, starting from right etc)
                                    };
    
    public final String numbers[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    
    public String tokenizerDelims;
    
    private void initIgnoringChars() {
        ignoringCharList.addLast("\u200d");
        ignoringCharList.addLast("\u200c");
        ignoringCharList.addLast("\u0160");
        ignoringCharList.addLast("\u00ad");
        ignoringCharList.addLast("Á");
        ignoringCharList.addLast("À");
        ignoringCharList.addLast("®");
        ignoringCharList.addLast("¡");
        ignoringCharList.addLast("ª");
        ignoringCharList.addLast("º");
        ignoringCharList.addLast("¤");
        ignoringCharList.addLast("¼");
        ignoringCharList.addLast("¾");
        ignoringCharList.addLast("Ó");
        ignoringCharList.addLast("ø");
        ignoringCharList.addLast("½");
        ignoringCharList.addLast("ˆ");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("¢");
        ignoringCharList.addLast("ÿ");
        ignoringCharList.addLast("·");
        ignoringCharList.addLast("í");
        ignoringCharList.addLast("Ω");
        ignoringCharList.addLast("°");
        ignoringCharList.addLast("×");
        ignoringCharList.addLast("µ");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("~");
        ignoringCharList.addLast("ƒ");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("ë");
        ignoringCharList.addLast("Î");
        ignoringCharList.addLast("‰");
        ignoringCharList.addLast("»");
        ignoringCharList.addLast("«");
        ignoringCharList.addLast("à");
        ignoringCharList.addLast("«");
        ignoringCharList.addLast("·");
        ignoringCharList.addLast("¨");
        ignoringCharList.addLast("…");
        ignoringCharList.addLast("⋆");
        ignoringCharList.addLast("›");
        ignoringCharList.addLast("¥");
        ignoringCharList.addLast("⋆");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("˝");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("◊");
        ignoringCharList.addLast("\u200b");
        ignoringCharList.addLast("\ufeff");
        ignoringCharList.addLast("Ł");
        ignoringCharList.addLast("\u0088");
        ignoringCharList.addLast("\uf086");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("ê");
        ignoringCharList.addLast("Õ");
        ignoringCharList.addLast("Ä");
        ignoringCharList.addLast("á");
        ignoringCharList.addLast("Ñ");
        ignoringCharList.addLast("Í");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("Ñ");
        ignoringCharList.addLast("ç");
        ignoringCharList.addLast("Æ");
        ignoringCharList.addLast("ô");
        ignoringCharList.addLast("Ž");
        ignoringCharList.addLast("€");
        ignoringCharList.addLast("§");
        ignoringCharList.addLast("Æ");
        ignoringCharList.addLast("÷");
        ignoringCharList.addLast("é");
        ignoringCharList.addLast("¯");
        ignoringCharList.addLast("é");
        ignoringCharList.addLast("æ");
        ignoringCharList.addLast("î");
        ignoringCharList.addLast("ï");
        ignoringCharList.addLast("ä");
        ignoringCharList.addLast("Ô");
        ignoringCharList.addLast("õ");
        ignoringCharList.addLast("È");
        ignoringCharList.addLast("Ý");
        ignoringCharList.addLast("ß");
        ignoringCharList.addLast("õ");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("ù");
        ignoringCharList.addLast("å");
        ignoringCharList.addLast("Ø");
        ignoringCharList.addLast("Œ");
        ignoringCharList.addLast("Ô");
        ignoringCharList.addLast("Ü");
        ignoringCharList.addLast("");
        ignoringCharList.addLast("Ö");
        ignoringCharList.addLast("Û");
        ignoringCharList.addLast("Ï");
        ignoringCharList.addLast("ñ");
        ignoringCharList.addLast("ý");
        ignoringCharList.addLast("œ");
    }
    
    public SinhalaWordTokenizer() {
        ignoringCharList = new LinkedList<String>();
        initIgnoringChars();
                
        String tmp = "[";
        for(String s : punctuationDelims) {
            tmp += s;
        }
        for(String s : numbers) {
            tmp += s;
        }
        tmp += "]";
        tokenizerDelims = tmp;
    }
    
    private boolean isASinhalaWord(String s) {
       if(s.length() != 1) return true;
       int sinhalaLowerBound = 3456;
       int sinhalaUpperBound = 3583;

       int cp = s.codePointAt(0);
       if(cp >= sinhalaLowerBound && cp <= sinhalaUpperBound) {
           return true;
       }
       return false;
    }
    
    LinkedList<String> splitPhrase(String str) {
        String parts[] = str.split(tokenizerDelims);
        LinkedList<String> list = new LinkedList<String>();
        for(String part : parts) {
            // if p is a symbol
            if(part.length() == 1 && !isASinhalaWord(part)) {
                continue;
            }
            
            // if no sinhala chars are present
            if(Util.getSinhalaOnlyRatio(part) == 0) {
                continue;
            }
            
            // remove special chars at the middle of the words
            for(String ignoringChar : ignoringCharList) {
                if(part.contains(ignoringChar)) {
                    part = part.replaceAll(ignoringChar, "");
                }
            }
            
            // add accpted words to list
            if(!part.equals("")) {
                list.addLast(part);
            }
        }
        return list;
    }
    
    public static void main(String[] args) throws IOException {
        WildCardWordListCreator x = new WildCardWordListCreator();
        WildCardWordListCreator.createWordFile();
    }
}
