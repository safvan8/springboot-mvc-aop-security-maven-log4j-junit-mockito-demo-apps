package org.st.process;

import org.springframework.batch.item.ItemProcessor;
import org.st.model.Product;

public class ProductItemProcessor 
	implements ItemProcessor<Product, Product>
{
	@Override
	public Product process(Product p) throws Exception {
		Double gst=p.getCost()*18/100.0;
		Double disc=p.getCost()*5/100.0;
		p.setGst(gst);
		p.setDisc(disc);
		return p;
	}
	
}




