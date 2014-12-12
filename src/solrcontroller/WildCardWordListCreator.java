package solrcontroller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.XMLOutputFactory2;
/**
 *
 * @author lahiru
 */
public class WildCardWordListCreator {

    OMFactory factory;
    OMElement root;
    OMElement add;
    int fileCount;
    
    public WildCardWordListCreator() {
        fileCount = 0;
        initDoc();
    }
    
    private void initDoc() {
        factory = OMAbstractFactory.getOMFactory();
        root = factory.createOMElement(new QName("root"));
        add = factory.createOMElement(new QName("add"));
    }
    
    public static void createWordFile() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/home/lahiru/Desktop/1.xml"));
        String line;
        FileWriter writer = new FileWriter("/home/lahiru/Desktop/words.txt");
        while((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line, "[\u002E\u003F\u0021\u0020]");
            while(st.hasMoreElements()) {
                writer.write((String)st.nextElement());
                writer.write("\n");
            }
        }
        writer.close();
        br.close();
    }
    
    public void parseToXMLs() throws IOException {
        int count = 0;
        
        BufferedReader br = new BufferedReader(new FileReader("/home/lahiru/Desktop/word.csv"));
        String line;
        //SinhalaDocFilter filter = new SinhalaDocFilter(0.67);
        br.readLine();
        while((line = br.readLine()) != null) {
            
            String parts[] = line.split("\u002C");
            if(parts.length != 3) continue;

            String id = parts[0];
            String word = parts[1];
            String freq = parts[2];

            word = word.replaceAll("\"", "");
            word = word.replaceAll(" ", "");
            word = word.replaceAll("”", "");
            word = word.replaceAll("‘", "");
            word = word.replaceAll("'", "");
            word = word.replaceAll("“", "");

            addWord(id, word, freq);
            ++count;
            
            if(count > 100000) {
                try {
                    System.out.println("wrote" + fileCount);
                    writeToFile("/home/lahiru/Desktop/parsed/temp" + fileCount + ".xml");
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(WildCardWordListCreator.class.getName()).log(Level.SEVERE, null, ex);
                } catch (XMLStreamException ex) {
                    Logger.getLogger(WildCardWordListCreator.class.getName()).log(Level.SEVERE, null, ex);
                }
                fileCount++;
                count = 0;
                initDoc();
            }
        }
        if(count != 0) {
            try {
                writeToFile("/home/lahiru/Desktop/parsed/temp" + fileCount + ".xml");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(WildCardWordListCreator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (XMLStreamException ex) {
                Logger.getLogger(WildCardWordListCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        br.close();
    }
    
    void addWord(String id, String word, String freq) {
        OMElement doc = factory.createOMElement(new QName("doc"));
        OMElement idField = factory.createOMElement(new QName("field"));
        idField.addAttribute("name", "id", null);
        idField.setText(id);

        OMElement contentField = factory.createOMElement(new QName("field"));
        contentField.addAttribute("name", "content", null);
        contentField.setText(new QName(word));
        
        doc.addChild(idField);
        doc.addChild(contentField);
        add.addChild(doc);
    }
    
    void writeToFile(String fileName) throws FileNotFoundException, XMLStreamException {
        root.addChild(add);
        OutputStream out = new FileOutputStream(fileName);
        XMLStreamWriter writer = XMLOutputFactory2.newInstance().createXMLStreamWriter(out);
        root.serialize(writer);
        writer.flush();
    }
    
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException, IOException {
        WildCardWordListCreator x = new WildCardWordListCreator();
        x.parseToXMLs();
    }
}
