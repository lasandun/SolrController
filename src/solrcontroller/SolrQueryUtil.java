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
    
    public static int wrodFreqency(String word, String catagory, String field) throws XMLStreamException, UnsupportedEncodingException {
        word = Util.encodeURL(word);
        String query = "select?q=" + field + ":" + word + "&fl=,fl:termfreq(" + field + "," + word + ")";
        String result = execQuery(query, catagory);
        
        // read the query time from the xml file
        OMElement documentElement = AXIOMUtil.stringToOM(result);
        OMElement resultDoc = documentElement.getFirstChildWithName(new QName("result"));
        OMElement docDoc = resultDoc.getFirstChildWithName(new QName("doc"));
        Iterator childElem = docDoc.getChildElements();
        int count = 0;
        while(childElem.hasNext()) {
            OMElement intElement = (OMElement) childElem.next();
            count += Integer.parseInt(intElement.getText());
        }
        return count;
    }
    
    public static String execQuery(String q, String catagory) {
        String content = "";
        try {
            // create connection and query to Solr Server
            String serverUrl = Util.refactorDirPath(SysProperty.getProperty("solrServerURL"));
            URL query = new URL(serverUrl + catagory + "/" + q);
            System.out.println("sending query:" + serverUrl + catagory + "/" + q);
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
    
    public static int wordFrequencyInPeriod(String word, String catagory, String field,
            String startDate, String endDate) {
        
        
        
        int count = 0;
        return count;
    }
    
    public static void main(String[] args) throws XMLStreamException, UnsupportedEncodingException {
        System.out.println(wrodFreqency("ද", "collection1", "content"));
    }
}
