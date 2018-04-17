package com.offershopper.searchservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.offershopper.searchservice.controller.TypeNextSuggestionController;


@SpringBootApplication
public class SearchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchServiceApplication.class, args);
    /*TypeNextSuggestionController obj= new TypeNextSuggestionController();
    try {
     // obj.load_data("names");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }*/
		//TypeNextSuggestionController.typeNextSuggestionController();

		
/*		String key = "mostUsedLanguages";
		 Jedis jedis = new Jedis("localhost");
		 //Adding a value with score to the set
		 jedis.zadd(key,100,"Java");//ZADD
		 
		 //We could add more than one value in one calling
		 Map<Double, String> scoreMembers = new HashMap<Double, String>();
		 scoreMembers.put(90d, "Python");
		 scoreMembers.put(80d, "Javascript");
		 jedis.zadd(key, scoreMembers);
		 
		 //We could get the score for a member
		 System.out.println("Number of Java users:" + jedis.zscore(key, "Java"));
		 
		 //We could get the number of elements on the set
		// System.out.println("Number of elements:" + jedis.zcard(key));//ZCARD
*/		 }
		
		
	
}
