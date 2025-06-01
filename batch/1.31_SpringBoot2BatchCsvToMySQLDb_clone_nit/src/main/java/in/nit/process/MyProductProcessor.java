package in.nit.process;

import org.springframework.batch.item.ItemProcessor;

import in.nit.model.Product;

public class MyProductProcessor 
	implements ItemProcessor<Product, Product>
{

	@Override
	public Product process(Product p) throws Exception {
		var cost=p.getProdCost();
		var disc=cost * 4/100.0;
		var gst=cost * 8/100.0;
		p.setProdGst(gst);
		p.setProdDisc(disc);
		return p;
	}
}






