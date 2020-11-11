package oeg.tagger.main;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

/**
 * Main class of the jar.
 *
 * @author vroddon
 * @author mnavas
 */
public class Main {

    static final Logger logger = Logger.getLogger(Main.class.getName());
    static boolean logs = false;
    static String outpfilename = null;

    public static void main(String[] args) {

        // We do this to avoid the Ixa-Pipes debugging messages...
        PrintStream systemerr = System.err;

        init(args);

        if (args.length != 0) {
            String res = parsear(args);
            if (!res.isEmpty()) {
                System.out.println(res);
            }
        }
        
        System.setErr(systemerr);
    }

    public static void init(String[] args) {
        logs = Arrays.asList(args).contains("-logs");
        initLogger(logs);
        
        
        //Welcome message
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            String welcome = model.getArtifactId() + " " + model.getVersion() + "\n-----------------------------------------------------\n";
            logger.info(welcome);
        } catch (Exception e) {
        }

    }

    public static String parsear(String[] args) {
        ///Response
        StringBuilder res = new StringBuilder();
        CommandLineParser parser = null;
        CommandLine cmd = null;
        try {

            Options options = new Options();
            options.addOption("nologs", false, "OPTION to disables logs");
            options.addOption("logs", false, "OPTION to enable logs");
            options.addOption("text", true, "COMMAND to parse a text");
            options.addOption("f", true, "COMMAND to parse a file");
            options.addOption("outf", true, "COMMAND to save the output to a file"); 
            options.addOption("help", false, "COMMAND to show help (Help)");
            parser = new BasicParser();
            cmd = parser.parse(options, args);
            String outp = "";
            
            if (cmd.hasOption("help") || args.length == 0) {
                new HelpFormatter().printHelp(Main.class.getCanonicalName(), options);
            }
            
            if (cmd.hasOption("f")) {
                String filename = cmd.getOptionValue("f");
                logger.info("Trying to parse the file " + filename);
                outp = parse(filename);
            }
            if (cmd.hasOption("text")) {
                String text = cmd.getOptionValue("text");
                logger.info("Trying to process the text " + text);
                outp = parseText(text);
            }
            if(cmd.hasOption("outf")){
                outpfilename = cmd.getOptionValue("outf");
                if(!writeFile(outp, outpfilename)){
                    logger.warning("Error while writing."); // ERROR
                } else{
                    logger.info("Output correctly written to " + outpfilename);
                }
            }
            if(outp != null){                
                if(logs){
                    System.out.println("\n----------------\n");
                }
                System.out.println(outp);
                if(logs){
                    System.out.println("\n----------------\n");
                }
            }

        } catch (Exception e) {
System.out.println(e.toString());
        }

        return res.toString();
    }

    public static String parse(String filename) {   
        String res = "";
        try {
            File f = new File(filename);
            logger.info("parsing the folder " + filename); // DEBUG
            String input = FileUtils.readFileToString(f, "UTF-8");
            res = parseText(input);
                
        } catch (Exception e) {
            logger.warning("error opening file"); // ERROR
            return "";
        }
        logger.info("Parsing correct\n\n");
        return res;
    }
    
    public static String parseText(String txt) {
        String res = "";
        try{
           
                PrintWriter out;

        out = new PrintWriter(System.out);

        // Usamos las opciones de espanol de CoreNLP
        Properties properties = StringUtils.argsToProperties(new String[]{"-props", "StanfordCoreNLP-spanish.properties"});

        // Path de los modelos de IXA para el lematizador y el pos
        String posModel = "../CoreNLPtest-core/src/main/resources/ixa-pipes/morph-models-1.5.0/es/es-pos-perceptron-autodict01-ancora-2.0.bin";
        String lemmaModel = "../CoreNLPtest-core/src/main/resources/ixa-pipes/morph-models-1.5.0/es/es-lemma-perceptron-ancora-2.0.bin";

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
        
        } catch (Exception e) {
            logger.warning("error processing text:\n" + res); // ERROR
            return "";
        }
       logger.info("Text processing correct:\n" + res);

        return res;
    }

    public static void initLogger(boolean logs) {
        if (logs) {
            initLoggerDebug();
        } else {
            initLoggerDisabled();
        }

    }

    /**
     * Shuts up all the loggers. 
     * Also the logs from third parties.
     */
    private static void initLoggerDisabled() {
        Logger.getLogger("").setLevel(Level.FINEST);
//
//        List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
//        loggers.add(LogManager.getRootLogger());
//        for (Logger log : loggers) {
//            log.setLevel(Level.OFF);
//        }
//        Logger.getRootLogger().setLevel(Level.OFF);
        
        // We do this to void IxaPipes messages...
        PrintStream falseerr = new PrintStream(new OutputStream(){
            public void write(int b) {
            }
        });
        System.setErr(falseerr);
        
        // We turn off CoreNLP logger
        RedwoodConfiguration.current().clear().apply();        
        
        // We turn off some inner IxaPipes loggers
//        ch.qos.logback.classic.Logger logger1 = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(SpanishReadabilityModel.class);
//        logger1.setLevel(ch.qos.logback.classic.Level.OFF);           
//        ch.qos.logback.classic.Logger logger2 = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Hyphenator.class);
//        logger2.setLevel(ch.qos.logback.classic.Level.OFF);
//        ch.qos.logback.classic.Logger logger3 = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(BasicAnnotator.class);
//        logger3.setLevel(ch.qos.logback.classic.Level.OFF);
//        
//        logger.setLevel(Level.OFF);

//        Logger.getRootLogger().removeAllAppenders();
//Logger.getRootLogger().addAppender(new NullAppender());
    }

    /**
     * Si se desean logs, lo que se hace es: - INFO en consola - DEBUG en
     * archivo de logs logs.txt
     */
    private static void initLoggerDebug() {

//        Enumeration currentLoggers = LogManager.getCurrentLoggers();
//        List<Logger> loggers = Collections.<Logger>list(currentLoggers);
//        loggers.add(LogManager.getRootLogger());
//        for (Logger log : loggers) {
//            log.setLevel(Level.OFF);
//        }
//
//        Logger root = Logger.getRootLogger();
//        root.setLevel((Level) Level.DEBUG);
//
//        //APPENDER DE CONSOLA (INFO)%d{ABSOLUTE} 
//        PatternLayout layout = new PatternLayout("%d{HH:mm:ss} [%5p] %13.13C{1}:%-4L %m%n");
//        ConsoleAppender appenderconsole = new ConsoleAppender(); //create appender
//        appenderconsole.setLayout(layout);
//        appenderconsole.setThreshold(Level.INFO);
//        appenderconsole.activateOptions();
//        appenderconsole.setName("console");
//        root.addAppender(appenderconsole);
//
//        //APPENDER DE ARCHIVO (DEBUG)
//        PatternLayout layout2 = new PatternLayout("%d{ISO8601} [%5p] %13.13C{1}:%-4L %m%n");
//        FileAppender appenderfile = null;
//        String filename = "./logs/logs.txt";
//        try {
//            MavenXpp3Reader reader = new MavenXpp3Reader();
//            Model model = reader.read(new FileReader("pom.xml"));
//            filename = "./logs/" + model.getArtifactId() + ".txt";
//        } catch (Exception e) {
//        }
//        try {
//            appenderfile = new FileAppender(layout2, filename, false);
//            appenderfile.setName("file");
//            appenderfile.setThreshold(Level.DEBUG);
//            appenderfile.activateOptions();
//        } catch (Exception e) {
//        }
//
//        root.addAppender(appenderfile);
//
//
//        logger = Logger.getLogger(Main.class.getName());
    }
    public static boolean writeFile(String input, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            OutputStreamWriter w = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(w);
            bw.write(input);
            bw.flush();
            bw.close();
            return true;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return false;
    }

}
