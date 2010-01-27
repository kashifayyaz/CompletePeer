



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC_11;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author kashif
 */
public class RDFHandeling {

   public static void createRDF(Annotation ann, LinkedList queryTerm) throws SQLException{
        String[] ids = {"1","2","3","4","5"};
        String fileName = null;
        Model model = ModelFactory.createDefaultModel();
        model = ModelFactory.createDefaultModel();

             Blob testblob = ann.getDigitalSignatures();
             String cnvDS = HibernateActions.convertDStoBase64(testblob);
             String annAuthor = ann.getAnnotationAuthor();
             fileName = ann.getFileLocation();
       Resource node = model.createResource(ann.getAnnID())
         .addProperty(DC_11.creator, ann.getAnnotationAuthor())
         .addProperty(DC_11.date, ann.getCreationTime().toString())
         .addProperty(DC_11.publisher, cnvDS);

      
       int qtLength = queryTerm.size()/2;
            int j = 0;
            for(int i = 0; i < qtLength; i++){
           Property id = model.createProperty(ids[i]);
           Resource resource = model.createResource()
                   .addProperty(DC_11.description, queryTerm.get(j).toString())
                   .addProperty(DC_11.language, queryTerm.get(j+1).toString());
                   model.add(node,id,resource);
             }
        //model.write( System.out, "N3" );
       
		try{
			FileOutputStream fout=new FileOutputStream(fileName);
			model.write(fout, "N3");
                }catch(IOException e){
				System.out.println("Exception caught"+e.getMessage());
			}
      
    }

   public static LinkedList readRDF(){

        //System.out.println("going to read RDF...........................................");
       String fileName = "C:\\test.n3";
    Model model = ModelFactory.createDefaultModel();
    InputStream in = FileManager.get().open(fileName);
    if (in == null) {
    throw new IllegalArgumentException("File: " + fileName + " not found");
    }
model.read(in, null, "N3");

String uuid = null;
String annotates = null;
String digSig = null;
String timeStamp = null;
String authorEmail = null;
LinkedList queryTerm = new LinkedList();
LinkedList concept = new LinkedList();
LinkedList text = new LinkedList();
int i = 0;
int q = 0;
int t = 0;
int c = 0;
StmtIterator iter = model.listStatements();
LinkedList list = new LinkedList();
while(iter.hasNext()){
    Statement stmt = iter.next();
    Resource subject = stmt.getSubject();
    Property predicate = stmt.getPredicate();
    RDFNode node = stmt.getObject();

if (node instanceof Resource) {
		System.out.println(" Object" + node.toString());
        uuid = subject.getLocalName();
       }
	else {
            //uuid = subject.getLocalName();
            //System.out.println("Local name is "+subject.getLocalName());
//            if(subject.toString().contains("January") && (i==0 )){
//                String check = subject.toString();
//                //System.out.println("Subject is "+ check );
//                int index = check.indexOf("January");
//                uuid = check.substring(index+8);
//                System.out.println("uuid = "+uuid);
//                i++;
//            }
            if(predicate.toString().endsWith("http://purl.org/dc/elements/1.1/source")){
                annotates = node.toString();
                //System.out.println("Annotates = "+ annotates);
            }
            if(predicate.toString().endsWith("http://purl.org/dc/elements/1.1/publisher")){
                digSig = node.toString();
                //System.out.println("Dig Sigs = "+ digSig);
            }
            if(predicate.toString().endsWith("http://purl.org/dc/elements/1.1/date")){
                timeStamp = node.toString();
                //System.out.println("Time Stamp = " + timeStamp);
            }
            if(predicate.toString().endsWith("http://purl.org/dc/elements/1.1/creator")){
                authorEmail = node.toString();
                //System.out.println("Creator = " + authorEmail);
            }
            if(predicate.toString().endsWith("http://purl.org/dc/elements/1.1/title")){
                queryTerm.add(q,node);
                q++;
            }
            if(predicate.toString().endsWith("http://purl.org/dc/elements/1.1/language")){
                text.add(c,node);
                c++;
            }
            if(predicate.toString().endsWith("http://purl.org/dc/elements/1.1/description")){
                concept.add(t, node);
                t++;
            }
       }
    }

   list.add(uuid);
   list.add(authorEmail);
   list.add(timeStamp);
   list.add(annotates);
   list.add(digSig);

        for(int l = 0; l < queryTerm.size(); l++){
            list.add(queryTerm.get(l).toString());
            //System.out.println("Query is = " + queryTerm.get(l).toString());
             list.add(concept.get(l).toString());
            //System.out.println("Concept is = " + concept.get(l).toString());
             list.add(text.get(l).toString());
            //System.out.println("Text is = " + text.get(l).toString());
        }

//   System.out.println("uuid = " + list.get(0));
//   System.out.println("creator = " + list.get(1));
//   System.out.println("timestamp = " + list.get(2));
//   System.out.println("annotates = " + list.get(3));
//   System.out.println("digsig = " + list.get(4));
//   System.out.println("Concept is = " + concept.get(0).toString());
//   System.out.println("text is = " + text.get(0).toString());
   return list;
   }


}
