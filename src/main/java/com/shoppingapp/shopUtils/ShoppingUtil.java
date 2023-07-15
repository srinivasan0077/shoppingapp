package com.shoppingapp.shopUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import com.shoppingapp.controllers.ProductController;
import com.shoppingapp.dbutils.Column;
import com.shoppingapp.dbutils.Criteria;
import com.shoppingapp.dbutils.DBAdapter;
import com.shoppingapp.dbutils.DataHolder;
import com.shoppingapp.dbutils.Join;
import com.shoppingapp.dbutils.OrderBy;
import com.shoppingapp.dbutils.Row;
import com.shoppingapp.dbutils.SelectQuery;
import com.shoppingapp.dbutils.TableHolder;
import com.shoppingapp.dbutils.UpdateQuery;
import com.shoppingapp.dbutils.OrderBy.Order;
import com.shoppingapp.entities.BannerImage;
import com.shoppingapp.entities.Cart;
import com.shoppingapp.entities.Category;
import com.shoppingapp.entities.Color;
import com.shoppingapp.entities.GetInfo;
import com.shoppingapp.entities.Inventory;
import com.shoppingapp.entities.Product;
import com.shoppingapp.entities.ProductItem;
import com.shoppingapp.entities.ProductVariant;
import com.shoppingapp.entities.Size;
import com.shoppingapp.entities.Topic;
import com.shoppingapp.entities.User;
import com.shoppingapp.entities.VariantImage;
import com.shoppingapp.productUtils.ProductManagementUtil;
import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.ExceptionCause;
import com.shoppingapp.utils.ThreadLocalUtil;

public class ShoppingUtil implements ShoppingUtilInterface {
	
	private static final Logger logger=LogManager.getLogger(ShoppingUtil.class);
	
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
	
	public ArrayList<Topic> getTopicsToDisplay() throws Exception{
		ArrayList<Topic> topics=new ArrayList<>();
		SelectQuery sq=new SelectQuery(TableNames.TOPICS);
		/*select * from topics inner join topic_variant_relation on topics.id=topic_variant_relation.topicId
		 * inner join product_variant on
		 * topic_variant_relation.variantId=product_variant.id inner join product_item
		 * on product_variant.itemId=product_item.id inner join colors on
		 * product_variant.colorId=colors.id inner join product_images on
		 * product_variant.id=product_images.variantId inner join product_inventory on
		 * product_variant.id=product_inventory.variantId inner join size on
		 * product_inventory.sizeId=size.id where topics.active=true;
		 */
		Join join1=new Join(new Column(TableNames.TOPICS,"id"),new Column(TableNames.TOPIC_VARIANT_RELATION,"topicId"),Join.INNER_JOIN);
		Join join2=new Join(new Column(TableNames.TOPIC_VARIANT_RELATION,"variantId"),new Column(TableNames.PRODUCT_VARIANT,"id"),Join.INNER_JOIN);
		Join join3=new Join(new Column(TableNames.PRODUCT_VARIANT,"itemId"),new Column(TableNames.PRODUCT_ITEM,"id"),Join.INNER_JOIN);
		Join join4=new Join(new Column(TableNames.PRODUCT_VARIANT,"colorId"),new Column(TableNames.COLORS,"id"),Join.INNER_JOIN);
		Join join5=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_IMAGES,"variantId"),Join.INNER_JOIN);
		Join join6=new Join(new Column(TableNames.PRODUCT_VARIANT,"id"),new Column(TableNames.PRODUCT_INVENTORY,"variantId"),Join.INNER_JOIN);
		Join join7=new Join(new Column(TableNames.PRODUCT_INVENTORY,"sizeId"),new Column(TableNames.SIZE, "id"),Join.INNER_JOIN);

		
		sq.setJoin(join1);
		sq.addJoin(join2);
		sq.addJoin(join3);
		sq.addJoin(join4);
		sq.addJoin(join5);
		sq.addJoin(join6);
		sq.addJoin(join7);
		
		Criteria criteria=new Criteria(new Column(TableNames.TOPICS, "active"), true);
		criteria.setComparator(Criteria.EQUAL);
		
		Criteria criteria2=new Criteria(new Column(TableNames.PRODUCT_ITEM, "isActive"), true);
		criteria2.setComparator(Criteria.EQUAL);
		
		Criteria criteria3=new Criteria(new Column(TableNames.PRODUCT_VARIANT, "isActive"), true);
		criteria3.setComparator(Criteria.EQUAL);
		
		criteria2.and(criteria3);
		criteria.and(criteria2);
		
		sq.setCriteria(criteria);
		
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
		
		if(cartTh!=null && cartTh.getRows().size()==1) {
			     Row row=cartTh.getRows().get(0);
			     if(((Integer)row.get("count"))+1>inventoryFromDB.getAvailableStocks()) {
			    	 throw new ExceptionCause("Item not available!",HttpStatus.BAD_REQUEST);
			     }
			     UpdateQuery uq=new UpdateQuery(TableNames.CART);
			     uq.setCriteria(criteria1);
			     ArrayList<Column> cols=new ArrayList<Column>();
				 cols.add(new Column(TableNames.CART,"count",(Integer)row.get("count")+1));
				 uq.setFields(cols);
				 adapter.updateData(uq);

		}else {
			if(getCartCount()>=50) {
				 throw new ExceptionCause("Cannot add more than 50 items in cart!",HttpStatus.BAD_REQUEST);
			}
			//Configurations
			String[] fieldNames= {"userId","inventoryId","count"};
			TableHolder th=new TableHolder(TableNames.CART,fieldNames);
			
			//Add data
			Row row=new Row();
			Map<String,Object> columns=row.getColumns();

			columns.put("userId",user.getUserid());
			columns.put("inventoryId",inventory.getInventoryId());
			columns.put("count",1);
            th.setRow(row);
            
            adapter.persistData(th);
		}
		
		
	}
	
	public int getCartCount() throws Exception{
		User user=ThreadLocalUtil.currentUser.get();
		SelectQuery sq=new SelectQuery(TableNames.CART);
		
		Criteria criteria=new Criteria(new Column(TableNames.CART,"userId"),user.getUserid());
		criteria.setComparator(Criteria.EQUAL);
		sq.setCriteria(criteria);
		
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
				inventory.setInventoryId((Long)inventoryRow.get("inventoryId"));
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
			criteria.and(criteria2);
			
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
					inventory.setInventoryId((Long)inventoryRow.get("inventoryId"));
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
        Criteria criteria2=new Criteria(new Column(TableNames.PRODUCT_INVENTORY,"inventory"), 0);
        criteria2.setComparator(Criteria.GREATER);
        criteria.and(criteria2);
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
		ProductVariant variant=new ProductVariant();
		if(variantTh!=null) {
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
		}
		return variant;
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
				product.setHeader((Boolean)row.get("isHeader"));
				
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
				product.setHeader((Boolean)row.get("isHeader"));
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
		 
		 OrderBy orderBy=new OrderBy(new Column(TableNames.PRODUCT_VARIANT,"id"),Order.ASC);
		 subQuery.setOrderBy(orderBy);
		 
		 subQuery.setLimit(12);
		 
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
		 
		 Criteria criteria=new Criteria(new Column(TableNames.PRODUCT_ITEM,"productId"),productId);
		 criteria.setComparator(Criteria.EQUAL);
		 
		 if(info.getPaginationKey()!=null) {
				Criteria criteria2=new Criteria(new Column(TableNames.PRODUCT_VARIANT,"id"), info.getPaginationKey());
				criteria2.setComparator(Criteria.GREATER);
				criteria.and(criteria2);
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
			    	criteria3.and(criteria);
			    	criteria=criteria3;
				    
				}
		  }
		 
		  Criteria criteria4=new Criteria(new Column(TableNames.PRODUCT_ITEM, "isActive"), true);
		  criteria4.setComparator(Criteria.EQUAL);
			
		  Criteria criteria5=new Criteria(new Column(TableNames.PRODUCT_VARIANT, "isActive"), true);
		  criteria5.setComparator(Criteria.EQUAL);
		 
		  criteria4.and(criteria);
		  criteria5.and(criteria4);
		  
		  subQuery.setCriteria(criteria5);
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
	
}
