package solrcontroller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lahiru
 */
public class SolrController {

    public static void main(String[] args) {
        if(args == null) {
            System.exit(0);
        }
        
        if(args.length > 0) {
            
            if(args[0].equals("parse_xml") && args[1] != null && args[2] != null && args[3] != null) {
                XMLParser parser = new XMLParser(args[1], args[2], args[3]);
                parser.parseXMLFiles();
            }
            
            else if(args[0].equals("upload_xml") && args[1] != null && args[2] != null) {
                try {
                    SolrParseNUploadUtil.uploadFiles(args[1], args[2]);
                } catch (IOException ex) {
                    Logger.getLogger(SolrController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            else if(args[0].equals("upload_xml") && args[1] != null) {
                try {
                    SolrParseNUploadUtil.uploadFiles(args[1], "collection1");
                } catch (IOException ex) {
                    Logger.getLogger(SolrController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            else if(args[0].equals("parse_and_upload_from_dir") && args[1] != null && args[2] != null && args[3] != null) {
                SolrParseNUploadUtil.parseNUploadXMLs(args[1], args[2], args[3]);
            }
            
            else if(args[0].equals("parse_and_upload_from_dir") && args[1] != null) {
                SolrParseNUploadUtil.parseNUploadXMLs(args[1], "collection1", "divaina");
            }
            
            else if(args[0].equals("parse_and_upload_one_file") && args[1] != null && args[2] != null && args[3] != null) {
                SolrParseNUploadUtil.parseNUploadOneXMLFile(args[1], args[2], args[3]);
            }
            
            else if(args[0].equals("parse_and_upload_one_file") && args[1] != null) {
                SolrParseNUploadUtil.parseNUploadOneXMLFile(args[1], "collection1", "divaina");
            }
            
            else {
                System.out.println("Unknown operation : " + args[0]);
                System.exit(1);
            }            
        }
        else {
            System.out.println("No operation specified");
            System.exit(1);
        }
    }
}
