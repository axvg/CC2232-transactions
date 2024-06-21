package com.example;

public class Vertex {
    private Transaction value;
    private int status;
    private Edge startEdge;
    private Vertex next;

    public Vertex(Transaction value) {
        this.status = 0;
        this.value = value;
        this.startEdge = null; // 0 ?
        this.next = null; // 0 ?
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setValue(Transaction value) {
        this.value = value;
    }

    public Transaction getValue() {
        return this.value;
    }

    public void setNext(Vertex next) {
        this.next = next;
    }

    public Vertex getNext() {
        return this.next;
    }

    public void setStartEdge(Edge startEdge) {
        this.startEdge = startEdge;
    }

    public Edge getStartEdge() {
        return this.startEdge;
    }
}