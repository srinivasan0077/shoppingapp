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
     }
     
     .form-child{
         margin:10px;
     }
     
     .row-style{
          display: flex;
          flex-direction: row;
       }
</style>
<body>
  <form class="form-style">
      <h3>Add Product Item</h3>
	  <div class="form-group form-child" >
	    <label for="product-item">Enter Product Item:</label>
	    <input type="text" class="form-control" id="product-item"  placeholder="Product Item">
	  </div>
	  <div class="form-group form-child">
	    <label for="product">Choose a Product:</label>
	  	<select name="product" id="productId" class="form-select" aria-label="Default select example">
		 
		</select>
	  </div>
	  <div class="form-group form-child">
	    <label for="price">Enter Price</label>
	    <input type="number" class="form-control" id="item-price" placeholder="Price">
	  </div>
	  <div class="form-group form-child">
	    <label for="description">Enter Description</label>
	    <input type="text" class="form-control" id="description" placeholder="Description">
	  </div>
	  
	  <div class="container form-child">
	     <div id="variant-rows">
		      
	      </div>
	      <button type="button" id="add-variant" class="btn btn-primary">Add Variant</button>
	  </div>
	  <button type="button" id="product-item-button" class="btn btn-primary">Add</button>
    </form>
    
    <script type="text/javascript">
        const origin=window.location.origin+"/shoppingapp/admin/";
        const variantRows=document.getElementById("variant-rows");
        let tracker=1;
        let trackerIds=[];
        let colors=[];
        let sizes=[];
        let selectedProduct;
        
        document.addEventListener("DOMContentLoaded",async ()=>{
        	await fetchProducts();
        	await fetchColors();
        	await fetchSizes(selectedProduct);
        	createVariantOptions();
   
        })
        
        document.getElementById("add-variant").addEventListener("click",()=>{
        	createVariantOptions();
        })
       
        document.getElementById("product-item-button").addEventListener("click",()=>{
        	let url=origin+"api/productItem";
        	let itemName=document.getElementById("product-item").value;
        	let productId=document.getElementById("productId").value;
        	let price=document.getElementById("item-price").value;
        	let description=document.getElementById("description").value;
        	let formData = new FormData();
        	let input={
        			"itemName":itemName,
        			"productId":productId,
        			"price":price,
        			"description":description,
        			"variants":[]
        	}
        	
        	for(let i=0;i<trackerIds.length;i++){
        		let variant={sizes:[]};
        		let color=document.getElementById("color"+trackerIds[i]);
        		let image=document.getElementById("image"+trackerIds[i]);
        		for(let j=0;j<sizes.length;j++){
        			let size=document.getElementById(sizes[j].sizeId+""+sizes[j].name+""+trackerIds[i]);
        			if(size.checked){
        				variant.sizes.push(size.value);
        			}
        		}
        		variant.color=color.value;
        		let temp=0;
        		for(let file of image.files){
        			formData.append("file[]",file,"file;"+(i)+";"+(temp));
        			temp+=1;
        		}
        		input.variants.push(variant);
        		
        	}
        	formData.append("product_item",JSON.stringify(input));
        	console.log(input);
        	console.log(formData.keys);
        	
        	fetch(url, 
		            {
		                method:'POST',
		                body:formData
		            }).then(res=>res.json()).then(data=>{
		            	 if(data.map.status==2000){
		            	    
 		                	alert(data.map.message);
 		                	tracker=1;
 		                	trackerIds=[];
 		                	variantRows.innerHTML="";
 		                	createVariantOptions();
 		                	document.getElementById("product-item").value="";
 		               	    document.getElementById("item-price").value="";
 		               	    document.getElementById("description").value="";
 		                	
 		                }else{
 		                	alert(data.map.message);
 		                }
		            	
		            })
        	
        })
        
        document.getElementById("productId").addEventListener("change",async ()=>{
        	tracker=1;
            trackerIds=[];
        	selectedProduct=document.getElementById("productId").value;
        	await fetchSizes(selectedProduct);
        	createVariantOptions();
        })
        
        async function fetchColors(){
        	let colorUrl=origin+"api/colors";
       		await fetch(colorUrl).then(res=>res.json()).then(obj=>{
           		for(let i=0;i<obj.length;i++){
           			colors[i]=obj[i];
           		}  	
           	})
        	
        }
        
        async function fetchSizes(productId){
        	console.log(colors)
        	sizes=[]
        	variantRows.innerHTML="";
        	let input={
      			  productId:productId
      	    }
        	let sizeUrl=origin+"api/sizes?input="+encodeURIComponent(JSON.stringify(input));
        	
        	if(productId!=undefined){
	        	await fetch(sizeUrl).then(res=>res.json()).then(obj=>{
	        		for(let i=0;i<obj.length;i++){
	        			sizes[i]=obj[i];		
	        		}
	        		sortSize();
	      
	        	})
        	}
        }
        
		async function fetchProducts(){
			let url=origin+"api/products";
        	await fetch(url).then(res=>res.json()).then(obj=>{
        		const productSelect=document.getElementById("productId");
        		if(obj.length>0){
        			selectedProduct=obj[0].productId;
        		}
        		for(let i=0;i<obj.length;i++){
        			let option=document.createElement("option");
          		    option.value=obj[i].productId;
              	    option.innerHTML=obj[i].productName;	
              	    productSelect.appendChild(option);
              	   
        		}
        	})
        }
		
		function sortSize(){
			for(let i=0;i<sizes.length-1;i++){
				for(let j=i+1;j<sizes.length;j++){
					if(sizes[i].order>sizes[j].order){
						let temp=sizes[i];
						sizes[i]=sizes[j];
						sizes[j]=temp;
					}
				}
			}
		}
		
		function createVariantOptions(){
			let newRow=document.createElement("div");
    		let sizeRow=document.createElement("div");
        	let colorRow=document.createElement("div");
        	let imageRow=document.createElement("div");
        	let cancelDiv=document.createElement("div");
        	newRow.id="variant;"+tracker;
        	newRow.className="row";
        	sizeRow.className="col";
        	colorRow.className="col";
        	imageRow.className="col";
        	cancelDiv.className="col";
        	
        	//sizes
        	sizeRow.style="display: flex;flex-direction: row;";
        	for(let i=0;i<sizes.length;i++){
        		let sizeElement=document.createElement("div");
        		let sizeLabel=document.createElement("label");
        		let sizeInput=document.createElement("input");
        		
        		sizeElement.className="form-check";
        		sizeInput.type="checkbox";
        		sizeInput.id=sizes[i].sizeId+""+sizes[i].name+""+tracker;
        		sizeInput.value=sizes[i].sizeId;
        		
        		sizeLabel.for=sizeInput.id;
        		sizeLabel.innerHTML=sizes[i].name;
        		
        		sizeElement.appendChild(sizeInput);
        		sizeElement.appendChild(sizeLabel);
        		sizeRow.appendChild(sizeElement);
        	}
        	
        	//colors
        	let colorChildDiv=document.createElement("div");
        	let colorChildDiv1=document.createElement("div");
        	let colorChildDiv2=document.createElement("div");
        	let colorSelectOption=document.createElement("select");
        	
        	colorChildDiv.className="row-style";
        	colorChildDiv.appendChild(colorChildDiv1);
        	colorChildDiv.appendChild(colorChildDiv2);
        
        	colorChildDiv1.innerHTML="Select Color:";
        	colorChildDiv2.appendChild(colorSelectOption);
        	
        	colorSelectOption.id="color"+tracker;
        	colorSelectOption.className="form-select";
        	
        	for(let i=0;i<colors.length;i++){
        		let option=document.createElement("option");
      		    option.value=colors[i].colorId;
          	    option.innerHTML=colors[i].name;	
          	    colorSelectOption.appendChild(option);
        	}
        	
        	colorRow.appendChild(colorChildDiv);
        	
        	//image Input
        	let imageLabel=document.createElement("label");
        	let imageInput=document.createElement("input");
        	
        	imageInput.id="image"+tracker;
        	imageInput.type="file";
        	imageInput.accept="image/*";
        	imageInput.multiple=true;
        	
        	imageLabel.for="image"+tracker;
        	imageLabel.innerHTML="Select Image:";
        	imageRow.appendChild(imageLabel);
        	imageRow.appendChild(imageInput);
        	
        	//Cancel Button
        	let cancelBut=document.createElement("input");
        	cancelBut.type="button";
        	cancelBut.value="X";
        	cancelDiv.appendChild(cancelBut);
        	
        	cancelBut.addEventListener("click",()=>{
        		let variantIdSplit=newRow.id.split(";");
        		variantRows.removeChild(newRow);
        		console.log(variantIdSplit)
        		trackerIds=trackerIds.filter(id=>{
        			console.log(id==variantIdSplit[1])
        			console.log(id)
        			if(id!=variantIdSplit[1]){
        				return true;
        			}
        		});
        		console.log(trackerIds);
        	})
        	
        	newRow.appendChild(sizeRow);
        	newRow.appendChild(colorRow);
        	newRow.appendChild(imageRow);
        	newRow.appendChild(cancelDiv);
        	
        	variantRows.appendChild(newRow);
        	trackerIds.push(tracker);
        	console.log(trackerIds);
        	tracker++;
		}
    </script>
</body>
</html>