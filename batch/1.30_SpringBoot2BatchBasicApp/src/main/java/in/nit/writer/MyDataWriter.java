package in.nit.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class MyDataWriter 
	implements ItemWriter<String>
{

	@Override
	public void write(List<? extends String> items) throws Exception {
		System.out.println("---from writer---");
		System.out.println("Data from Writer is:"+items);
	}
}





