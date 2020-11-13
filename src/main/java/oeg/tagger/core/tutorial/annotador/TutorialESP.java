package oeg.tagger.core.tutorial.annotador;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

/**
 * Tutorial of the Annotador tagger Tagging of a sentence with detailed output
 * in the stdout
 *
 * @author mnavas
 */
public class TutorialESP {

    public static void main(String[] args) throws IOException {
        PrintWriter out;

        out = new PrintWriter(System.out);

        // Usamos las opciones de espanol de CoreNLP
        Properties properties = StringUtils.argsToProperties(new String[]{"-props", "StanfordCoreNLP-spanish.properties"});

        // Path de los modelos de IXA para el lematizador y el pos
        String posModel = "../CoreNLPtest/src/main/resources/ixa-pipes/morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin";
        String lemmaModel = "../CoreNLPtest/src/main/resources/ixa-pipes/morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin";

        properties.setProperty("annotators", "tokenize,ssplit,spanish,readability");
//      Este es el pipeline que tenia yo, por si en algun momento lo quereis expandir:
//      properties.setProperty("annotators", "tokenize,ssplit,spanish,readability,ner,tokensregexdemo");

//      Pasamos los paths de los modelos de IXA al pipeline
        properties.setProperty("spanish.posModel", posModel);
        properties.setProperty("spanish.lemmaModel", lemmaModel);
        properties.setProperty("readability.language", "es");

        // Estos seran los anotadores de IXA
        properties.setProperty("customAnnotatorClass.spanish", "oeg.tagger.core.time.aidCoreNLP.BasicAnnotator");
        properties.setProperty("customAnnotatorClass.readability", "eu.fbk.dh.tint.readability.ReadabilityAnnotator");

        // Creamos el pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(properties);


        // Un texto de ejemplo. En vuestro caso, lo recibiriais en la request.
        String txt = "Ella come manzanas.";

        // Lo anotamos
        Annotation annotation = new Annotation(txt);
        pipeline.annotate(annotation);


        
        
        // Vemos las anotaciones
        out.println();
        out.println("The top level annotation");
        out.println(annotation.toShorterString());

        // Miramos las anotaciones de los tokens uno a uno (iterando por frases) y los imprimimos.
        // En vuestro caso, lo devolveriais en el formato que os interese
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {

                // Print out words, lemma, ne, and normalized ne
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
           
        out.println("token: " + "word="+word + "\t lemma=" + lemma + "\t pos=" + pos + "\t\t" + token.value());
        out.flush();
            }
        }
        out.flush();
    }

}
