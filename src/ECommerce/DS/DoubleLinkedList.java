package ECommerce.DS;

import ECommerce.Model.CartItem;

public class DoubleLinkedList {
    class Node {
        CartItem data;
        Node prev, next;

        public Node(CartItem data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    public Node head;
    public Node tail;
    public int size;

    // Add last
    public void addToCart(CartItem item) {
        Node newNode = new Node(item);

        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
        System.out.println("Product added to cart: " + item.getProductName());
    }

    // Remove item - Fixed implementation
    public boolean remove(CartItem item) {
        if (head == null) return false;

        Node current = head;
        while (current != null) {
            if (current.data.getProductId() == item.getProductId() &&
                    current.data.getProductName().equals(item.getProductName())) {

                if (current.prev != null) {
                    current.prev.next = current.next;
                } else {
                    head = current.next;
                }

                if (current.next != null) {
                    current.next.prev = current.prev;
                } else {
                    tail = current.prev;
                }

                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Get size
    public int size() {
        return size;
    }

    // Check if empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Get element at index
    public CartItem get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);

        Node temp = head;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp.data;
    }

    // Clear list
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}