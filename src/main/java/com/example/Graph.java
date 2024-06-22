package com.example;

public class Graph {
    private EdgeStack edgeStack;
    private Vertex root;
    private int nodeCount;
    private int edgeCount;
    private Transaction mostRecent;
    private int transactionCount = 0;

    public Graph() {
        this.root = null;
        this.mostRecent = null;
        this.nodeCount = 0;
        this.edgeCount = 0;
    }

    // Agrega nodo por cada transaccion
    // Si no hay nodos, crea el nodo raiz
    // Si ya existe el nodo, no lo agrega
    // Si no existe, agrega el nodo
    public void insertNode(Transaction t) {
        boolean exists = false;
        if (root == null) {
            t.setOrder(transactionCount);
            transactionCount++;
            root = new Vertex(t);
        } else {
            Vertex temp = root;
            while (temp.getNext() != null && !exists) {
                if (temp.getValue().equals(t)) exists = true;
                temp = temp.getNext();
            }
            if (!exists) {
                t.setOrder(transactionCount);
                transactionCount++;
                temp.setNext(new Vertex(t));
                nodeCount++;
            }
        }
    }

    // Agrega arista entre dos transacciones
    // Si no existen las transacciones, no agrega la arista
    // Si existen las transacciones, agrega la arista
    // Si ya existe la arista, no la agrega
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

    // Elimina nodo de la lista usando transaccion
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


    // Obtiene la transaccion mas reciente que forma un ciclo
    public Transaction findMostRecentTransaction() {
        edgeStack = new EdgeStack(edgeCount);
        clearStatus();

        boolean cycleExists = cycleDFS(root);
        if (cycleExists) {
            return mostRecent;
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


    // Usa DFS (Depth First Search) para encontrar ciclos
    // Si encuentra un ciclo, devuelve true
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
                    if (mostRecent == null){
                        mostRecent = t.getAdjacentNode().getValue();
                    }
                    if (mostRecent.getOrder() < t.getAdjacentNode().getValue().getOrder()) {
                        mostRecent = t.getAdjacentNode().getValue();
                    }
                }
                return true;
            }
        }
        return false;
    }

    // Todos los nodos a status 0
    private void clearStatus() {
        Vertex temp = root;
        while (temp != null) {
            temp.setStatus(0);
            temp = temp.getNext();
        }
    }
}