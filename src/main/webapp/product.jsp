<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Shop Daily</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-GLhlTQ8iRABdZLl6O3oVMWSktQOp6b7In1Zl3/Jr59b6EGGoI1aFkw7cmDA6j6gD" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js" integrity="sha384-w76AqPfDkMBDXo30jS1Sgez6pr3x5MlQ1ZAGC+nuZB+EYdgRZgiwxhTBTkF7CXvN" crossorigin="anonymous"></script>
</head>
<style>
     .form-style{
          border-radius: 10px;
          box-shadow: 2px 2px 30px rgba(0,0,0,0.2);
          padding: 20px;
          margin:40px;
          width:400px;
     }
     
     .form-popup-style{
          border-radius: 10px;
          background-color:white;
          box-shadow: 2px 2px 30px rgba(0,0,0,0.2);
          padding: 20px;
          margin:40px;
          width:400px;
     }
    
</style>
<body>
    
     <h3>Products</h3>
    <table class="table">
	  <thead>
	    <tr>
	      <th scope="col">Id</th>
	      <th scope="col">Name</th>
	      <th scope="col">Category</th>
	      <th scope="col">Description</th>
	      <th scope="col">Created Time</th>
	      <th scope="col">Modified Time</th>
	    </tr>
	  </thead>
	   <tbody id="product-data">
	
	 
	  </tbody>
	</table>
    <div class="container">
         <div class="row">
              <div class="col">
                  <form class="form-style">
				      <h3>Add Products</h3>
					  <div class="form-group" >
					    <label for="product">Enter Product</label>
					    <input type="text" class="form-control" id="product"  placeholder="Product">
					  </div>
					  <div class="form-group">
					    <label for="category">Choose a Category:</label>
					  	<select name="category" id="categoryId" class="form-select" aria-label="Default select example">
						 
						</select>
					  </div>
					  <div class="form-group">
					    <label for="description">Enter Description</label>
					    <input type="text" class="form-control" id="description" placeholder="Description">
					  </div>
					  <button type="button" id="product-button" class="btn btn-primary">Add</button>
				    </form>
				    
				    <form id="edit-product-form" class="form-style" style="display: none;">
				      <h3>Edit Product</h3>
				      <div class="form-group">
					    <label for="id">Product ID</label>
					    <input type="text" class="form-control" id="edit-product-id" readonly="readonly" >
					  </div>
					  <div class="form-group">
					    <label for="edit-product">Enter Category</label>
					    <input type="text" class="form-control" id="edit-product" >
					  </div>
					  <div class="form-group">
					    <label for="category">Choose a Category:</label>
					  	<select name="category" id="edit-categoryId" class="form-select" aria-label="Default select example">
				
						</select>
					  </div>
					  <div class="form-group">
					    <label for="edit-description">Enter Description</label>
					    <input type="text" class="form-control" id="edit-description">
					  </div>
					  <div>
					  <button type="button" id="edit-product-button" class="btn btn-primary">Save</button>
					  <button type="button" id="close-edit-product-button" class="btn btn-primary">Close</button>
					  </div>
				    </form> 
              </div>
              <div class="col">
                      <div>
                      <div id="size-title"></div>
                      <div><button class="btn btn-primary" id="add-size">Add</button></div>
                      </div>
                      <table class="table">
						  <thead>
						    <tr>
						      <th scope="col">Id</th>
						      <th scope="col">Name</th>
						      <th scope="col">Order</th>
                              <th scope="col">Description</th>
						    </tr>
						  </thead>
						   <tbody id="size-data">
						
						 
						  </tbody>
						</table>
              </div>
         </div>
         
    </div>
   
    <div class="form-popup-style" id="edit-add-size-form" style="position: absolute;top:0%;left:30%;display: none;">
                     <h3 id="edit-add-size-form-title">Add Sizes</h3>
                      <input type="number" id="sizeId" hidden="true"/>
					  <div class="form-group" >
					    <label for="productId">Product Id</label>
					    <input type="number" class="form-control" id="productId" disabled="disabled">
					  </div>
					   <div class="form-group">
					    <label for="size-name">Enter Name</label>
					    <input type="text" class="form-control" id="size-name" placeholder="Name">
					  </div>
					   <div class="form-group">
					    <label for="size-order">Enter Order</label>
					    <input type="number" class="form-control" id="size-order" >
					  </div>
					  <div class="form-group">
					    <label for="size-description">Enter Description</label>
					    <input type="text" class="form-control" id="size-description" placeholder="Description">
					  </div>
					  <div>
					  <button type="button" id="size-button" class="btn btn-primary">Add</button>
					  <button type="button" id="close-size-button" class="btn btn-primary">Close</button>
			</div>
    </div>
    
    <script>
            const origin=window.location.origin+"/shoppingapp/admin/";
            const categoryIdNameMap={};
            
            document.addEventListener("DOMContentLoaded",()=>{
            	fetchCategories();
            	fetchProducts();
          
            })
           
		  document.getElementById("product-button").addEventListener("click",()=>{
			    let name=document.getElementById("product").value.trim();
			    let categoryId=document.getElementById("categoryId").value.trim();
		        let description=document.getElementById("description").value.trim();
		        let input={
		        	productName:name,
		        	productType:{
		        		productTypeId:categoryId
		        	},
		            description:description
		        }
		        let url=origin+"api/products";
		        
		        if(validateProducts(input)){
				        fetch(url, 
					            {
					                method:'POST',
					                body:JSON.stringify(input),
					                headers: {
					                 'Content-Type': 
					                'application/json;charset=utf-8'
					                }
					            }).then(res=>res.json()).then(data=>{
					            	 if(data.map.status==2000){
					            		 document.getElementById("product").value="";
					            		 document.getElementById("description").value="";
			 		                	 alert(data.map.message);
			 		                	 fetchProducts();
			 		                }else{
			 		                	alert(data.map.message);
			 		                }
					            	
					            })
		        }else{
		        	 alert("Enter valid Input");
		        }
		  })
		  
		  document.getElementById("edit-product-button").addEventListener("click",()=>{
			  let id=document.getElementById("edit-product-id").value.trim();
      		  let name=document.getElementById("edit-product").value.trim();
      		  let categoryId=document.getElementById("edit-categoryId").value.trim();
      		  let description=document.getElementById("edit-description").value.trim();
      		  let url=origin+"api/products";
      		  let input={
      				productId:id,
		        	productName:name,
		        	productType:{
		        		productTypeId:categoryId
		        	},
		            description:description
		        }
      		  if(validateProducts(input) && !isNaN(input.productId)){
      			fetch(url, 
    		            {
    		                method:'PUT',
    		                body:JSON.stringify(input),
    		                headers: {
    		                 'Content-Type': 
    		                'application/json;charset=utf-8'
    		                }
    		            }).then(res=>res.json()).then(data=>{
    		                if(data.map.status==2000){
    		                	fetchProducts();
    		                	alert(data.map.message);
    		                }else{
    		                	alert(data.map.message);
    		                }
    		            });
      			
      		  }else{
      			   alert("Enter valid Input");
      		  }
      		 document.getElementById("edit-product-form").style.display="none";
		  })
		  
		  document.getElementById("close-edit-product-button").addEventListener("click",()=>{
			  document.getElementById("edit-product-form").style.display="none";
		  })
		 
		  
		  
		  function fetchProducts(){
            	console.log("fetchProducts() called");
                let url=origin+"api/products";
                let tbody=document.getElementById("product-data");
                tbody.innerHTML="";
                fetch(url).then(res=>res.json()).then(obj=>{
                   console.log(obj);
                   for(let i=0;i<obj.length;i++){
                   	   let product=obj[i];
                   	   let tr=document.createElement("tr");
                   	   let btn=document.createElement("button");
                   	   let buttontd=document.createElement("td");
                   	   btn.className="btn btn-primary";
                   	   btn.innerHTML="EDIT";
                   	   btn.addEventListener("click",()=>{
                   		   document.getElementById("edit-product-id").value=product["productId"];
                   		   document.getElementById("edit-product").value=product["productName"];
                   		   document.getElementById("edit-description").value=product["description"];
                   		   document.getElementById("edit-product-form").style.display="block";
                   		   let selectedOption=document.getElementById(product["productType"].productTypeId);
                   		   selectedOption.selected=true;
                   		  
                   	   })
                   	   
                   	   buttontd.appendChild(btn);
                   	   for (let key in product) {
                   		   let td=document.createElement("td");
                   		   if(key=="createdAt" || key=="modifiedAt"){
                   			   td.innerHTML=new Date(product[key]);
                   		   }else if(key=="productType"){
                   			   td.innerHTML=product[key].productTypeName;
                   			   
                   		   }else{
                   		       td.innerHTML=product[key];
                   		   }
                   		   tr.appendChild(td);
                   	   }
                   	   tr.appendChild(buttontd);
                   	   tr.addEventListener("click",()=>{
                   		   fetchSizes(product["productId"],"Size Details for "+product["productName"]);
                   	   })
                       tbody.appendChild(tr);
                   }
                })
          }
            
          function fetchCategories(){
        	 
        	  let url=origin+"api/categories";
              fetch(url).then(res=>res.json()).then(obj=>{
            	  let selectOption=document.getElementById("categoryId");
            	  let editSelectOption=document.getElementById("edit-categoryId");
            	  for(let i=0;i<obj.length;i++){ 
            		  let option=document.createElement("option");
            		  let editOption;
            		  option.value=obj[i].productTypeId;
                	  option.innerHTML=obj[i].productTypeName;
                      editOption=option.cloneNode(true);
                      editOption.id=obj[i].productTypeId;
                      selectOption.appendChild(option);
                      editSelectOption.appendChild(editOption);
            		  categoryIdNameMap[obj[i].productTypeId]=obj[i].productTypeName;
            	  }
            	  
              })
          }
          
          function fetchSizes(productId,productName){
         	 
        	  document.getElementById("size-title").innerHTML=productName;
        	  document.getElementById("productId").value=productId;
        	  
        	  let input={
        			  productId:productId
        	  }
        	  let url=origin+"api/sizes?input="+encodeURIComponent(JSON.stringify(input));
        	  let tbody=document.getElementById("size-data");
        	  tbody.innerHTML="";
              fetch(url).then(res=>res.json()).then(obj=>{
            	  
            	  for(let i=0;i<obj.length;i++){
            		  let tr=document.createElement("tr");
            		  let size=obj[i];
            		  let btn=document.createElement("button");
                  	  let buttontd=document.createElement("td");
                  	  btn.className="btn btn-primary";
                  	  btn.innerHTML="EDIT";

                  	  btn.addEventListener("click",()=>{
                  		   document.getElementById("sizeId").value=size["sizeId"];
                  		   document.getElementById("size-name").value=size["name"];
                  		   document.getElementById("size-description").value=(size["description"]==undefined || size["description"]==null)?"":size["description"];
                  		   document.getElementById("size-order").value=size["order"];
                  		   document.getElementById("edit-add-size-form").style.display="block";
                  		   document.getElementById("edit-add-size-form-title").innerHTML="Edit Size";
                  		   document.getElementById("size-button").innerHTML="Edit";
                  	  })
                  	  
                  	   
                  	  buttontd.appendChild(btn);
            		  for (let key in size) {
                  		   let td=document.createElement("td");
                  		   td.innerHTML=size[key];
                  		   tr.appendChild(td);
                  	   }
            		  if(size["description"]==undefined){
            			   let td=document.createElement("td");
                 		   td.innerHTML="";
                 		   tr.appendChild(td);
            		  }
            		  tr.appendChild(buttontd);
            		  tbody.appendChild(tr);
            	  }
            	  
            	  
              })
          }
          
          document.getElementById("close-size-button").addEventListener("click",()=>{
			  document.getElementById("edit-add-size-form").style.display="none";
		  })
		  
		  document.getElementById("size-button").addEventListener("click",()=>{
			   let url="api/sizes";
			   let operation="POST";
			   let id=document.getElementById("sizeId").value;
     		   let productId=document.getElementById("productId").value;
     		   let name=document.getElementById("size-name").value.trim();
     		   let desc=document.getElementById("size-description").value.trim();
     		   let order=document.getElementById("size-order").value;
     		   let input={
     				name:name,
     				order:order,
     				product:{
  					   productId:productId
     			    }
     		   }
     		   if(desc!=undefined && desc!=""){
     			   input.description=desc;
     		   }
     		   if(id!=undefined && id!=""){
     			   input.sizeId=id;
     			   operation="PUT"
     		   }
     		 
     		  fetch(url, 
  		            {
  		                method:operation,
  		                body:JSON.stringify(input),
  		                headers: {
  		                 'Content-Type': 
  		                'application/json;charset=utf-8'
  		                }
  		            }).then(res=>res.json()).then(data=>{
  		                if(data.map.status==2000){
  		                	alert(data.map.message);
  		                	fetchSizes(productId,document.getElementById("size-title").innerHTML);
  		                	document.getElementById("edit-add-size-form").style.display="none";
  		                }else{
  		                	alert(data.map.message);
  		                }
  		       });
     		   
		  })
		  
		  document.getElementById("add-size").addEventListener("click",()=>{
			   document.getElementById("sizeId").value="";
     		   document.getElementById("size-name").value="";
     		   document.getElementById("size-description").value="";
     		   document.getElementById("size-order").value="";
     		   document.getElementById("edit-add-size-form").style.display="block";
     		   document.getElementById("edit-add-size-form-title").innerHTML="Add Size";
     		   document.getElementById("size-button").innerHTML="Add";
		  })
		  function validateProducts(input){
		        if(input.productName==undefined || input.description==undefined
		        		|| input.productName=="" || input.description=="" || isNaN(input.productType.productTypeId)){
		            return false;
		        }
		
		        if(input.productName.length<=1 || input.description.length<=1){
		            return false;
		        }
		
		        return true;
		  }
    </script>
</body>
</html>