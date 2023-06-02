package com.shoppingapp.productUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.startup.Catalina;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import com.shoppingapp.dbutils.Column;
import com.shoppingapp.dbutils.Criteria;
import com.shoppingapp.dbutils.DBAdapter;
import com.shoppingapp.dbutils.DataHolder;
import com.shoppingapp.dbutils.DeleteQuery;
import com.shoppingapp.dbutils.Join;
import com.shoppingapp.dbutils.OrderBy;
import com.shoppingapp.dbutils.OrderBy.Order;
import com.shoppingapp.dbutils.Row;
import com.shoppingapp.dbutils.SelectQuery;
import com.shoppingapp.dbutils.TableHolder;
import com.shoppingapp.dbutils.UpdateQuery;
import com.shoppingapp.entities.Category;
import com.shoppingapp.entities.Color;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;
import com.shoppingapp.entities.Product;
import com.shoppingapp.entities.ProductItem;
import com.shoppingapp.entities.ProductVariant;
import com.shoppingapp.entities.Size;
import com.shoppingapp.entities.Topic;
import com.shoppingapp.entities.VariantImage;
import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.BeanValidator;
import com.shoppingapp.utils.ExceptionCause;


public class ProductManagementUtil implements ProductManagementInterface {
	
	private DBAdapter adapter;
	public String CREATED="CREATED";
	public String FAILED="FAILED";
	public String SUCCESS="SUCCESS";
	public String EXIST="EXIST";
	public String NOT_EXIST="NOT_EXIST";
	public String NOT_VERIFIED="NOT_VERIFIED";
	
	//tableNames
	public String PRODUCT_CATEGORY="product_category";
	public String PRODUCT="product";
	public String PRODUCT_ITEM="product_item";
	public String PRODUCT_VARIANT="product_variant";
	public String PRODUCT_INVENTORY="product_inventory";
	public String PRODUCT_IMAGES="product_images";
	public String COLORS="colors";
	public String SIZE="size";
	public String TOPICS="topics";
	public String TOPIC_ITEM_RELATION="topic_item_relation";
	
	private static final Logger logger=LogManager.getLogger(ProductManagementUtil.class);
	
	//CATEGORIES FUNCTIONS
	public ProductManagementUtil(DBAdapter adapter){
		this.adapter=adapter;
	}

	public String createCategories(ArrayList<Category> categories) {
		
		try {
			//Configurations
			String[] fieldNames= {"name","description","createdat","modifiedat"};
			TableHolder th=new TableHolder(PRODUCT_CATEGORY,fieldNames);
			
			//data
			for(int i=0;i<categories.size();i++) {
				Category cat=categories.get(i);
				Row row=new Row();
				Map<String,Object> columns=row.getColumns();
				columns.put("name", cat.getProductTypeName());
				columns.put("description", cat.getDescription());
				columns.put("createdat", new Date().getTime());
				columns.put("modifiedat",new Date().getTime());
				th.setRow(row);
			}
			
			adapter.persistData(th);
			return CREATED;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return FAILED;
	}

	public String createCategory(Category cat) {
		try {
			if(getCategoryByName(cat.getProductTypeName())!=null) {
				return EXIST;
			}
			BeanValidator.setNullForEmptyString(cat);
			ArrayList<Category> cats=new ArrayList<Category>();
			cats.add(cat);
			return createCategories(cats);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return FAILED;
	}
	
	public String editCategory(Category cat) {
		try {
			BeanValidator.setNullForEmptyString(cat);
			Category verifiedCat=getCategoryById(cat.getProductTypeId());
			if(verifiedCat!=null) {
				UpdateQuery uq=new UpdateQuery(PRODUCT_CATEGORY);
				ArrayList<Column> cols=new ArrayList<Column>();
				cols.add(new Column(PRODUCT_CATEGORY,"name",cat.getProductTypeName()));
				cols.add(new Column(PRODUCT_CATEGORY,"description",cat.getDescription()));
				cols.add(new Column(PRODUCT_CATEGORY,"modifiedat",new Date().getTime()));
				uq.setFields(cols);
				Criteria crit=new Criteria(new Column(PRODUCT_CATEGORY,"id"),cat.getProductTypeId());
				crit.setComparator(Criteria.EQUAL);
				uq.setCriteria(crit);
				adapter.updateData(uq);
				return SUCCESS;
			}
			return NOT_EXIST;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return FAILED;
	}


	public ArrayList<Category> getCategories(){
		ArrayList<Category> categories=new ArrayList<Category>();
		try {
			SelectQuery sq=new SelectQuery(PRODUCT_CATEGORY);
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder th=dh.getTable(PRODUCT_CATEGORY);
			if(th!=null) {
				Map<Integer,Row> catMap=th.getRows();
				for(int i=0;i<catMap.size();i++) {
					Category cat=new Category();
					Row row=catMap.get(i);
					cat.setProductTypeId((Long)row.get("id"));
					cat.setProductTypeName((String)row.get("name"));
					cat.setDescription((String)row.get("description"));
					cat.setCreatedAt((Long)row.get("createdat"));
					cat.setModifiedAt((Long)row.get("modifiedat"));
					categories.add(cat);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return categories;
	}
	
	public ArrayList<Category> getCategoriesAndProducts(){
		ArrayList<Category> categories=new ArrayList<Category>();
		try {
			SelectQuery sq=new SelectQuery(PRODUCT_CATEGORY);
			Join join=new Join(new Column(PRODUCT_CATEGORY,"id"),new Column(PRODUCT,"id"),Join.LEFT_JOIN);
			sq.setJoin(join);
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder th=dh.getTable(PRODUCT_CATEGORY);
			TableHolder productTh=dh.getTable(PRODUCT);
			if(th!=null) {
				Map<Integer,Row> catMap=th.getRows();
				Map<Integer,Row> productMap=productTh.getRows();
				HashMap<String,Integer> catIndexMap=new HashMap<String, Integer>();
				for(int i=0;i<catMap.size();i++) {
					Row row=catMap.get(i);
					Row prow=productMap.get(i);
					if(catIndexMap.containsKey((String)row.get("name"))) {
						Product product=new Product();
						product.setProductId((Long)prow.get("id"));
						product.setProductName((String)prow.get("name"));
						product.setDescription((String)prow.get("description"));
						product.setCreatedAt((Long)prow.get("createdat"));
						product.setModifiedAt((Long)prow.get("modifiedat"));
						categories.get(catIndexMap.get((String)row.get("name"))).getProducts().add(product);
					}else {
						Category cat=new Category();
						cat.setProductTypeId((Long)row.get("id"));
						cat.setProductTypeName((String)row.get("name"));
						cat.setDescription((String)row.get("description"));
						cat.setCreatedAt((Long)row.get("createdat"));
						cat.setModifiedAt((Long)row.get("modifiedat"));
						ArrayList<Product> products=new ArrayList<Product>();
						if(prow.get("id")!=null) {
							Product product=new Product();
							product.setProductId((Long)prow.get("id"));
							product.setProductName((String)prow.get("name"));
							product.setDescription((String)prow.get("description"));
							product.setCreatedAt((Long)prow.get("createdat"));
							product.setModifiedAt((Long)prow.get("modifiedat"));
							products.add(product);
						}
						cat.setProducts(products);
						categories.add(cat);
						catIndexMap.put(cat.getProductTypeName(),i);
					}
				}
			}
			
	    }catch (Exception e) {
			e.printStackTrace();
		}
		return categories;
	}

	
	public Category getCategoryBy(String name,Object value,int comparator) {
		// TODO Auto-generated method stub
		try {
			Category cat=new Category();
			SelectQuery sq=new SelectQuery(PRODUCT_CATEGORY);
			
			Criteria criteria=new Criteria(new Column(PRODUCT_CATEGORY,name),value);
			criteria.setComparator(comparator);
			sq.setCriteria(criteria);
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder th=dh.getTable(PRODUCT_CATEGORY);
			if(th!=null) {
				if(th.getRows().size()==1) {
					Row row=th.getRows().get(0);
					cat.setProductTypeId((Long)row.get("id"));
					cat.setProductTypeName((String)row.get("name"));
					cat.setDescription((String)row.get("description"));
					cat.setCreatedAt((Long)row.get("createdat"));
					cat.setModifiedAt((Long)row.get("modifiedat"));
					return cat;
				}
			}
			
	    }catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Category getCategoryById(Long id) {
		return getCategoryBy("id", id,Criteria.EQUAL);
	}
	
	public Category getCategoryByName(String name) {
		return getCategoryBy("name", name,Criteria.EQUAL);
	}
	
	
	//PRODUCTS FUNCTIONS
	public void createProducts(ArrayList<Product> products) throws ExceptionCause,Exception {
		
		//Configurations
		String[] fieldNames= {"name","category","description","createdat","modifiedat"};
		TableHolder th=new TableHolder(PRODUCT,fieldNames);
		
		//data
		for(int i=0;i<products.size();i++) {
			Product prod=products.get(i);
			Row row=new Row();
			Map<String,Object> columns=row.getColumns();
			columns.put("name", prod.getProductName());
			columns.put("category", prod.getProductType().getProductTypeId());
			columns.put("description", prod.getDescription());
			columns.put("createdat", new Date().getTime());
			columns.put("modifiedat",new Date().getTime());
			th.setRow(row);
		}
		
		adapter.persistData(th);
			
	}
	
	
	
	public void createProduct(Product prod) throws ExceptionCause,Exception {
		
		if(getProductByName(prod.getProductName())!=null) {
			throw new ExceptionCause("Product Name already used!",HttpStatus.BAD_REQUEST);
		}
		
		if(getCategoryById(prod.getProductType().getProductTypeId())==null) {
			throw new ExceptionCause("Category with id "+prod.getProductType().getProductTypeId()+" doesn't exist!",HttpStatus.BAD_REQUEST);
		}
		
		BeanValidator.setNullForEmptyString(prod);
		ArrayList<Product> prods=new ArrayList<Product>();
		prods.add(prod);
		createProducts(prods);
		
	}
	
	public void editProduct(Product product) throws ExceptionCause,Exception {
		
		Product productFromDB=getProductById(product.getProductId());
		
		if(productFromDB==null) {
			throw new ExceptionCause("Suitable Product Id is mandatory to edit product!",HttpStatus.BAD_REQUEST);
		}
		
		if(!productFromDB.getProductName().equals(product.getProductName()) && getProductByName(product.getProductName())!=null) {
			throw new ExceptionCause("Product Name already used!",HttpStatus.BAD_REQUEST);
		}
		
		if(getCategoryById(product.getProductType().getProductTypeId())==null) {
			throw new ExceptionCause("Category with id "+product.getProductType().getProductTypeId()+" doesn't exist!",HttpStatus.BAD_REQUEST);
		}
		
		BeanValidator.setNullForEmptyString(product);
		UpdateQuery uq=new UpdateQuery(PRODUCT);
		ArrayList<Column> cols=new ArrayList<Column>();
		cols.add(new Column(PRODUCT,"name",product.getProductName()));
		cols.add(new Column(PRODUCT,"description",product.getDescription()));
		cols.add(new Column(PRODUCT,"category",product.getProductType().getProductTypeId()));
		cols.add(new Column(PRODUCT,"modifiedat",new Date().getTime()));
		uq.setFields(cols);
		Criteria crit=new Criteria(new Column(PRODUCT,"id"),product.getProductId());
		crit.setComparator(Criteria.EQUAL);
		uq.setCriteria(crit);
		adapter.updateData(uq);

	}
	
	public ArrayList<Product> getProducts() throws Exception {
		ArrayList<Product> products=new ArrayList<Product>();
	
		SelectQuery sq=new SelectQuery(PRODUCT);
		Join join=new Join(new Column(PRODUCT,"category"),new Column(PRODUCT_CATEGORY,"id"),Join.INNER_JOIN);
		sq.setJoin(join);
		DataHolder dh=adapter.executeQuery(sq);
		
		TableHolder th=dh.getTable(PRODUCT);
		TableHolder categoryTh=dh.getTable(PRODUCT_CATEGORY);
		if(th!=null && categoryTh!=null) {
			Map<Integer,Row> productMap=th.getRows();
			Map<Integer,Row> categoryMap=categoryTh.getRows();
			for(int i=0;i<productMap.size();i++) {
				Row row=productMap.get(i);
				Row catRow=categoryMap.get(i);
				Product product=new Product();
				Category cat=new Category();
				product.setProductId((Long)row.get("id"));
				product.setProductName((String)row.get("name"));
				product.setDescription((String)row.get("description"));
				product.setCreatedAt((Long)row.get("createdat"));
				product.setModifiedAt((Long)row.get("modifiedat"));
				cat.setProductTypeId((Long)row.get("category"));
				cat.setProductTypeName((String)catRow.get("name"));
				product.setProductType(cat);
				products.add(product);
	
			}
		}
			
		return products;
	}
	
	public Product getProductBy(String name,Object value) {

		try {
			Product product=new Product();
			Category cat=new Category();
			SelectQuery sq=new SelectQuery(PRODUCT);
			Criteria criteria=new Criteria(new Column(PRODUCT,name),value);
			criteria.setComparator(Criteria.EQUAL);
			sq.setCriteria(criteria);
			Join join=new Join(new Column(PRODUCT,"category"),new Column(PRODUCT_CATEGORY,"id"),Join.INNER_JOIN);
			sq.setJoin(join);
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder th=dh.getTable(PRODUCT);
			TableHolder categoryTh=dh.getTable(PRODUCT_CATEGORY);
			if(th!=null && categoryTh!=null) {
				if(th.getRows().size()==1 && categoryTh.getRows().size()==1) {
					Row row=th.getRows().get(0);
					Row catRow=categoryTh.getRows().get(0);
					product.setProductId((Long)row.get("id"));
					product.setProductName((String)row.get("name"));
					product.setDescription((String)row.get("description"));
					product.setCreatedAt((Long)row.get("createdat"));
					product.setModifiedAt((Long)row.get("modifiedat"));
					cat.setProductTypeId((Long)catRow.get("id"));
					cat.setProductTypeName((String)catRow.get("name"));
					cat.setDescription((String)catRow.get("description"));
					cat.setCreatedAt((Long)catRow.get("createdat"));
					cat.setModifiedAt((Long)catRow.get("modifiedat"));
					product.setProductType(cat);
					return product;
				}
			}
			
	    }catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
		
	public Product getProductById(Long id) {
		return getProductBy("id", id);
	}
	
	public Product getProductByName(String name) {
		return getProductBy("name", name);
	}
	
	
	//PRODUCT ITEMS FUNCTIONS
	
	public void createProductItems(ArrayList<ProductItem> productItems) throws Exception {
				
		String[] fieldNames= {"name","productId","description","isActive","createdat","modifiedat"};
		TableHolder th=new TableHolder(PRODUCT_ITEM,fieldNames);
		
		//data
		for(int i=0;i<productItems.size();i++) {
			ProductItem prodItem=productItems.get(i);
			Row row=new Row();
			Map<String,Object> columns=row.getColumns();
			columns.put("name", prodItem.getProductItemName());
			columns.put("productId", prodItem.getProduct().getProductId());
			columns.put("description", prodItem.getDescription());
			columns.put("isActive", false);
			columns.put("createdat", new Date().getTime());
			columns.put("modifiedat",new Date().getTime());
			th.setRow(row);
		}
		
		adapter.persistData(th);
		
	}
	
	public void createProductItem(ProductItem prodItem) throws Exception {
		if(getProductItemBy("name",prodItem.getProductItemName())!=null) {
			throw new ExceptionCause("Item name already exist!",HttpStatus.BAD_REQUEST);
		}
		
		if(getProductBy("id", prodItem.getProduct().getProductId())==null) {
			throw new ExceptionCause("Product with id "+prodItem.getProduct().getProductId()+" doesn't exist!",HttpStatus.BAD_REQUEST);
		}
		
		BeanValidator.setNullForEmptyString(prodItem);
		ArrayList<ProductItem> prodItems=new ArrayList<ProductItem>();
		prodItems.add(prodItem);
		createProductItems(prodItems);
		
	}
	
	public void putProductItem(ProductItem prodItem) throws Exception {
		
		ProductItem itemFromDB=getProductItemBy("id",prodItem.getProductItemId());
		if(itemFromDB==null) {
			throw new ExceptionCause("Suitable Item id is neccessary to edit item!",HttpStatus.BAD_REQUEST);
		}
		
		if(!itemFromDB.getProductItemName().equals(prodItem.getProductItemName()) && getProductItemBy("name",prodItem.getProductItemName())!=null) {
			throw new ExceptionCause("Item name already exist!",HttpStatus.BAD_REQUEST);
		}
		
		if(getProductBy("id", prodItem.getProduct().getProductId())==null) {
			throw new ExceptionCause("Product with id "+prodItem.getProduct().getProductId()+" doesn't exist!",HttpStatus.BAD_REQUEST);
		}
		
		UpdateQuery uq=new UpdateQuery(PRODUCT_ITEM);
		ArrayList<Column> cols=new ArrayList<Column>();
		cols.add(new Column(PRODUCT_ITEM, "name",prodItem.getProductItemName()));
		cols.add(new Column(PRODUCT_ITEM, "description",prodItem.getDescription()));
	    cols.add(new Column(PRODUCT_ITEM, "productId",prodItem.getProduct().getProductId()));
        cols.add(new Column(PRODUCT_ITEM, "isActive",prodItem.getIsActive()));
        cols.add(new Column(PRODUCT_ITEM, "modifiedat",new Date().getTime()));
        uq.setFields(cols);
		Criteria criteria=new Criteria(new Column(PRODUCT_ITEM,"id"),prodItem.getProductItemId());
		criteria.setComparator(Criteria.EQUAL);
		uq.setCriteria(criteria);
		System.out.println(uq.getUpdateQueryString());
		adapter.updateData(uq);
				
	}
	
	
	public ProductItem getProductItemBy(String name,Object value) {

		try {
			ProductItem productItem=new ProductItem();
			Product product=new Product();
			Category cat=new Category();
			SelectQuery sq=new SelectQuery(PRODUCT_ITEM);
			Criteria criteria=new Criteria(new Column(PRODUCT_ITEM,name),value);
			criteria.setComparator(Criteria.EQUAL);
			sq.setCriteria(criteria);
			Join join1=new Join(new Column(PRODUCT_ITEM,"productId"),new Column(PRODUCT,"id"),Join.INNER_JOIN);
			Join join2=new Join(new Column(PRODUCT,"category"),new Column(PRODUCT_CATEGORY,"id"),Join.INNER_JOIN);
			sq.setJoin(join1);
			sq.addJoin(join2);
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder th=dh.getTable(PRODUCT_ITEM);
			TableHolder categoryTh=dh.getTable(PRODUCT_CATEGORY);
			TableHolder productTh=dh.getTable(PRODUCT);
			if(th!=null && categoryTh!=null && productTh!=null) {
				if(th.getRows().size()==1 && categoryTh.getRows().size()==1 && productTh.getRows().size()==1) {
					Row row=th.getRows().get(0);
					Row catRow=categoryTh.getRows().get(0);
					Row prodRow=productTh.getRows().get(0);
					
					//setting product
					product.setProductId((Long)prodRow.get("id"));
					product.setProductName((String)prodRow.get("name"));
					product.setDescription((String)prodRow.get("description"));
					product.setCreatedAt((Long)prodRow.get("createdat"));
					product.setModifiedAt((Long)prodRow.get("modifiedat"));
					cat.setProductTypeId((Long)catRow.get("id"));
					cat.setProductTypeName((String)catRow.get("name"));
					cat.setDescription((String)catRow.get("description"));
					cat.setCreatedAt((Long)catRow.get("createdat"));
					cat.setModifiedAt((Long)catRow.get("modifiedat"));
					product.setProductType(cat);
					
					//setting product item
					productItem.setProductItemId((Long)row.get("id"));
					productItem.setProductItemName((String)row.get("name"));
					productItem.setProduct(product);
					productItem.setDescription((String)row.get("description"));
					productItem.setIsActive((Boolean)row.get("isActive"));
					productItem.setCreatedAt((Long)row.get("createdat"));
					productItem.setModifiedAt((Long)row.get("modifiedat"));
					return productItem;
				}
			}
			
	    }catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<ProductItem> getProductItems(GetInfo info) throws Exception{
		ArrayList<ProductItem> productItems=new ArrayList<ProductItem>();

		SelectQuery sq=new SelectQuery(PRODUCT_ITEM);
		OrderBy orderBy=new OrderBy(new Column(PRODUCT_ITEM,"id"),Order.ASC);
		sq.setOrderBy(orderBy);

		if(info.getPaginationKey()!=null) {
			Criteria criteria=new Criteria(new Column(PRODUCT_ITEM,"id"), info.getPaginationKey());
			criteria.setComparator(Criteria.GREATER);
			sq.setCriteria(criteria);
		}
		if(info.getRange()!=null) {
			sq.setLimit(info.getRange());
		}else {
			sq.setLimit(10);
		}
		Join join1=new Join(new Column(PRODUCT_ITEM,"productId"),new Column(PRODUCT,"id"),Join.INNER_JOIN);

		sq.setJoin(join1);
	
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(PRODUCT_ITEM);
		TableHolder productTh=dh.getTable(PRODUCT);

		if(th!=null && productTh!=null) {
			Map<Integer,Row> rows=th.getRows();
			Map<Integer,Row> productRows=productTh.getRows();
			for(int i=0;i<rows.size();i++) {
				Row row=rows.get(i);
				Row productRow=productRows.get(i);
				ProductItem item=new ProductItem();
				Product product=new Product();
				
				
				item.setProductItemId((Long)row.get("id"));
				item.setProductItemName((String)row.get("name"));
				item.setDescription(row.get("description")==null?"":(String)row.get("description"));
				item.setModifiedAt((Long)row.get("modifiedat"));
				item.setCreatedAt((Long)row.get("createdat"));
				item.setIsActive((Boolean)row.get("isActive"));
			    product.setProductId((Long)productRow.get("id"));
			    product.setProductName((String)productRow.get("name"));
			    item.setProduct(product);
				productItems.add(item);
				
			}
			
		}
		
		return productItems;
		
	}
	
	//VARIANTS FUNCTIONS
	
	public ArrayList<ProductVariant> getProductVariants(Long itemId,GetInfo info){
		 ArrayList<ProductVariant> variants=new ArrayList<ProductVariant>();
		 try {
			 SelectQuery sq=new SelectQuery(PRODUCT_VARIANT);
			 OrderBy orderBy=new OrderBy(new Column(PRODUCT_VARIANT,"id"),Order.ASC);
			 sq.setOrderBy(orderBy);
			 
			 if(info.getPaginationKey()!=null) {
					Criteria criteria=new Criteria(new Column(PRODUCT_VARIANT,"id"), info.getPaginationKey());
					criteria.setComparator(Criteria.GREATER);
					sq.setCriteria(criteria);
			 }
			 
			 if(info.getRange()!=null) {
				sq.setLimit(info.getRange());
			 }else {
				sq.setLimit(10);
			 }
			 
			 Join join1=new Join(new Column(PRODUCT_VARIANT,"colorId"),new Column(COLORS,"id"),Join.LEFT_JOIN);
			 sq.setJoin(join1);
			 
			 Criteria criteria=new Criteria(new Column(PRODUCT_VARIANT, "itemId"), itemId);
			 criteria.setComparator(Criteria.EQUAL);
			 if(sq.getCriteria()!=null) {
				 sq.getCriteria().and(criteria);
			 }else {
				 sq.setCriteria(criteria);
			 }
			 
			 DataHolder dh=adapter.executeQuery(sq);
			 TableHolder th=dh.getTable(PRODUCT_VARIANT);
			 TableHolder colorsTh=dh.getTable(COLORS);
			 
			 if(th!=null && colorsTh!=null) {
					Map<Integer,Row> rows=th.getRows();
					Map<Integer,Row> colorsRow=colorsTh.getRows();
					for(int i=0;i<rows.size();i++) {
						Row row=rows.get(i);
						Row colorRow=colorsRow.get(i);

						ProductVariant var=new ProductVariant();
						var.setVariantId((Long)row.get("id"));
						var.setName((String)row.get("name"));
						var.setPrice((Integer)row.get("price"));
						var.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name")));
					    variants.add(var);
					}
				}
			 
			 
		 }catch (Exception e) {
			e.printStackTrace();
		 }
		 return variants;
	}
	
	public void createProductVariants(ArrayList<ProductVariant> productVars) throws Exception {
		
		//Configurations
		String[] fieldNames= {"name","itemId","price","colorId"};
		TableHolder th=new TableHolder(PRODUCT_VARIANT,fieldNames);
	;
		//data
		for(int i=0;i<productVars.size();i++) {
			ProductVariant prodVar=productVars.get(i);
			Row row=new Row();
			Map<String,Object> columns=row.getColumns();
			columns.put("name",prodVar.getName());
			columns.put("itemId", prodVar.getItem().getProductItemId());
			columns.put("price", prodVar.getPrice());
			columns.put("colorId", prodVar.getColor().getColorId());	
			th.setRow(row);
			
		}
		
		adapter.persistData(th);
			
	}
	
	public ProductVariant getProductVariantByUnique(Criteria criteria) {

		try {
			SelectQuery sq=new SelectQuery(PRODUCT_VARIANT);
			sq.setCriteria(criteria);
			Join join1=new Join(new Column(PRODUCT_VARIANT,"colorId"),new Column(COLORS,"id"),Join.LEFT_JOIN);
		    sq.setJoin(join1);
		    DataHolder dh=adapter.executeQuery(sq);
			TableHolder th=dh.getTable(PRODUCT_VARIANT);
			TableHolder colorsTh=dh.getTable(COLORS);
			
			if(th!=null) {
				Map<Integer,Row> rows=th.getRows();
				Map<Integer,Row> colorsRow=colorsTh.getRows();	
				Row row=rows.get(0);
				Row colorRow=colorsRow.get(0);
				
				ProductVariant variant=new ProductVariant();
				variant.setVariantId((Long)row.get("id"));
				variant.setName((String)row.get("name"));
				variant.setPrice((Integer)row.get("price"));
				variant.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name")));
				ProductItem item=new ProductItem();
				item.setProductItemId((Long)row.get("itemId"));
				variant.setItem(item);
				return variant;
		
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ProductVariant getProductVariantById(Long variantId) {
        Criteria criteria=new Criteria(new Column(PRODUCT_VARIANT,"id"), variantId);
        criteria.setComparator(Criteria.EQUAL);
		return getProductVariantByUnique(criteria);
	}
	
	public void createProductVariant(ProductVariant variant) throws Exception {
		
		if(getProductItemBy("id",variant.getItem().getProductItemId())==null) {
			throw new ExceptionCause("Item id not exist!",HttpStatus.BAD_REQUEST);
		}
		
		Criteria criteria1=new Criteria(new Column(PRODUCT_VARIANT,"name"), variant.getName());
		criteria1.setComparator(Criteria.EQUAL);
		Criteria criteria2=new Criteria(new Column(PRODUCT_VARIANT,"itemId"), variant.getItem().getProductItemId());
		criteria2.setComparator(Criteria.EQUAL);
		criteria1.and(criteria2);

		if(getProductVariantByUnique(criteria1)!=null) {
			throw new ExceptionCause("Variant name already used under this item!",HttpStatus.BAD_REQUEST);
		}
		
		if(getColorById(variant.getColor().getColorId())==null) {
			throw new ExceptionCause("Color id not exist!",HttpStatus.BAD_REQUEST);
		}
		
		ArrayList<ProductVariant> variants=new ArrayList<ProductVariant>();
		variants.add(variant);
		createProductVariants(variants);
		
	}
	
    public void putProductVariant(ProductVariant variant) throws Exception {
		
		if(getProductItemBy("id",variant.getItem().getProductItemId())==null) {
			throw new ExceptionCause("Item id not exist!",HttpStatus.BAD_REQUEST);
		}
		
        ProductVariant variantFromDB=getProductVariantById(variant.getVariantId());
        
        if(variantFromDB==null) {
        	throw new ExceptionCause("Suitable variant id is neccessary to edit item!",HttpStatus.BAD_REQUEST);
        }
        
		if(!variantFromDB.getName().equals(variant.getName()) || !variantFromDB.getItem().getProductItemId().equals(variant.getItem().getProductItemId())) {
			Criteria criteria1=new Criteria(new Column(PRODUCT_VARIANT,"name"), variant.getName());
			criteria1.setComparator(Criteria.EQUAL);
			Criteria criteria2=new Criteria(new Column(PRODUCT_VARIANT,"itemId"), variant.getItem().getProductItemId());
			criteria2.setComparator(Criteria.EQUAL);
			criteria1.and(criteria2);
			if(getProductVariantByUnique(criteria1)!=null) {
				throw new ExceptionCause("Variant name already used under this item!",HttpStatus.BAD_REQUEST);
			}
		}
		
		if(getColorById(variant.getColor().getColorId())==null) {
			throw new ExceptionCause("Color id not exist!",HttpStatus.BAD_REQUEST);
		}
		
		UpdateQuery uq=new UpdateQuery(PRODUCT_VARIANT);
		ArrayList<Column> cols=new ArrayList<Column>();
		cols.add(new Column(PRODUCT_VARIANT, "name",variant.getName()));
		cols.add(new Column(PRODUCT_VARIANT, "itemId",variant.getItem().getProductItemId()));
	    cols.add(new Column(PRODUCT_VARIANT, "colorId",variant.getColor().getColorId()));
        cols.add(new Column(PRODUCT_VARIANT, "price",variant.getPrice()));

        uq.setFields(cols);
		Criteria criteria=new Criteria(new Column(PRODUCT_VARIANT,"id"),variant.getVariantId());
		criteria.setComparator(Criteria.EQUAL);
		uq.setCriteria(criteria);

		adapter.updateData(uq);
	}
	
	public ProductItem getProductItemAndVariantsBy(String name,Object value) {

		try {
			ProductItem productItem=getProductItemBy(name, value);
			if(productItem!=null) {
				SelectQuery sq=new SelectQuery(PRODUCT_VARIANT);
				Criteria criteria=new Criteria(new Column(PRODUCT_VARIANT,"itemId"),value);
				criteria.setComparator(Criteria.EQUAL);
				sq.setCriteria(criteria);
				Join join1=new Join(new Column(PRODUCT_VARIANT,"id"),new Column(PRODUCT_IMAGES,"id"),Join.LEFT_JOIN);
				Join join4=new Join(new Column(PRODUCT_VARIANT,"id"),new Column(PRODUCT_INVENTORY,"variantId"),Join.LEFT_JOIN);
				Join join2=new Join(new Column(PRODUCT_VARIANT,"sizeId"),new Column(SIZE,"id"),Join.LEFT_JOIN);
				Join join3=new Join(new Column(PRODUCT_VARIANT,"colorId"),new Column(COLORS,"id"),Join.LEFT_JOIN);
		
				sq.setJoin(join1);
				sq.addJoin(join2);
				sq.addJoin(join3);
				sq.addJoin(join4);
				DataHolder dh=adapter.executeQuery(sq);
				TableHolder th=dh.getTable(PRODUCT_VARIANT);
				TableHolder sizeTh=dh.getTable(SIZE);
				TableHolder colorsTh=dh.getTable(COLORS);
				TableHolder invenTh=dh.getTable(PRODUCT_INVENTORY);
				if(th!=null) {
					Map<Integer,Row> rows=th.getRows();
					Map<Integer,Row> inventRows=invenTh.getRows();
					Map<Integer,Row> sizesRow=sizeTh.getRows();
					Map<Integer,Row> colorsRow=colorsTh.getRows();
					for(int i=0;i<rows.size();i++) {
						Row row=rows.get(i);
						Row inventRow=inventRows.get(i);
						Row colorRow=colorsRow.get(i);
						Row sizeRow=sizesRow.get(i);
						ProductVariant var=new ProductVariant();
						var.setVariantId((Long)row.get("id"));
						var.setPrice((Integer)row.get("price"));
						var.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name")));
						var.setSize(new Size((Long)sizeRow.get("id"), (String)sizeRow.get("name"),(Integer)sizeRow.get("ord"),(String)row.get("description")));
						var.setImageUrl((String)row.get("imageUrl"));

						productItem.getVariants().add(var);
					}
				}
				return productItem;
				
			}
	    }catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	public void deleteImages(String imagePath) {

		File file=new File(imagePath);
		if(file.exists()) {
		    file.delete();
		}
		
	}
	
	public void closeConnection() {
		try {
			adapter.closeConnection();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
   

	
	//COLORS
	public ArrayList<Color> getColors() {
		ArrayList<Color> colors=new ArrayList<Color>();
		try {
			SelectQuery sq=new SelectQuery(COLORS);
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder colorTh=dh.getTable(COLORS);
			if(colorTh!=null) {
				Map<Integer,Row> rows=colorTh.getRows();
				for(int i=0;i<rows.size();i++) {
					Row row=rows.get(i);
					Color color=new Color((Long)row.get("id"),(String) row.get("name"));
					colors.add(color);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return colors;
	}
	
	public ArrayList<Color> searchColorByName(String name){
		ArrayList<Color> colors=new ArrayList<Color>();
		try {
			SelectQuery sq=new SelectQuery(COLORS);
			Criteria criteria=new Criteria(new Column(COLORS, "name"),splitCharactersByPercentage(name));
			criteria.setComparator(Criteria.LIKE);
			sq.setCriteria(criteria);
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder colorTh=dh.getTable(COLORS);
			if(colorTh!=null) {
				Map<Integer,Row> rows=colorTh.getRows();
				for(int i=0;i<rows.size();i++) {
					Row row=rows.get(i);
					Color color=new Color((Long)row.get("id"),(String) row.get("name"));
					colors.add(color);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return colors;
	}
	
	public Color getColorById(Long colorId){
		
		try {
			SelectQuery sq=new SelectQuery(COLORS);
			Criteria criteria=new Criteria(new Column(COLORS, "id"),colorId);
			criteria.setComparator(Criteria.EQUAL);
			sq.setCriteria(criteria);
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder colorTh=dh.getTable(COLORS);
			if(colorTh!=null) {
				Map<Integer,Row> rows=colorTh.getRows();
				Row row=rows.get(0);
				Color color=new Color((Long)row.get("id"),(String) row.get("name"));
				return color;
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//SIZES
	public ArrayList<Size> getSizesByProductId(Long productId) throws Exception {
		ArrayList<Size> sizes=new ArrayList<Size>();
	
		SelectQuery sq=new SelectQuery(SIZE);
		Criteria criteria=new Criteria(new Column(SIZE,"productId"), productId);
		criteria.setComparator(Criteria.EQUAL);
		sq.setCriteria(criteria);
		sq.setOrderBy(new OrderBy(new Column(SIZE,"id"),Order.ASC));
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder sizeTh=dh.getTable(SIZE);
		if(sizeTh!=null) {
			Map<Integer,Row> rows=sizeTh.getRows();
			for(int i=0;i<rows.size();i++) {
				Row row=rows.get(i);
				Size size=new Size((Long)row.get("id"), (String)row.get("name"),(Integer)row.get("ord"),(String)row.get("description"));
			    sizes.add(size);
			} 
		}
		
		return sizes;
	}
	
	public Size getSizeBy(String param,Object value) {
		
		try {
			SelectQuery sq=new SelectQuery(SIZE);
			Criteria criteria=new Criteria(new Column(SIZE,param), value);
			criteria.setComparator(Criteria.EQUAL);
			sq.setCriteria(criteria);
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder sizeTh=dh.getTable(SIZE);
			if(sizeTh!=null) {
				Map<Integer,Row> rows=sizeTh.getRows();
				Row row=rows.get(0);
				Size size=new Size((Long)row.get("id"), (String)row.get("name"),(Integer)row.get("ord"),(String)row.get("description"));
			    Product product=new Product();
			    product.setProductId((Long)row.get("productId"));
			    size.setProduct(product);
				return size;
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public String createSize(Size size) {
    	try {
    		BeanValidator.setNullForEmptyString(size);
    		Size oldSize=getSizeBy("name",size.getName());
    		if(oldSize==null){
    			String fieldNames[]= {"name","productId","description","ord"};
    			TableHolder th=new TableHolder(SIZE, fieldNames);
    			
    			Row row=new Row();
    			Map<String,Object> columns=row.getColumns();
    			columns.put("name", size.getName());
    			columns.put("productId", size.getProduct().getProductId());
    			columns.put("description", size.getDescription());
    			columns.put("ord", size.getOrder());
    			th.setRow(row);
    			adapter.persistData(th);
    			return CREATED;
    		}
    		return EXIST;
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return FAILED;
    }
    
	public String editSize(Size size) {
		try {
			BeanValidator.setNullForEmptyString(size);
			Size oldSize=getSizeBy("id",size.getSizeId());
			if(oldSize!=null) {
				UpdateQuery uq=new UpdateQuery(SIZE);
				ArrayList<Column> cols=new ArrayList<Column>();
			    cols.add(new Column(SIZE,"productId",size.getProduct().getProductId()));
				cols.add(new Column(SIZE,"name",size.getName()));
				cols.add(new Column(SIZE,"ord",size.getOrder()));
				cols.add(new Column(SIZE,"description",size.getDescription()));
				uq.setFields(cols);
				Criteria criteria=new Criteria(new Column(SIZE,"id"),oldSize.getSizeId());
				criteria.setComparator(Criteria.EQUAL);
				uq.setCriteria(criteria);
				adapter.updateData(uq);
				return SUCCESS;
			}
			return NOT_EXIST;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return FAILED;
	}
	
	public void createTopic(Topic newTopic) throws Exception {
		
		Criteria criteria=new Criteria(new Column(TOPICS,"name"),newTopic.getName());
		criteria.setComparator(Criteria.EQUAL);
		
		Topic topic=getTopicByUniqueCriteria(criteria);
		if(topic==null) {
			String fieldNames[]= {"name"};
			TableHolder th=new TableHolder(TOPICS, fieldNames);
			Row row=new Row();
			Map<String,Object> columns=row.getColumns();
			columns.put("name", newTopic.getName());
			th.setRow(row);
			adapter.persistData(th);
		}else {
			new ExceptionCause("Topic name already exists!",HttpStatus.BAD_REQUEST);
		}
		
	}
	
	public void editTopic(Topic topic) throws Exception {
			
			Criteria criteria=new Criteria(new Column(TOPICS,"name"),topic.getName());
			criteria.setComparator(Criteria.EQUAL);
			
			Topic topicFromDB=getTopicByUniqueCriteria(criteria);
			if(topicFromDB==null || topic.getName().equals(topicFromDB.getName())) {
				UpdateQuery uq=new UpdateQuery(TOPICS);
				ArrayList<Column> cols=new ArrayList<Column>();
				cols.add(new Column(TOPICS,"name",topic.getName()));
				if(topic.getActive()!=null) {
					cols.add(new Column(TOPICS,"active",topic.getActive()));
				}
				uq.setFields(cols);
				Criteria crit=new Criteria(new Column(TOPICS,"id"),topic.getId());
				crit.setComparator(Criteria.EQUAL);
				uq.setCriteria(crit);
				adapter.updateData(uq);
			}else {
				new ExceptionCause("Topic name already exists!",HttpStatus.BAD_REQUEST);
		}
		
	}
	
	public ArrayList<Topic> getTopics() throws Exception {
		
		ArrayList<Topic> topics=new ArrayList<Topic>();
		
		SelectQuery sq=new SelectQuery(TOPICS);
		OrderBy orderby=new OrderBy(new Column(TOPICS,"id"),Order.ASC);
		sq.setOrderBy(orderby);
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(TOPICS);
		if(th!=null) {
			Map<Integer,Row> rows=th.getRows();
			for(int i=0;i<rows.size();i++) {
				Row row=rows.get(i);
				Topic topic=new Topic();
				topic.setId((Long)row.get("id"));
				topic.setName((String)row.get("name"));
				topic.setActive((Boolean)row.get("active"));
				topics.add(topic);
			}
		}
			
		return topics;
	}
	
	public Topic getTopicByUniqueCriteria(Criteria criteria) throws Exception {
		Topic topic=null;

		SelectQuery sq=new SelectQuery(TOPICS);
		sq.setCriteria(criteria);
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(TOPICS);
		if(th!=null && th.getRows().size()==1) {
			 topic=new Topic();
			 Row row=th.getRows().get(0);
			 topic.setId((Long)row.get("id"));
			 topic.setName((String)row.get("name"));
		}
			
		return topic;
	}
	
	public boolean isTopicItemRelationExist(Long topicId,Long itemId)throws Exception {
		
		SelectQuery sq=new SelectQuery(TOPIC_ITEM_RELATION);
		Criteria crit1=new Criteria(new Column(TOPIC_ITEM_RELATION,"topicId"),topicId);
		crit1.setComparator(Criteria.EQUAL);
		Criteria crit2=new Criteria(new Column(TOPIC_ITEM_RELATION,"itemId"),itemId);
		crit2.setComparator(Criteria.EQUAL);
		crit1.and(crit2);
		sq.setCriteria(crit2);
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(TOPIC_ITEM_RELATION);
		if(th!=null && th.getRows().size()==1) {
			return true;
		}else {
			return false;
		}
		
	}
	
	public void addProductItemToTopic(Long topicId,Long itemId) throws Exception {
		
		Criteria criteria=new Criteria(new Column(TOPICS,"id"),topicId);
		criteria.setComparator(Criteria.EQUAL);
		
		Topic topic=getTopicByUniqueCriteria(criteria);
		ProductItem item=getProductItemBy("id", itemId);
		if(topic!=null && item!=null) {
			if(!isTopicItemRelationExist(topicId, itemId)) {
				
					String fields[]= {"topicId","itemId"};
					TableHolder th=new TableHolder(TOPIC_ITEM_RELATION, fields);
					Row row=new Row();
					Map<String,Object> cols=row.getColumns();
					cols.put("itemId",itemId);
					cols.put("topicId",topicId);
					th.setRow(row);
					adapter.persistData(th);			
			}else {
				throw new ExceptionCause("Relation already exist!",HttpStatus.BAD_REQUEST);
			}
			
		}else {
			throw new ExceptionCause("topic id or item id not exist!",HttpStatus.BAD_REQUEST);
		}
	}
	
	public void removeItemTopicRelation(Long topicId,Long itemId) throws Exception {
	
    	if(isTopicItemRelationExist(topicId, itemId)) {
    		DeleteQuery dq=new DeleteQuery(TOPIC_ITEM_RELATION);
    		Criteria crit1=new Criteria(new Column(TOPIC_ITEM_RELATION,"topicId"),topicId);
			crit1.setComparator(Criteria.EQUAL);
			Criteria crit2=new Criteria(new Column(TOPIC_ITEM_RELATION,"itemId"),itemId);
			crit2.setComparator(Criteria.EQUAL);
			crit1.and(crit2);
			dq.setCriteria(crit1);
			adapter.deleteData(dq);
    	}else {
    		new ExceptionCause("Item Topic Relation not exist!", HttpStatus.BAD_REQUEST);
    	}
		
	}
	
	public void deleteTopicById(Long topicId) throws Exception {
	
		Criteria criteria=new Criteria(new Column(TOPICS,"id"),topicId);
		criteria.setComparator(Criteria.EQUAL);
		
		Topic topic=getTopicByUniqueCriteria(criteria);
		if(topic!=null) {
			DeleteQuery dq=new DeleteQuery(TOPICS);
			dq.setCriteria(criteria);
		    adapter.deleteData(dq);
		}else {
			throw new ExceptionCause("topic id not exist!", HttpStatus.BAD_REQUEST);
		}
		
	}
	
	//variant images
	public ArrayList<VariantImage> getVariantImagesByVariantId(Long variantId) throws Exception{
		ArrayList<VariantImage> images=new ArrayList<VariantImage>();
		
		SelectQuery sq=new SelectQuery(PRODUCT_IMAGES);
		Criteria criteria=new Criteria(new Column(PRODUCT_IMAGES,"variantId"), variantId);
		criteria.setComparator(Criteria.EQUAL);
		sq.setCriteria(criteria);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(PRODUCT_IMAGES);
		if(th!=null) {
			
			Map<Integer,Row> rows=th.getRows();
			
			for(int i=0;i<rows.size();i++) {
				Row row=rows.get(i);
				VariantImage image=new VariantImage();
				image.setImageId((Long)row.get("id"));
				ProductVariant variant=new ProductVariant();
				variant.setVariantId((Long)row.get("variantId"));
				image.setVariant(variant);
				image.setUrl((String)row.get("imageUrl"));
				image.setName((String)row.get("name"));
				image.setOrd((Integer)row.get("ord"));
				images.add(image);
			}
			
		}
		return images;
	}
	
	public VariantImage getVariantImageByNameAndVariantId(Long variantId,String name) throws Exception{
		
		
		SelectQuery sq=new SelectQuery(PRODUCT_IMAGES);
		Criteria criteria=new Criteria(new Column(PRODUCT_IMAGES,"variantId"), variantId);
		criteria.setComparator(Criteria.EQUAL);
		Criteria criteria2=new Criteria(new Column(PRODUCT_IMAGES,"name"), name);
		criteria2.setComparator(Criteria.EQUAL);
		criteria.and(criteria2);
		
		sq.setCriteria(criteria);
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(PRODUCT_IMAGES);
		if(th!=null && th.getRows().size()==1) {
			
			Row row=th.getRows().get(0);
			VariantImage image=new VariantImage();
			image.setImageId((Long)row.get("id"));
			ProductVariant variant=new ProductVariant();
			variant.setVariantId((Long)row.get("variantId"));
			image.setVariant(variant);
			image.setUrl((String)row.get("imageUrl"));
			image.setOrd((Integer)row.get("ord"));
			return image;
			
			
		}
		return null;
	}
	
    public VariantImage getVariantImageById(Long imageId) throws Exception{
		
		
		SelectQuery sq=new SelectQuery(PRODUCT_IMAGES);
		Criteria criteria=new Criteria(new Column(PRODUCT_IMAGES,"id"), imageId);
		criteria.setComparator(Criteria.EQUAL);
		sq.setCriteria(criteria);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(PRODUCT_IMAGES);
		if(th!=null && th.getRows().size()==1) {
			
			Row row=th.getRows().get(0);
			VariantImage image=new VariantImage();
			image.setImageId((Long)row.get("id"));
			ProductVariant variant=new ProductVariant();
			variant.setVariantId((Long)row.get("variantId"));
			image.setVariant(variant);
			image.setUrl((String)row.get("imageUrl"));
			image.setOrd((Integer)row.get("ord"));
			return image;
			
			
		}
		return null;
	}
	
	public void createImageForVariants(VariantImage imageInfo) throws Exception {
		
		String fieldNames[]= {"name","ord","variantId","imageUrl"};
		TableHolder th=new TableHolder(PRODUCT_IMAGES, fieldNames);
		Row row=new Row();
		Map<String,Object> columns=row.getColumns();
		columns.put("name",imageInfo.getName());
		columns.put("variantId",imageInfo.getVariant().getVariantId());
		columns.put("ord",imageInfo.getOrd());
		columns.put("imageUrl",imageInfo.getUrl());
		th.setRow(row);
		adapter.persistData(th);
		
	}
	
	public void deletImageById(Long imageId) throws Exception {
		String tomcatBase = System.getProperty("catalina.base")+"\\wtpwebapps\\shoppingapp\\resources\\img";
		VariantImage image=getVariantImageById(imageId);
		if(image==null) {
			throw new ExceptionCause("Invalid image id", HttpStatus.BAD_REQUEST);
		}
		
		DeleteQuery dq=new DeleteQuery(PRODUCT_IMAGES);
		Criteria criteria=new Criteria(new Column(PRODUCT_IMAGES,"id"), imageId);
		criteria.setComparator(Criteria.EQUAL);
		dq.setCriteria(criteria);
		
		int affected=adapter.deleteData(dq);
		
		if(affected>0) {
			String filePath=tomcatBase+File.separator+image.getUrl();
			deleteImages(filePath);
		}
	}
	
	public void editImage(VariantImage image) throws Exception {
		VariantImage imageFromDb=getVariantImageById(image.getImageId());
		
		if(imageFromDb==null) {
			throw new ExceptionCause("Image id is invalid", HttpStatus.BAD_REQUEST);
		}
		
		UpdateQuery uq=new UpdateQuery(PRODUCT_IMAGES);
		
		ArrayList<Column> cols=new ArrayList<Column>();
		cols.add(new Column(PRODUCT_IMAGES,"ord",image.getOrd()));
		uq.setFields(cols);
		Criteria crit=new Criteria(new Column(PRODUCT_IMAGES,"id"),image.getImageId());
		crit.setComparator(Criteria.EQUAL);
		uq.setCriteria(crit);
		adapter.updateData(uq);
		
	}
	
	//variant inventories
	
	@Override
	public ArrayList<Inventory> getInventoriesByVariantId(Long variantId) throws Exception {
		ArrayList<Inventory> inventories=new ArrayList<Inventory>();
		
		SelectQuery sq=new SelectQuery(PRODUCT_INVENTORY);
		Criteria criteria=new Criteria(new Column(PRODUCT_INVENTORY,"variantId"), variantId);
		criteria.setComparator(Criteria.EQUAL);
		sq.setCriteria(criteria);
		
		Join join=new Join(new Column(PRODUCT_INVENTORY,"sizeId"),new Column(SIZE, "id"),Join.LEFT_JOIN);
		sq.setJoin(join);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(PRODUCT_INVENTORY);
		TableHolder sizeth=dh.getTable(SIZE);
		if(th!=null && sizeth!=null) {
			
			Map<Integer,Row> rows=th.getRows();
			Map<Integer,Row> sizeRows=sizeth.getRows();
			for(int i=0;i<rows.size();i++) {
				Row row=rows.get(i);
				Row sizeRow=sizeRows.get(i);
				
				Inventory inventory=new Inventory();
				inventory.setInventoryId((Long)row.get("id"));
				inventory.setAvailableStocks((Integer)row.get("inventory"));
				Size size=new Size();
				size.setSizeId((Long)sizeRow.get("id"));
				size.setName((String)sizeRow.get("name"));
				inventory.setSize(size);
				inventories.add(inventory);
			
			}
			
		}
		return inventories;
	}
	
	public Inventory getInventoryById(Long id)throws Exception{
		Criteria criteria=new Criteria(new Column(PRODUCT_INVENTORY, "id"), id);
		criteria.setComparator(Criteria.EQUAL);
		return getInventoryByUnique(criteria);
	}
	
	public Inventory getInventoryByUnique(Criteria criteria) throws Exception {
		SelectQuery sq=new SelectQuery(PRODUCT_INVENTORY);
		sq.setCriteria(criteria);
		Join join=new Join(new Column(PRODUCT_INVENTORY,"sizeId"),new Column(SIZE, "id"),Join.LEFT_JOIN);
		sq.setJoin(join);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(PRODUCT_INVENTORY);
		TableHolder sizeth=dh.getTable(SIZE);
		if(th!=null && sizeth!=null) {
			
			Map<Integer,Row> rows=th.getRows();
			Map<Integer,Row> sizeRows=sizeth.getRows();
	        if(rows.size()==1 && sizeRows.size()==1) {
				Row row=rows.get(0);
				Row sizeRow=sizeRows.get(0);
				
				Inventory inventory=new Inventory();
				inventory.setInventoryId((Long)row.get("id"));
				inventory.setAvailableStocks((Integer)row.get("inventory"));
				Size size=new Size();
				size.setSizeId((Long)sizeRow.get("id"));
				size.setName((String)sizeRow.get("name"));
				inventory.setSize(size);
				return inventory;
	        }
			
		}
		
		return null;
	}
	
	@Override
	public void createInventories(Inventory inventory) throws Exception {
		Criteria criteria=new Criteria(new Column(PRODUCT_INVENTORY, "variantId"), inventory.getVariant().getVariantId());
		criteria.setComparator(Criteria.EQUAL);
		
		Criteria criteria2=new Criteria(new Column(PRODUCT_INVENTORY, "sizeId"), inventory.getSize().getSizeId());
		criteria2.setComparator(Criteria.EQUAL);
		
		criteria.and(criteria2);
		
		Inventory inventoryFromDB=getInventoryByUnique(criteria);
		if(inventoryFromDB!=null) {
			throw new ExceptionCause("Inventory record for this size under this inventory already exist!",HttpStatus.BAD_REQUEST);
		}
		
		String fields[]= {"sizeId","variantId","inventory"};
		TableHolder th=new TableHolder(PRODUCT_INVENTORY, fields);
		Row row=new Row();
		Map<String,Object> cols=row.getColumns();
		cols.put("sizeId",inventory.getSize().getSizeId());
		cols.put("inventory",inventory.getAvailableStocks());
		cols.put("variantId", inventory.getVariant().getVariantId());
		th.setRow(row);
		
		adapter.persistData(th);
		
		
	}
	
	@Override
	public void editInventories(Inventory inventory) throws Exception {
		Criteria criteria=new Criteria(new Column(PRODUCT_INVENTORY, "variantId"), inventory.getVariant().getVariantId());
		criteria.setComparator(Criteria.EQUAL);
		
		Criteria criteria2=new Criteria(new Column(PRODUCT_INVENTORY, "sizeId"), inventory.getSize().getSizeId());
		criteria2.setComparator(Criteria.EQUAL);
		
		criteria.and(criteria2);
		
		UpdateQuery uq=new UpdateQuery(PRODUCT_INVENTORY);
		ArrayList<Column> cols=new ArrayList<Column>();
		cols.add(new Column(PRODUCT_INVENTORY,"inventory",inventory.getAvailableStocks()));
		uq.setFields(cols);
		
		uq.setCriteria(criteria);
		
		adapter.updateData(uq);
	}
	
	@Override
	public void deleteInventories(Long inventoryId) throws Exception {

		Criteria criteria=new Criteria(new Column(PRODUCT_INVENTORY, "id"), inventoryId);
		criteria.setComparator(Criteria.EQUAL);
		
		DeleteQuery dq=new DeleteQuery(PRODUCT_INVENTORY);
		dq.setCriteria(criteria);
		
		adapter.deleteData(dq);
	}
	
	
	//helper methods
	private String splitCharactersByPercentage(String arg) {
		StringBuilder builder=new StringBuilder();
		char chArr[]=arg.toCharArray();
	
		for(int i=0;i<chArr.length;i++) {
			builder.append(chArr[i]);
			builder.append('%');
		}
		
		return builder.toString();
	}
	


	
}
