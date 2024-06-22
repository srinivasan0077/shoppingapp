package com.shoppingapp.shopUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import com.shoppingapp.dbutils.Column;
import com.shoppingapp.dbutils.Criteria;
import com.shoppingapp.dbutils.DBAdapter;
import com.shoppingapp.dbutils.DataHolder;
import com.shoppingapp.dbutils.DeleteQuery;
import com.shoppingapp.dbutils.Join;
import com.shoppingapp.dbutils.OrderBy;
import com.shoppingapp.dbutils.Row;
import com.shoppingapp.dbutils.SelectQuery;
import com.shoppingapp.dbutils.TableHolder;
import com.shoppingapp.dbutils.UpdateQuery;
import com.shoppingapp.dbutils.OrderBy.Order;
import com.shoppingapp.entities.Address;
import com.shoppingapp.entities.BannerImage;
import com.shoppingapp.entities.Cart;
import com.shoppingapp.entities.Category;
import com.shoppingapp.entities.Color;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;
import com.shoppingapp.entities.OrderEntity;
import com.shoppingapp.entities.OrderItems;
import com.shoppingapp.entities.Product;
import com.shoppingapp.entities.ProductItem;
import com.shoppingapp.entities.ProductVariant;
import com.shoppingapp.entities.Size;
import com.shoppingapp.entities.Topic;
import com.shoppingapp.entities.User;
import com.shoppingapp.entities.VariantImage;
import com.shoppingapp.entities.OrderEntity.Status;
import com.shoppingapp.paymentservice.StripePaymentUtil;
import com.shoppingapp.productUtils.ProductManagementUtil;
import com.shoppingapp.utils.AWSSimpleEmailService;
import com.shoppingapp.utils.ExceptionCause;
import com.shoppingapp.utils.HtmlTemplates;
import com.shoppingapp.utils.ThreadLocalUtil;
import com.stripe.model.PaymentIntent;

public class ShoppingUtil implements ShoppingUtilInterface {
	
	private static final Logger logger=LogManager.getLogger(ShoppingUtil.class);
	private static List<String> adminEmails;
	
	static {
		adminEmails=new ArrayList<>();
		adminEmails.add("srinivasandhandapani071201@gmail.com");
		adminEmails.add("ramnathdaising@gmail.com");
	}
	
	//adapter
	private DBAdapter adapter;
	
	public ShoppingUtil(DBAdapter adapter){
		this.adapter=adapter;
	}
	
	public void closeConnection() {
		try {
			adapter.closeConnection();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Topic> getTopicsToDisplay(GetInfo info) throws Exception{
		ArrayList<Topic> topics=new ArrayList<>();
		SelectQuery sq=new SelectQuery(TableNames.TOPICS);
		
		SelectQuery subQuery=new SelectQuery(TableNames.TOPICS);
		 
		ArrayList<Column> fields=new ArrayList<>();
		fields.add(new Column(TableNames.TOPICS,"id"));
		subQuery.setFields(fields);
		 
		Criteria sqCriteria=new Criteria(new Column(TableNames.TOPICS, "active"), true);
		sqCriteria.setComparator(Criteria.EQUAL);
		 
		if(info.getPaginationKey()!=null) {
				Criteria sqCriteria2=new Criteria(new Column(TableNames.TOPICS,"id"), info.getPaginationKey());
				sqCriteria2.setComparator(Criteria.GREATER);
				sqCriteria.and(sqCriteria2);
	    }
		subQuery.setCriteria(sqCriteria);
		subQuery.setOrderBy(new OrderBy(new Column(TableNames.TOPICS,"id"), Order.ASC));
		subQuery.setLimit(3);
		
		
		Join criteriaJoin=new Join(subQuery,new Column(TableNames.TOPICS,"id"),new Column(TableNames.TOPICS,"id"),"filter",Join.INNER_JOIN);	 
		Join join1=new Join(new Column(TableNames.TOPICS,"id"),new Column(TableNames.TOPIC_VARIANT_RELATION,"topicId"),Join.INNER_JOIN);
		Join join2=new Join(new Column(TableNames.TOPIC_VARIANT_RELATION,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
		Join join3=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
		Join join4=new Join(new Column(TableNames.PRODUCT_VARIANT,"colorId"),new Column(TableNames.COLORS,"id"),Join.INNER_JOIN);
		Join join5=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_IMAGES,"variantId"),Join.INNER_JOIN);
		Join join6=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_INVENTORY,"variantId"),Join.INNER_JOIN);
		Join join7=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE, "id"),Join.INNER_JOIN);

		sq.setJoin(criteriaJoin);
		sq.addJoin(join1);
		sq.addJoin(join2);
		sq.addJoin(join3);
		sq.addJoin(join4);
		sq.addJoin(join5);
		sq.addJoin(join6);
		sq.addJoin(join7);
		
		sq.setOrderBy(new OrderBy(new Column(TableNames.TOPICS,"id"), Order.ASC));
		
		Criteria criteria2=new Criteria(new Column(TableNames.PRODUCT_ITEM, "isActive"), true);
		criteria2.setComparator(Criteria.EQUAL);
		
		Criteria criteria3=new Criteria(new Column(TableNames.PRODUCT_VARIANT, "isActive"), true);
		criteria3.setComparator(Criteria.EQUAL);
		
		criteria2.and(criteria3);
		
		sq.setCriteria(criteria2);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(TableNames.TOPICS);
		TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
		TableHolder itemTh=dh.getTable(TableNames.PRODUCT_ITEM);
		TableHolder colorTh=dh.getTable(TableNames.COLORS);
		TableHolder imageTh=dh.getTable(TableNames.PRODUCT_IMAGES);
		TableHolder inventoryTh=dh.getTable(TableNames.PRODUCT_INVENTORY);
		TableHolder sizeTh=dh.getTable(TableNames.SIZE);
		Map<Long,Integer> topicIndexMap=new HashMap<>();
		Map<String,Integer> variantIndexMap=new HashMap<>();
		Map<String,Integer> inventoryIndexMap=new HashMap<>();
		Map<String,Integer> imageIndexMap=new HashMap<>();
		if(th!=null) {
			Map<Integer,Row> topicRows=th.getRows();
			Map<Integer,Row> variantRows=variantTh.getRows();
			Map<Integer,Row> itemRows=itemTh.getRows();
			Map<Integer,Row> colorRows=colorTh.getRows();
			Map<Integer,Row> imageRows=imageTh.getRows();
			Map<Integer,Row> inventoryRows=inventoryTh.getRows();
			Map<Integer,Row> sizeRows=sizeTh.getRows();
			for(int i=0;i<topicRows.size();i++) {
				Row row=topicRows.get(i);
				Row variantRow=variantRows.get(i);
				Row colorRow=colorRows.get(i);
				Row itemRow=itemRows.get(i);
				Row imageRow=imageRows.get(i);
				Row inventoryRow=inventoryRows.get(i);
				Row sizeRow=sizeRows.get(i);
				if(topicIndexMap.containsKey((Long)row.get("id"))) {
					Topic topic=topics.get(topicIndexMap.get((Long)row.get("id")));
					if(variantIndexMap.containsKey(String.valueOf(topic.getId())+":"+(Long)variantRow.get("id"))) {
						ProductVariant variant=topic.getVariants().get(variantIndexMap.get(String.valueOf(String.valueOf(topic.getId())+":"+(Long)variantRow.get("id"))));
						if(!imageIndexMap.containsKey(topic.getId()+":"+String.valueOf(variant.getVariantId())+":"+(Long)imageRow.get("id"))) {
							ArrayList<VariantImage> images=variant.getImages();
							VariantImage image=new VariantImage();
							image.setImageId((Long)imageRow.get("id"));
							image.setUrl((String)imageRow.get("imageUrl"));
							image.setName((String)imageRow.get("name"));
							image.setOrd((Integer)imageRow.get("ord"));
							imageIndexMap.put(topic.getId()+":"+String.valueOf(variant.getVariantId())+":"+image.getImageId(),images.size());
							images.add(image);
						}
						
						if(!inventoryIndexMap.containsKey(topic.getId()+":"+String.valueOf(variant.getVariantId())+":"+(Long)inventoryRow.get("id"))) {
							ArrayList<Inventory> inventories=variant.getInventories();
							Inventory inventory=new Inventory();
							inventory.setInventoryId((Long)inventoryRow.get("id"));
							inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
							Size size=new Size();
							size.setSizeId((Long)sizeRow.get("id"));
							size.setName((String)sizeRow.get("name"));
							inventory.setSize(size);
							inventoryIndexMap.put(topic.getId()+":"+String.valueOf(variant.getVariantId())+":"+inventory.getInventoryId(),inventories.size());
							inventories.add(inventory);
						}
					}else {
						ArrayList<ProductVariant> variants=topic.getVariants();
						ProductVariant variant=new ProductVariant();
						variant.setVariantId((Long)variantRow.get("id"));
						variant.setName((String)variantRow.get("name"));
						variant.setPrice((Integer)variantRow.get("price"));
						variant.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name"),(String)colorRow.get("csscolor")));
						//set product item in variant
						ProductItem item=new ProductItem();
						item.setProductItemId((Long)itemRow.get("id"));
						item.setProductItemName((String)itemRow.get("name"));
						item.setDescription(itemRow.get("description")==null?"":(String)itemRow.get("description"));
						item.setModifiedAt((Long)itemRow.get("modifiedat"));
						item.setCreatedAt((Long)itemRow.get("createdat"));
						item.setIsActive((Boolean)itemRow.get("isActive"));
						variant.setItem(item);
						//set images
						ArrayList<VariantImage> images=new ArrayList<>();
						VariantImage image=new VariantImage();
						image.setImageId((Long)imageRow.get("id"));
						image.setUrl((String)imageRow.get("imageUrl"));
						image.setName((String)imageRow.get("name"));
						image.setOrd((Integer)imageRow.get("ord"));
						imageIndexMap.put(topic.getId()+":"+String.valueOf(variant.getVariantId())+":"+image.getImageId(),images.size());
						images.add(image);
						variant.setImages(images);
						//set inventories
						ArrayList<Inventory> inventories=new ArrayList<>();
						Inventory inventory=new Inventory();
						inventory.setInventoryId((Long)inventoryRow.get("id"));
						inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
						Size size=new Size();
						size.setSizeId((Long)sizeRow.get("id"));
						size.setName((String)sizeRow.get("name"));
						inventory.setSize(size);
						inventoryIndexMap.put(topic.getId()+":"+String.valueOf(variant.getVariantId())+":"+inventory.getInventoryId(),inventories.size());
						inventories.add(inventory);
						variant.setInventories(inventories);
					
						variantIndexMap.put(String.valueOf(topic.getId())+":"+variant.getVariantId(),variants.size());
						variants.add(variant);
					}
					
				}else {
					Topic topic=new Topic();
					topic.setId((Long)row.get("id"));
					topic.setName((String)row.get("name"));
					
					ArrayList<ProductVariant> variants=new ArrayList<>();
					ProductVariant variant=new ProductVariant();
					variant.setVariantId((Long)variantRow.get("id"));
					variant.setName((String)variantRow.get("name"));
					variant.setPrice((Integer)variantRow.get("price"));
					variant.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name"),(String)colorRow.get("csscolor")));
					//set product item in variant
					ProductItem item=new ProductItem();
					item.setProductItemId((Long)itemRow.get("id"));
					item.setProductItemName((String)itemRow.get("name"));
					item.setDescription(itemRow.get("description")==null?"":(String)itemRow.get("description"));
					item.setModifiedAt((Long)itemRow.get("modifiedat"));
					item.setCreatedAt((Long)itemRow.get("createdat"));
					item.setIsActive((Boolean)itemRow.get("isActive"));
					variant.setItem(item);
					//set images
					ArrayList<VariantImage> images=new ArrayList<>();
					VariantImage image=new VariantImage();
					image.setImageId((Long)imageRow.get("id"));
					image.setUrl((String)imageRow.get("imageUrl"));
					image.setName((String)imageRow.get("name"));
					image.setOrd((Integer)imageRow.get("ord"));
					imageIndexMap.put(topic.getId()+":"+String.valueOf(variant.getVariantId())+":"+image.getImageId(),images.size());
					images.add(image);
					variant.setImages(images);
					//set inventories
					ArrayList<Inventory> inventories=new ArrayList<>();
					Inventory inventory=new Inventory();
					inventory.setInventoryId((Long)inventoryRow.get("id"));
					inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
					Size size=new Size();
					size.setSizeId((Long)sizeRow.get("id"));
					size.setName((String)sizeRow.get("name"));
					inventory.setSize(size);
					inventoryIndexMap.put(topic.getId()+":"+String.valueOf(variant.getVariantId())+":"+inventory.getInventoryId(),inventories.size());
					inventories.add(inventory);
					variant.setInventories(inventories);
				
					variantIndexMap.put(String.valueOf(topic.getId())+":"+variant.getVariantId(),variants.size());
					variants.add(variant);
					topic.setVariants(variants);
					
					topicIndexMap.put(topic.getId(),topics.size());
					topics.add(topic);
					
				}
			}
		}
		return topics;
		
	}
	
	
	public void addItemToCart(Cart cart) throws Exception {
		User user=ThreadLocalUtil.currentUser.get();
		int count=cart.getCount()<1?1:cart.getCount();
		Inventory inventory=cart.getInventory();
		
		SelectQuery sq=new SelectQuery(TableNames.CART);
		
		Criteria criteria1=new Criteria(new Column(TableNames.CART,"userId"),user.getUserid());
		criteria1.setComparator(Criteria.EQUAL);
		Criteria criteria2=new Criteria(new Column(TableNames.CART,"inventoryId"),inventory.getInventoryId());
		criteria2.setComparator(Criteria.EQUAL);
		criteria1.and(criteria2);
		
		sq.setCriteria(criteria1);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder cartTh=dh.getTable(TableNames.CART);
		
		Criteria inventoryCriteria=new Criteria(new Column(TableNames.PRODUCT_INVENTORY,"id"),inventory.getInventoryId());
		inventoryCriteria.setComparator(Criteria.EQUAL);
		Inventory inventoryFromDB=getInventoryByUnique(inventoryCriteria);
		
		if(inventoryFromDB==null) {
			throw new ExceptionCause("Cannot add item to cart!",HttpStatus.BAD_REQUEST);
		}
		
		if(getCartCount()+count>50) {
			 throw new ExceptionCause("Cannot add more than 50 items to cart!",HttpStatus.BAD_REQUEST);
		}
		
		if(count>inventoryFromDB.getAvailableStocks()) {
	    	 throw new ExceptionCause("Only "+inventoryFromDB.getAvailableStocks()+" items available!",HttpStatus.BAD_REQUEST);
	    }
		
		if(cartTh!=null && cartTh.getRows().size()==1) {
			     Row row=cartTh.getRows().get(0);
			  
			     if(((Integer)row.get("count"))+count>inventoryFromDB.getAvailableStocks()) {
			    	 throw new ExceptionCause("Item not avialable.Please check your cart!",HttpStatus.BAD_REQUEST);
			     }
			     UpdateQuery uq=new UpdateQuery(TableNames.CART);
			     uq.setCriteria(criteria1);
			     ArrayList<Column> cols=new ArrayList<Column>();
				 cols.add(new Column(TableNames.CART,"count",(Integer)row.get("count")+count));
				 uq.setFields(cols);
				 adapter.updateData(uq);

		}else {
			//Configurations
			String[] fieldNames= {"userId","inventoryId","count"};
			TableHolder th=new TableHolder(TableNames.CART,fieldNames);
			
			//Add data
			Row row=new Row();
			Map<String,Object> columns=row.getColumns();

			columns.put("userId",user.getUserid());
			columns.put("inventoryId",inventory.getInventoryId());
			columns.put("count",count);
            th.setRow(row);
            
            adapter.persistData(th);
		}
		
		
	}
	
	public void removeItemFromCart(Cart cart) throws Exception {
		User user=ThreadLocalUtil.currentUser.get();
		Inventory inventory=cart.getInventory();
		
		SelectQuery sq=new SelectQuery(TableNames.CART);
		
		Criteria criteria1=new Criteria(new Column(TableNames.CART,"userId"),user.getUserid());
		criteria1.setComparator(Criteria.EQUAL);
		Criteria criteria2=new Criteria(new Column(TableNames.CART,"inventoryId"),inventory.getInventoryId());
		criteria2.setComparator(Criteria.EQUAL);
		criteria1.and(criteria2);
		
		sq.setCriteria(criteria1);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder cartTh=dh.getTable(TableNames.CART);
		
		Criteria inventoryCriteria=new Criteria(new Column(TableNames.PRODUCT_INVENTORY,"id"),inventory.getInventoryId());
		inventoryCriteria.setComparator(Criteria.EQUAL);
		Inventory inventoryFromDB=getInventoryByUnique(inventoryCriteria);
		
		if(inventoryFromDB==null || cartTh==null) {
			throw new ExceptionCause("Item not found in cart!",HttpStatus.BAD_REQUEST);
		}
		
		if(cartTh!=null && cartTh.getRows().size()==1) {
			     Row row=cartTh.getRows().get(0);
			     if(((Integer)row.get("count"))-1==0) {
			    	 Criteria criteria=new Criteria(new Column(TableNames.CART, "cartId"),(Long)row.get("cartId"));
			 		 criteria.setComparator(Criteria.EQUAL);
			 		
			 		 DeleteQuery dq=new DeleteQuery(TableNames.CART);
			 		 dq.setCriteria(criteria);
			 		
			 		 adapter.deleteData(dq);
			     }else {
				     UpdateQuery uq=new UpdateQuery(TableNames.CART);
				     uq.setCriteria(criteria1);
				     ArrayList<Column> cols=new ArrayList<Column>();
					 cols.add(new Column(TableNames.CART,"count",(Integer)row.get("count")-1));
					 uq.setFields(cols);
					 adapter.updateData(uq);
			     }

		}
	}
	
	public int getCartCount() throws Exception{
		User user=ThreadLocalUtil.currentUser.get();
		SelectQuery sq=new SelectQuery(TableNames.CART);
		
		Join join1=new Join(new Column(TableNames.CART,"inventoryId"),new Column(TableNames.PRODUCT_INVENTORY,"id"),Join.INNER_JOIN);
		Join join2=new Join(new Column(TableNames.PRODUCT_INVENTORY,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
		Join join3=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
		
		sq.setJoin(join1);
		sq.addJoin(join2);
		sq.addJoin(join3);
		
		Criteria criteria1=new Criteria(new Column(TableNames.CART,"userId"),user.getUserid());
		criteria1.setComparator(Criteria.EQUAL);
		Criteria criteria2=new Criteria(new Column(TableNames.PRODUCT_VARIANT,"isActive"),true);
		criteria2.setComparator(Criteria.EQUAL);
		Criteria criteria3=new Criteria(new Column(TableNames.PRODUCT_ITEM,"isActive"),true);
		criteria3.setComparator(Criteria.EQUAL);
		
		criteria2.and(criteria3);
		criteria1.and(criteria2);
		sq.setCriteria(criteria1);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder cartTh=dh.getTable(TableNames.CART);

		int count=0;
		if(cartTh!=null) {
			Map<Integer,Row> cartRows=cartTh.getRows();

			for(int i=0;i<cartRows.size();i++) {
				Row cartRow=cartRows.get(i);
				count+=(Integer)cartRow.get("count");
			}
			
		}
		return count;
	}
	
	public Inventory getInventoryByUnique(Criteria criteria) throws Exception {
		SelectQuery sq=new SelectQuery(TableNames.PRODUCT_INVENTORY);
		sq.setCriteria(criteria);
		Join join=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE, "id"),Join.LEFT_JOIN);
		sq.setJoin(join);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(TableNames.PRODUCT_INVENTORY);
		TableHolder sizeth=dh.getTable(TableNames.SIZE);
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
	
	public List<Cart> getCartOfUser() throws Exception{
		User user=ThreadLocalUtil.currentUser.get();
		List<Cart> carts=new ArrayList<>();
		SelectQuery sq=new SelectQuery(TableNames.CART);
		Criteria criteria1=new Criteria(new Column(TableNames.CART,"userId"),user.getUserid());
		criteria1.setComparator(Criteria.EQUAL);
		Criteria criteria2=new Criteria(new Column(TableNames.PRODUCT_IMAGES,"ord"),1);
		criteria2.setComparator(Criteria.EQUAL);
		criteria1.and(criteria2);
		sq.setCriteria(criteria1);
		
		Join join1=new Join(new Column(TableNames.CART,"inventoryId"),new Column(TableNames.PRODUCT_INVENTORY,"id"),Join.INNER_JOIN);
		Join join2=new Join(new Column(TableNames.PRODUCT_INVENTORY,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
		Join join3=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
		Join join4=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE, "id"),Join.INNER_JOIN);
		Join join5=new Join(new Column(TableNames.PRODUCT_VARIANT,"colorId"),new Column(TableNames.COLORS,"id"),Join.INNER_JOIN);
		Join join6=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_IMAGES,"variantId"),Join.INNER_JOIN);
		sq.setJoin(join1);
		sq.addJoin(join2);
		sq.addJoin(join3);
		sq.addJoin(join4);
		sq.addJoin(join5);
		sq.addJoin(join6);
		
		sq.setOrderBy(new OrderBy(new Column(TableNames.CART,"cartId"),Order.DESC));
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder cartTh=dh.getTable(TableNames.CART);
		TableHolder inventoryTh=dh.getTable(TableNames.PRODUCT_INVENTORY);
		TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
		TableHolder itemTh=dh.getTable(TableNames.PRODUCT_ITEM);
		TableHolder sizeTh=dh.getTable(TableNames.SIZE);
		TableHolder colorTh=dh.getTable(TableNames.COLORS);
		TableHolder imagesTh=dh.getTable(TableNames.PRODUCT_IMAGES);
		if(cartTh!=null) {
			Map<Integer,Row> cartRows=cartTh.getRows();
			Map<Integer,Row> inventoryRows=inventoryTh.getRows();
			Map<Integer,Row> variantRows=variantTh.getRows();
			Map<Integer,Row> itemRows=itemTh.getRows();
			Map<Integer,Row> sizeRows=sizeTh.getRows();
			Map<Integer,Row> colorRows=colorTh.getRows();
			Map<Integer,Row> imageRows=imagesTh.getRows();
			for(int i=0;i<cartRows.size();i++) {
				Row cartRow=cartRows.get(i);
				Row inventoryRow=inventoryRows.get(i);
				Row variantRow=variantRows.get(i);
				Row itemRow=itemRows.get(i);
				Row sizeRow=sizeRows.get(i);
				Row colorRow=colorRows.get(i);
				Row imageRow=imageRows.get(i);
				 
				Cart cart=new Cart();
				cart.setCartId((Long)cartRow.get("cartId"));
				cart.setCount((Integer)cartRow.get("count"));
				Inventory inventory=new Inventory();
				inventory.setInventoryId((Long)inventoryRow.get("id"));
				Size size=new Size();
				size.setSizeId((Long)sizeRow.get("id"));
				size.setName((String)sizeRow.get("name"));
				inventory.setSize(size);
				ProductVariant variant=new ProductVariant();
				variant.setVariantId((Long)variantRow.get("id"));
				variant.setName((String)variantRow.get("name"));
				variant.setPrice((Integer)variantRow.get("price"));
				variant.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name"),(String)colorRow.get("csscolor")));
				//set product item in variant
				ProductItem item=new ProductItem();
				item.setProductItemId((Long)itemRow.get("id"));
				item.setProductItemName((String)itemRow.get("name"));
				item.setDescription(itemRow.get("description")==null?"":(String)itemRow.get("description"));
				item.setModifiedAt((Long)itemRow.get("modifiedat"));
				item.setCreatedAt((Long)itemRow.get("createdat"));
				item.setIsActive((Boolean)itemRow.get("isActive"));
				variant.setItem(item);
				//set images
				ArrayList<VariantImage> images=new ArrayList<>();
				VariantImage image=new VariantImage();
				image.setImageId((Long)imageRow.get("id"));
				image.setUrl((String)imageRow.get("imageUrl"));
				image.setName((String)imageRow.get("name"));
				image.setOrd((Integer)imageRow.get("ord"));
				images.add(image);
				variant.setImages(images);
				
				inventory.setVariant(variant);
				inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
				cart.setInventory(inventory);
				
				carts.add(cart);
				
			}
		}
		
		return carts;
	}
	
	
	
	public List<Inventory> getInventories(List<Inventory> inventories) throws Exception{
		List<Inventory> inventoryDetails=new ArrayList<>();
		ArrayList<Object> inventoryIds=new ArrayList<>();
		for(int i=0;i<inventories.size();i++) {
			inventoryIds.add(inventories.get(i).getInventoryId());
		}
		
		if(inventoryIds.size()>0) {

			SelectQuery sq=new SelectQuery(TableNames.PRODUCT_INVENTORY);
			Criteria criteria=new Criteria(new Column(TableNames.PRODUCT_INVENTORY,"id"));
			criteria.setDataCollection(inventoryIds);
			criteria.setComparator(Criteria.IN);
			Criteria criteria2=new Criteria(new Column(TableNames.PRODUCT_IMAGES,"ord"),1);
			criteria2.setComparator(Criteria.EQUAL);
			Criteria criteria3=new Criteria(new Column(TableNames.PRODUCT_VARIANT,"isActive"),true);
			criteria3.setComparator(Criteria.EQUAL);
			Criteria criteria4=new Criteria(new Column(TableNames.PRODUCT_ITEM,"isActive"),true);
			criteria4.setComparator(Criteria.EQUAL);
			criteria.and(criteria2);
			criteria2.and(criteria3);
			criteria3.and(criteria4);
			
			sq.setCriteria(criteria);
			
			Join join1=new Join(new Column(TableNames.PRODUCT_INVENTORY,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
			Join join2=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
			Join join3=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE, "id"),Join.INNER_JOIN);
			Join join4=new Join(new Column(TableNames.PRODUCT_VARIANT,"colorId"),new Column(TableNames.COLORS,"id"),Join.INNER_JOIN);
			Join join5=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_IMAGES,"variantId"),Join.INNER_JOIN);
			
			sq.setJoin(join1);
			sq.addJoin(join2);
			sq.addJoin(join3);
			sq.addJoin(join4);
			sq.addJoin(join5);
			DataHolder dh=adapter.executeQuery(sq);

			TableHolder inventoryTh=dh.getTable(TableNames.PRODUCT_INVENTORY);
			TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
			TableHolder itemTh=dh.getTable(TableNames.PRODUCT_ITEM);
			TableHolder sizeTh=dh.getTable(TableNames.SIZE);
			TableHolder colorTh=dh.getTable(TableNames.COLORS);
			TableHolder imagesTh=dh.getTable(TableNames.PRODUCT_IMAGES);
			
			
			if(inventoryTh!=null) {
				Map<Integer,Row> inventoryRows=inventoryTh.getRows();
				Map<Integer,Row> variantRows=variantTh.getRows();
				Map<Integer,Row> itemRows=itemTh.getRows();
				Map<Integer,Row> sizeRows=sizeTh.getRows();
				Map<Integer,Row> colorRows=colorTh.getRows();
				Map<Integer,Row> imageRows=imagesTh.getRows();
				for(int i=0;i<inventoryRows.size();i++) {
					Row inventoryRow=inventoryRows.get(i);
					Row variantRow=variantRows.get(i);
					Row itemRow=itemRows.get(i);
					Row sizeRow=sizeRows.get(i);
					Row colorRow=colorRows.get(i);
	                Row imageRow=imageRows.get(i);
				   
	                Inventory inventory=new Inventory();
					inventory.setInventoryId((Long)inventoryRow.get("id"));
					Size size=new Size();
					size.setSizeId((Long)sizeRow.get("id"));
					size.setName((String)sizeRow.get("name"));
					inventory.setSize(size);
					ProductVariant variant=new ProductVariant();
					variant.setVariantId((Long)variantRow.get("id"));
					variant.setName((String)variantRow.get("name"));
					variant.setPrice((Integer)variantRow.get("price"));
					variant.setIsCOD((Boolean)variantRow.get("cod"));
					variant.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name"),(String)colorRow.get("csscolor")));
					//set product item in variant
					ProductItem item=new ProductItem();
					item.setProductItemId((Long)itemRow.get("id"));
					item.setProductItemName((String)itemRow.get("name"));
					item.setDescription(itemRow.get("description")==null?"":(String)itemRow.get("description"));
					item.setModifiedAt((Long)itemRow.get("modifiedat"));
					item.setCreatedAt((Long)itemRow.get("createdat"));
					item.setIsActive((Boolean)itemRow.get("isActive"));
					variant.setItem(item);
					//set images
					ArrayList<VariantImage> images=new ArrayList<>();
					VariantImage image=new VariantImage();
					image.setImageId((Long)imageRow.get("id"));
					image.setUrl((String)imageRow.get("imageUrl"));
					image.setName((String)imageRow.get("name"));
					image.setOrd((Integer)imageRow.get("ord"));
					images.add(image);
					variant.setImages(images);
					inventory.setVariant(variant);
					inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
					
					inventoryDetails.add(inventory);
								
				}
			}
			
			
		}
		return inventoryDetails;
	}
	
	public ProductVariant getVariantToView(Long variantId) throws Exception {
		SelectQuery sq=new SelectQuery(TableNames.PRODUCT_VARIANT);
		Criteria criteria=new Criteria(new Column(TableNames.PRODUCT_VARIANT,"id"), variantId);
        criteria.setComparator(Criteria.EQUAL);

		sq.setCriteria(criteria);
		Join join1=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
		Join join2=new Join(new Column(TableNames.PRODUCT_VARIANT,"colorId"),new Column(TableNames.COLORS,"id"),Join.INNER_JOIN);
		Join join3=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_IMAGES,"variantId"),Join.INNER_JOIN);
		Join join4=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_INVENTORY,"variantId"),Join.INNER_JOIN);
		Join join5=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE, "id"),Join.INNER_JOIN);
		sq.setJoin(join1);
		sq.addJoin(join2);
		sq.addJoin(join3);
		sq.addJoin(join4);
		sq.addJoin(join5);
		sq.setOrderBy(new OrderBy(new Column(TableNames.SIZE,"ord"),Order.ASC));
	    DataHolder dh=adapter.executeQuery(sq);
	    TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
		TableHolder itemTh=dh.getTable(TableNames.PRODUCT_ITEM);
		TableHolder colorTh=dh.getTable(TableNames.COLORS);
		TableHolder imageTh=dh.getTable(TableNames.PRODUCT_IMAGES);
		TableHolder inventoryTh=dh.getTable(TableNames.PRODUCT_INVENTORY);
		TableHolder sizeTh=dh.getTable(TableNames.SIZE);
		
		if(variantTh!=null) {
			ProductVariant variant=new ProductVariant();
			Map<Integer,Row> variantRows=variantTh.getRows();
			Map<Integer,Row> itemRows=itemTh.getRows();
			Map<Integer,Row> colorRows=colorTh.getRows();
			Map<Integer,Row> imageRows=imageTh.getRows();
			Map<Integer,Row> inventoryRows=inventoryTh.getRows();
			Map<Integer,Row> sizeRows=sizeTh.getRows();
			
			Row variantRow=variantRows.get(0);
			Row colorRow=colorRows.get(0);
			Row itemRow=itemRows.get(0);

			variant.setVariantId((Long)variantRow.get("id"));
			variant.setName((String)variantRow.get("name"));
			variant.setPrice((Integer)variantRow.get("price"));
			variant.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name"),(String)colorRow.get("csscolor")));
			variant.setIsCOD((Boolean)variantRow.get("cod"));
			//set product item in variant
			ProductItem item=new ProductItem();
			item.setProductItemId((Long)itemRow.get("id"));
			item.setProductItemName((String)itemRow.get("name"));
			item.setDescription(itemRow.get("description")==null?"":(String)itemRow.get("description"));
			item.setModifiedAt((Long)itemRow.get("modifiedat"));
			item.setCreatedAt((Long)itemRow.get("createdat"));
			item.setIsActive((Boolean)itemRow.get("isActive"));
			variant.setItem(item);
			
			ArrayList<VariantImage> images=new ArrayList<>();
			Set<Long> imageKeys=new HashSet<>();
			for(int i=0;i<imageRows.size();i++) {
				Row imageRow=imageRows.get(i);
				if(!imageKeys.contains((Long)imageRow.get("id"))) {
					VariantImage image=new VariantImage();
					image.setImageId((Long)imageRow.get("id"));
					image.setUrl((String)imageRow.get("imageUrl"));
					image.setName((String)imageRow.get("name"));
					image.setOrd((Integer)imageRow.get("ord"));
					images.add(image);
					imageKeys.add((Long)imageRow.get("id"));
				}
			}
			variant.setImages(images);
			
			ArrayList<Inventory> inventories=new ArrayList<>();
			Set<Long> inventoryKeys=new HashSet<>();
			for(int i=0;i<inventoryRows.size();i++) {
				Row inventoryRow=inventoryRows.get(i);
				Row sizeRow=sizeRows.get(i);
				if(!inventoryKeys.contains((Long)inventoryRow.get("id"))) {
					Inventory inventory=new Inventory();
					inventory.setInventoryId((Long)inventoryRow.get("id"));
					inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
					Size size=new Size();
					size.setSizeId((Long)sizeRow.get("id"));
					size.setName((String)sizeRow.get("name"));
					inventory.setSize(size);
					inventories.add(inventory);
					inventoryKeys.add((Long)inventoryRow.get("id"));
				}
			}
			variant.setInventories(inventories);
			return variant;
		}
		return null;
	}
	
	public List<ProductVariant> getVariantsByItemId(Long itemId) throws Exception
	{
		SelectQuery sq=new SelectQuery(TableNames.PRODUCT_VARIANT);
		OrderBy orderBy=new OrderBy(new Column(TableNames.PRODUCT_VARIANT,"id"),Order.ASC);
		sq.setOrderBy(orderBy);
		
		Criteria criteria=new Criteria(new Column(TableNames.PRODUCT_VARIANT,"itemId"),itemId);
		criteria.setComparator(Criteria.EQUAL);
		sq.setCriteria(criteria);
		
		Join join1=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
		Join join2=new Join(new Column(TableNames.PRODUCT_VARIANT,"colorId"),new Column(TableNames.COLORS,"id"),Join.INNER_JOIN);
		Join join3=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_IMAGES,"variantId"),Join.INNER_JOIN);
		Join join4=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_INVENTORY,"variantId"),Join.INNER_JOIN);
		Join join5=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE, "id"),Join.INNER_JOIN);
		
		sq.setJoin(join1);
		sq.addJoin(join2);
		sq.addJoin(join3);
		sq.addJoin(join4);
		sq.addJoin(join5);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
		TableHolder itemTh=dh.getTable(TableNames.PRODUCT_ITEM);
		TableHolder colorTh=dh.getTable(TableNames.COLORS);
		TableHolder imageTh=dh.getTable(TableNames.PRODUCT_IMAGES);
		TableHolder inventoryTh=dh.getTable(TableNames.PRODUCT_INVENTORY);
		TableHolder sizeTh=dh.getTable(TableNames.SIZE);
		
		ArrayList<ProductVariant> variants=new ArrayList<>();
		Map<String,Integer> variantIndexMap=new HashMap<>();
		Map<String,Integer> inventoryIndexMap=new HashMap<>();
		Map<String,Integer> imageIndexMap=new HashMap<>();
		
		if(variantTh!=null) {
			Map<Integer,Row> variantRows=variantTh.getRows();
			Map<Integer,Row> itemRows=itemTh.getRows();
			Map<Integer,Row> colorRows=colorTh.getRows();
			Map<Integer,Row> imageRows=imageTh.getRows();
			Map<Integer,Row> inventoryRows=inventoryTh.getRows();
			Map<Integer,Row> sizeRows=sizeTh.getRows();
			for(int i=0;i<variantRows.size();i++) {

				Row variantRow=variantRows.get(i);
				Row colorRow=colorRows.get(i);
				Row itemRow=itemRows.get(i);
				Row imageRow=imageRows.get(i);
				Row inventoryRow=inventoryRows.get(i);
				Row sizeRow=sizeRows.get(i);
				if(variantIndexMap.containsKey(String.valueOf((Long)variantRow.get("id")))) {


					ProductVariant variant=variants.get(variantIndexMap.get(String.valueOf((Long)variantRow.get("id"))));
					if(!imageIndexMap.containsKey(variant.getVariantId()+":"+(Long)imageRow.get("id"))) {
							ArrayList<VariantImage> images=variant.getImages();
							VariantImage image=new VariantImage();
							image.setImageId((Long)imageRow.get("id"));
							image.setUrl((String)imageRow.get("imageUrl"));
							image.setName((String)imageRow.get("name"));
							image.setOrd((Integer)imageRow.get("ord"));
							imageIndexMap.put(variant.getVariantId()+":"+image.getImageId(),images.size());
							images.add(image);
					}
						
					if(!inventoryIndexMap.containsKey(variant.getVariantId()+":"+(Long)inventoryRow.get("id"))) {
							ArrayList<Inventory> inventories=variant.getInventories();
							Inventory inventory=new Inventory();
							inventory.setInventoryId((Long)inventoryRow.get("id"));
							inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
							Size size=new Size();
							size.setSizeId((Long)sizeRow.get("id"));
							size.setName((String)sizeRow.get("name"));
							inventory.setSize(size);
							inventoryIndexMap.put(variant.getVariantId()+":"+inventory.getInventoryId(),inventories.size());
							inventories.add(inventory);
					}
					
					
				}else {

					ProductVariant variant=new ProductVariant();
					variant.setVariantId((Long)variantRow.get("id"));
					variant.setName((String)variantRow.get("name"));
					variant.setPrice((Integer)variantRow.get("price"));
					variant.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name"),(String)colorRow.get("csscolor")));
					//set product item in variant
					ProductItem item=new ProductItem();
					item.setProductItemId((Long)itemRow.get("id"));
					item.setProductItemName((String)itemRow.get("name"));
					item.setDescription(itemRow.get("description")==null?"":(String)itemRow.get("description"));
					item.setModifiedAt((Long)itemRow.get("modifiedat"));
					item.setCreatedAt((Long)itemRow.get("createdat"));
					item.setIsActive((Boolean)itemRow.get("isActive"));
					variant.setItem(item);
					//set images
					ArrayList<VariantImage> images=new ArrayList<>();
					VariantImage image=new VariantImage();
					image.setImageId((Long)imageRow.get("id"));
					image.setUrl((String)imageRow.get("imageUrl"));
					image.setName((String)imageRow.get("name"));
					image.setOrd((Integer)imageRow.get("ord"));
					imageIndexMap.put(variant.getVariantId()+":"+image.getImageId(),images.size());
					images.add(image);
					variant.setImages(images);
					//set inventories
					ArrayList<Inventory> inventories=new ArrayList<>();
					Inventory inventory=new Inventory();
					inventory.setInventoryId((Long)inventoryRow.get("id"));
					inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
					Size size=new Size();
					size.setSizeId((Long)sizeRow.get("id"));
					size.setName((String)sizeRow.get("name"));
					inventory.setSize(size);
					inventoryIndexMap.put(variant.getVariantId()+":"+inventory.getInventoryId(),inventories.size());
					inventories.add(inventory);
					variant.setInventories(inventories);
				
					variantIndexMap.put(String.valueOf(variant.getVariantId()),variants.size());
					variants.add(variant);
					
				}
			}
		}
		return variants;
	}
	
	public List<BannerImage> getBanners()throws Exception{
		ArrayList<BannerImage> images=new ArrayList<BannerImage>();
				
		SelectQuery sq=new SelectQuery(TableNames.BANNER_IMAGES);
		sq.setOrderBy(new OrderBy(new Column(TableNames.BANNER_IMAGES,"ord"),Order.ASC));
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(TableNames.BANNER_IMAGES);
		if(th!=null) {
			
			Map<Integer,Row> rows=th.getRows();
			
			for(int i=0;i<rows.size();i++) {
				Row row=rows.get(i);
				BannerImage banner=new BannerImage();
				banner.setImageId((Long)row.get("id"));
		        banner.setName((String)row.get("name"));
		        banner.setUrl((String)row.get("imageUrl"));
		        banner.setOrd((Integer)row.get("ord"));
		        images.add(banner);
			}
			
		}
		return images;
	}
	
	public List<Product> getHeaders()throws Exception{
		ArrayList<Product> headers=new ArrayList<>();
				
		SelectQuery sq=new SelectQuery(TableNames.PRODUCT);
		sq.setOrderBy(new OrderBy(new Column(TableNames.PRODUCT,"id"),Order.ASC));
		
		Criteria criteria=new Criteria(new Column(TableNames.PRODUCT,"isHeader"),true);
		criteria.setComparator(Criteria.EQUAL);
		sq.setCriteria(criteria);
        DataHolder dh=adapter.executeQuery(sq);
		
		TableHolder th=dh.getTable(TableNames.PRODUCT);
		if(th!=null) {
			Map<Integer,Row> productMap=th.getRows();
			for(int i=0;i<productMap.size();i++) {
				Row row=productMap.get(i);		
				Product product=new Product();

				product.setProductId((Long)row.get("id"));
				product.setProductName((String)row.get("name"));
				product.setDescription((String)row.get("description"));
				product.setCreatedAt((Long)row.get("createdat"));
				product.setModifiedAt((Long)row.get("modifiedat"));
				product.setIsHeader((Boolean)row.get("isHeader"));
				
				headers.add(product);
	
			}
		}
			
		return headers;
	}
	
	public Product getHeaderById(Long id)throws Exception {
		Product product=new Product();
		Category cat=new Category();
		SelectQuery sq=new SelectQuery(TableNames.PRODUCT);
		Criteria criteria=new Criteria(new Column(TableNames.PRODUCT,"id"),id);
		criteria.setComparator(Criteria.EQUAL);
		sq.setCriteria(criteria);
		Join join=new Join(new Column(TableNames.PRODUCT,"category"),new Column(TableNames.PRODUCT_CATEGORY,"id"),Join.INNER_JOIN);
		sq.setJoin(join);
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder th=dh.getTable(TableNames.PRODUCT);
		TableHolder categoryTh=dh.getTable(TableNames.PRODUCT_CATEGORY);
		if(th!=null && categoryTh!=null) {
			if(th.getRows().size()==1 && categoryTh.getRows().size()==1) {
				Row row=th.getRows().get(0);
				Row catRow=categoryTh.getRows().get(0);
				product.setProductId((Long)row.get("id"));
				product.setProductName((String)row.get("name"));
				product.setDescription((String)row.get("description"));
				product.setCreatedAt((Long)row.get("createdat"));
				product.setModifiedAt((Long)row.get("modifiedat"));
				product.setIsHeader((Boolean)row.get("isHeader"));
				cat.setProductTypeId((Long)catRow.get("id"));
				cat.setProductTypeName((String)catRow.get("name"));
				cat.setDescription((String)catRow.get("description"));
				cat.setCreatedAt((Long)catRow.get("createdat"));
				cat.setModifiedAt((Long)catRow.get("modifiedat"));
				product.setProductType(cat);
			}
		}else {
			logger.log(Level.WARN,"Product with id '"+id+"' not found");
		}
		return product;
	}
	
	public ArrayList<ProductVariant> getProductVariants(GetInfo info,Long productId)throws Exception{

		 SelectQuery sq=new SelectQuery(TableNames.PRODUCT_VARIANT);
		 
		 //subquery start
		 SelectQuery subQuery=new SelectQuery(TableNames.PRODUCT_VARIANT);
		 ArrayList<Column> fields=new ArrayList<>();
		 fields.add(new Column(TableNames.PRODUCT_VARIANT,"id"));
		 subQuery.setFields(fields);
		 
		 OrderBy orderBy=new OrderBy(new Column(TableNames.PRODUCT_VARIANT,"id"),Order.DESC);
		 subQuery.setOrderBy(orderBy);
		 
		 subQuery.setLimit(8);
		 
		 Join sqjoin1=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
		 Join sqjoin2=new Join(new Column(TableNames.PRODUCT_VARIANT,"colorId"),new Column(TableNames.COLORS,"id"),Join.INNER_JOIN);
		 Join sqjoin3=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_IMAGES,"variantId"),Join.INNER_JOIN);
		 Join sqjoin4=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_INVENTORY,"variantId"),Join.INNER_JOIN);
		 Join sqjoin5=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE, "id"),Join.INNER_JOIN);
		 subQuery.setJoin(sqjoin1);
		 subQuery.addJoin(sqjoin2);
		 subQuery.addJoin(sqjoin3);
		 subQuery.addJoin(sqjoin4);
		 subQuery.addJoin(sqjoin5);
		 
		 Criteria criteria=null;
		 Criteria trackCriteria=null;
		 
		 if(productId!=null) {
			 criteria=new Criteria(new Column(TableNames.PRODUCT_ITEM,"productId"),productId);
			 criteria.setComparator(Criteria.EQUAL);
			 trackCriteria=criteria;
		 }
		 
		 if(info.getPaginationKey()!=null) {
				Criteria criteria2=new Criteria(new Column(TableNames.PRODUCT_VARIANT,"id"), info.getPaginationKey());
				criteria2.setComparator(Criteria.LESSER);
				if(criteria==null) {
					criteria=criteria2;
				}else {
					trackCriteria.and(criteria2);
				}
				trackCriteria=criteria2;
	     }
		 
		 if(info.getFilterBy()!=null) {
				int comparator;
				if(info.getFilterValue() instanceof String) {
					info.setFilterValue(ProductManagementUtil.splitCharactersByPercentage((String)info.getFilterValue()));
					comparator=Criteria.LIKE;
				}else {
					comparator=Criteria.EQUAL;
				}
		
				String dbFieldName=ProductVariant.classDbNameMapForSearch.get(info.getFilterBy());
				if(dbFieldName!=null) {
					Criteria criteria3=new Criteria(new Column(TableNames.PRODUCT_VARIANT,dbFieldName),info.getFilterValue());
					criteria3.setComparator(comparator);
					if(info.getFilterValue() instanceof String) {
						Criteria criteriaForItem=new Criteria(new Column(TableNames.PRODUCT_ITEM,"name"),info.getFilterValue());
						criteriaForItem.setComparator(comparator);
						criteria3.childOr(criteriaForItem);
					}
					
					if(criteria==null) {
						criteria=criteria3;
					}else {
						trackCriteria.and(criteria3);
					}
					trackCriteria=criteria3;
				}
		  }
		 
		  Criteria criteria4=new Criteria(new Column(TableNames.PRODUCT_ITEM, "isActive"), true);
		  criteria4.setComparator(Criteria.EQUAL);
			
		  Criteria criteria5=new Criteria(new Column(TableNames.PRODUCT_VARIANT, "isActive"), true);
		  criteria5.setComparator(Criteria.EQUAL);
		 
		  criteria4.and(criteria5);
		  if(trackCriteria!=null) {
			  trackCriteria.and(criteria4);
		  }else {
			  criteria=criteria4;
		  }
		  
		  subQuery.setCriteria(criteria);
		  subQuery.setUseDistinct(true);
		 
		 //subquery end
		 
		 Join criteriaJoin=new Join(subQuery,new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_VARIANT,"id"),"filter",Join.INNER_JOIN);
		 Join join1=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
		 Join join2=new Join(new Column(TableNames.PRODUCT_VARIANT,"colorId"),new Column(TableNames.COLORS,"id"),Join.INNER_JOIN);
		 Join join3=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_IMAGES,"variantId"),Join.INNER_JOIN);
		 Join join4=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_INVENTORY,"variantId"),Join.INNER_JOIN);
		 Join join5=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE, "id"),Join.INNER_JOIN);
	 
		 sq.setJoin(criteriaJoin);
		 sq.addJoin(join1);
		 sq.addJoin(join2);
		 sq.addJoin(join3);
		 sq.addJoin(join4);
		 sq.addJoin(join5);
		 
		 sq.setOrderBy(orderBy);
		 
		 DataHolder dh=adapter.executeQuery(sq);
		 TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
		 TableHolder itemTh=dh.getTable(TableNames.PRODUCT_ITEM);
		 TableHolder colorTh=dh.getTable(TableNames.COLORS);
		 TableHolder imageTh=dh.getTable(TableNames.PRODUCT_IMAGES);
		 TableHolder inventoryTh=dh.getTable(TableNames.PRODUCT_INVENTORY);
		 TableHolder sizeTh=dh.getTable(TableNames.SIZE);
		
		 ArrayList<ProductVariant> variants=new ArrayList<>();
		 Map<String,Integer> variantIndexMap=new HashMap<>();
		 Map<String,Integer> inventoryIndexMap=new HashMap<>();
		 Map<String,Integer> imageIndexMap=new HashMap<>();
		
		 if(variantTh!=null) {
			Map<Integer,Row> variantRows=variantTh.getRows();
			Map<Integer,Row> itemRows=itemTh.getRows();
			Map<Integer,Row> colorRows=colorTh.getRows();
			Map<Integer,Row> imageRows=imageTh.getRows();
			Map<Integer,Row> inventoryRows=inventoryTh.getRows();
			Map<Integer,Row> sizeRows=sizeTh.getRows();
			for(int i=0;i<variantRows.size();i++) {

				Row variantRow=variantRows.get(i);
				Row colorRow=colorRows.get(i);
				Row itemRow=itemRows.get(i);
				Row imageRow=imageRows.get(i);
				Row inventoryRow=inventoryRows.get(i);
				Row sizeRow=sizeRows.get(i);
				if(variantIndexMap.containsKey(String.valueOf((Long)variantRow.get("id")))) {


					ProductVariant variant=variants.get(variantIndexMap.get(String.valueOf((Long)variantRow.get("id"))));
					if(!imageIndexMap.containsKey(variant.getVariantId()+":"+(Long)imageRow.get("id"))) {
							ArrayList<VariantImage> images=variant.getImages();
							VariantImage image=new VariantImage();
							image.setImageId((Long)imageRow.get("id"));
							image.setUrl((String)imageRow.get("imageUrl"));
							image.setName((String)imageRow.get("name"));
							image.setOrd((Integer)imageRow.get("ord"));
							imageIndexMap.put(variant.getVariantId()+":"+image.getImageId(),images.size());
							images.add(image);
					}
						
					if(!inventoryIndexMap.containsKey(variant.getVariantId()+":"+(Long)inventoryRow.get("id"))) {
							ArrayList<Inventory> inventories=variant.getInventories();
							Inventory inventory=new Inventory();
							inventory.setInventoryId((Long)inventoryRow.get("id"));
							inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
							Size size=new Size();
							size.setSizeId((Long)sizeRow.get("id"));
							size.setName((String)sizeRow.get("name"));
							inventory.setSize(size);
							inventoryIndexMap.put(variant.getVariantId()+":"+inventory.getInventoryId(),inventories.size());
							inventories.add(inventory);
					}
					
					
				}else {

					ProductVariant variant=new ProductVariant();
					variant.setVariantId((Long)variantRow.get("id"));
					variant.setName((String)variantRow.get("name"));
					variant.setPrice((Integer)variantRow.get("price"));
					variant.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name"),(String)colorRow.get("csscolor")));
					//set product item in variant
					ProductItem item=new ProductItem();
					item.setProductItemId((Long)itemRow.get("id"));
					item.setProductItemName((String)itemRow.get("name"));
					item.setDescription(itemRow.get("description")==null?"":(String)itemRow.get("description"));
					item.setModifiedAt((Long)itemRow.get("modifiedat"));
					item.setCreatedAt((Long)itemRow.get("createdat"));
					item.setIsActive((Boolean)itemRow.get("isActive"));
					variant.setItem(item);
					//set images
					ArrayList<VariantImage> images=new ArrayList<>();
					VariantImage image=new VariantImage();
					image.setImageId((Long)imageRow.get("id"));
					image.setUrl((String)imageRow.get("imageUrl"));
					image.setName((String)imageRow.get("name"));
					image.setOrd((Integer)imageRow.get("ord"));
					imageIndexMap.put(variant.getVariantId()+":"+image.getImageId(),images.size());
					images.add(image);
					variant.setImages(images);
					//set inventories
					ArrayList<Inventory> inventories=new ArrayList<>();
					Inventory inventory=new Inventory();
					inventory.setInventoryId((Long)inventoryRow.get("id"));
					inventory.setAvailableStocks((Integer)inventoryRow.get("inventory"));
					Size size=new Size();
					size.setSizeId((Long)sizeRow.get("id"));
					size.setName((String)sizeRow.get("name"));
					inventory.setSize(size);
					inventoryIndexMap.put(variant.getVariantId()+":"+inventory.getInventoryId(),inventories.size());
					inventories.add(inventory);
					variant.setInventories(inventories);
				
					variantIndexMap.put(String.valueOf(variant.getVariantId()),variants.size());
					variants.add(variant);
					
				}
			}
		 }
				
		 return variants;
	}
	
	
	public Address getAddressByAddressandUserIds(Long userId,Long addressId)throws Exception {

		Address address=null;
		
		SelectQuery sq=new SelectQuery(TableNames.USER_ADDRESS);
		
		Criteria criteria1=new Criteria(new Column(TableNames.USER_ADDRESS,"userId"),userId);
		criteria1.setComparator(Criteria.EQUAL);
		
		Criteria criteria2=new Criteria(new Column(TableNames.USER_ADDRESS,"addressId"),addressId);
		criteria2.setComparator(Criteria.EQUAL);
		
		criteria1.and(criteria2);
		
		sq.setCriteria(criteria1);
		
		DataHolder dh=adapter.executeQuery(sq);
		
		TableHolder addressTh=dh.getTable(TableNames.USER_ADDRESS);
		
		if(addressTh!=null && addressTh.getRows().size()==1) {
			Row addressRow=addressTh.getRows().get(0);
			address=new Address();
			address.setAddressId((Long)addressRow.get("addressId"));
			address.setAddressLine1((String)addressRow.get("address_line1"));
			address.setAddressLine2((String)addressRow.get("address_line2"));
			address.setCity((String)addressRow.get("city"));
			address.setState((String)addressRow.get("state"));
			address.setCountry((String)addressRow.get("country"));
			address.setPostalCode((String)addressRow.get("postal_code"));
			address.setMobile((String)addressRow.get("mobile"));
		}
		
		return address;
		
	}
	
	private void checkVariantAvailability(Address address,boolean doCODCheck) throws Exception {
		address.setTotalValue(0);
		List<OrderItems> orderItems=address.getOrderItems();
		ArrayList<Object> inventoryIds=new ArrayList<>();
		Map<Long,Integer> inventoryCountMap=new HashMap<>();
		for(OrderItems item : orderItems) {
			if(item.getInventory().getInventoryId()==null) {
				throw new ExceptionCause("Null value passed.Invalid Request!",HttpStatus.BAD_REQUEST);
			}
			inventoryIds.add(item.getInventory().getInventoryId());
			inventoryCountMap.put(item.getInventory().getInventoryId(),item.getCount());
		}
		
		SelectQuery sq=new SelectQuery(TableNames.PRODUCT_INVENTORY);
		Join join1=new Join(new Column(TableNames.PRODUCT_INVENTORY,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
		Join join2=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
		sq.setJoin(join1);
		sq.addJoin(join2);
		
		Criteria criteria=new Criteria(new Column(TableNames.PRODUCT_INVENTORY,"id"));
		criteria.setDataCollection(inventoryIds);
		criteria.setComparator(Criteria.IN);
		sq.setCriteria(criteria);
		
		DataHolder dh=adapter.executeQuery(sq);
		
		TableHolder inventoryTh=dh.getTable(TableNames.PRODUCT_INVENTORY);
		TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
		TableHolder itemTh=dh.getTable(TableNames.PRODUCT_ITEM);
		
		if(inventoryTh!=null && inventoryTh.getRows().size()==orderItems.size()) {
			Map<Integer,Row> inventoryRows=inventoryTh.getRows();
			Map<Integer,Row> variantRows=variantTh.getRows();
			Map<Integer,Row> itemRows=itemTh.getRows();
			for(int i=0;i<inventoryRows.size();i++) {
				Row inventoryRow=inventoryRows.get(i);
				Row variantRow=variantRows.get(i);
				Row itemRow=itemRows.get(i);
				int inventoryAvailable=(Integer)(inventoryRow.get("inventory"));
				int inventoryOrdered=inventoryCountMap.get((Long)(inventoryRow.get("id")));
				if(inventoryOrdered>inventoryAvailable) {
					throw new ExceptionCause("Cannot create order.Check items availability!",HttpStatus.BAD_REQUEST);
				}
				if(Boolean.FALSE.equals((Boolean)itemRow.get("isActive")) || Boolean.FALSE.equals((Boolean)variantRow.get("isActive"))) {
					throw new ExceptionCause("Cannot create order.One of the items not active!",HttpStatus.BAD_REQUEST);
				}
				if(doCODCheck) {
					if(!(Boolean)variantRow.get("cod")) {
						throw new ExceptionCause("Cash on Delivery not available.Please check COD availability!",HttpStatus.BAD_REQUEST);
					}
				}
				address.setTotalValue(address.getTotalValue()+(((Integer)variantRow.get("price"))*inventoryOrdered));
			}
		}else {
			throw new ExceptionCause("Invalid Input.Cannot create order!",HttpStatus.BAD_REQUEST);
		}
	}
	
	
	public OrderEntity placeOrder(Address address,boolean COD) throws Exception{
		User user=ThreadLocalUtil.currentUser.get();
		if(address.getOrderItems().size()==0) {
			throw new ExceptionCause("Invalid Input.Cannot create order!",HttpStatus.BAD_REQUEST);
		}
		
		checkVariantAvailability(address,COD);
		
		if(address.getAddressId()!=null) {
			
			Address addressFromDB=getAddressByAddressandUserIds(user.getUserid(),address.getAddressId());
			if(addressFromDB==null) {
				throw new ExceptionCause("Address with address id ["+address.getAddressId()+"] not found!", HttpStatus.BAD_REQUEST);
			}
			
			adapter.beginTxn();
			try {
				  addressFromDB.setTotalValue(address.getTotalValue());
				  addressFromDB.setOrderItems(address.getOrderItems());
				  addressFromDB.setIsCart(address.getIsCart());
				  OrderEntity order;
				  if(COD) {
					  order=createCODOrder(addressFromDB);
				  }else {
					  order=relateOrdersWithInventory(addressFromDB);
				  }
	              adapter.commitTxn();
	              if(COD) {
		        	  try {
							if(!AWSSimpleEmailService.sendEmail(user.getEmail(),"Order Confirmation!",HtmlTemplates.getConfirmOrderTemplate(order))) {
								throw new Exception();
							}
					  }catch (Exception e) {
							logger.log(Level.ERROR,"Error while sending order confirmation email! Order id #"+order.getOrderId());
					  }
		          }
	              return order;
	        }catch (Exception e) {
	              adapter.revertTxn();
				  throw e;
			 }
			
		}else {
			//Configurations
			String[] fieldNames;
			if(user!=null) {
				fieldNames= new String[]{"address_line1","address_line2","userId",
				"city","state","country","postal_code","mobile","created_date"};
			}else {
				fieldNames= new String[]{"address_line1","address_line2",
						"city","state","country","postal_code","mobile","created_date"};
			}
			
			TableHolder th=new TableHolder(TableNames.USER_ADDRESS,fieldNames);
			
            adapter.beginTxn();
            try {
            	  Long addressId;
            	  Row newAddressRow=createNewAddressRow(address);
 			      newAddressRow.getColumns().put("userId",user.getUserid());
 			      th.setRow(newAddressRow);
 			      addressId=adapter.insertAndGetRowID(th);
             
		          if(addressId==null) {
		        	  throw new ExceptionCause("Order creation failed!",HttpStatus.BAD_REQUEST);
		          }
		          
		          address.setAddressId(addressId);
		          OrderEntity order;
		          if(COD) {
					  order=createCODOrder(address);
				  }else {
					  order=relateOrdersWithInventory(address);
				  }
		          adapter.commitTxn();
		          if(COD) {
		        	  try {
		        		  if(!AWSSimpleEmailService.sendEmail(user.getEmail(),"Order Confirmation!",HtmlTemplates.getConfirmOrderTemplate(order))
		  						|| !AWSSimpleEmailService.sendBulkEmail(adminEmails,"New Order Notification!",HtmlTemplates.getOrderNotifyTemplate(order))) {
		  						throw new Exception();
		  					}
					  }catch (Exception e) {
							logger.log(Level.ERROR,"Error while sending order confirmation email! Order id #"+order.getOrderId());
					  }
		          }
		          return order;
            }catch (Exception e) {
            	adapter.revertTxn();
				throw e;
			}
		}
		
	}
	
	private OrderEntity createCODOrder(Address address)throws Exception {
		  List<OrderItems> orderItems=address.getOrderItems();
		 
          String[] orderFields= {"orderedby","ordertime","status"};
          Long orderTime=new Date().getTime();
          Row orderRow=new Row();
		  Map<String,Object> orderColumns=orderRow.getColumns();
		  orderColumns.put("orderedby",address.getAddressId());
		  orderColumns.put("ordertime",orderTime);
		  orderColumns.put("status",OrderEntity.Status.COD.toString());
		  
		  TableHolder orderTh=new TableHolder(TableNames.ORDERS, orderFields);
		  orderTh.setRow(orderRow);
		  
		  Long orderID=adapter.insertAndGetRowID(orderTh);
		  if(orderID==null) {
        	  throw new ExceptionCause("Order creation failed!",HttpStatus.BAD_REQUEST);
	      }
		  
		  OrderEntity order=new OrderEntity();
		  order.setOrderId(orderID);
		  order.setStatusCode(OrderEntity.Status.COD);
		  order.setDate(new Date(orderTime));
		  order.setShipTo(address);
		  order.setTotalPrice(address.getTotalValue());
		  
		  String[] orderVariantRelFields= {"orderId","inventoryId","count"};
		  TableHolder orderVariantRelTh=new TableHolder(TableNames.ORDER_VARIANT_RELATION,orderVariantRelFields);
		  
		  String updateQuery="update product_inventory set inventory=inventory-? where id=? and inventory>0;";	
		  List<List<Object>> rows=new ArrayList<>();
		  
		  for(OrderItems item : orderItems) {
			   Row orderVariantRelRow=new Row();
			   Map<String,Object> orderVariantRelColumns=orderVariantRelRow.getColumns();
			   orderVariantRelColumns.put("orderId",order.getOrderId());
			   orderVariantRelColumns.put("inventoryId",item.getInventory().getInventoryId());
			   orderVariantRelColumns.put("count",item.getCount());
			   orderVariantRelTh.setRow(orderVariantRelRow);
			   
			   Inventory inventory=item.getInventory();
			   List<Object> dataToSet=new ArrayList<>();
			   dataToSet.add(item.getCount());
	           dataToSet.add(inventory.getInventoryId());
	           rows.add(dataToSet);
		  }
		  adapter.insertRowsOfTable(orderVariantRelTh);
		  adapter.updateMultipleRows(updateQuery, rows);
		  
		  return order;
	}
	
	private Row createNewAddressRow(Address address) throws Exception{
		Row row=new Row();
		Map<String,Object> columns=row.getColumns();
		columns.put("address_line1",address.getAddressLine1());
		columns.put("address_line2",address.getAddressLine2());
		columns.put("city",address.getCity());
		columns.put("state",address.getState());
		columns.put("country",address.getCountry());
		columns.put("postal_code",address.getPostalCode());
		columns.put("mobile",address.getMobile());
		columns.put("created_date",new Date().getTime());
		return row;
	}
	
	private OrderEntity relateOrdersWithInventory(Address address)throws Exception {
		  List<OrderItems> orderItems=address.getOrderItems();
		 
          String[] orderFields= {"orderedby","ordertime","status"};
          Long orderTime=new Date().getTime();
          Row orderRow=new Row();
		  Map<String,Object> orderColumns=orderRow.getColumns();
		  orderColumns.put("orderedby",address.getAddressId());
		  orderColumns.put("ordertime",orderTime);
		  orderColumns.put("status",OrderEntity.Status.WAITING.toString());
		  
		  TableHolder orderTh=new TableHolder(TableNames.ORDERS, orderFields);
		  orderTh.setRow(orderRow);
		  
		  Long orderID=adapter.insertAndGetRowID(orderTh);
		  if(orderID==null) {
            	  throw new ExceptionCause("Order creation failed!",HttpStatus.BAD_REQUEST);
		  }
		  
		  //update paymentIntentId in Database
		 
		  PaymentIntent paymentIntent=StripePaymentUtil.createPaymentIntent(orderID,address.getTotalValue()*100,address.getIsCart());
		  UpdateQuery uq=new UpdateQuery(TableNames.ORDERS);
		  List<Column> fields=new ArrayList<>();
		  fields.add(new Column(TableNames.ORDERS,"clientsecret",paymentIntent.getClientSecret()));
		  uq.setFields(fields);
		  Criteria criteria=new Criteria(new Column(TableNames.ORDERS,"id"),orderID);
		  criteria.setComparator(Criteria.EQUAL);
		  uq.setCriteria(criteria);
		  adapter.updateData(uq);
		  
		  OrderEntity order=new OrderEntity();
		  order.setOrderId(orderID);
		  order.setStatusCode(OrderEntity.Status.WAITING);
		  order.setDate(new Date(orderTime));
		  order.setShipTo(address);
		  order.setClientSecret(paymentIntent.getClientSecret());
		  order.setTotalPrice(address.getTotalValue());
		  
		  String[] orderVariantRelFields= {"orderId","inventoryId","count"};
		  TableHolder orderVariantRelTh=new TableHolder(TableNames.ORDER_VARIANT_RELATION,orderVariantRelFields);
		  for(OrderItems item : orderItems) {
			   Row orderVariantRelRow=new Row();
			   Map<String,Object> orderVariantRelColumns=orderVariantRelRow.getColumns();
			   orderVariantRelColumns.put("orderId",order.getOrderId());
			   orderVariantRelColumns.put("inventoryId",item.getInventory().getInventoryId());
			   orderVariantRelColumns.put("count",item.getCount());
			   orderVariantRelTh.setRow(orderVariantRelRow);
		  }
		  adapter.insertRowsOfTable(orderVariantRelTh);
		  return order;
	}
	
	public List<Address> getAddressesOfUser() throws Exception {
		User user=ThreadLocalUtil.currentUser.get();
		if(user!=null) {
			ArrayList<Address> addresses=new ArrayList<>();
			SelectQuery sq=new SelectQuery(TableNames.USER_ADDRESS);
			
			Criteria criteria1=new Criteria(new Column(TableNames.USER_ADDRESS,"userId"),user.getUserid());
			criteria1.setComparator(Criteria.EQUAL);
			
			sq.setCriteria(criteria1);

			sq.setOrderBy(new OrderBy(new Column(TableNames.USER_ADDRESS,"created_date"), Order.DESC));
			
			DataHolder dh=adapter.executeQuery(sq);
			
			TableHolder addressTh=dh.getTable(TableNames.USER_ADDRESS);
			
			if(addressTh!=null) {
				Map<Integer,Row> rows=addressTh.getRows();
				for(int i=0;i<rows.size();i++) {
					Row addressRow=rows.get(i);
					Address address=new Address();
					address.setAddressId((Long)addressRow.get("addressId"));
					address.setAddressLine1((String)addressRow.get("address_line1"));
					address.setAddressLine2((String)addressRow.get("address_line2"));
					address.setCity((String)addressRow.get("city"));
					address.setState((String)addressRow.get("state"));
					address.setCountry((String)addressRow.get("country"));
					address.setPostalCode((String)addressRow.get("postal_code"));
					address.setMobile((String)addressRow.get("mobile"));
					addresses.add(address);
				}
			}
			return addresses;
		}
		return null;
	}
	
	public Address getAddressById(Long addressId)throws Exception{
		SelectQuery sq=new SelectQuery(TableNames.USER_ADDRESS);
		Criteria criteria1=new Criteria(new Column(TableNames.USER_ADDRESS,"addressId"),addressId);
		criteria1.setComparator(Criteria.EQUAL);
		
		sq.setCriteria(criteria1);
		
		DataHolder dh=adapter.executeQuery(sq);
		
		TableHolder addressTh=dh.getTable(TableNames.USER_ADDRESS);
		
		if(addressTh!=null && addressTh.getRows().size()==1) {
			Row addressRow=addressTh.getRows().get(0);
			Address address=new Address();
			address.setAddressId((Long)addressRow.get("addressId"));
			address.setAddressLine1((String)addressRow.get("address_line1"));
			address.setAddressLine2((String)addressRow.get("address_line2"));
			address.setCity((String)addressRow.get("city"));
			address.setState((String)addressRow.get("state"));
			address.setCountry((String)addressRow.get("country"));
			address.setPostalCode((String)addressRow.get("postal_code"));
			address.setMobile((String)addressRow.get("mobile"));
			address.setUserId((Long)addressRow.get("userId"));
			return address;
		}
		return null;
	}
	
	public OrderEntity getOrderByIdAndUserId(Long id,Long userId)throws Exception {
		SelectQuery sq=new SelectQuery(TableNames.ORDERS);
		Criteria criteria=new Criteria(new Column(TableNames.ORDERS,"id"),id);
		criteria.setComparator(Criteria.EQUAL);
		
		Criteria criteria2=new Criteria(new Column(TableNames.USER_ADDRESS,"userId"),userId);
		criteria2.setComparator(Criteria.EQUAL);
		
		criteria.and(criteria2);

		
		sq.setCriteria(criteria);
		
		Join join1=new Join(new Column(TableNames.ORDERS, "orderedby"),new Column(TableNames.USER_ADDRESS,"addressId"),Join.INNER_JOIN);
		Join join2=new Join(new Column(TableNames.ORDERS, "id"),new Column(TableNames.ORDER_VARIANT_REL,"orderId"),Join.INNER_JOIN);
		Join join3=new Join(new Column(TableNames.ORDER_VARIANT_REL, "inventoryId"),new Column(TableNames.PRODUCT_INVENTORY,"id"),Join.INNER_JOIN);
		Join join4=new Join(new Column(TableNames.PRODUCT_INVENTORY,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
		sq.setJoin(join1);
		sq.addJoin(join2);
		sq.addJoin(join3);
		sq.addJoin(join4);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder orderTh=dh.getTable(TableNames.ORDERS);
		TableHolder ordVarRelTh=dh.getTable(TableNames.ORDER_VARIANT_REL);
		TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
		
		if(orderTh!=null && orderTh.getRows().size()>0) {
			int totalValue=0;
			Row orderRow=orderTh.getRows().get(0);
			Map<Integer,Row> ordVarRelRows=null;
			Map<Integer,Row> variantRows=null;
			if(ordVarRelTh!=null) {
				ordVarRelRows=ordVarRelTh.getRows();
				variantRows=variantTh.getRows();
			}

			OrderEntity order=new OrderEntity();
			order.setOrderId((Long)orderRow.get("id"));
			order.setDate(new Date((Long)orderRow.get("ordertime")));
			order.setStatusCode(Status.valueOf((String)orderRow.get("status")));
			order.setClientSecret((String)orderRow.get("clientsecret"));
			
			List<Inventory> inventories=new ArrayList<>();
			
			for(int i=0;i<ordVarRelRows.size();i++) {
				 Row orderVarRelRow=ordVarRelRows.get(i);
				 Row variantRow=variantRows.get(i);
				 Inventory inventory=new Inventory();
				 
				 inventory.setInventoryId((Long)orderVarRelRow.get("inventoryId"));
				 inventory.setOrderedCount((Integer)orderVarRelRow.get("count"));
				 totalValue+=((Integer)orderVarRelRow.get("count")*(Integer)variantRow.get("price"));
				 inventories.add(inventory);				 
			}
			order.setInventories(inventories);
			order.setTotalPrice(totalValue);
			return order;
 
		}
		return null;
	}
	
	public OrderEntity getOrderByIdAndStatus(Long id,Status status)throws Exception{
		SelectQuery sq=new SelectQuery(TableNames.ORDERS);
		Criteria criteria=new Criteria(new Column(TableNames.ORDERS,"id"),id);
		criteria.setComparator(Criteria.EQUAL);
		if(status!=null) {
			Criteria criteria2=new Criteria(new Column(TableNames.ORDERS,"status"),status.toString());
			criteria2.setComparator(Criteria.EQUAL);
			criteria.and(criteria2);
		}
		sq.setCriteria(criteria);
		
		Join join1=new Join(new Column(TableNames.ORDERS, "id"),new Column(TableNames.ORDER_VARIANT_REL,"orderId"),Join.INNER_JOIN);
		Join join2=new Join(new Column(TableNames.ORDER_VARIANT_REL, "inventoryId"),new Column(TableNames.PRODUCT_INVENTORY,"id"),Join.INNER_JOIN);
		Join join3=new Join(new Column(TableNames.PRODUCT_INVENTORY,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
		Join join4=new Join(new Column(TableNames.ORDERS, "orderedby"),new Column(TableNames.USER_ADDRESS,"addressId"),Join.INNER_JOIN);
		Join join5=new Join(new Column(TableNames.USER_ADDRESS, "userId"),new Column(TableNames.USERS,"id"),Join.INNER_JOIN);
		sq.setJoin(join1);
		sq.addJoin(join2);
		sq.addJoin(join3);
		sq.addJoin(join4);
		sq.addJoin(join5);
		
		DataHolder dh=adapter.executeQuery(sq);
		TableHolder orderTh=dh.getTable(TableNames.ORDERS);
		TableHolder ordVarRelTh=dh.getTable(TableNames.ORDER_VARIANT_REL);
		TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
		TableHolder addressTh=dh.getTable(TableNames.USER_ADDRESS);
		TableHolder userTh=dh.getTable(TableNames.USERS);
		
		if(orderTh!=null && orderTh.getRows().size()>0) {
			int totalValue=0;
			Row orderRow=orderTh.getRows().get(0);
			Map<Integer,Row> ordVarRelRows=ordVarRelTh.getRows();
			Map<Integer,Row> variantRows=variantTh.getRows();
			Row addressRow=addressTh.getRows().get(0);
			Row userRow=userTh.getRows().get(0);
		
			OrderEntity order=new OrderEntity();
			order.setOrderId((Long)orderRow.get("id"));
			order.setDate(new Date((Long)orderRow.get("ordertime")));
			order.setStatusCode(Status.valueOf((String)orderRow.get("status")));
			order.setClientSecret((String)orderRow.get("clientsecret"));
			
			Address address=new Address();
			address.setAddressId((Long)addressRow.get("addressId"));
			address.setAddressLine1((String)addressRow.get("address_line1"));
			address.setAddressLine2((String)addressRow.get("address_line2"));
			address.setCity((String)addressRow.get("city"));
			address.setState((String)addressRow.get("state"));
			address.setCountry((String)addressRow.get("country"));
			address.setPostalCode((String)addressRow.get("postal_code"));
			address.setMobile((String)addressRow.get("mobile"));
			order.setShipTo(address);
			
			User user=new User();
			user.setEmail((String)userRow.get("email"));
    		user.setUserid((Long)userRow.get("id"));
    		user.setPhone((String)userRow.get("phoneno"));
    		user.setFirstname((String)userRow.get("firstname"));
    		user.setLastname((String)userRow.get("lastname"));
    		order.setOrderedBy(user);
			
			List<Inventory> inventories=new ArrayList<>();
			
			for(int i=0;i<ordVarRelRows.size();i++) {
				 Row orderVarRelRow=ordVarRelRows.get(i);
				 Row variantRow=variantRows.get(i);
				 Inventory inventory=new Inventory();
				 
				 inventory.setInventoryId((Long)orderVarRelRow.get("inventoryId"));
				 inventory.setOrderedCount((Integer)orderVarRelRow.get("count"));
				 totalValue+=((Integer)orderVarRelRow.get("count")*(Integer)variantRow.get("price"));
				 inventories.add(inventory);				 
			}
			order.setInventories(inventories);
			order.setTotalPrice(totalValue);
			return order;
 
		}
		return null;

	}
	
	public void changeStatusOfOrder(Long orderId,Status status)throws Exception {
		UpdateQuery uq=new UpdateQuery(TableNames.ORDERS);
		List<Column> fields=new ArrayList<>();
	    fields.add(new Column(TableNames.ORDERS,"status",status.toString()));
	    uq.setFields(fields);
	    Criteria criteria=new Criteria(new Column(TableNames.ORDERS,"id"),orderId);
	    criteria.setComparator(Criteria.EQUAL);
	    uq.setCriteria(criteria);
	    adapter.updateData(uq);
	}
	
	
	public void confirmOrder(Long orderId)throws Exception {
		OrderEntity orderDetails=getOrderByIdAndStatus(orderId,Status.WAITING);
		if(orderDetails!=null) {
			try {
				adapter.beginTxn();
				changeStatusOfOrder(orderId, Status.OPEN);
	            String updateQuery="update product_inventory set inventory=inventory-? where id=? and inventory>0;";	
				List<Inventory> inventories=orderDetails.getInventories();
				
				List<List<Object>> rows=new ArrayList<>();
				
				for(int i=0;i<inventories.size();i++) {
					Inventory inventory=inventories.get(i);
					List<Object> dataToSet=new ArrayList<>();
	                dataToSet.add(inventory.getOrderedCount());
	                dataToSet.add(inventory.getInventoryId());
	                rows.add(dataToSet);
				}
				adapter.updateMultipleRows(updateQuery, rows);
				adapter.commitTxn();
				
				try {
					if(!AWSSimpleEmailService.sendEmail(orderDetails.getOrderedBy().getEmail(),"Order Confirmation!",HtmlTemplates.getConfirmOrderTemplate(orderDetails))
						|| !AWSSimpleEmailService.sendBulkEmail(adminEmails,"New Order Notification!",HtmlTemplates.getOrderNotifyTemplate(orderDetails))) {
						throw new Exception();
					}
				}catch (Exception e) {
					logger.log(Level.ERROR,"Error while sending order confirmation email! Order id #"+orderId);
				}
			}catch (Exception e) {
				changeStatusOfOrder(orderId,Status.FAILED);
				logger.log(Level.ERROR,"Payment received.But could not change status of order with id ["+orderId+"]!");
				logger.log(Level.ERROR,ExceptionCause.getStackTrace(e));
			}
		}
	}
	
	public List<OrderEntity> getOrdersByUserId(GetInfo info)throws Exception{
		User user=ThreadLocalUtil.currentUser.get();
		if(user!=null) {
			//Sub Query
			SelectQuery subQuery=new SelectQuery(TableNames.ORDERS);
			ArrayList<Column> fieldSelect=new ArrayList<>();
			fieldSelect.add(new Column(TableNames.ORDERS, "id"));
			subQuery.setFields(fieldSelect);
			
			Join sqJoin=new Join(new Column(TableNames.ORDERS, "orderedby"),new Column(TableNames.USER_ADDRESS,"addressId"),Join.INNER_JOIN);
			subQuery.setJoin(sqJoin);
			
			Criteria criteria=new Criteria(new Column(TableNames.USER_ADDRESS,"userId"),user.getUserid());
			criteria.setComparator(Criteria.EQUAL);
			
			Criteria criteria2=new Criteria(new Column(TableNames.ORDERS,"status"),Status.WAITING.toString());
			criteria2.setComparator(Criteria.NOTEQUAL);
			criteria.and(criteria2);
			
			if(info.getPaginationKey()!=null) {
				Criteria criteria3=new Criteria(new Column(TableNames.ORDERS,"id"),info.getPaginationKey());
				criteria3.setComparator(Criteria.LESSER);
				criteria2.and(criteria3);
			}
			
			subQuery.setCriteria(criteria);
			
			OrderBy orderBy=new OrderBy(new Column(TableNames.ORDERS,"id"), Order.DESC);
			subQuery.setOrderBy(orderBy);
			subQuery.setLimit(8);
			
			
			
			//Select Query
			SelectQuery sq=new SelectQuery(TableNames.ORDERS);
			
			
			Join join1=new Join(new Column(TableNames.ORDERS, "id"),new Column(TableNames.ORDER_VARIANT_REL,"orderId"),Join.LEFT_JOIN);
			Join join2=new Join(new Column(TableNames.ORDERS, "orderedby"),new Column(TableNames.USER_ADDRESS,"addressId"),Join.INNER_JOIN);
			Join join3=new Join(new Column(TableNames.ORDER_VARIANT_REL, "inventoryId"),new Column(TableNames.PRODUCT_INVENTORY,"id"),Join.INNER_JOIN);
			Join join4=new Join(new Column(TableNames.PRODUCT_INVENTORY,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
			Join join5=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE,"id"),Join.INNER_JOIN);
			Join criteriaJoin=new Join(subQuery,new Column(TableNames.ORDERS,"id"),new Column(TableNames.ORDERS,"id"),"filter",Join.INNER_JOIN);
			sq.setJoin(join1);
			sq.addJoin(join2);
			sq.addJoin(join3);
			sq.addJoin(join4);
			sq.addJoin(join5);
			sq.addJoin(criteriaJoin);
			
			sq.setOrderBy(orderBy);
			
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder orderTh=dh.getTable(TableNames.ORDERS);
			TableHolder ordVarRelTh=dh.getTable(TableNames.ORDER_VARIANT_REL);
			TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
			TableHolder sizeTh=dh.getTable(TableNames.SIZE);
			
			if(orderTh!=null && orderTh.getRows().size()>0) {
				List<OrderEntity> orders=new ArrayList<>();
				Map<Integer,Row> orderRows=orderTh.getRows();
				Map<Integer,Row> ordVarRelRows=ordVarRelTh.getRows();
				Map<Integer,Row> variantRows=variantTh.getRows();
				Map<Integer,Row> sizeRows=sizeTh.getRows();
				
				Map<Long,Integer> orderIndexMap=new HashMap<>();
				
				for(int i=0;i<orderRows.size();i++) {
					Row orderRow=orderRows.get(i);
					Row ordVarRelRow=ordVarRelRows.get(i);
					Row variantRow=variantRows.get(i);
					Row sizeRow=sizeRows.get(i);
					
					if(!orderIndexMap.containsKey((Long)orderRow.get("id"))) {
						 OrderEntity order=new OrderEntity();
						 order.setOrderId((Long)orderRow.get("id"));
						 order.setStatusCode(Status.valueOf((String)orderRow.get("status")));
						 order.setTotalPrice((Integer)ordVarRelRow.get("count")*(Integer)variantRow.get("price"));
						 List<Inventory> inventories=new ArrayList<>();
						 
						 Inventory inventory=new Inventory();
						 inventory.setInventoryId((Long)ordVarRelRow.get("inventoryId"));
						 
						 Size size=new Size();
						 size.setSizeId((Long)sizeRow.get("id"));
						 size.setName((String)sizeRow.get("name"));
						 inventory.setSize(size);
						 
						 ProductVariant variant=new ProductVariant();
						 variant.setVariantId((Long)variantRow.get("id"));
						 variant.setName((String)variantRow.get("name"));
						 inventory.setVariant(variant);
						 
						 inventories.add(inventory);
						 order.setInventories(inventories);
						 orders.add(order);
						 orderIndexMap.put(order.getOrderId(),orders.size()-1);
					}else {
						OrderEntity order=orders.get(orderIndexMap.get((Long)orderRow.get("id")));
						int totalPrice=((Integer)ordVarRelRow.get("count")*(Integer)variantRow.get("price"))+order.getTotalPrice();
						order.setTotalPrice(totalPrice);
						
						List<Inventory> inventories=order.getInventories();
						Inventory inventory=new Inventory();
						inventory.setInventoryId((Long)ordVarRelRow.get("inventoryId"));
						 
						Size size=new Size();
						size.setSizeId((Long)sizeRow.get("id"));
						size.setName((String)sizeRow.get("name"));
						inventory.setSize(size);
						 
						ProductVariant variant=new ProductVariant();
						variant.setVariantId((Long)variantRow.get("id"));
						variant.setName((String)variantRow.get("name"));
						inventory.setVariant(variant);
						
						inventories.add(inventory);

					}
				}
				
				return orders;
			}
	 
			

		}
		return null;

	}
	
	public OrderEntity getOrderById(Long id)throws Exception{
		User user=ThreadLocalUtil.currentUser.get();
		if(user!=null) {
			SelectQuery sq=new SelectQuery(TableNames.ORDERS);
			
			Criteria criteria=new Criteria(new Column(TableNames.ORDERS,"id"),id);
			criteria.setComparator(Criteria.EQUAL);
			Criteria criteria2=new Criteria(new Column(TableNames.USER_ADDRESS,"userId"),user.getUserid());
			criteria2.setComparator(Criteria.EQUAL);
			criteria.and(criteria2);
			
			sq.setCriteria(criteria);
			
			Join join1=new Join(new Column(TableNames.ORDERS, "id"),new Column(TableNames.ORDER_VARIANT_REL,"orderId"),Join.LEFT_JOIN);
			Join join2=new Join(new Column(TableNames.ORDERS, "orderedby"),new Column(TableNames.USER_ADDRESS,"addressId"),Join.INNER_JOIN);
			Join join3=new Join(new Column(TableNames.ORDER_VARIANT_REL, "inventoryId"),new Column(TableNames.PRODUCT_INVENTORY,"id"),Join.INNER_JOIN);
			Join join4=new Join(new Column(TableNames.PRODUCT_INVENTORY,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
			Join join5=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE,"id"),Join.INNER_JOIN);
			Join join6=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
			Join join7=new Join(new Column(TableNames.PRODUCT_VARIANT,"colorId"),new Column(TableNames.COLORS,"id"),Join.INNER_JOIN);
	
			sq.setJoin(join1);
			sq.addJoin(join2);
			sq.addJoin(join3);
			sq.addJoin(join4);
			sq.addJoin(join5);
			sq.addJoin(join6);
			sq.addJoin(join7);
	
			
			DataHolder dh=adapter.executeQuery(sq);
			TableHolder orderTh=dh.getTable(TableNames.ORDERS);
			TableHolder addressTh=dh.getTable(TableNames.USER_ADDRESS);
			TableHolder ordVarRelTh=dh.getTable(TableNames.ORDER_VARIANT_REL);
			TableHolder sizeTh=dh.getTable(TableNames.SIZE);
			TableHolder variantTh=dh.getTable(TableNames.PRODUCT_VARIANT);
			TableHolder itemTh=dh.getTable(TableNames.PRODUCT_ITEM);
			TableHolder colorTh=dh.getTable(TableNames.COLORS);
			
			if(orderTh!=null && orderTh.getRows().size()>0) {
				Row orderRow=orderTh.getRows().get(0);
				Row addressRow=addressTh.getRows().get(0);
				Map<Integer,Row> ordVarRelRows=null;
				Map<Integer,Row> sizeRows=null;
				Map<Integer,Row> variantRows=null;
				Map<Integer,Row> itemRows=null;
				Map<Integer,Row> colorRows=null;
				
				if(ordVarRelTh!=null) {
					ordVarRelRows=ordVarRelTh.getRows();
					sizeRows=sizeTh.getRows();
					variantRows=variantTh.getRows();
					itemRows=itemTh.getRows();
					colorRows=colorTh.getRows();
				}
	
				OrderEntity order=new OrderEntity();
				order.setOrderId((Long)orderRow.get("id"));
				order.setDate(new Date((Long)orderRow.get("ordertime")));
				order.setStatusCode(Status.valueOf((String)orderRow.get("status")));
				order.setClientSecret((String)orderRow.get("clientsecret"));
				
				Address address=new Address();
				address.setAddressId((Long)addressRow.get("addressId"));
				address.setAddressLine1((String)addressRow.get("address_line1"));
				address.setAddressLine2((String)addressRow.get("address_line2"));
				address.setCity((String)addressRow.get("city"));
				address.setState((String)addressRow.get("state"));
				address.setCountry((String)addressRow.get("country"));
				address.setPostalCode((String)addressRow.get("postal_code"));
				address.setMobile((String)addressRow.get("mobile"));
				order.setShipTo(address);
			
				int totalValue=0;
				List<Inventory> inventories=new ArrayList<>();
				
				for(int i=0;i<ordVarRelRows.size();i++) {
					 Row orderVarRelRow=ordVarRelRows.get(i);
					 Row sizeRow=sizeRows.get(i);
					 Row variantRow=variantRows.get(i);
					 Row itemRow=itemRows.get(i);
					 Row colorRow=colorRows.get(i);
					 
					 Inventory inventory=new Inventory();
					 
					 inventory.setInventoryId((Long)orderVarRelRow.get("inventoryId"));
					 inventory.setOrderedCount((Integer)orderVarRelRow.get("count"));
					 Size size=new Size();
					 size.setSizeId((Long)sizeRow.get("id"));
					 size.setName((String)sizeRow.get("name"));
					 inventory.setSize(size);
					 
					 ProductVariant variant=new ProductVariant();
					 variant.setVariantId((Long)variantRow.get("id"));
					 variant.setName((String)variantRow.get("name"));
					 variant.setPrice((Integer)variantRow.get("price"));
					 variant.setColor(new Color((Long)colorRow.get("id"), (String)colorRow.get("name"),(String)colorRow.get("csscolor")));
					 variant.setIsActive((Boolean)variantRow.get("isActive"));
					 //set product item in variant
					 ProductItem item=new ProductItem();
					 item.setProductItemId((Long)itemRow.get("id"));
					 item.setProductItemName((String)itemRow.get("name"));
					 item.setDescription(itemRow.get("description")==null?"":(String)itemRow.get("description"));
					 item.setModifiedAt((Long)itemRow.get("modifiedat"));
					 item.setCreatedAt((Long)itemRow.get("createdat"));
					 item.setIsActive((Boolean)itemRow.get("isActive"));
					 variant.setItem(item);
					 
					 inventory.setVariant(variant);
					 
					 inventories.add(inventory);
					 
					 totalValue+=((Integer)orderVarRelRow.get("count")*(Integer)variantRow.get("price"));
				}
				order.setInventories(inventories);
				order.setTotalPrice(totalValue);
				return order;
	 
			}
		}
		return null;

	}
	
	/**public static void main(String args[]) {
		 ShoppingUtilInterface shopUtil=(ShoppingUtilInterface)BeanFactoryWrapper.getBeanFactory().getBean("shoppingutil");
         try {
            GetInfo info=new GetInfo();
            info.setPaginationKey(2L);
         	shopUtil.getTopicsToDisplay(info);
         }catch (Exception e) {
 			e.printStackTrace();
 		}
         shopUtil.closeConnection();
	}**/
}
