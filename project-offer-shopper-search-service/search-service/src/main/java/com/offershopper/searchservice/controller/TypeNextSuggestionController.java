package com.offershopper.searchservice.controller;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;


@RestController
public class TypeNextSuggestionController {
  
  /**
   * Let's Define some variables here.
   * jedis = object of Jedis (Redis Java Client)
   * key = redis key used to store data
   * q = search term
   * result_count = total number of suggestions returned
   */
  Jedis jedis;
  String key = "keywords";
  int result_count = 10;
  
  /**
   * connect() will establish connection with Jedis server.
   * I am using Redis Windows64 server which is running on 6379 port.
   */
  public void connect() {
    jedis = new Jedis("127.0.0.1",6379);
  }
  
  /*
   * Release Redis Connection
   */
  public void disconnect() {
    jedis.disconnect();
  }
  
  
  @GetMapping("/q/{q}")
  public static List<String> typeNextSuggestionController(@PathVariable(value = "q") String q) {
    q=q.toLowerCase().trim();
   TypeNextSuggestionController m = new TypeNextSuggestionController();
    int result_count = 10;

    List<String> results = new ArrayList<String>();
    try {
      m.connect();
     // Long start = m.jedis.zrank(m.key, m.q);
      Long start = m.jedis.zrank(m.key, q);
      System.out.println(start+"start value++++++++++++++++++++++");
      long rangelen = 50;
      
      //No Data Found
      if(start == null) {
        System.out.println("Start is NULL");
       // System.exit(0);
      }
      
      //while(results.size() != m.result_count) {
      while(results.size() != result_count) {
        Set<String> range = m.jedis.zrange(m.key, start, start+rangelen-1);
        start += rangelen;
        if(range.size() == 0) { 
          System.out.println("No records Found");
          break;
        } 
        for(String entry: range) {
          if(!entry.startsWith(q)) {
            break;
          }
          if(entry.contains("*"))
          results.add(entry.substring(0,entry.length()-1));
          
          //Log Results
          System.out.println(entry);
          
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      m.disconnect();
      return results;
    }
  }
  
  /**
   * Load Data into redis if not available
   * @param key
   * @throws Exception
   */
  
  @PostMapping("/load-data")
  public void load_data(@RequestBody Document document) throws Exception {
    TypeNextSuggestionController m = new TypeNextSuggestionController();
    m.connect();
    //String title = (String) document.get("offerTitle");
    String keywords = (String) document.get("keywords");
    //String category = (String) document.get("category");
    String regx="[,]+";
    String[] keywordsSplit = keywords.toLowerCase().split(regx);
    int i=0;
    for (String str : keywordsSplit) {
      //set.add(str.toLowerCase().trim());
      i=0;
      //char []strArray=str.toCharArray();
      while(i++<str.length()) {
        
      m.jedis.zadd(key, 0, str.substring(0, i).trim());
      }
      m.jedis.zadd(key, 0, str.substring(0, i-1).trim()+"*");
    }

/*    BufferedReader br = new BufferedReader (new FileReader(new File("/home/sapient/Documents/project-offer-shopper-search-service/search-service/src/main/java/com/offershopper/searchservice/controller/names.txt")));
      String name;
      while ((name = br.readLine()) != null) {
        m.jedis.zadd(key, 0, name);
      }
    br.close();*/
    m.disconnect();
  }
}
