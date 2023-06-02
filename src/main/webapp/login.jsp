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
    <h3>Login Page</h3>
    
    <form class="form-style">
  
	  <div class="form-group" >
	    <label for="login-email">Email</label>
	    <input type="email" class="form-control" id="login-email"  placeholder="Enter Email">
	  </div>
	  <div class="form-group">
	    <label for="login-password">Enter Password</label>
	    <input type="password" class="form-control" id="login-password" placeholder="Enter Password">
	  </div>
	  <button type="button" id="login-button" class="btn btn-primary">Login</button>
    </form>
    <script type="text/javascript">
            const origin=window.location.origin+"/shoppingapp/";
            document.getElementById("login-button").addEventListener("click",()=>{
            	let url=origin+"login";
            	let email=document.getElementById("login-email").value.trim();
            	let password=document.getElementById("login-password").value.trim();
            	let input={
            			email:email,
            			password:password
            	};
            	
            	fetch(url, 
    		            {
    		                method:'POST',
    		                body:JSON.stringify(input),
    		                headers: {
    		                 'Content-Type': 
    		                'application/json;charset=utf-8'
    		                }
    		            }).then(res=>res.json()).then(data=>{
    		                if(data.map.status==200){
    		                	alert(data.map.message);
    		                }else{
    		                	alert(data.map.message);
    		                }
    		            });
            })
    </script>
</body>
</html>