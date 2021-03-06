package org.adorsys.xlseasy.cbe;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the mapping of an excel file. Each collection field is an
 * excel-sheet. Non collection fields are ignored.
 * 
 * Getters and setters are required. Sheet can be lists or sets.
 * 
 * Sheet are processed in the order of the fields. If this class extends another
 * one, sheets of this class are process first, then sheet from other classes
 * are processed.
 * 
 * In any case, the Processing Interface allow the specifications.
 * 
 * @author Sandro Sonntag
 * @author Francis Pouatcha
 */
public class ShopManagement {

	/** Sheet products. */
	private List<Product> products = new ArrayList<Product>();

	/** Sheet suppliers. */
	private List<Supplier> suppliers = new ArrayList<Supplier>();

	/**
	 * Gets the sheet products.
	 *
	 * @return the sheet products
	 */
	public List<Product> getProducts() {
		return products;
	}

	/**
	 * Sets the sheet suppliers.
	 *
	 * @param suppliers the new sheet suppliers
	 */
	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
	}

	/**
	 * Gets the sheet suppliers.
	 *
	 * @return the sheet suppliers
	 */
	public List<Supplier> getSuppliers() {
		return suppliers;
	}

	/**
	 * Sets the sheet products.
	 *
	 * @param products the new sheet products
	 */
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}