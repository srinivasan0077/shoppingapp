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
<body>
    <h3>Product Items</h3>
    <button class="btn btn-primary" id="prev">Previous</button>
    <button class="btn btn-primary" id="next">Next</button>
    <table class="table">
	  <thead>
	    <tr>
	      <th scope="col">Id</th>
	      <th scope="col">Name</th>
	      <th scope="col">Product</th>
	      <th scope="col">Description</th>
	      <th scope="col">Created Time</th>
	      <th scope="col">Modified Time</th>
	    </tr>
	  </thead>
	  <tbody id="item-data">
	
	 
	  </tbody>
	</table>
	
	<script type="text/javascript">
			 const origin=window.location.origin+"/shoppingapp/admin/";
			 let stack=[0];
			 let lastItem;
			 let range=10;
			 document.addEventListener("DOMContentLoaded",()=>{
				 let input={
						 range:range
				 }
				 fetchProductItems(input);
                
	         })
	         
	         document.getElementById("next").addEventListener("click",()=>{
	        	 let input={
	        			 range:range
	        	 }
	        	 if(stack.length>0){
	        		 input["paginationKey"]=stack[stack.length-1];
	        		 fetchProductItems(input);
	        	
	        	 }
	       
	         })
	         
	         document.getElementById("prev").addEventListener("click",()=>{
	        	 let input={
	        			 range:range
	        	 }
	        	 if(stack.length>0){
	        		 for(let i=stack.length-1;i>stack.length-2 && i>0;i--){
	        			
	        				 stack.pop();
	        			 
	        		 }
	        		 input["paginationKey"]=stack[stack.length-1];
	        		 fetchProductItems(input);
		        	
	        	 }
	        	 console.log(stack);
	        	
	         })
	         
	         function fetchProductItems(input){
				    let url=origin+"api/productItem?input="+encodeURIComponent(JSON.stringify(input));
	                let tbody=document.getElementById("item-data");
	                tbody.innerHTML="";
		            fetch(url).then(res=>res.json()).then(obj=>{
	                   console.log(obj);
	                   for(let i=0;i<obj.length;i++){
	                   	   let item=obj[i];
	                   	   let tr=document.createElement("tr");
	                   	   let btn=document.createElement("button");
	                   	   let buttontd=document.createElement("td");
	                   	   btn.className="btn btn-primary";
	                   	   
	                   	   for (let key in item) {
	                   		   let td=document.createElement("td");
	                   		   if(key=="createdAt" || key=="modifiedAt"){
	                   			   td.innerHTML=new Date(item[key]);
	                   		   }else if(key=="product"){
	                   			   td.innerHTML=item[key]["productName"];
	                   		   }else if(key=="isActive"){
	                   			   continue;
	                   		   }else{ 
	                   		    	  td.innerHTML = item[key];
	                   		   }
	                   		   tr.appendChild(td);
	                   	   }
	                   	  
	                   	   if(item.isActive){
	                   		   btn.innerHTML="Deactivate";
	                   	   }else{
	                   		   btn.innerHTML="Activate";
	                   	   }
	                   	   
	                   	   btn.addEventListener('click',()=>{
	                   		   item.isActive=(item.isActive==true)?false:true;
	                   		   putItem(item);
	                   	   })
	                   	   
	                   	   buttontd.appendChild(btn)
	                   	   tr.appendChild(buttontd);
	                       tbody.appendChild(tr);
	                   }
	          
	                   if(obj.length>0){ 
	                	  
	                	   stack.push(obj[obj.length-1]["productItemId"]);
	                	   console.log(stack)
	                   }
	                })
			 }
			 
			 function putItem(item){
				    let url=origin+"api/productItem/"+item.productItemId;
				    let input=item;
				    console.log(item)
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
	    		                	alert(data.map.message);
	    		                	let index;
	    		                	if(stack.length-2<0){
	    		                		index=0;
	    		                	}else{
	    		                		index=stack.length-2;
	    		                		stack.pop();
	    		                	}
	    		                	 let refreshInput={
	    		    	        			 range:range,
	    		    	        			 paginationKey:stack[index]
	    		    	        	 }
	    		                	 fetchProductItems(refreshInput);
	    		                }else{
	    		                	alert(data.map.message);
	    		                }
	    		            });
			 }
	         
	         
	</script>
</body>
</html>