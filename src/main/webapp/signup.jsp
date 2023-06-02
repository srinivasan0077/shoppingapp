<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body>
    <form>
        <label for="fname">First name:</label><br>
        <input type="text" id="fname" name="fname"><br>
        <label for="lname">Last name:</label><br>
        <input type="text" id="lname" name="lname">  
        <label for="email">Email ID:</label><br>
        <input type="email" id="email" name="email">  
        <label for="pw">Password:</label><br>
        <input type="text" id="pw" name="pw">  
        <label for="phone">Phone No:</label><br>
        <input type="number" id="phone" name="phone">  
        <input type="button" id="signup-bt" value="SignUp">
    </form>
    <input type="text" id="otp">
    <input type="button" id="otp-bt" value="Send">
    <script type="text/javascript">
        document.getElementById("signup-bt").addEventListener("click",()=>{
        	var fname=document.getElementById("fname").value;
        	var lname=document.getElementById("lname").value;
        	var email=document.getElementById("email").value;
        	var pw=document.getElementById("pw").value;
        	var phone=document.getElementById("phone").value;
        	var input_data={
        			firstname:fname,
        			lastname:lname,
        			email:email,
        			password:pw,
        			phone:phone
        	}
        	console.log(input_data)
        	fetch("http://localhost:8080/shoppingapp/signup", {
        	     
        	    // Adding method type
        	    method: "POST",
        	     
        	    // Adding body or contents to send
        	    body:JSON.stringify(input_data),
        	     
        	    // Adding headers to the request
        	    headers: {
        	        "Content-type": "application/json; charset=UTF-8"
        	    }
        	})
        	 
        	// Converting to JSON
        	.then(response =>{
        		console.log(response)
        	})
        })
        
        document.getElementById("otp-bt").addEventListener("click",()=>{
        	var otp=document.getElementById("otp").value;
        	var input_data={
        			otp:otp
        	}
        	console.log(input_data)
        	fetch("http://localhost:8080/shoppingapp/otpvalidation", {
        	     
        	    // Adding method type
        	    method: "POST",
        	     
        	    // Adding body or contents to send
        	    body:JSON.stringify(input_data),
        	     
        	    // Adding headers to the request
        	    headers: {
        	        "Content-type": "application/json; charset=UTF-8"
        	    }
        	})
        	 
        	// Converting to JSON
        	.then(response =>{
        		console.log(response)
        	})
        })
    </script>
</body>
</html>
