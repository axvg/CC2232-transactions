package com.example;
import java.util.ArrayList;
import java.util.List;

public class DBTransactionManager {
        private Graph waitForGraph;
        private List<Data> dbData; // use generics DS?
        private List<Transaction> dbTransactions;
        private String[] commands;
        private int commandCount;
        private int transactionCount;

        public DBTransactionManager() {
            this.waitForGraph = new Graph();
            this.dbData = new ArrayList<>();
            this.dbTransactions = new ArrayList<>();
            this.commandCount = 0;
            this.transactionCount = 0;
        }
    
        public Data addData(String add) {
            if (add == ""){
                return null;
            }
            Data data = null;
            boolean exists = false;
            for (Data d : dbData) {
                if (d.getName().equals(add)) {
                    exists = true;
                    data = d;
                }
            }

            if (!exists) {
                data = new Data(add);
                dbData.add(data);
            }
            return data;
        }
    
        public Transaction addTransaction(String transaction) {
            Transaction transaction2 = null;
            boolean exits = false;

            for (Transaction t : dbTransactions) {
                if (t.getName().equals(transaction)) {
                    exits = true;
                    transaction2 = t;
                }
            }

            if (!exits) {
                transaction2 = new Transaction(transaction);
                transaction2.setOrder(transactionCount);
                transactionCount++;
                dbTransactions.add(transaction2);
            }
            return transaction2;
        }
    
        void getInstructions() {
            // TODO: get input for txt file, not console
        }
    
        public void executeCommands(String[] commands, int commandCount) {
            // TODO:  run 
        }
    
        private int parseInstruction(String instruction) {
            // TODO:  this handles (, ), , and command type?
            // TODO:  this should return status of transaction
            return 0;
        }
    
        private int execute(String instructionType, Transaction findTrans, Data findData) {
            // TODO:  this should return status of transaction
            return 0;
        }
    }
