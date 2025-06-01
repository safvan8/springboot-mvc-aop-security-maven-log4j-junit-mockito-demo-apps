package in.nit.process;

import org.springframework.batch.item.ItemProcessor;
public class MyDataProcessor 
	implements ItemProcessor<String, String>
{

	@Override
	public String process(String item) throws Exception {
		System.out.println("--from processor---");
		return "Processed=>"+item.toUpperCase();
	}
}
