package com;

import java.util.concurrent.locks.ReentrantLock;

public class FineList<T> implements Set<T> {
    private static class Node<T> {
        final T item;
        final int key;
        Node<T> next;
        final ReentrantLock lock = new ReentrantLock();

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

    public FineList() {
        head = new Node<>(Integer.MIN_VALUE);
        head.next = new Node<>(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();
        head.lock.lock();
        Node<T> pred = head;
        try {
            Node<T> curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if (curr.key == key) {
                    return false;
                }
                Node<T> node = new Node<>(item);
                node.next = curr;
                pred.next = node;
                return true;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        head.lock.lock();
        Node<T> pred = head;
        try {
            Node<T> curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if (curr.key == key) {
                    pred.next = curr.next;
                    return true;
                }
                return false;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    @Override
    public boolean contains(T item) {
        int key = item.hashCode();
        head.lock.lock();
        Node<T> pred = head;
        try {
            Node<T> curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                return curr.key == key;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }
}
