package com.example;

public class Edge {
    private Data waitingForData;
    private Edge nextEdge;
    private Vertex startNode;
    private Vertex adjacentNode;

    public Edge(Data waitingForData) {
        this.waitingForData = waitingForData;
        this.nextEdge = null;
        this.adjacentNode = null;
    }

    public boolean isEqual(Edge otherEdge) {
        return this.startNode == otherEdge.startNode &&
               this.adjacentNode == otherEdge.adjacentNode &&
               this.waitingForData == otherEdge.waitingForData;
    }

    public void unlockData() {
        waitingForData.unlockData();
    }

    public void lockData(Transaction transaction) {
        waitingForData.lockData(transaction.getName());
    }

public Data getWaitingForData() {
    return waitingForData;
}

public Edge getNextEdge() {
    return nextEdge;
}

public Vertex getStartNode() {
    return startNode;
}

public Vertex getAdjacentNode() {
    return adjacentNode;
}

public void setWaitingForData(Data waitingForData) {
    this.waitingForData = waitingForData;
}

public void setNextEdge(Edge nextEdge) {
    this.nextEdge = nextEdge;
}

public void setStartNode(Vertex startNode) {
    this.startNode = startNode;
}

public void setAdjacentNode(Vertex adjacentNode) {
    this.adjacentNode = adjacentNode;
}
}