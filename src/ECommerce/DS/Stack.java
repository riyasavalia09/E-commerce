//
package ECommerce.DS;

import java.util.EmptyStackException;

public class Stack
{
       static class Node
    {
        Object data;
        Node next;

        public Node(Object data)
        {
            this.data = data;
        }
    }

       Node top;
       int size;

    public Stack()
    {
        top = null;
        size = 0;
    }

    public boolean push(Object item)
    {
        Node newNode = new Node(item);
        newNode.next = top;
        top = newNode;
        size++;
        return false;
    }

    public Object pop()
    {
        if (isEmpty())
        {
            throw new EmptyStackException();
        }
        Object data = top.data;
        top = top.next;
        size--;
        return data;
    }

    public boolean isEmpty()
    {
        return top == null;
    }

    public int size()
    {
        return size;
    }
}
