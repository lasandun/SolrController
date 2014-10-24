package solrcontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lahiru
 */
public class SolrParseNUploadUtil {
    
    public static void uploadFiles(String filePath, String core) throws IOException {
        LinkedList<String> xmlFileList = Util.getXMLFileList(filePath);
        for(String xmlFile : xmlFileList) {
            System.out.println("uploading file:" + xmlFile);
            String shPath = Util.refactorDirPath(SysProperty.getProperty("solrPostshPath"));
            String command = "sh " + shPath + core + ".sh " + xmlFile;
            System.out.println(command);
            Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
            InputStream solrInputStream = p.getInputStream();
            BufferedReader solrStreamReader = new BufferedReader(new InputStreamReader(solrInputStream));
            String line = "";
            while ((line = solrStreamReader.readLine ()) != null) {
                if(line.contains("WARNING")){
                    System.out.println("--------------------");
                    System.out.println("Error at uploading file: " + xmlFile);
                    System.out.println("--------------------");
                    continue;
                }
                //System.out.println(line);
            }
        }
    }
    
    public static void uploadOneXMLFile(String filePath, String core) throws IOException {
        String shPath = Util.refactorDirPath(SysProperty.getProperty("solrPostshPath"));
        String command = "sh " + shPath + core + ".sh " + filePath;
        System.out.println(command);
        Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
        InputStream solrInputStream = p.getInputStream();
        BufferedReader solrStreamReader = new BufferedReader(new InputStreamReader(solrInputStream));
        String line = "";
        while ((line = solrStreamReader.readLine ()) != null) {
            if(line.contains("WARNING")){
                System.out.println("--------------------");
                System.out.println("Error at uploading file: " + filePath);
                System.out.println("--------------------");
                continue;
            }
            //System.out.println(line);
        }
    }
    
    public static void parseXMLFile(String filePath, String destinationFilePath, String source) {
        String tempDir = Util.refactorDirPath(SysProperty.getProperty("tempDataPath"));
        XMLParser x = new XMLParser(filePath, tempDir, source);
        x.parseOneXMLFile();
        
    }
    
    public static void parseNUploadXMLs(String xmlPath, String core, String source) {
        String tempDir = Util.refactorDirPath(SysProperty.getProperty("tempDataPath"));
        try {
            // create temp directory if not exist
            Util.runCommand("mkdir -p " + tempDir);
            // clear temp directory
            Util.runCommand("rm " + tempDir + "*");
        } catch (IOException ex) {
            System.out.println("Error while accesssing temporary directory.");
            Logger.getLogger(SolrController.class.getName()).log(Level.SEVERE, null, ex);
        }                

        // parse xml files
        XMLParser parser = new XMLParser(xmlPath, tempDir, source);
        parser.parseXMLFiles();

        // upload files
        try {
            uploadFiles(tempDir, core);
        } catch (IOException ex) {
            Logger.getLogger(SolrController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            // remove temp directory
            Util.runCommand("rm -r " + tempDir);
        } catch (IOException ex) {
            Logger.getLogger(SolrController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // parse and upload one file
    public static void parseNUploadOneXMLFile(String xmlPath, String core, String source) {
        String tempDir = Util.refactorDirPath(SysProperty.getProperty("tempDataPath"));
        try {
            // create temp directory if not exist
            Util.runCommand("mkdir -p " + tempDir);
            // clear temp directory
            Util.runCommand("rm " + tempDir + "*");
        } catch (IOException ex) {
            System.out.println("Error while accesssing temporary directory.");
            Logger.getLogger(SolrController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // parse xml files
        XMLParser parser = new XMLParser(xmlPath, tempDir, source);
        parser.parseOneXMLFile();
        
        File f = new File(xmlPath);
        String outFilePath = Util.refactorDirPath(SysProperty.getProperty("tempDataPath")) + f.getName();
        // upload files
        System.out.println("outpath :" + outFilePath);
        try {
            //uploadFiles(tempDir, core);
            uploadOneXMLFile(outFilePath, core);
        } catch (IOException ex) {
            Logger.getLogger(SolrController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            // remove temp directory
            Util.runCommand("rm -r " + tempDir);
        } catch (IOException ex) {
            Logger.getLogger(SolrController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static boolean xmlFileExistInCollection() {
        return false;
    }
    
}
