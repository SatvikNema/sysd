//package com.satvik.dsa;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Random;
//
//public class PriorityQueue {
//    public static void main(String[] args) {
//
//
//        List<Integer> arr = Arrays.asList(7, 8, 3, 2, 6, 10, 4, 5, 1, 9);
//
//        Random random = new Random();
//        arr = new ArrayList<>(random.ints().limit(1000).boxed().toList());
//        Heap heap = new Heap(10);
//        for(int e:arr) {
//            heap.add(e);
//            boolean isValid = heap.isValidHeap(0);
//            if(!isValid){
//                System.out.println("current heap is NOT valid. breaking...");
//                break;
//            }
//        }
//        heap.print();
//
//        System.out.println("=================================");
//
//        List<Integer> ans = new ArrayList<>();
//        while (!heap.isEmpty()) {
//            int topElement = heap.remove();
////            System.out.print(topElement+", ");
//            ans.add(topElement);
//            boolean isValid = heap.isValidHeap(0);
//            if(!isValid){
//                System.out.println("current heap is NOT valid. breaking...");
//                break;
//            }
//        }
//        System.out.println();
//        arr.sort(Collections.reverseOrder());
//        System.out.println(arr.equals(ans));
//    }
//
//    static class Heap {
//        private int[] arr;
//        private int size;
//        private int capacity;
//
//        public Heap(int size) {
//            this.size = size;
//            arr = new int[size];
//            capacity = 0;
//        }
//
//        public void add(int element){
//            if(capacity == size){
//                int newSize = size * 2;
//                arr = Arrays.copyOf(arr, newSize);
//                this.size = newSize;
//            }
//            arr[capacity++] = element;
//            heapifyUp(capacity-1);
//        }
//
//        public int remove(){
//            if(capacity == 0){
//                throw new IllegalStateException("heap is empty");
//            }
//            int element = arr[0];
//            arr[0] = arr[capacity-1];
//            heapifyDown(0);
//            capacity--;
//            return element;
//        }
//
//        public void heapifyDown(int index){
//            if(isLeaf(index)){
//                return;
//            }
//            int leftChild = getLeftChildIndex(index);
//            int rightChild = getRightChildIndex(index);
//
//            if(arr[leftChild] > arr[rightChild] && arr[leftChild] > arr[index]){
//                swap(index, leftChild);
//                heapifyDown(leftChild);
//            } else if(arr[rightChild] > arr[leftChild] && arr[rightChild] > arr[index]){
//                swap(index, rightChild);
//                heapifyDown(rightChild);
//            }
//        }
//
//        public void heapifyUp(int index){
//            if(index <= 0){
//                return;
//            }
//            int parentIndex = getParentIndex(index);
//            if(arr[index] > arr[parentIndex]){
//                swap(index, parentIndex);
//                heapifyUp(parentIndex);
//            }
//        }
//
//        public void swap(int i, int j){
//            int temp = arr[i];
//            arr[i] = arr[j];
//            arr[j] = temp;
//        }
//
//        public int getLeftChildIndex(int index){
//            return 2 * index + 1;
//        }
//
//        public int getRightChildIndex(int index){
//            return 2 * index + 2;
//        }
//
//        public int getParentIndex(int index){
//            return (index - 1) / 2;
//        }
//
//        public boolean isEmpty(){
//            return capacity == 0;
//        }
//
//        public boolean isLeaf(int index){
//            return getLeftChildIndex(index) >= capacity || getRightChildIndex(index) >= capacity;
//        }
//
//        public void print(){
//            int loop;
//            int index = 0;
//            int i1=0;
//            while(index < capacity){
//                loop = 1<<i1;
//                for(int i = 0; i < loop && index < capacity; i++){
//                    System.out.print(arr[index++]+", ");
//                }
//                i1++;
//                System.out.println();
//            }
//
//        }
//
//        public boolean isValidHeap(int index){
//            int leftChild = getLeftChildIndex(index);
//            int rightChild = getRightChildIndex(index);
//
//            boolean leftValid = true, rightValid = true;
//            if(leftChild < capacity){
//                leftValid = arr[leftChild] <=  arr[index] && isValidHeap(leftChild);
//            }
//
//            if(rightChild < capacity){
//                rightValid = arr[rightChild] <=  arr[index] && isValidHeap(rightChild);
//            }
//            return leftValid && rightValid;
//        }
//    }
//}
//
//
