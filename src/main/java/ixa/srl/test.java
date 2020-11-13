/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ixa.srl;

import ixa.kaflib.KAFDocument;
import java.io.BufferedReader;
import java.io.StringReader;

/**
 *
 * @author mnavas
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
					String clientLang = "spa";
					String stdInString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<NAF xml:lang=\"es\" version=\"1\">\n" +
"  <nafHeader />\n" +
"  <text>\n" +
"    <wf id=\"w1\" offset=\"0\" length=\"4\" sent=\"1\" para=\"1\">Ella</wf>\n" +
"    <wf id=\"w2\" offset=\"5\" length=\"4\" sent=\"1\" para=\"1\">come</wf>\n" +
"    <wf id=\"w3\" offset=\"10\" length=\"8\" sent=\"1\" para=\"1\">manzanas</wf>\n" +
"    <wf id=\"w4\" offset=\"18\" length=\"1\" sent=\"1\" para=\"1\">.</wf>\n" +
"  </text>\n" +
"  <terms>\n" +
"    <!--Ella-->\n" +
"    <term id=\"t1\" type=\"close\" lemma=\"él\" pos=\"Q\" morphofeat=\"PP3FS000\">\n" +
"      <span>\n" +
"        <target id=\"w1\" />\n" +
"      </span>\n" +
"    </term>\n" +
"    <!--come-->\n" +
"    <term id=\"t2\" type=\"open\" lemma=\"comer\" pos=\"V\" morphofeat=\"VMIP3S0\">\n" +
"      <span>\n" +
"        <target id=\"w2\" />\n" +
"      </span>\n" +
"    </term>\n" +
"    <!--manzanas-->\n" +
"    <term id=\"t3\" type=\"open\" lemma=\"manzana\" pos=\"N\" morphofeat=\"NCFP000\">\n" +
"      <span>\n" +
"        <target id=\"w3\" />\n" +
"      </span>\n" +
"    </term>\n" +
"    <!--.-->\n" +
"    <term id=\"t4\" type=\"close\" lemma=\".\" pos=\"O\" morphofeat=\"FP\">\n" +
"      <span>\n" +
"        <target id=\"w4\" />\n" +
"      </span>\n" +
"    </term>\n" +
"  </terms>\n" +
"</NAF>";
                                        PreProcessNew preprocess = new PreProcessNew("spa","");
					
					AnnotateNew annotator = new AnnotateNew(preprocess);
					
					
                                        BufferedReader stdInReader = new BufferedReader(new StringReader(stdInString));
					KAFDocument kaf = KAFDocument.createFromStream(stdInReader);

					annotator.SRLToKAF(kaf, clientLang, "");
					
					
					BufferedReader kafReader = null;
					kafReader = new BufferedReader(new StringReader(kaf.toString()));				
					String kafLine = kafReader.readLine();
					while(kafLine != null){
											System.out.println(kafLine);

						kafLine = kafReader.readLine();
					}
                                        
//					dataOutFlow.writeBoolean(true);
				} catch( Exception e ) {
	    	    	System.out.println( e.getMessage() );
	    	    }
			
    }
    
}
