package solrcontroller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;

/**
 *
 * @author lahiru
 */
public class SolrQueryUtil {
    
    public static int wordFreqency(String word, String catagory, String field) throws XMLStreamException, UnsupportedEncodingException {
        word = Util.encodeURL(word);
        String query = "select?q=" + field + ":" + word + "&fl=,fl:termfreq(" + field + "," + word + ")";
        String result = execQuery(query, catagory);
        
        // calculate the result from the result query
        OMElement documentElement = AXIOMUtil.stringToOM(result);
        OMElement resultDoc = documentElement.getFirstChildWithName(new QName("result"));
        Iterator docElements = resultDoc.getChildElements();
        int count = 0;
        while(docElements.hasNext()) {
            OMElement intElement = ((OMElement) docElements.next()).getFirstChildWithName(new QName("int"));
            count += Integer.parseInt(intElement.getText());
        }
        return count;
    }
    
    public static int wordFrequencyInPeriod(String word, String catagory, String field,
            String startDate, String endDate) throws UnsupportedEncodingException, XMLStreamException {
        
        word = Util.encodeURL(word);
        String query = "select?q=" + field + ":" + word + "&date:[" + startDate + "%20TO%20" + endDate 
                + "]&fl=,fl:termfreq(" + field + "," + word + ")";
        String result = execQuery(query, catagory);
        
        // calculate the result from the result query
        OMElement documentElement = AXIOMUtil.stringToOM(result);
        OMElement resultDoc = documentElement.getFirstChildWithName(new QName("result"));
        Iterator docElements = resultDoc.getChildElements();
        int count = 0;
        while(docElements.hasNext()) {
            OMElement intElement = ((OMElement) docElements.next()).getFirstChildWithName(new QName("int"));
            count += Integer.parseInt(intElement.getText());
        }
        return count;
    }
    
    public static String execQuery(String q, String catagory) {
        String content = "";
        try {
            // create connection and query to Solr Server
            String serverUrl = Util.refactorDirPath(SysProperty.getProperty("solrServerURL"));
            String queryString  = serverUrl + "solr/" + catagory + "/" + q;
            URL query = new URL(queryString);
            System.out.println("sending query:\n" + queryString);
            URLConnection connection = query.openConnection();
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            // read the result to a string
            while ((line = inputStream.readLine()) != null) {
                content += line;
            }
            inputStream.close();
        }  catch(IOException e) {
            Logger.getLogger(SolrQueryUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return content;
    }
    
    public static void main(String[] args) throws XMLStreamException, UnsupportedEncodingException {
        System.out.println(wordFrequencyInPeriod("ද", "collection1", "content", "2012-01-01T23:59:59.999Z", "2014-01-01T23:59:59.999Z"));
    }
}
