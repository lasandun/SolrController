package solrcontroller;

/**
 *
 * @author lahiru
 */
public class SinhalaDocFilter {
    public static double getSinhalaOnlyRatio(String str) {
        // sinhala unicode range is 0D80–0DFF. (from http://ucsc.cmb.ac.lk/ltrl/publications/uni_sin.pdf )
        int sinhalaLowerBound = 3456;
        int sinhalaUpperBound = 3583;
        int sinhalaCharCount = 0;
        int nonSinhalaCharCount = 0;
        
        for(int i = 0; i < str.length() ; i++) {
           int cp = str.codePointAt(i);
           if(cp >= sinhalaLowerBound && cp <= sinhalaUpperBound) {
               sinhalaCharCount++;
           }
           else {
               nonSinhalaCharCount++;
           }
        }
        
        if(sinhalaCharCount == 0) return 0;
        if(nonSinhalaCharCount == 0) return 1.0;
        return (1.0 * sinhalaCharCount / (sinhalaCharCount + nonSinhalaCharCount));
    }
    
    public static String showUnicodeChar(char c) {
        return Integer.toHexString(c | 0x10000).substring(1);
    }
}
