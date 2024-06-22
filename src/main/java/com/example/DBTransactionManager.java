package com.example;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DBTransactionManager {
        private Graph waitForGraph;
        private List<Data> dbData;
        private List<Transaction> dbTransactions;
        private String[] commands;
        private String[] suspended;
        private int commandCount;
        private int transactionCount;
        private int suspendedCount;
        boolean modified;

        public DBTransactionManager() {
            this.waitForGraph = new Graph();
            this.dbData = new ArrayList<>();
            this.dbTransactions = new ArrayList<>();
            this.commandCount = 0;
            this.transactionCount = 0;
        }
    
        // Agrega data a List<Data> dbData si no esta la lista
        public Data addData(String dataName) {
            if (dataName.isEmpty()) {
                return null;
            }
    
            for (Data data : dbData) {
                if (data.getName().equals(dataName)) {
                    return data;
                }
            }
    
            Data newData = new Data(dataName);
            dbData.add(newData);
            return newData;
        }
    
        // Agrega transaccion a List<Transaction> dbTransactions si no esta la lista
        public Transaction addTransaction(String transactionName) {
            for (Transaction transaction : dbTransactions) {
                if (transaction.getName().equals(transactionName)) {
                    return transaction;
                }
            }

            Transaction newTransaction = new Transaction(transactionName);
            newTransaction.setOrder(transactionCount++);
            dbTransactions.add(newTransaction);
            return newTransaction;
        }
    
        // Lee el archivo de instrucciones y ejecuta los comandos
        void getInstructions(String fileName) {
            File file = new File(fileName);
            Scanner scanner;
            try {
                scanner = new Scanner(file);
                commandCount = 0;
        
                while (scanner.hasNextLine()) {
                    scanner.nextLine();
                    commandCount++;
                }
        
        
                commands = new String[commandCount];
                suspended = new String[commandCount];
        
                scanner = new Scanner(file);
                int i = 0;
        
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    // System.out.println("line " + line);
                    commands[i] = line;
                    i++;
                }
                executeCommands(commands, commandCount);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + " " + e.getCause());
                e.printStackTrace();
                throw new RuntimeException("Error reading file");
            }
        }
    
        private void resetSuspendedCount() {
            suspendedCount = 0;
        }

        public void executeCommands(String[] commands, int commandCount) {
            resetSuspendedCount();

            for (String command : commands) {
                if (command == null || command.isEmpty()) {
                    continue;
                }
                
                // Esto se usa para saber si se ha modificado el grafo en cada loop
                modified = false;

                // Obtiene el codigo de estado de command (0, 1, 2)
                int response = parseInstruction(command);

                // Si se ha modificado el grafo, se reorganiza el array de comandos
                if (modified){
                    for (int i = 0; i < commandCount - 1; i++){
                            commands[i] = commands[i + 1];
                        }
                        commands[commandCount - 1] = command;
                } else {
                    // Si no se ha modificado el grafo, se imprime el mensaje correspondiente
                    if (response == 0){
                        System.out.println("Transaction " + command + " is finished");
                    }

                    if (response == 1){
                        System.out.println("Transaction " + command + " is suspended");
                        suspended[suspendedCount] = command;
                        suspendedCount++;
                    }

                    if (response == 2){
                        for (int i = 0; i < suspendedCount - 1; i++){
                            // System.out.println("Suspended number: " + suspendedCount);
                            // System.out.println("??? " + suspended[i]);
                        }
                        // En este caso (2), se ejecutan los comandos suspendidos
                        executeCommands(suspended, suspendedCount);
                    }
                }
            }
        }
    
        // Parsea la instruccion y ejecuta la instruccion correspondiente
        // La entrada tiene esta forma: read(T1, A3) o end(T2)
        // El formato es: <instruccion>(<transaction>, <data>)
        private int parseInstruction(String instruction) {
            int openBracketIdx = instruction.indexOf('(');
            int closeBracketIdx = instruction.indexOf(')');
            String instructionType = instruction.substring(0, openBracketIdx);
            String content = instruction.substring(openBracketIdx + 1, closeBracketIdx);

            // System.out.println("Instruction Type: " + instructionType + " Content: " + content + " Instruction: " + instruction);

            String data;
            String transaction;

            // Si la instruccion es end, no hay data porque es el final de la transaccion
            if (instructionType.equals("end")) {
                data = "";
                transaction = content;
            } else {
                // Si la instruccion no es end, se obtiene la data y la transaccion
                String[] dataTransaction = getDataTransaction(content);
                data = dataTransaction[0];
                transaction = dataTransaction[1];
            }

            // System.out.println("Data: " + data + " Transaction: " + transaction);

            Data findData = addData(data);
            Transaction findTransaction = addTransaction(transaction);

            // Ejecuta la instruccion con la transaccion y la data
            return execute(instructionType, findTransaction, findData);
        }

        private String[] getDataTransaction(String content) {
            String[] split = content.split(",");
            return split;
        }
    
        private int execute(String instructionType, Transaction findTransaction, Data findData) {

            // Para cada transaccion, se inserta un nodo en el grafo
            waitForGraph.insertNode(findTransaction);

            // El estado inicial es 0
            int executionStatus = 0;

            // Si la instruccion no es 'end'
            if (!instructionType.equals("end")){
                if (findData.isLocked() && findData.lockTransaction() != findTransaction.getName()){
                    executionStatus = 1;

                    // Se agrega una arista al grafo
                    Transaction waitFor = addTransaction(findData.lockTransaction());
                    waitForGraph.insertEdge(waitFor, findTransaction, findData);
                } else {
                    executionStatus = 0;
                    // Si la instruccion es 'read', se bloquea la data
                    findData.lockData(findTransaction.getName());
                }
            } else {
                    // Si la instruccion es 'end' se borra el nodo
                waitForGraph.deleteNode(findTransaction);
                executionStatus = 2;
            }

            if (executionStatus != 0){
                Transaction toDelete = waitForGraph.findMostRecentTransaction();

                if (toDelete != null){
                    waitForGraph.deleteNode(toDelete);
                    modified = true;
                }
            }
            return executionStatus;
        }
    }
