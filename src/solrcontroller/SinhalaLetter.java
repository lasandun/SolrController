package solrcontroller;

/**
 *
 * @author lahiru
 */
public class SinhalaLetter {
    
    private boolean visargaya;
    private int sinhalaLetter;
    private int sinhalaVowelSign;
    private String searchLetter;
    
    public SinhalaLetter(String searchLetter) {
        this.searchLetter = searchLetter;
    }
    
    public boolean isSearchLetter() {
        return (searchLetter != null);
    }
    
    public String getSearchLetter() {
        return searchLetter;
    }

    public SinhalaLetter(int sinhalaLetter) {
        visargaya = false;
        sinhalaVowelSign = 0;
        this.sinhalaLetter = sinhalaLetter + 1;
        searchLetter = null;
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
