/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package solrcontroller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lahiru
 */
public class SinhalaDocFilter {
    
    private HashSet<String> ignoringChars;
    final double ACCEPTANCE_RATIO;
    boolean debug = false;
    
    public SinhalaDocFilter(double acceptenceRatio) {
        ACCEPTANCE_RATIO = acceptenceRatio;
        ignoringChars = new HashSet<String>();
        
        // check http://unicode-table.com/en/#control-character
        ignoringChars.add("\u0020"); // space
        ignoringChars.add("\u002C"); // ,
        
        ignoringChars.add("\u007b"); // {
        ignoringChars.add("\u007c"); // |
        ignoringChars.add("\u007d"); // }
        ignoringChars.add("\u007e"); // ~
    }
    
    public String getAcceptedSentences(String doc) {
        String sentences[] = doc.split("[\u002E\u003F\u0021]");
        String acceptedSentences = "";
        String rejectedSentences = "";
        for(String sentence : sentences) {
            double ratio = checkString(sentence);
            if(debug) System.out.println("ratio of sentence: " + ratio);
            if(ratio >= ACCEPTANCE_RATIO) {
                acceptedSentences += sentence + ".";
            }
            else {
                rejectedSentences += sentence + ".";
            }
        }
        if(debug) {
            System.out.println("accepted : -------------------------");
            System.out.println(acceptedSentences);
            System.out.println("rejected : -------------------------");
            System.out.println(rejectedSentences);
            System.out.println("------------------------------------");
        }
        
        return acceptedSentences;
    }

    public double checkString(String str) {
        // sinhala unicode range is 0D80–0DFF. (from http://ucsc.cmb.ac.lk/ltrl/publications/uni_sin.pdf )
        int sinhalaLowerBound = 3456;
        int sinhalaUpperBound = 3583;
        int sinhalaCharCount = 0;
        int nonSinhalaCharCount = 0;
        
        for(int i = 0; i < str.length() ; i++) {
           int cp = str.codePointAt(i);
           if(isIgnoringChar(str.charAt(i) + "")) {
               // ignoring chars
               if(debug) System.out.println("ignoring char: " + str.charAt(i));
               continue;
           }
           else if((cp >= 0 && cp <= 31)) {
               // commands
               continue;
           }
           else if(cp >= 48 && cp <= 57) {
               // numbers (0 - 9)
               sinhalaCharCount++;
           }
           else if(cp >= 33 && cp <= 64) {
               // other symbols - do this check after checking for numbers
               continue;
           }
           else if(cp >= sinhalaLowerBound && cp <= sinhalaUpperBound) {
               // sinhala
               if(debug) System.out.println("sinhala character: " + str.charAt(i));
               sinhalaCharCount++;
           }
           else {
               // other
               if(debug) System.out.println("non sinhala character: " + str.charAt(i));
               nonSinhalaCharCount++;
           }
        }
        return (1.0 * sinhalaCharCount / nonSinhalaCharCount);
    }
    
    boolean isIgnoringChar(String character) {
        if(ignoringChars.contains(character)) {
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) {
        SinhalaDocFilter x = new SinhalaDocFilter(0.67);
        
        //double ratio = x.checkString("දෙරණ Dream Star හි.");
        //System.out.println("ratio: " + ratio);
        
        
        String s = "";
        String content = "";
        try {
            //String accepted = x.getAcceptedSentences("අභාවප්‍රාප්ත ප්‍රවීණ රංගන ශිල්පිනී මර්සි එදිරිසිංහ. "
            //        + "දෙරණ Dream Star හි. 2008 වසරේදී ත්‍රස්තවාදීන්ගේ බෝම්බ ප්‍රහාරයක");
            
            BufferedReader br = new BufferedReader(new FileReader("/home/lahiru/Desktop/a.txt"));
            while( (s = br.readLine()) != null) {
                content += s;
            }
        } catch (IOException ex) {
            Logger.getLogger(SinhalaDocFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(content);
        
        double accepted = x.checkString(content);
        System.out.println(accepted);
    }
    
}
