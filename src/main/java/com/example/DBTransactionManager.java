package com.example;
import java.io.File;
import java.io.FileNotFoundException;
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
        public void getInstructions(String filePath) {
            try {
                File file = new File(filePath);
                Scanner scanner = new Scanner(file);
                List<String> instructionsList = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String instruction = scanner.nextLine();
                    instructionsList.add(instruction);
                }
                scanner.close();
                commands = new String[instructionsList.size()];
                suspended = new String[instructionsList.size()];
                commandCount = instructionsList.size();
                instructionsList.toArray(commands);
                executeCommands(commands, commandCount);
            } catch (FileNotFoundException e) {
                System.err.println("Error al leer archivo: " + e.getMessage());
            }
        }

        private void resetSuspendedCount() {
            suspendedCount = 0;
        }

        public void executeCommands(String[] commands, int commandCount) {
            resetSuspendedCount();
            for (int i = 0; i < commandCount; i++) {
                String instruction = commands[i];

                // Para saber si el grafo ha sido modificado
                modified = false;
                
                // Obtiene el codigo de estado de command (0, 1, 2)
                int response = parseInstruction(instruction);
                if (modified) {
                    // Si el grafo se ha modificado
                    // Se reorganiza el array de comandos
                    // Se elimina el comando actual y se agrega al final
                    for (int j = i; j < commandCount - 1; j++) {
                        commands[j] = commands[j + 1];
                    }
                    commands[commandCount - 1] = instruction;
                } else {
                    // Si no ha sido modificado
                    // Se imprime el mensaje correspondiente
                    if (response == 0) {
                        System.out.println(instruction + " is executing");
                    } else if (response == 1) {
                        System.out.println(instruction + " is suspended");
                        suspended[suspendedCount++] = instruction;
                    } else {
                        System.out.println("Executing suspended commands...");
                        executeCommands(suspended, suspendedCount);
                    }
                }
            }
        }

    
        // Parsea la instruccion y ejecuta la instruccion correspondiente
        // La entrada tiene esta forma: read(T1, A3) o end(T2)
        // El formato es: <instruccion>(<transaction>, <data>)
        private int parseInstruction(String instruction) {
            int openBracketIndex = instruction.indexOf('(');
            String instructionType = instruction.substring(0, openBracketIndex);
            instruction = instruction.substring(openBracketIndex + 1);
    
            String data;
            String transaction;

            // Si la instruccion es end, no hay data porque es el final de la transaccion
            if (instructionType.equals("end")) {
                transaction = instruction.substring(0, instruction.indexOf(')'));
                data = "";
            } else {
                // Si la instruccion no es end, se obtiene la data y la transaccion
                int commaIndex = instruction.indexOf(',');
                transaction = instruction.substring(0, commaIndex);
                instruction = instruction.substring(commaIndex + 1);
                int closedBracketIndex = instruction.indexOf(')');
                data = instruction.substring(0, closedBracketIndex);
            }
    
            // System.out.println("Data: " + data + " Transaction: " + transaction);

            Data dataAdded = addData(data);
            Transaction addedTransaction = addTransaction(transaction);
            
            // Ejecuta la instruccion con la transaccion y la data
            return execute(instructionType, addedTransaction, dataAdded);
        }

    
        private int execute(String instructionType, Transaction targetTransaction, Data findData) {
            waitForGraph.insertNode(targetTransaction);
            int executionStatus = 0;
            if (!instructionType.equals("end")) {
                if (findData.isLocked() && !findData.lockTransaction().equals(targetTransaction.getName())) {
                    executionStatus = 1;
                    targetTransaction.suspend();
                    Transaction waitFor = addTransaction(findData.lockTransaction());
                    waitForGraph.insertEdge(waitFor, targetTransaction, findData);
                } else {
                    executionStatus = 0;
                    findData.lockData(targetTransaction.getName());
                }
            } else {
                System.out.println("End transaction " + targetTransaction.getName());
                waitForGraph.deleteNode(targetTransaction);
                executionStatus = 2;
            }
            if (executionStatus != 0) {
                Transaction toDelete = waitForGraph.findMostRecentTransaction();
                if (toDelete != null) {
                    waitForGraph.deleteNode(toDelete);
                    modified = true;
                }
            }
            return executionStatus;
        }
    }
