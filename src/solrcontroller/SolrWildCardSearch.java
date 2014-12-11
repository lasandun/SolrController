/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package solrcontroller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

/**
 *
 * @author lahiru
 */
public class SolrWildCardSearch {
    
    String serverUrl;

    public SolrWildCardSearch() {
        serverUrl = SysProperty.getProperty("solrServerURL");
    }
    
    int wildcardSearch(String word, String collection) {
        String query = "select?q=content_permuterm:" + encordeWildcardSyntaxToTURL(word) + "&fl=id&rows=100000";
        int time = execQuery(query, collection);
        //System.out.println(time + "   " + query);
        return time;
    }
    
    void searchAllWildcards() {
        String collection = "collection1";
//        String words[] = {"*", "*ම*", "මහින්?", "මහින්*", "??න්ද", "*න්ද", "ම*ද"};
        String words[] = {"ම?"};
        System.out.println("-----------------------------");
        for(String word : words) {
            System.out.println("###" + word);
            float time = (float) wildcardSearch(word, collection) / 1000;
            System.out.println(time);
        }
        System.out.println("-----------------------------");
    }
    
    int execQuery(String q, String collection) {
        long time = -1;
        try {
            // create connection and query to Solr Server
            URL query = new URL(serverUrl + "solr/" + collection + "/" + q);
            //System.out.println(serverUrl + "solr/" + collection + "/" + q);
            time = System.nanoTime();
            URLConnection connection = query.openConnection();
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            String content = "";
            // read the result to a string
            while ((line = inputStream.readLine()) != null) {
                content += line;
            }
            inputStream.close();
            time = System.nanoTime() - time;
            // read the query time from the xml file
            OMElement documentElement = AXIOMUtil.stringToOM(content);
            OMElement resultDoc = documentElement.getFirstChildWithName(new QName("result"));
            Iterator childElem = resultDoc.getChildElements();
            int count = 0;
            while(childElem.hasNext()) {
                childElem.next();
                ++count;
            }
            System.out.println("count = " + count);
        } catch (XMLStreamException ex) {
            Logger.getLogger(SolrWildCardSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch(MalformedURLException ex) {
            Logger.getLogger(SolrWildCardSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch(IOException ex) {
            Logger.getLogger(SolrWildCardSearch.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return (int)(time / 1000000);
    }
    
    String encordeWildcardSyntaxToTURL(String word) {
        String parts[] = word.split("\\?");
        if(parts.length == 1) {
            if(parts[0].length() == word.length()) {
                try {
                    return URLEncoder.encode(parts[0], "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(SolrWildCardSearch.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
                try {
                    return (URLEncoder.encode(parts[0], "UTF-8") + "?");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(SolrWildCardSearch.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        }
        
        String converted = "";
        for(int i = 0; i < parts.length; ++i) {
            try {
                converted += URLEncoder.encode(parts[i], "UTF-8") + "?";
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SolrWildCardSearch.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        converted = converted.substring(0, converted.length() - 1);
        return converted;
    }
    
    
    public static void main(String[] args) throws Exception {
        
        SolrWildCardSearch x = new SolrWildCardSearch();
        x.searchAllWildcards();
        //System.out.println();
        
    }
}

