package ECommerce.Model;

import java.util.Objects;

public class CartItem
{
     int productId;
     String productName;
     String sellerName;
     double mrp;
     int quantity;

    public CartItem(int productId, String productName, String sellerName, double mrp, int quantity)
    {
        this.productId = productId;
        this.productName = productName;
        this.sellerName = sellerName;
        this.mrp = mrp;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CartItem cartItem = (CartItem) obj;
        return productId == cartItem.productId &&
                Objects.equals(productName, cartItem.productName);
    }

    // Getters remain the same
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getSellerName() { return sellerName; }
    public double getMrp() { return mrp; }
    public int getQuantity() { return quantity; }

    public String toString()
    {
        return "Product ID: " + productId + ", Name: " + productName + ", MRP: ₹" + mrp +
                ", Quantity: " + quantity + ", Seller: " + sellerName;
    }
}