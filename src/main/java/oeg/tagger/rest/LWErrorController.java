package oeg.tagger.rest;

import org.springframework.boot.web.servlet.error.ErrorController;

public class LWErrorController implements ErrorController {
  
    private static final String PATH = "/error2";

    @Override
    public String getErrorPath() {
        return PATH; 
    }
     

}