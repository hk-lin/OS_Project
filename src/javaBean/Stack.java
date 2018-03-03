package javaBean;

public class Stack {
	private int top = -1;  
    public int[] objs;  
      
    public Stack(int capacity) throws Exception{  
        if(capacity < 0)  
            throw new Exception("Illegal capacity:"+capacity);  
        objs = new int[capacity];  
    }  
      
    public void push(int obj) throws Exception{  
        if(top == objs.length - 1)  
            throw new Exception("Stack is full!");  
        objs[++top] = obj;  
    }  
      
    public int pop() throws Exception{  
        if(top == -1)  
            throw new Exception("Stack is empty!");  
        return objs[top--];  
    }  
}
