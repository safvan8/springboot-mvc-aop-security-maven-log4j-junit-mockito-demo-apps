package in.nit.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class MyDataReader implements ItemReader<String>{

	private String[] input= {
			"Welcome",
			"to",
			"All",
			"Students",
			"Spring Boot"
			};
	private int count=0;
	
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		System.out.println("--from reader--");
		if(count<input.length) {
			return input[count++];
		}else {
			count=0;
		}
		return null;
	}
}




