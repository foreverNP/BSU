package com;

import java.util.concurrent.locks.ReentrantLock;

public class CoarseList<T> implements Set<T> {
    private static class Node<T> {
        final T item;
        final int key;
        Node<T> next;

        Node(int key) {
            this.item = null;
            this.key = key;
        }

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }
    }

    private final Node<T> head;
    private final ReentrantLock lock = new ReentrantLock();

    public CoarseList() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();
        lock.lock();
        try {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if (curr.key == key) {
                return false;
            } else {
                Node<T> node = new Node<>(item);
                node.next = curr;
                pred.next = node;
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        lock.lock();
        try {
            Node<T> pred = head;
            Node<T> curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            if (curr.key == key) {
                pred.next = curr.next;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean contains(T item) {
        int key = item.hashCode();
        lock.lock();
        try {
            Node<T> curr = head.next;
            while (curr.key < key) {
                curr = curr.next;
            }
            return curr.key == key;
        } finally {
            lock.unlock();
        }
    }
}
