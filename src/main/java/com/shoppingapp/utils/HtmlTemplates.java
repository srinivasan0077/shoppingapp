package com.shoppingapp.utils;

import com.shoppingapp.entities.Address;
import com.shoppingapp.entities.OrderEntity;

public class HtmlTemplates {

	public static String getSignupOtpTemplate(String name,String otp) {
		return "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">\r\n"
				+ "  <div style=\"margin:50px auto;width:70%;padding:20px 0\">\r\n"
				+ "    <div style=\"border-bottom:1px solid #eee\">\r\n"
				+ "      <a href=\"\" style=\"font-size:1.4em;color: #00466a;text-decoration:none;font-weight:600\">Royall</a>\r\n"
				+ "    </div>\r\n"
				+ "    <p style=\"font-size:1.1em\">Hi "+name+",</p>\r\n"
				+ "    <p>Thank you for choosing Royall. Use the following OTP to log in. OTP is valid for 3 minutes</p>\r\n"
				+ "    <h2 style=\"background: #00466a;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">"+otp+"</h2>\r\n"
				+ "    <p style=\"font-size:0.9em;\">Regards,<br />Royall</p>\r\n"
				+ "    <hr style=\"border:none;border-top:1px solid #eee\" />\r\n"
				+ "    <div style=\"float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300\">\r\n"
				+ "      <p>Royall</p>\r\n"
				+ "      <p>Puducherry,India</p>\r\n"
				+ "    </div>\r\n"
				+ "  </div>\r\n"
				+ "</div>";
	}
	
	
	public static String getConfirmOrderTemplate(OrderEntity order) {
		Address address=order.getShipTo();
		String shippingAddress=address.getAddressLine1()+(address.getAddressLine2()==null?"":" "+address.getAddressLine2())
				+" "+address.getCity()+" "+address.getState()+" "+address.getCountry()+" "+address.getPostalCode()+" "+address.getMobile();
	
		return "<html><head><link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.3.1/css/bootstrap.min.css\"><script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.3.1/js/bootstrap.min.js\"></script><style></style><style>\r\n"
				+ "    @import url('https://fonts.googleapis.com/css2?family=Montserrat&display=swap');\r\n"
				+ "    body {\r\n"
				+ "    background-color: #ffe8d2;\r\n"
				+ "    font-family: 'Montserrat', sans-serif\r\n"
				+ "    }\r\n"
				+ "    .card {\r\n"
				+ "    border: none\r\n"
				+ "    }\r\n"
				+ "    .logo {\r\n"
				+ "    background-color: #eeeeeea8\r\n"
				+ "    }\r\n"
				+ "    .totals tr td {\r\n"
				+ "    font-size: 13px\r\n"
				+ "    }\r\n"
				+ "    .footer {\r\n"
				+ "    background-color: #eeeeeea8\r\n"
				+ "    }\r\n"
				+ "    .footer span {\r\n"
				+ "    font-size: 12px\r\n"
				+ "    }\r\n"
				+ "    .product-qty span {\r\n"
				+ "    font-size: 12px;\r\n"
				+ "    color: #dedbdb\r\n"
				+ "    }\r\n"
				+ "</style>\r\n"
				+ "</head><body><div class=\"container mt-5 mb-5\">\r\n"
				+ "<div class=\"row d-flex justify-content-center\">\r\n"
				+ "<div class=\"col-md-8\">\r\n"
				+ "<div class=\"card\">\r\n"
				+ "<div class=\"text-left logo p-2 px-5\"> Royall </div>\r\n"
				+ "<div class=\"invoice p-5\">\r\n"
				+ "<h5>Your order Confirmed!</h5> <span class=\"font-weight-bold d-block mt-4\">Hello, "+order.getOrderedBy().getEmail()+"</span> <span>You order has been confirmed and will be shipped soon!</span>\r\n"
				+ "<div class=\"payment border-top mt-3 mb-3 border-bottom table-responsive\">\r\n"
				+ "<table class=\"table table-borderless\">\r\n"
				+ "<tbody>\r\n"
				+ "<tr>\r\n"
				+ "<td>\r\n"
				+ "<div class=\"py-2\"> <span class=\"d-block text-muted\">Order Date</span> <span>"+order.getDate()+"</span> </div>\r\n"
				+ "</td>\r\n"
				+ "<td>\r\n"
				+ "<div class=\"py-2\"> <span class=\"d-block text-muted\">Order No</span> <span>#"+order.getOrderId()+"</span> </div>\r\n"
				+ "</td>\r\n"
				+ "\r\n"
				+ "<td>\r\n"
				+ "<div class=\"py-2\"> <span class=\"d-block text-muted\">Shiping Address</span> <span>"+shippingAddress+"</span> </div>\r\n"
				+ "</td>\r\n"
				+ "</tr>\r\n"
				+ "</tbody>\r\n"
				+ "</table>\r\n"
				+ "</div>\r\n"
				+ "<div style=\"display:flex;justify-content:center;\"><a href=\"https://royall.in/my-orders\">View Order</a></div>\r\n"
				+ "<p class=\"font-weight-bold mb-0\">Thanks for shopping with us!</p> <span>Royall Team</span>\r\n"
				+ "</div>\r\n"
				+ "<div class=\"d-flex justify-content-between footer p-3\"> <span>Need Help? visit our <a href=\"https://royall.in/customer-care\"> help center</a></span></div>\r\n"
				+ "</div>\r\n"
				+ "</div>\r\n"
				+ "</div>\r\n"
				+ "</div> <script type=\"text/javascript\"></script></body></html>";
	}
	
	public static String getOrderNotifyTemplate(OrderEntity order) {
		Address address=order.getShipTo();
		String shippingAddress=address.getAddressLine1()+(address.getAddressLine2()==null?"":" "+address.getAddressLine2())
				+" "+address.getCity()+" "+address.getState()+" "+address.getCountry()+" "+address.getPostalCode()+" "+address.getMobile();
	
		return "<html><head><link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.3.1/css/bootstrap.min.css\"><script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\"></script><script src=\"https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.3.1/js/bootstrap.min.js\"></script><style></style><style>\r\n"
				+ "    @import url('https://fonts.googleapis.com/css2?family=Montserrat&display=swap');\r\n"
				+ "    body {\r\n"
				+ "    background-color: #ffe8d2;\r\n"
				+ "    font-family: 'Montserrat', sans-serif\r\n"
				+ "    }\r\n"
				+ "    .card {\r\n"
				+ "    border: none\r\n"
				+ "    }\r\n"
				+ "    .logo {\r\n"
				+ "    background-color: #eeeeeea8\r\n"
				+ "    }\r\n"
				+ "    .totals tr td {\r\n"
				+ "    font-size: 13px\r\n"
				+ "    }\r\n"
				+ "    .footer {\r\n"
				+ "    background-color: #eeeeeea8\r\n"
				+ "    }\r\n"
				+ "    .footer span {\r\n"
				+ "    font-size: 12px\r\n"
				+ "    }\r\n"
				+ "    .product-qty span {\r\n"
				+ "    font-size: 12px;\r\n"
				+ "    color: #dedbdb\r\n"
				+ "    }\r\n"
				+ "</style>\r\n"
				+ "</head><body><div class=\"container mt-5 mb-5\">\r\n"
				+ "<div class=\"row d-flex justify-content-center\">\r\n"
				+ "<div class=\"col-md-8\">\r\n"
				+ "<div class=\"card\">\r\n"
				+ "<div class=\"text-left logo p-2 px-5\"> Royall </div>\r\n"
				+ "<div class=\"invoice p-5\">\r\n"
				+ "<h5>This is the notification for new order!</h5> <span class=\"font-weight-bold d-block mt-4\">Hello,</span> <span>for more details view admin page!</span>\r\n"
				+ "<div class=\"payment border-top mt-3 mb-3 border-bottom table-responsive\">\r\n"
				+ "<table class=\"table table-borderless\">\r\n"
				+ "<tbody>\r\n"
				+ "<tr>\r\n"
				+ "<td>\r\n"
				+ "<div class=\"py-2\"> <span class=\"d-block text-muted\">Order Date</span> <span>"+order.getDate()+"</span> </div>\r\n"
				+ "</td>\r\n"
				+ "<td>\r\n"
				+ "<div class=\"py-2\"> <span class=\"d-block text-muted\">Order No</span> <span>#"+order.getOrderId()+"</span> </div>\r\n"
				+ "</td>\r\n"
				+ "\r\n"
				+ "<td>\r\n"
				+ "<div class=\"py-2\"> <span class=\"d-block text-muted\">Shiping Address</span> <span>"+shippingAddress+"</span> </div>\r\n"
				+ "</td>\r\n"
				+ "</tr>\r\n"
				+ "</tbody>\r\n"
				+ "</table>\r\n"
				+ "</div>\r\n"
				+ "<div style=\"display:flex;justify-content:center;\"><a href=\"https://royall.in/admin/hack/orders/"+order.getOrderId()+"\">View Order</a></div>\r\n"
				+ "<p class=\"font-weight-bold mb-0\">Please click view order to confirm it!</p> <span>Royall Team</span>\r\n"
				+ "</div>\r\n"
				+ "<div class=\"d-flex justify-content-between footer p-3\"> <span>Need Help? visit our <a href=\"https://royall.in/customer-care\"> help center</a></span></div>\r\n"
				+ "</div>\r\n"
				+ "</div>\r\n"
				+ "</div>\r\n"
				+ "</div> <script type=\"text/javascript\"></script></body></html>";
	}
	
	
}
