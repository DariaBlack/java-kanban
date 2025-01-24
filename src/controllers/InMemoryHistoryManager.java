package controllers;

import controllers.interfaces.HistoryManager;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.containsKey(task.getId())) {
                remove(task.getId());
            }
            linkLast(task);
        }
    }

    private void linkLast(Task task) {
        Node node = new Node(task, null, tail);

        if (tail == null) {
            head = node;
        } else {
            tail.setNext(node);
            node.setPrev(tail);
        }

        tail = node;
        history.put(task.getId(), node);
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node currentNode = head;

        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node != null) {
            Node nextNode = node.getNext();
            Node prevNode = node.getPrev();

            if (node == head) {
                head = nextNode;
            }
            if (node == tail) {
                tail = prevNode;
            }
            if (prevNode != null) {
                prevNode.setNext(nextNode);
            }
            if (nextNode != null) {
                nextNode.setPrev(prevNode);
            }

            node.setNext(null);
            node.setPrev(null);
        }
    }

    @Override
    public void remove(int id) {
        if (history.isEmpty()) {
            return;
        }

        Node nodeToRemove = history.get(id);
        if (nodeToRemove == null) {
            return;
        }

        removeNode(nodeToRemove);
        history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node currentNode = head;

        while (currentNode != null) {
            tasks.add(currentNode.getData());
            currentNode = currentNode.getNext();
        }
        return tasks;
    }

    @Override
    public String toString() {
        return history.toString();
    }

    public class Node {
        private Task data;
        private Node next;
        private Node prev;

        public Node(Task data, Node next, Node last) {
            this.data = data;
            this.next = next;
            this.prev = last;
        }

        public Task getData() {
            return data;
        }

        public void setData(Task data) {
            this.data = data;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }
    }
}