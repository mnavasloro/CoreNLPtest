package oeg.tagger.rest;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import ixa.kaflib.KAFDocument;
import ixa.srl.AnnotateNew;
import ixa.srl.PreProcessNew;
import java.io.BufferedReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.List;
import java.util.Properties;
import oeg.tagger.core.time.aidCoreNLP.BasicAnnotator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.ResponseStatus;



@RestController
@Tag(name = "IXA Annotation REST API", description = "Test services implemented about annotation")
public class AnnotationController {
    
    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    PreProcessNew preprocess;
    AnnotateNew annotator;


    @Operation(summary = "Annotates every possible temporal entity", description = "", tags = "annotation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully annotated")
        
        ,@ApiResponse(responseCode = "403", description = "Access denied"),
    @ApiResponse(responseCode = "404", description = "Model not found")
        ,
    @ApiResponse(responseCode = "401", description = "Internal error")
        ,
    @ApiResponse(responseCode = "500", description = "NIF ERROR")})
    @RequestMapping(value = "/annotate/pos", method = RequestMethod.POST, produces = {"text/plain"}, consumes = {"text/plain"})//, "application/xml"})
//    @Async("threadPoolTaskExecutor")
    public ResponseEntity<String> temporalTXT2TXT(
            @Parameter(name = "Text to annotate") @RequestBody String txtinput         ) {

                   try{
                String out = annotate(txtinput);
            return new ResponseEntity(out, HttpStatus.OK);
            } catch(Exception e){
                HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "L500");
            jsonObject.put("message", e.toString());
            String payload = jsonObject.toString();
            return new ResponseEntity(payload, responseHeaders,HttpStatus.BAD_REQUEST);//;
            }//;

    }
    
    
    
    @Operation(summary = "Annotates every possible temporal entity", description = "", tags = "annotation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully annotated")
        
        ,@ApiResponse(responseCode = "403", description = "Access denied"),
    @ApiResponse(responseCode = "404", description = "Model not found")
        ,
    @ApiResponse(responseCode = "401", description = "Internal error")
        ,
    @ApiResponse(responseCode = "500", description = "NIF ERROR")})
    @RequestMapping(value = "/annotate/srl", method = RequestMethod.POST, produces = {"text/plain"}, consumes = {"text/plain"})//, "application/xml"})
//    @Async("threadPoolTaskExecutor")
    public ResponseEntity<String> temporalKAF(
            @Parameter(name = "Text to annotate") @RequestBody String txtinput         ) {

                   try{
                String out = annotate2(txtinput);
            return new ResponseEntity(out, HttpStatus.OK);
            } catch(Exception e){
                HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", "L500");
            jsonObject.put("message", e.toString());
            String payload = jsonObject.toString();
            return new ResponseEntity(payload, responseHeaders,HttpStatus.BAD_REQUEST);//;
            }//;

    }
    
    
    

//    {
//        return "Service up and running by UPM-OEG. Temporal annotations are given";
//    }
    @Operation(summary = "Test method to show info about the deployed version", tags = "internal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK")
        ,@ApiResponse(responseCode = "403", description = "Access denied")
        ,
    @ApiResponse(responseCode = "401", description = "Internal error")})
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    
    public String index() {
        log.debug("This is a debug message");
        log.info("This is an info message");
        log.warn("This is a warn message");
        log.error("This is an error message");
        return "Service up and running by UPM-OEG.";
    }
    
    

    

    @Operation(summary = "Internal operations", description = "Not documented.", tags = "internal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "403", description = "Not authorized"),
        @ApiResponse(responseCode = "500", description = "Internal error")})
    @RequestMapping(value = "/internal", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public String internal(@Parameter(description = "Joker parameter", required = false, schema = @Schema(defaultValue = "valor")) @RequestParam("param") String param) {
        log.debug("This is a debug message");
        log.info("This is an info message");
        log.warn("This is a warn message");
        log.error("This is an error message");
        return "Internal information";
    }



StanfordCoreNLP pipeline;

public String annotate(String txt) {
        String res = "";
        try{
            if(pipeline==null){
           
                
        Properties properties = StringUtils.argsToProperties(new String[]{"-props", "StanfordCoreNLP-spanish.properties"});

        // Path de los modelos de IXA para el lematizador y el pos
        String posModel = "./src/main/resources/ixa-pipes/morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin";
        String lemmaModel = "./src/main/resources/ixa-pipes/morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin";

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
         pipeline = new StanfordCoreNLP(properties);
            }


        // Lo anotamos
        Annotation annotation = new Annotation(txt);
        pipeline.annotate(annotation);



        // Miramos las anotaciones de los tokens uno a uno (iterando por frases) y los imprimimos.
        // En vuestro caso, lo devolveriais en el formato que os interese
        
        JSONObject json = new JSONObject();

        JSONArray array = new JSONArray();
           
                    
                      

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {

                // Print out words, lemma, ne, and normalized ne
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                Integer beginOff = token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
                Integer endOff = token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
                
                JSONObject item = new JSONObject();
                    item.put("beginIndex", beginOff);
                    item.put("endIndex", endOff);
                    item.put("word", word );
                    item.put("lemma", lemma );
                    item.put("pos", pos );
                    
                    array.add(item);
                
          
            }
        }
        
        json.put("text", txt);
        json.put("annotations", array);
        res = json.toString();
        
        } catch (Exception e) {
            
            return "";
        }

        return res;
    }


public String annotate2(String txt) {
        String res = "";
        Properties properties = StringUtils.argsToProperties(new String[]{"-props", "StanfordCoreNLP-spanish.properties"});

        try{
            if(pipeline==null){
           
                
        
        // Path de los modelos de IXA para el lematizador y el pos
        String posModel = "./src/main/resources/ixa-pipes/morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin";
        String lemmaModel = "./src/main/resources/ixa-pipes/morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin";

        properties.setProperty("annotators", "tokenize,ssplit,spanish,readability");
//        properties.setProperty("annotators", "tokenize,ssplit");//,spanish,readability");
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
         pipeline = new StanfordCoreNLP(properties);
            }


        // Lo anotamos
        Annotation annotation = new Annotation(txt);
        
        pipeline.annotate(annotation);
        
        BasicAnnotator ba = new BasicAnnotator("a", properties);
        res = ba.annotateKAF(annotation);
        
        if(preprocess==null){
        preprocess = new PreProcessNew("spa","");
        }
        if(annotator ==null){
	annotator = new AnnotateNew(preprocess);
        }
        
        BufferedReader stdInReader = new BufferedReader(new StringReader(res));
					KAFDocument kaf = KAFDocument.createFromStream(stdInReader);

					annotator.SRLToKAF(kaf, "spa", "");
					
					res =kaf.toString();			
        


//
//        // Miramos las anotaciones de los tokens uno a uno (iterando por frases) y los imprimimos.
//        // En vuestro caso, lo devolveriais en el formato que os interese
//        
//        JSONObject json = new JSONObject();
//
//        JSONArray array = new JSONArray();
//           
//                    
//                      
//
//        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
//        for (CoreMap sentence : sentences) {
//            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//
//                // Print out words, lemma, ne, and normalized ne
//                String word = token.get(CoreAnnotations.TextAnnotation.class);
//                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
//                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                Integer beginOff = token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
//                Integer endOff = token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
//                
//                JSONObject item = new JSONObject();
//                    item.put("beginIndex", beginOff);
//                    item.put("endIndex", endOff);
//                    item.put("word", word );
//                    item.put("lemma", lemma );
//                    item.put("pos", pos );
//                    
//                    array.add(item);
//                
//          
//            }
//        }
//        
//        json.put("text", txt);
//        json.put("annotations", array);
//        res = json.toString();
//        
        } catch (Exception e) {
            
            return "";
        }

        return res;
    }
}
