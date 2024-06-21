package com.example;

public class EdgeStack {
    private int stackPointer;
    private int size;
    private Edge[] stack;

    public EdgeStack(int size) {
        this.stackPointer = -1;
        this.size = size;
        this.stack = new Edge[size];
    }

    public boolean isEmpty() {
        return stackPointer == -1;
    }

    public int numberOfElements() {
        return stackPointer + 1;
    }

    public boolean isFull() {
        return stackPointer + 1 == size;
    }

    public Edge pop() {
        if (!isEmpty()) {
            Edge temp = stack[stackPointer];
            stack[stackPointer] = null;
            stackPointer--;
            return temp;
        }
        return null;
    }

    public void push(Edge value) {
        if (!isFull()) {
            stack[++stackPointer] = value;
        }
    }

    public boolean contains(Edge element) {
        for (int i = 0; i <= stackPointer; i++) {
            if (stack[i].isEqual(element)) {
                return true;
            }
        }
        return false;
    }

    public Edge at(int index) {
        if (index < 0 || index > stackPointer) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + (stackPointer + 1));
        }
        return stack[index];
    }

    public int stackSize() {
        return size;
    }

    public void reverseStack() {
        for (int i = 0; i <= stackPointer / 2; i++) {
            Edge temp = stack[i];
            stack[i] = stack[stackPointer - i];
            stack[stackPointer - i] = temp;
        }
    }
}