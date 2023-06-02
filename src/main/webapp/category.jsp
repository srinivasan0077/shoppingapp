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
    
</style>
<body>
   
    <h3>Categories</h3>
    <table class="table">
	  <thead>
	    <tr>
	      <th scope="col">Id</th>
	      <th scope="col">Name</th>
	      <th scope="col">Description</th>
	      <th scope="col">Created Time</th>
	      <th scope="col">Modified Time</th>
	    </tr>
	  </thead>
	  <tbody id="category-data">
	
	 
	  </tbody>
	</table>
    
    <form class="form-style">
      <h3>Add Categories</h3>
	  <div class="form-group" >
	    <label for="category">Enter Category</label>
	    <input type="text" class="form-control" id="category"  placeholder="Category">
	  </div>
	  <div class="form-group">
	    <label for="description">Enter Description</label>
	    <input type="text" class="form-control" id="description" placeholder="Description">
	  </div>
	  <button type="button" id="category-button" class="btn btn-primary">Add</button>
    </form>
    
    <form id="edit-category-form" class="form-style" style="display: none;">
      <h3>Edit Category</h3>
      <div class="form-group">
	    <label for="id">Category ID</label>
	    <input type="text" class="form-control" id="edit-category-id" readonly="readonly" >
	  </div>
	  <div class="form-group">
	    <label for="edit-category">Enter Category</label>
	    <input type="text" class="form-control" id="edit-category" >
	  </div>
	  <div class="form-group">
	    <label for="edit-description">Enter Description</label>
	    <input type="text" class="form-control" id="edit-description">
	  </div>
	  <div>
	  <button type="button" id="edit-category-button" class="btn btn-primary">Save</button>
	  <button type="button" id="close-edit-category-button" class="btn btn-primary">Close</button>
	  </div>
    </form>
    
    <script>
            const origin=window.location.origin+"/shoppingapp/admin/";
            
            
            document.addEventListener("DOMContentLoaded",()=>{
            	fetchCategories();

            })
            
		    document.getElementById("category-button").addEventListener("click",()=>{
		        let category=document.getElementById("category").value.trim();
		        let description=document.getElementById("description").value.trim();
		        let input={
		        	productTypeName:category,
		            description:description
		        }
		        let url=origin+"api/categories";
		        if(validateCategory(input)){
		
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
		            		 document.getElementById("category").value="";
		            		 document.getElementById("description").value="";
 		                	alert(data.map.message);
 		                	fetchCategories();
 		                }else{
 		                	alert(data.map.message);
 		                }
		            	
		            })
		        }else{
		        	alert("Enter valid Input");
		        }
		    
		  })
		
		  document.getElementById("edit-category-button").addEventListener("click",()=>{
			  let id=document.getElementById("edit-category-id").value.trim();
      		  let name=document.getElementById("edit-category").value.trim();
      		  let description=document.getElementById("edit-description").value.trim();
      		  let url=origin+"api/categories";
      		  let input={
      				productTypeId:id,
      				productTypeName:name,
      				description:description
      		  }
      		  if(!isNaN(input.productTypeId) && validateCategory(input)){
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
    		                	fetchCategories();
    		                }else{
    		                	alert(data.map.message);
    		                }
    		            });
      			
      		  }else{
      			   alert("Enter valid Input");
      		  }
      		 document.getElementById("edit-category-form").style.display="none";
		  })
		  
		  document.getElementById("close-edit-category-button").addEventListener("click",()=>{
			  document.getElementById("edit-category-form").style.display="none";
		  })
		  
		  function fetchCategories(){
                let url=origin+"api/categories";
                let tbody=document.getElementById("category-data");
                tbody.innerHTML="";
                fetch(url).then(res=>res.json()).then(obj=>{
                   console.log(obj);
                   for(let i=0;i<obj.length;i++){
                   	   let category=obj[i];
                   	   let tr=document.createElement("tr");
                   	   let btn=document.createElement("button");
                   	   let buttontd=document.createElement("td");
                   	   btn.className="btn btn-primary";
                   	   btn.innerHTML="EDIT";
                   	   btn.addEventListener("click",()=>{
                   		   document.getElementById("edit-category-id").value=category["productTypeId"];
                   		   document.getElementById("edit-category").value=category["productTypeName"];
                   		   document.getElementById("edit-description").value=category["description"];
                   		   document.getElementById("edit-category-form").style.display="block";
                   	   })
                   	   
                   	   buttontd.appendChild(btn);
                   	   for (let key in category) {
                   		   let td=document.createElement("td");
                   		   if(key=="createdAt" || key=="modifiedAt"){
                   			   td.innerHTML=new Date(category[key]);
                   		   }else{
                   		       td.innerHTML=category[key];
                   		   }
                   		   tr.appendChild(td);
                   	   }
                   	   tr.appendChild(buttontd);
                       tbody.appendChild(tr);
                   }
                })
          }
            
		  function validateCategory(input){
		        if(input.productTypeName==undefined || input.description==undefined || input.productTypeName=="" || input.description==""){
		            return false;
		        }
		
		        if(input.productTypeName.length<=1 || input.description.length<=1){
		            return false;
		        }
		
		        return true;
		  }
    </script>

</body>
</html>