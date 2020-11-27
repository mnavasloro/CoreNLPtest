package oeg.tagger.rest;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Status method to show internal information.
 * @author vroddon
 */
@RestController
public class StatusController {

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<String> status()
    {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errorcode", "ok");
            String out = jsonObject.toString();
            return new ResponseEntity(out, HttpStatus.OK);
    }

    @RequestMapping(value = "/statuspost", method = RequestMethod.POST)
    public ResponseEntity<String> statuspost(@RequestBody String body)
    {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errorcode", "ok " + body);
            String out = jsonObject.toString();
            return new ResponseEntity(out, HttpStatus.OK);
    }
    
}
