package com.example;

public class Graph {
    private EdgeStack edgeStack;
    private Vertex root;
    private int nodeCount;
    private int edgeCount;
    private Transaction youngest;
    private int transCount = 0;

    public Graph() {
        this.root = null;
        this.youngest = null;
        this.nodeCount = 0;
        this.edgeCount = 0;
    }

    public void insertNode(Transaction t) {
        boolean exists = false;
        if (root == null) {
            t.setOrder(transCount);
            transCount++;
            root = new Vertex(t);
        } else {
            Vertex temp = root;
            while (temp.getNext() != null && !exists) {
                if (temp.getValue().equals(t)) exists = true;
                temp = temp.getNext();
            }
            if (!exists) {
                t.setOrder(transCount);
                transCount++;
                temp.setNext(new Vertex(t));
                nodeCount++;
            }
        }
    }

    public void insertEdge(Transaction t1, Transaction t2, Data waitForData) {
        Vertex first = root;
        Vertex second = root;
        boolean firstFound = false;
        boolean secondFound = false;

        while (first != null && second != null && !(firstFound && secondFound)){
            if (first.getValue() == t1){
                firstFound = true;
            } else {
                first = first.getNext();
            }
            if (second.getValue() == t2){
                secondFound = true;
            } else {
                second = second.getNext();
            }
        }

        if (firstFound && secondFound){
            insertEdge(first, second, waitForData);
        }
    }

    public void deleteNode(Transaction t) {
        boolean exists = false;

        if (root != null){
            Vertex temp = root;
            Vertex prev = null;

            while (temp .getNext() != null && !exists){
                if (temp.getValue().getName().equals(t.getName())){
                    exists = true;
                } else {
                    prev = temp;
                    temp = temp.getNext();
                }
            }

            if (exists){
                deleteEdges(temp);
                deleteNode(temp);

                if (prev == null){
                    root = root.getNext();
                } else {
                    prev.setNext(temp.getNext());
                }
            }
        }
    }

    public Transaction youngestFormsLoop() {
        edgeStack = new EdgeStack(edgeCount);
        clearStatus();

        boolean cycleExists = cycleDFS(root);
        if (cycleExists) {
            return youngest;
        }
        return null;
    }

    void insertEdge(Vertex source, Vertex destination, Data waitForData) {
        Edge start = source.getStartEdge();
        if (start == null) {
            edgeCount++;
            source.setStartEdge(new Edge(waitForData));
            source.getStartEdge().setStartNode(source);
            source.getStartEdge().setAdjacentNode(destination);
        } else {
            boolean exists = false;
            while (start.getNextEdge() != null && !exists) {
                if (start.getStartNode() == source && start.getAdjacentNode() == destination) {
                    if (start.getWaitingForData() == waitForData){
                        exists = true;
                    }
                }
                start = start.getNextEdge();
            }
            if (!exists) {
                edgeCount++;
                start.setNextEdge(new Edge(waitForData));
                start.getNextEdge().setAdjacentNode(destination);
                start.getNextEdge().setStartNode(source);
            }
        }
    }

    private void deleteEdges(Vertex temp) {
        temp.getValue().suspend();
        Edge start = temp.getStartEdge();
        Edge t = start;
        while (start != null) {
            start.unlockData();
            start = start.getNextEdge();
            // deleteEdge(t);
            // t = null;
            t = start;
        }
    }

    private void deleteNode(Vertex node){
        Vertex t = root;
        while (t != null){
            if (t != node){
                Edge e = t.getStartEdge();
                while (e != null){
                    if (e.getAdjacentNode() == node){
                        e.setAdjacentNode(null);
                    }
                    e = e.getNextEdge();
                }
            }
            t = t.getNext();
        }
    }

    private boolean cycleDFS(Vertex current) {
        if (current != null) {
            if (current.getStatus() != 1) {
                current.setStatus(1);
                Edge startEdge = current.getStartEdge();

                if (startEdge == null) {
                    return false;
                }

                while (startEdge != null) {
                    edgeStack.push(startEdge);
                    if (cycleDFS(startEdge.getAdjacentNode())) return true;
                    edgeStack.pop();
                    if (startEdge.getAdjacentNode() != null) startEdge.getAdjacentNode().setStatus(0);
                    startEdge = startEdge.getNextEdge();
                }
            } else {
                // Loop found
                int limit = edgeStack.stackSize();
                for (int i = 0; i < limit; i++) {
                    Edge t = edgeStack.at(i);
                    if (youngest == null){
                        youngest = t.getAdjacentNode().getValue();
                    }
                    if (youngest.getOrder() < t.getAdjacentNode().getValue().getOrder()) {
                        youngest = t.getAdjacentNode().getValue();
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void clearStatus() {
        Vertex temp = root;
        while (temp != null) {
            temp.setStatus(0);
            temp = temp.getNext();
        }
    }
}