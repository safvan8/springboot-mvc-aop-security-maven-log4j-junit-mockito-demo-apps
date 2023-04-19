package in.ineuron.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.ineuron.bo.Product;

/* stored procedure: 
 * 
 * CREATE DEFINER=`root`@`localhost` PROCEDURE `P_GET_PRODUCT_BY_NAME`(IN name1 VARCHAR(20), IN name2 VARCHAR(20))
BEGIN
		SELECT pid, pname, price, qty FROM products WHERE pname IN (name1,name2); 
	END$$

DELIMITER ;
 * 
 */

@Component("service")
public class ProductMgmtServiceImpl implements IProductMgmtService
{
	@Autowired
	private EntityManager manager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> fetchProductsByName(String product1, String product2)
	{

		System.out.println("EntityManager => " + manager.getClass().getName());

		// creates a StoredProcedureQuery object for the stored procedure
		// named "P_GET_PRODUCT_BY_NAME", which returns objects of type Product.
		StoredProcedureQuery query = manager.createStoredProcedureQuery("P_GET_PRODUCT_BY_NAME", Product.class);

		// register the stored procedure's input parameters.
		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter(2, String.class, ParameterMode.IN);

		// set the values of the stored procedure's input parameters.
		query.setParameter(1, product1);
		query.setParameter(2, product2);

		// execute stored procedure and returns the results as a List of Product objects.
		List<Product> listProducts = query.getResultList();
		
		return listProducts;
	}

}
