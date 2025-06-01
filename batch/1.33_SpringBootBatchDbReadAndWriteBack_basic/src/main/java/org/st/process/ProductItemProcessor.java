package org.st.process;

import org.springframework.batch.item.ItemProcessor;
import org.st.model.Product;

/**
 * Increases cost and gst by ₹10 for each product.
 */
public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(Product p) throws Exception {
        Double newCost = p.getCost() + 10.0;
        Double newGst  = p.getGst()  + 10.0;
        p.setCost(newCost);
        p.setGst(newGst);
        return p;
    }
}
