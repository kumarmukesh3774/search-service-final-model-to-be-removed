package com.offershopper.searchservice.controller;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;

@CrossOrigin
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
  ScanResult<Tuple> result=null;
  Set<String> resultList=new LinkedHashSet<String>();
  JedisPool jedisPool =null;
  /**
   * connect() will establish connection with Jedis server.
   * I am using Redis Windows64 server which is running on 6379 port.
   */
  public void connect() {
    //preparing a pool of jedis instances to handle large requirements
   jedisPool = new JedisPool("127.0.0.1", 6379);
    //jedis = new Jedis("127.0.0.1",6379);
  }
  
  /*
   * Release Redis Connection
   */
  public void disconnect() {
    jedis.disconnect();
  }
  
  
  @GetMapping(value="/q/",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
  public static ResponseEntity<List<String>> typeNextSuggestionControllerDefault() {
      List<String> results = new ArrayList<String>();
      results.add("default");
      return ResponseEntity.status(HttpStatus.OK).body(results);

  }
  
  
  public void addAllElements(ScanResult<Tuple> result ){
    

    Iterator<Tuple> itr = result.getResult().iterator();
    while( itr.hasNext()) {
      this.resultList.add(itr.next().getElement());
      
    }

    
  }

  
  
  
  
  @GetMapping(value="/q/{q}",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
  public static ResponseEntity<Set<String>> typeNextSuggestionController(@PathVariable(value = "q") String q) {
    q=q.toLowerCase().trim();
   TypeNextSuggestionController m = new TypeNextSuggestionController();
    int result_count = 10;
    
    String regx="[\\s]+";


    List<String> results = new ArrayList<String>();
    try {
      m.connect();
     m.jedis=  m.jedisPool.getResource();
     // Long start = m.jedis.zrank(m.key, m.q);
      
      m.result= m.jedis.zscan(m.key, "0",new ScanParams().match(q+"*"));
      ScanResult <Tuple> t=m.result;
      m.addAllElements( m.result );
   //   System.out.println(cursor.length());
      if(m.resultList.size()<10) {

        m.result= m.jedis.zscan(m.key, "0",new ScanParams().match("*"+q+"*"));
        m.addAllElements( m.result );
        if(m.resultList.size()<10) {
          
          String[] query = q.split(regx);
         // System.out.println(query[query.length-1]+"====================================================");
          if(m.resultList.size()<10) {
            m.result= m.jedis.zscan(m.key, "0",new ScanParams().match("*"+query[query.length-1]+"*"));
            m.addAllElements( m.result );
          }
          if(m.resultList.size()<10) {
            m.result= m.jedis.zscan(m.key, "0",new ScanParams().match(query[query.length-1]+"*"));
            m.addAllElements( m.result );
          }

        }
      }

        /*       Long start = m.jedis.zrank(m.key, q);
      System.out.println(start+"start value++++++++++++++++++++++");
      long rangelen = 50;
      
     //No Data Found
      if(start == null) {
        System.out.println("Start is NULL");
       // System.exit(0);
      }*/
      
      //while(results.size() != m.result_count) {
     /* while(results.size() != result_count) {
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
*/    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      m.disconnect();
      return ResponseEntity.status(HttpStatus.OK).body(m.resultList);

    }
  }
  
  /**
   * Load Data into redis if not available
   * @param key
   * @return 
   * @throws Exception
   */
  
  @PostMapping("/load-data")
  public void load_data(@RequestBody Document document) throws Exception {
    TypeNextSuggestionController m = new TypeNextSuggestionController();
    m.connect();
    //String title = (String) document.get("offerTitle");
    String keywords = (String) document.get("keywords");
     String regx="[,]+";
     int i=0;
    String[] keywordsSplit = keywords.toLowerCase().split(regx);
    for (String str : keywordsSplit) {
      m.jedis.zadd(m.key,i++, str.toLowerCase().trim()); 

    }
   // System.out.println("yfuuulllllllllllllllllllllllll"+m.jedis.zscan("abc", "0",new ScanParams().match("f")));
    //String category = (String) document.get("category");
   /* String regx="[,]+";
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
    }*/

    m.disconnect();
    
  }
}
