package org.example.samples;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

/*
 * // TODO:
 * Work with in-built DataStructure classes, from all java source libs
 */

/* // TODO: To-Use

Keywords - synchronized, record, 
Generics
Vavr, FunctionalJava
Functional Interfaces (all in-built & custom)
eg. Function (apply), Consumer (accept), Predicate, BiFunction, BiConsumer, BiPredicate;
Supplier (get), Callable (cal), UnaryOperator, BinaryOperator,  ... 
Lombok, 
..

*/

public class DataAlgoJava {

    public static void main(String[] args) {
        Main.main(args);
    }

    public void tests() {

        /*
        * Cannot infer type: lambda expressions require an explicit target type
        var f = (String x) -> { // Best to use Functional Interfaces
            System.out.println(x);
        }; f();
        */

        Consumer<String> f1 = (String x) -> { // Consumer FunctionalInterface - 'accepts' 1 arg, returns void
            System.out.println(x);
        }; f1.accept("123");

        Function<String, String> f2 = (String x) -> { // Function FunctionalInterface - 'applies' 1 arg, returns 1 type
            System.out.println(x);
            return x;
        }; String r = f2.apply("123");

        // * BiFunction, BiConsumer,  ...

    }

    // Static declarations in inner classes are not supported at language level '11'
    // unless inner classes are also declared static; or upgrade JDK to 16+

    public static class Main {

        ////////////////////////////////////////
        // SORTING ALGO'S
        ////////////////////////////////////////

        // todo: error if main() is inside a static Main class
        public static class Sorting {

            public Sorting() {
            }

            public int[] quickSort(int[] arr) {
                // TODO
                return arr;
            }

        }

        ////////////////////////////////////////
        // SEARCHING ALGO'S
        ////////////////////////////////////////

        public static class Searching {

            private int i;

            public Searching() {
                this.i = -1;
            }

            public int linearSearch(int[] a, int x) {
                for (int i = 0; i < a.length; i++)
                    if (x == a[i])
                        return i; // index
                return -1;
            }

            // No in-built functional interface for more than 2 args (create custom
            // interface)
            @FunctionalInterface
            interface RBinarySearch {
                int rBinarySearch2p(int[] a, int x, int f, int l);
            }

            public int binarySearch(int[] arr, int num) {
                // todo: NB: Base-cases should always come 1st (before any functional interface
                // or lambda assignments)
                // only regular inner functions (when they can be defined, in java) should be
                // defined before all logic

                if (arr.length == 0)
                    return -1;

                Arrays.sort(arr); // O(n log n) t

                // using FunctionalInterfaces with lambda's as values
                BiFunction<int[], Integer, Integer>[] rBinarySearch = new BiFunction[1];
                // must be an array (of length 1), if function-item is required for recursive-calls
                // so function-item can be retrieved by 'accessing' it by index [0] 1st, before recursive-calling
                // if not, java wouldn't have noted FunctionalInterface var's existence yet
                rBinarySearch[0] = (a, x) -> {
                    if (a.length == 0)
                        return -1;
                    int m = (int) Math.floor((double) (a.length / 2));
                    if (x == a[m])
                        return a[m];
                    else if (x < a[m])
                        return rBinarySearch[0].apply(Arrays.copyOfRange(a, 0, m), x);
                    else
                        return rBinarySearch[0].apply(Arrays.copyOfRange(a, m + 1, a.length), x);
                };

                RBinarySearch[] rbSearch = new RBinarySearch[1];
                rbSearch[0] = (a, x, f, l) -> {
                    if (a.length == 0)
                        return -1;
                    int m = (int) Math.floor((double) (f + (l - f) / 2));
                    if (x == a[m])
                        return m;
                    else if (x < a[m])
                        return rbSearch[0].rBinarySearch2p(a, x, f, m - 1);
                    else
                        return rbSearch[0].rBinarySearch2p(a, x, m + 1, l);
                };

                int f = 0, l = arr.length - 1;

                rBinarySearch[0].apply(arr, 7); // todo: int[] to Integer[] - Arrays.from(arr)
                rbSearch[0].rBinarySearch2p(arr, 7, f, l);

                int m;

                while (f < l) {
                    m = (int) Math.floor((double) (f + (l - f) / 2));
                    if (num == arr[m])
                        return m;
                    else if (num < arr[m])
                        l = m - 1;
                    else
                        f = m + 1;
                }

                return -1;
            }

            // More O(log n) t (binary) problems

            public int firstPositionOfItem(int[] nums, int num) {
                if (nums == null || nums.length == 0)
                    return -1;
                int left = 0, right = nums.length - 1, mid = 0;
                while (left + 1 < right) {
                    mid = left + (right - left) / 2;
                    // major difference between first & last position search
                    if (nums[mid] < num)
                        left = mid; // nums[mid] < num
                    else
                        right = mid;
                }
                if (nums[left] == num)
                    return left;
                if (nums[right] == num)
                    return right;
                return -1;
            }

            public int lastPositionOfItem(int[] nums, int num) {
                if (nums == null || nums.length == 0)
                    return -1;
                int left = 0, right = nums.length - 1, mid = 0;
                while (left + 1 < right) {
                    mid = left + (right - left) / 2;
                    // major difference between last & first position search
                    if (nums[mid] <= num)
                        left = mid; // nums[mid] <= num
                    else
                        right = mid;
                }
                if (nums[right] == num)
                    return right;
                if (nums[left] == num)
                    return left;
                return -1;
            }

            public int firstOrLastItem(int num) {

                Function<Integer, Boolean> isVersion = k -> {
                    return true; // TODO:
                };

                int start = 1, end = num, mid;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (isVersion.apply(mid))
                        end = mid;
                    else
                        start = mid;
                }
                if (isVersion.apply(start))
                    return start;
                return end;
            }

            @FunctionalInterface
            interface IsLeftCloser {
                boolean check(int[] a, int l, int r, int x);
            }

            public int[] kClosestNumbers(int[] arr, int num, int k) {

                if (arr == null || arr.length == 0)
                    return new int[0];

                BiFunction<int[], Integer, Integer> findLowerClosest = (a, x) -> {
                    int start = 0, end = a.length - 1, mid = -1;
                    while (start + 1 < end) {
                        mid = start + (end - start) / 2;
                        if (a[mid] < x)
                            start = mid;
                        else if (a[mid] >= x)
                            end = mid;
                    }
                    if (a[end] < x)
                        return mid;
                    if (a[start] < x)
                        return start;
                    return -1;
                };

                IsLeftCloser isLeftCloser = (a, l, r, x) -> {
                    if (l < 0)
                        return false;
                    if (r > a.length - 1)
                        return true;
                    if (x - a[l] <= a[r] - x)
                        return true;
                    return false;
                };

                int lowerClosestIndex = findLowerClosest.apply(arr, num);
                int[] res = new int[k];
                int left = lowerClosestIndex, right = lowerClosestIndex + 1, index = 0;
                for (int i = 0; i < k; i++) {
                    if (isLeftCloser.check(arr, left, right, num)) {
                        res[index] = arr[left];
                        index++;
                        left--;
                    } else {
                        res[index] = arr[right];
                        index++;
                        right++;
                    }
                }
                return res;
            }

            public int searchBigSortedArray(int[] arr, int num) {
                // Using Exponential Backoff method

                int k = 1;
                while (arr[k - 1] < num)
                    k = k + 2; // increase k exponentially

                int start = 0, end = k - 1, mid;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (arr[mid] < num)
                        start = mid;
                    else
                        end = mid;
                }
                if (arr[start] == num)
                    return start;
                if (arr[end] == num)
                    return end;
                return -1;
            }

            public int findMinimumRotatedSortedArray(int[] arr) {
                if (arr == null || arr.length == 0)
                    return -1;
                // find 1st position < last
                int num = arr[arr.length - 1];
                int start = 0, end = arr.length - 1, mid;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (num < arr[mid])
                        start = mid;
                    else
                        end = mid;
                }
                if (arr[start] < num)
                    return arr[start];
                else
                    return arr[end];
            }

            public int findMaximumNumberMountainSequence(int[] arr) {
                // find the 1st item > left && > right
                if (arr == null || arr.length == 0)
                    return Integer.MAX_VALUE;
                int start = 0, end = arr.length - 1, mid;
                // while arr has at least 3 items
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (arr[mid] > arr[mid + 1])
                        end = mid;
                    else
                        start = mid;
                }
                return Math.max(arr[start], arr[end]);
            }

            public int findMinimumAreaOfSmallestRectangleEnclosingBlackPixels(char[][] img, int x, int y) {

                if (img == null || img.length == 0)
                    return 0;
                if (img[0] == null || img[0].length == 0)
                    return 0;

                BiFunction<char[][], Integer, Boolean> rowHasBlackPixel = (im, row) -> {
                    for (int i = 0; i < im[row].length; i++) {
                        if (im[row][i] == '1')
                            return true;
                    }
                    return false;
                };

                BiFunction<char[][], Integer, Boolean> colHasBlackPixel = (im, col) -> {
                    for (int i = 0; i < im.length; i++) {
                        if (im[i][col] == '1')
                            return true;
                    } // todo: confirm column black-pixel check
                    return false;
                };

                int[] res = new int[4]; // top, bottom, left, right

                // 1st, find top ..
                int start = 0, end = x, mid;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (rowHasBlackPixel.apply(img, mid))
                        end = mid;
                    else
                        start = mid;
                }
                if (rowHasBlackPixel.apply(img, end))
                    res[0] = end;
                if (rowHasBlackPixel.apply(img, start))
                    res[0] = start;

                // now, find bottom ..
                start = x;
                end = img.length - 1;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (rowHasBlackPixel.apply(img, mid))
                        start = mid;
                    else
                        end = mid;
                }
                if (rowHasBlackPixel.apply(img, start))
                    res[1] = start;
                if (rowHasBlackPixel.apply(img, end))
                    res[1] = end;

                // find left
                start = 0;
                end = y;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (colHasBlackPixel.apply(img, mid))
                        end = mid;
                    else
                        start = mid;
                }
                if (colHasBlackPixel.apply(img, end))
                    res[2] = end;
                if (colHasBlackPixel.apply(img, start))
                    res[2] = start;

                // find right
                start = y;
                end = img[0].length - 1;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (colHasBlackPixel.apply(img, mid))
                        start = mid;
                    else
                        end = mid;
                }
                if (colHasBlackPixel.apply(img, start))
                    res[3] = start;
                if (colHasBlackPixel.apply(img, end))
                    res[3] = end;

                // finally, return the rectangle's area
                return (res[1] - res[0] + 1) * (res[3] - res[2] + 1);

            }

            public int findPeakItem(int[] arr) {

                BiFunction<int[], Integer, Integer> isPeak = (a, i) -> {
                    if (i < 0 || i > a.length - 1)
                        return Integer.MAX_VALUE;
                    // find the peak
                    if (a[i - 1] < a[i] && a[i] > a[i + 1])
                        return 0;
                    // eg. 0, 1, 2
                    if (a[i - 1] < a[i] && a[i] < a[i + 1])
                        return 1;
                    // eg. 2, 1, 0
                    if (a[i - 1] > a[i] && a[i] > a[i + 1])
                        return -1;
                    return Integer.MAX_VALUE;
                };

                int start = 0, end = arr.length - 1, mid, peak;
                while (start - 1 < end) {
                    mid = start + (end - start) / 2;
                    peak = isPeak.apply(arr, mid);
                    if (peak == 0)
                        return mid;
                    if (peak == 1)
                        start = mid;
                    if (peak == -1)
                        end = mid;
                    else
                        start = mid;
                }
                if (isPeak.apply(arr, start) == 0)
                    return start;
                if (isPeak.apply(arr, end) == 0)
                    return end;
                return Integer.MAX_VALUE;
            }

            public int searchRotatedSortedArray(int[] arr, int num) {
                if (arr == null || arr.length == 0)
                    return -1;
                int left = 0, right = arr.length - 1, mid;
                while (left + 1 < right) {
                    mid = (left + right) / 2;
                    if (arr[mid] == num)
                        return mid;
                    else if (arr[left] < arr[mid]) {
                        if (arr[left] <= num && arr[mid] > num)
                            right = mid;
                        else
                            left = mid;
                    } else {
                        if (arr[mid] < num && arr[right] >= num)
                            left = mid;
                        else
                            right = mid;
                    }
                }
                if (arr[left] == num)
                    return left;
                else if (arr[right] == num)
                    return right;
                return -1;
            }

            public int fastPower(int a, int b, int n) {
                if (n == 1)
                    return a % b;
                if (n == 0)
                    return 1 % b;
                long product = this.fastPower(a, b, n / 2);
                product = (product * product) % b; // or - (product ^ 2) % b
                if (n % 2 == 1)
                    product = (product * a) % b;
                return (int) product;
            }

            public int fastPowerNonRecursive(int a, int b, int n) {
                long ans = 1, tmp = a;
                while (n != 0) {
                    if (n % 2 == 1)
                        ans = (ans * tmp) % b;
                    tmp = (tmp ^ 2) % b;
                    n = n / 2;
                }
                return (int) ans % b;
            }

            public double myPower(double x, int n) {
                if (x == 0)
                    return 0;

                BiFunction<Double, Integer, Double>[] getPower = new BiFunction[1];
                getPower[0] = (x1, n1) -> {
                    if (n1 == 0)
                        return 1.0;
                    double tmp = getPower[0].apply(x1, n1 / 2);
                    return n1 % 2 == 0 ? tmp * tmp : Math.pow(tmp, 2) * x1; // tmp ^ 2 - Bitwise XOR instead
                };

                return n >= 0 ? getPower[0].apply(x, n) : 1 / getPower[0].apply(x, -n);
            }

            public double minmaxDistance(int[] locations, int k) {

                // Get number of lcoations needed with current distance d
                BiFunction<int[], Double, Integer> count = (ls, d) -> {
                    int c = 0;
                    for (int i = 0; i < ls.length - 1; i++)
                        c += (ls[i + 1] - ls[i]) / d;
                    return c;
                };

                // Binary Search on the result
                double left = 0, right = locations[locations.length - 1] - locations[0];
                double eps = 1e-6, mid;
                while (left + eps < right) {
                    mid = left + (right - left) / 2.0;
                    if (count.apply(locations, mid) > k)
                        left = mid;
                    else
                        right = mid;
                }
                if (count.apply(locations, left) == k)
                    return left;
                return right;
            }

        }

        ////////////////////////////////////////
        // OTHER ALGO'S
        ////////////////////////////////////////

        public static class DataStructures {

            public DataStructures() {
            }

            // Arrays & Strings

            // TODO: Fix

            public int reverse(int n) { // O(1 - regular number - of digits > n - very large number - of digits) t ; O(1) s

                // int r = Integer.parseInt(("" + n).reverse())
                // return r;

                // todo: handle all pre-checks in-loop - O(n) t

                String str = ""; Boolean negative = n < 0;

                // remove any preceding 0 (1st list item)
                if (n % 10 == 0) n = n / 10;

                while (n > 0) {
                    str = str + (n % 10); // cut out last digit of n & appends to str
                    n = n / 10; // remove last digit - already returns floored-integer - Math.floor not required
                }

                System.out.println(str);
                return Integer.parseInt(str)
                        * (
                        negative
                                ? -1 : 1
                );
            }

            // 3rd-Party
            public int reverse1(int x) { // O(n ~ 1) t; O(1) s
                int reversed = 0, pop = 0;
                while (x != 0) {
                    pop = x % 10; // modulo out last digit (also -ve if x -ve)
                    x /= 10; // integer-divide out last digit - Math.floor not required

                    // Check for overflow/underflow before adding the popped digit
                    // return 0 on very large-number digits
                    if (
                            reversed > Integer.MAX_VALUE/10 // max-possible number of digits (hence Integer.MAX_VALUE/10 last digit int-divided out)
                                    || (reversed == Integer.MAX_VALUE / 10 && pop > 7)
                    ) return 0;
                    if (
                            reversed < Integer.MIN_VALUE/10
                                    || (reversed == Integer.MIN_VALUE / 10 && pop < -8)
                    ) return 0;

                    reversed = reversed * 10 + pop; // pad another '0' digit, then add x-modulo'd out digit (if x -ve new '-ve additions' don't matter; -reversed already in -ve zone)
                }
                return reversed; // if x -ve, reversed already -ve
            }

            String reorganizeString(String S) { // TODO: verify - O(n + n log n) t | O(n) s

                Map<Character, Integer> counts = new HashMap<>();

                // O(n)
                for (char c : S.toCharArray())
                    counts.put(c, counts.getOrDefault(c, 0) + 1);

                PriorityQueue<Character> maxHeap = new PriorityQueue<>(
                        (a, b) -> counts.get(b) - counts.get(a));
                maxHeap.addAll(counts.keySet());

                // todo: NB: Concatenating a String takes in more buffer data or additional
                // memory overhead (more space complexity) than manually building up a String
                // with a mutable StringBuilder object, Character by Character
                // Concatenating to an immutable String object creates a new String object
                // (leading to additional memory overhead)

                StringBuilder result = new StringBuilder();
                char current;
                char next;
                char last;

                // O(n log n)
                while (maxHeap.size() > 1) {
                    current = maxHeap.poll();
                    next = maxHeap.poll();
                    result.append(current);
                    result.append(next);
                    counts.put(current, counts.get(current) - 1);
                    counts.put(next, counts.get(next) - 1);
                    if (counts.get(current) > 0)
                        maxHeap.add(current);
                    if (counts.get(next) > 0)
                        maxHeap.add(next);
                    // base-case check
                    if (!maxHeap.isEmpty()) {
                        last = maxHeap.poll();
                        if (counts.get(last) > 1)
                            return "";
                        result.append(last);
                    }
                }

                return result.toString();
            }

            @FunctionalInterface
            interface NumSwaps {
                int swap(int num, int[] a, int[] b);
            }

            // TODO: Test
            int minimumDominoRotations(int[] a, int[] b) {

                NumSwaps numSwaps = (num, a1, b1) -> {
                    int nSwaps = 0;
                    for (int i = 0; i < a1.length; i++) {
                        if (a1[i] != num && b1[i] != num)
                            return Integer.MAX_VALUE;
                        else if (a1[i] != num)
                            nSwaps++;
                    }
                    return nSwaps;
                };

                int minSwaps = Math.min(
                        numSwaps.swap(a[0], a, b),
                        numSwaps.swap(b[0], a, b));
                minSwaps = Math.min(minSwaps, numSwaps.swap(a[0], b, a));
                minSwaps = Math.min(minSwaps, numSwaps.swap(b[0], a, b));
                return minSwaps == Integer.MAX_VALUE ? -1 : minSwaps;
            }

            int maximumPointsFromCards(int[] points, int k) {
                int sum = 0, max = 0;
                int n = points.length - 1;
                int[] left = new int[k + 1];
                int[] right = new int[k + 1];
                left[0] = 0;
                right[0] = 0;

                for (int i = 1; i <= k; i++)
                    left[i] = left[i - 1] + points[i - 1];

                for (int i = 1; i <= k; i++)
                    right[i] = right[i - 1] + points[n--];

                for (int j = 0; j < left.length; j++) {
                    sum = left[j] + right[right.length - j - 1];
                    max = Math.max(sum, max);
                }

                return max;
            }

            // (Array) Lists & Tuples

            // Sets & Sequences

            // WeakMaps & WeakSets

            // HashMaps & HashTables

            // Matrices

            // TODO: Test
            int[][] rotateMatrix(int[][] matrix) { // O(n) t | O(1) s
                // only for square NxN matrices, or else, find number of columns too
                // (matrix[0].length)
                int N = matrix.length;
                int temp;

                for (int i = 0; i < N; i++)
                    for (int j = i; j < N; j++) {
                        temp = matrix[i][j];
                        matrix[i][j] = matrix[j][i];
                        matrix[j][i] = temp;
                    }

                for (int i = 0; i < N; i++)
                    for (int j = 0; j < N / 2; j++) {
                        temp = matrix[i][j];
                        matrix[i][j] = matrix[i][N - 1 - j];
                        matrix[i][N - 1 - j] = temp;
                    }

                return matrix;
            }

            // This diagonal iteration solution only works on square matrices
            void setZeroesDiagonally(int[][] matrix) {
                int r = 0;
                int c = 0;
                Boolean isZ = false;
                int r2 = 0;
                int rs = matrix.length;
                int cs = matrix[0].length;
                while (r < rs) {
                    while (c < cs) {
                        System.out.println(matrix[r][c]);
                        if (isZ)
                            matrix[r][c] = 0;
                        else if (matrix[r][c] == 0)
                            isZ = true;
                        else
                            break;
                    }
                    if (isZ) {
                        r2 = r + 1;
                        while (r2 < rs)
                            matrix[r2][c] = 0;
                    }
                    r++;
                    c++;
                }
            }

            // Q - Given 2 sparse matrices A & B, return AB (assume A's cols.length == B's
            // rows.length)

            int[][] sparseMatrixMultiplication(int[][] A, int[][] B) {
                int n = A.length,
                        m = B[0].length,
                        t = A[0].length; // A's column length should == B's row length, for matrix multiplication to be
                // possible
                int[][] res = new int[n][m];

                List<List<Integer>> col = new ArrayList<>();
                int i, j, k;

                for (i = 0; i < t; i++) {
                    col.add(new ArrayList<>());
                    for (j = 0; j < m; j++)
                        if (B[i][j] != 0)
                            col.get(i).add(j);
                }
                for (i = 0; i < n; i++)
                    for (k = 0; k < t; k++) {
                        if (A[i][k] == 0)
                            continue;
                        for (int x : col.get(k)) // todo: foreach loops always require declared datatype on iterator var
                            res[i][x] += A[i][k] * B[k][x];
                    }

                return res;
            }

            /*
             * Q - With an array of ith prices of a given stock on day i
             * and a permit for only 1 transaction per day,
             * Design an algo to find maximum profit
             * Cannot sell a stock before buying one
             */

            // or maximumProfit(..)
            int bestTimeToBuyOrSellStock(int[] prices) {
                if (prices == null || prices.length == 0)
                    return 0;

                int min = Integer.MAX_VALUE,
                        profit = 0;

                for (int i : prices) {
                    min = i < min ? i : min;
                    profit = (i - min) > profit
                            ? i - min
                            : profit;
                }

                return profit;
            }

            // Q - Given an nxm 2d grid map of 1s (land) & 0s (water), return number of
            // islands
            // Island = land (1) surrounded by water (0s) & is formed by connecting adjacent
            // lands horizontally / vertically
            // Can assume all 4 edges of the grid are all surrounded by water

            @FunctionalInterface
            interface BFS {
                void bfs(char[][] grid, int x, int y);
            }

            @FunctionalInterface
            interface IsValid {
                boolean check(int x, int y, char[][] grid);
            }

            int numberOfIslands(char[][] grid) {

                if (grid == null ||
                        grid.length == 0 ||
                        grid[0].length == 0)
                    return 0;

                IsValid isValid = (x, y, g) -> {
                    int n = g.length, m = g[0].length;
                    return x >= 0
                            && x < n
                            && y >= 0
                            && y < m
                            && g[x][y] == '1';
                };

                BFS bfs = (g, x, y) -> {
                    int[][] directions = {
                            { 0, 1 }, { 0, -1 },
                            { -1, 0 }, { 1, 0 }
                    };
                    Queue<int[]> queue = new java.util.LinkedList<>();
                    queue.offer(new int[] { x, y });
                    g[x][y] = '2'; // '2' means marked as 'visited' (or create a visited set)

                    int[] node = null;
                    int i, nextX, nextY;

                    while (!queue.isEmpty()) {
                        node = queue.poll();
                        for (i = 0; i < 4; i++) {
                            nextX = node[0] + directions[i][0];
                            nextY = node[1] + directions[i][1];

                            if (!isValid.check(nextX, nextY, grid))
                                continue;

                            g[nextX][nextY] = '2';
                            queue.offer(new int[] { nextX, nextY });
                        }
                    }

                };

                int n = grid.length,
                        m = grid[0].length,
                        islands = 0;

                for (int i = 0; i < n; i++)
                    for (int j = 0; j < m; j++)
                        if (grid[i][j] == '1') {
                            bfs.bfs(grid, i, j);
                            islands++;
                        }

                return islands;
            }

            /*
             * In an infinite chess board with coordinates from -inf to +inf, there's a
             * knight at square [0, 0]
             * A knight has 8 possible moves it can make. Each move is 2 squares in a
             * cardinal direction,
             * then 1 square in an orthogonal direction.
             *
             * Return the minimum number of steps needed to move the knight to the square
             * [x, y].
             * It is guaranteed the answer exists
             *
             * Constraint - |x| + |y| <= 300
             */

            @FunctionalInterface
            interface IsValid2 {
                boolean check(int x, int y, boolean[][] visited);
            }

            // minimumKnightMoves()
            int knightShortestPath(int x, int y) {
                if (x == 0 && y == 0)
                    return 0;

                IsValid2 isValid = (x1, y1, v) -> {
                    if (Math.abs(x1 - 300)
                            + Math.abs(y1 - 300) <= 300
                            && !v[x1][y1])
                        return true;
                    return false;
                };

                int[][] Directions = new int[][] {
                        { 1, 2 }, { 2, 1 }, { -1, 2 }, { 1, -2 },
                        { -1, -2 }, { -2, 1 }, { -2, -1 }, { 2, -1 }
                };
                Queue<int[]> queue = new java.util.LinkedList<>();
                boolean[][] visited = new boolean[601][601];

                int step = 0, size, nextX, nextY;
                int[] node;

                queue.offer(new int[] { 300, 300 });
                visited[300][300] = true;

                while (!queue.isEmpty()) {
                    size = queue.size();
                    while (size-- > 0) {
                        node = queue.poll();
                        for (int[] dir : Directions) {
                            nextX = node[0] + dir[0];
                            nextY = node[1] + dir[1];
                            if (nextX == x + 300 &&
                                    nextY == y + 300)
                                return step + 1;
                            if (isValid.check(nextX, nextY, visited)) {
                                queue.offer(new int[] { nextX, nextY });
                                visited[nextX][nextY] = true;
                            }
                        }
                    }
                    step++;
                }

                return step;
            }

            /*
             * Given a matrix and a target, return number of non-empty submatrices that sum
             * up to target
             * A submatrix [(x1, y1), (x2, y2)] is a set of all cells matrix[x][y],
             * with x1 <= x <= x2 and y1 <= y <= y2
             * 2 submatrices (x1, y1, x2, y2) and (x1', y1', x2', y2') are different if they
             * have some coordinate that's different
             */

            // Use prefix sum & convert it to. a 1-D Array
            // Find the subarray with sum that equals target

            int numberOfSubmatrixSumToTarget(int[][] mat, int target) {
                int n = mat.length, m = mat[0].length;
                int[][] prefixSum = new int[n][m];

                // calc prefix sum for each row
                int i, j;
                for (i = 0; i < n; i++)
                    for (j = 0; j < m; j++)
                        prefixSum[i][j] = j > 0
                                ? mat[i][j]
                                + prefixSum[i][j - 1]
                                : mat[i][j];

                int res = 0;
                HashMap<Integer, Integer> map = new HashMap<>();

                int startCol, endCol, sum, row;

                for (startCol = 0; startCol < m; startCol++)
                    for (endCol = startCol; endCol < m; endCol++) {
                        map.clear();
                        map.put(0, 1);
                        sum = 0;

                        // convert it to 1-D Array, find a subarray sum to target
                        for (row = 0; row < m; row++) {
                            if (startCol == 0)
                                sum += prefixSum[row][endCol];
                            else
                                sum += endCol > startCol
                                        ? (prefixSum[row][endCol]
                                        - prefixSum[row][startCol - 1])
                                        : mat[row][startCol];

                            res += map.getOrDefault(sum - target, 0);
                            map.put(sum, map.getOrDefault(sum, 0) + 1);
                        }
                    }

                return res;
            }

            // Linked Lists

            public class LinkedList {

                public class ListNode {
                    private Object value;
                    private ListNode next;

                    public ListNode(Object v, ListNode n) {
                        this.value = v;
                        this.next = n;
                    }

                    public Object getValue() {
                        return this.value;
                    }

                    public void setValue(Object v) {
                        this.value = v;
                    }

                    public ListNode getNext() {
                        return this.next;
                    }

                    public void setNext(ListNode v) {
                        this.next = v;
                    }
                }

                public class SListNode extends ListNode {
                    public SListNode(Object v, ListNode n) {
                        super(v, n);
                    }
                }

                public class DListNode extends ListNode {
                    private ListNode prev;

                    public DListNode(Object v, ListNode n, ListNode p) {
                        super(v, n);
                        this.prev = p;
                    }

                    public ListNode getPrev() {
                        return this.prev;
                    }

                    public void setPrev(ListNode v) {
                        this.prev = v;
                    }
                }

                private ListNode head;
                private ListNode tail;

                public LinkedList(ListNode h, ListNode t) {
                    this.head = h;
                    this.tail = t;
                }

                public ListNode getHead() {
                    return this.head;
                }

                public void setHead(ListNode v) {
                    this.head = v;
                }

                public ListNode getTail() {
                    return this.tail;
                }

                public void setTail(ListNode v) {
                    this.tail = v;
                }

                // todo: test out these 'Functional Interface' callback attempts - how they
                // affect referenced objects' state changes

                // with SListNode (or ListNode in general) find previous nodes on each
                // iteration, to be passed into the callbacks as an extra argument
                public ListNode findAction(String by, Object key, Function<ListNode, ListNode> cb) { // O(n) t ; O(1) s
                    ListNode node = this.head;
                    if (node == null)
                        return null;
                    switch (by) {
                        case "node":
                            ListNode toFind = (ListNode) key;
                            if (toFind == null)
                                return null;
                            while (node.getNext() != null) {
                                // (best option) reference equality - 2 references pointing to the exact same
                                // object in memory
                                // or (System.identityHashCode(slow) == System.identityHashCode(fast)) -
                                // comparing objects' unique hash codes assigned by the jvm
                                // not slow.equals(fast) - logical equality, based on object's
                                // state/contents/prop-values
                                if (node == toFind)
                                    return cb.apply(node);
                                node = node.getNext();
                            }
                            break;
                        case "index":
                            Integer i = (Integer) key;
                            int c = 0;
                            while (node.getNext() != null) {
                                if (c == i)
                                    return cb.apply(node);
                                node = node.getNext();
                                c++;
                            }
                            break;
                        case "value":
                            Object value = key; // unnecessary assignment (go ahead with key)
                            while (node.getNext() != null) {
                                // should cb on every object value match
                                if (node.getValue() == value)
                                    cb.apply(node);
                                node = node.getNext();
                            }
                            return null;
                        case "duplicates-inline":
                            while (node.getNext() != null) {
                                // should cb on every object value match
                                if (node.getValue() == node.getNext().getValue())
                                    cb.apply(node);
                                else
                                    node = node.getNext();
                                // else statement this time, forces iteration to stay with current node until
                                // all in-line duplicates are removed (in cb)
                            }
                            return null;
                        default:
                            return null;
                    }
                    return null;
                }

                private Function<ListNode, ListNode> remove = (node) -> {

                    // TODO: 'ClassCastException' Error-prone (for downcasts from ListNode to
                    // DListNode, if node was SListNode)

                    ListNode prev = ((DListNode) node).getPrev();
                    DListNode next = (DListNode) node.getNext();
                    prev.setNext(next);
                    next.setPrev(prev);
                    ((DListNode) node).setPrev(null);
                    node.setNext(null);

                    // or - Alt 3rd Party logic - works perfectly for Singly-linked-lists (because
                    // no need to work with previous node)
                    // using this logic below, change all DListNode calls to ListNode, so logic
                    // would work for both Singly (SListNode) & Doubly (DListNode) linked-lists

                    /*
                     * Error - variable next is already defined in instance initializer of class
                     * Need to re-use variable 'next'
                     */

                    // Nullify the reference to make the object eligible for garbage collection
                    next = null;
                    // Suggest the JVM to perform garbage collection
                    System.gc();


                    /*
                    * // todo: Redeclaring same 'next' variable in a new scope - syntax error
                    { // todo: test next line (some editors still call syntax error)
                        ListNode next = node.getNext();
                        ListNode nextNext = next.getNext();
                        node.setValue(next.getValue());
                        node.setNext(nextNext);
                        // no need to call .setPrev(), if DListNode isn't forced
                        ((DListNode) nextNext).setPrev(node);
                    }
                    */


                    // * Redeclaring a new 'next1' variable instead
                    ListNode next1 = node.getNext();
                    ListNode nextNext = next1.getNext();
                    node.setValue(next1.getValue());
                    node.setNext(nextNext);
                    // no need to call .setPrev(), if DListNode isn't forced
                    ((DListNode) nextNext).setPrev(node);

                    return node;
                };

                public ListNode removeNode(ListNode n) {
                    ListNode node = this.findAction("node", n, this.remove);
                    // work with node
                    return node;
                }

                public ListNode removeAtIndex(int i) {
                    ListNode node = this.findAction("index", i, this.remove);
                    return node;
                }

                public ListNode removeValue(Object v) { // wouldn't work for primitive data type values
                    ListNode node = this.findAction("value", v, this.remove);
                    return node;
                }

                public ListNode removeInlineDuplicates() {
                    ListNode duplicateNode = this.findAction("duplicates-inline", null, (node) -> {
                        ListNode next = node.getNext();
                        node.setNext(next.getNext());
                        return next;
                    });
                    return duplicateNode;
                }

                // Extra Functions

                ListNode middleNode(LinkedList ll) { // O(n/2 ~ n) t ; O(1) s
                    ListNode slow = ll.getHead();
                    if (slow == null || slow.getNext() == null)
                        return slow;
                    // shorthand for next 2 lines
                    // if (slow == null) return null;
                    // if (slow.getNext() == null) return slow;
                    ListNode fast = slow; // start from head, not next-to-head, so slow is at middle after loop-end
                    while (fast.getNext() != null &&
                            fast.getNext().getNext() != null) {
                        slow = slow.getNext();
                        fast = fast.getNext().getNext();
                    }
                    // 3rd party logic - check fast.next, in case loop ended with fast.next.next
                    ListNode middle = fast.getNext() == null ? slow : slow.getNext();
                    return middle;
                }

                boolean hasCycle(LinkedList ll) { // O(n) t ; O(1) s
                    // could be O(2n or 3n or 4n) based on the multiplied jump of the fast node

                    ListNode slow = ll.getHead();
                    if (slow == null || slow.getNext() == null)
                        return false;
                    ListNode fast = slow.getNext();
                    while (fast.getNext() != null && fast.getNext().getNext() != null) {
                        if (slow == fast)
                            return true;
                        slow = slow.getNext();
                        fast = fast.getNext().getNext();
                    }
                    return false;
                }

                // todo: test - generic logic (working for both Singly & Doubly-linked
                // ListNodes)
                LinkedList reverse(LinkedList ll) { // O(n) t ; O(1) s
                    ListNode node = ll.getHead(), next;
                    if (node == null || node.getNext() == null)
                        return ll;

                    // ListNode next = node.getNext(); // * not required - only prev & node are
                    // required for the loop
                    // ListNode tmp = null; // * not required - only prev & node are required for
                    // the loop
                    ListNode prev = null;

                    // first reset both head & tail of linked list
                    ll.setHead(ll.getTail());
                    ll.setTail(node);

                    // now, reverse ll regardless of whether it each pair of adjacent ListNodes are
                    // singly or doubly - linked

                    while (node != null) {
                        next = node.getNext(); // for the 'next' iteration
                        // tmp = next.getNext(); // * not required - using next for 'next' iteration
                        // instead

                        node.setNext(prev); // * only 2 changes required, for each iteration
                        if (node instanceof DListNode)
                            ((DListNode) node).setPrev(next); // * changes would be propagated in each node throughout
                        // loop

                        // next.setNext(node); // * not required - node would .setNext(prev) on next
                        // iteration

                        // * not required - node execs .setPrev(next) now, in this iteration
                        // prev != null && -> not required because it needs to be instantiated before
                        // taking any of ListNode's child classes
                        // either SListNode / DListNode
                        // if (prev instanceof DListNode) prev.setPrev(node);

                        prev = node;
                        node = next; // next = tmp; // * tmp not required; next is used for 'next' iteration
                    }
                    return ll;
                }

                // todo: Test all remaining Alt 3rd-Party (Tutorials) logic methods
                // * create a new ListNode class with all properties public

                public boolean hasCycle(ListNode head) {
                    if (head == null || head.next == null)
                        return false;
                    ListNode slow = head;
                    ListNode fast = head.next;
                    while (fast.next != null && fast.next.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                        if (slow == fast)
                            return true;
                    }
                    return false;
                }

                public ListNode middleNode(ListNode head) {
                    if (head == null || head.next == null)
                        return head;
                    ListNode slow = head;
                    ListNode fast = head;
                    while (fast.next != null && fast.next.next != null) {
                        slow = slow.next;
                        fast = fast.next.next;
                    }
                    ListNode middle = head;
                    if (fast.next == null)
                        middle = slow;
                    else
                        middle = slow.next;
                    return middle;
                }

                // works perfectly for singly-linked listnodes
                // reset node's value to next's value, then set node's .next to next's .next
                public void deleteNode(ListNode node) {
                    if (node == null || node.next == null)
                        return;
                    ListNode nextNext = node.next.next;
                    node.value = node.next.value;
                    node.next = nextNext;
                }

                public ListNode deleteDuplicates(ListNode head) {
                    if (head == null || head.next == null)
                        return head;
                    ListNode node = head;
                    while (node.next != null) {
                        if (node.value == node.next.value)
                            node.next = node.next.next;
                        else
                            node = node.next;
                        // else statement this time, forces iteration to stay with current node until
                        // all in-line duplicates are removed
                    }
                    return head;
                }

                // Alt 3rd-Party logic - Singly-linked list only, without a set tail ListNode
                public ListNode reverse(ListNode head) {
                    if (head == null || head.next == null)
                        return head;
                    ListNode prev = null, node = head, next = head.next;

                    while (node != null) {
                        node.next = prev;
                        prev = node;
                        node = next;
                        if (next != null)
                            next = next.next;
                    }
                    head = prev;
                    return head;
                }

                public LinkedList flattenBinaryTreeToLinkedList(Tree.TreeNode root) { // O(n) t ; O(n) s (not done
                    // in-place; LinkedList with n
                    // nodes required)

                    if (root == null)
                        return null;
                    ListNode lRoot = new ListNode(root.value(), null);

                    Tree.TreeNode node = null;
                    ListNode lNode = lRoot;

                    // bfs

                    // todo: Use Java's in-built Queue class, because Trees is the current study
                    java.util.Queue<Tree.TreeNode> q = new java.util.LinkedList<Tree.TreeNode>(); // not Queue();
                    // Java Queue cannot be instantiated directly
                    // can only be instantiated by classes that implement it as an interface
                    // eg. LinkedList, PriorityQueue, ArrayDeque, customs, etc

                    /*
                     * Enqueue: Adding elements to the queue is done using add() or offer().
                     * Dequeue: Removing elements from the queue is done using poll(), remove(),
                     * peek(), or element().
                     */

                    q.add(root); // not .enqueue() / .push();

                    while (!q.isEmpty()) {

                        node = (Tree.TreeNode) q.poll(); // not .dequeue() / .pop();
                        // no need to store node.value()s into ListNodes
                        // because root.value() has been stored, as lRoot (lNode in 1st iteration)
                        // only store node.left() & .right() .value()s (left to right in this case; or
                        // reverse-order - right to left)

                        // or a dummy root can be used, so only current node's value would be linked,
                        // before enqueueing left & right children

                        if (node.left() != null) {
                            lNode.setNext(new ListNode(node.left().value(), null));
                            q.add(node.left());
                            lNode = lNode.getNext();
                        }

                        if (node.right() != null) {
                            lNode.setNext(new ListNode(node.right().value(), null));
                            q.add(node.right());
                            lNode = lNode.getNext();
                        }

                        // todo: or a dummy root could've been used; so only current node's value would
                        // be linked, before enqueueing left & right children

                        /*
                         * lNode = lDummy (before root)
                         *
                         * node = (Tree.TreeNode) q.poll();
                         * lNode.setNext(new ListNode(node.value(), null));
                         * lNode = lNode.getNext();
                         * if (node.left() != null) q.add(node.left());
                         * if (node.right() != null) q.add(node.right());
                         *
                         * after loop, return lDummy.getNext(); as new root (set as new 'llRoot'; set
                         * lDummy = null; return new 'llRoot')
                         */

                    }

                    // lNode Variable used in lambda expression should be final or effectively final
                    // using it as an AtomicVariable instead (can also use a final 1-elem array, or an anonymous var :Object)

                    AtomicReference<ListNode> lNodeRef = new AtomicReference<>(lNode);

                    // dfs - average

                    Consumer<Tree.TreeNode>[] dfs = new Consumer[1];

                    dfs[0] = (n) -> {
                        if (n == null)
                            return;

                        // no need to store n.value()s into ListNodes
                        // because root.value() has been stored
                        // only store n.left() & .right() .value()s

                        // dfs - depth-traversing to left-side of B-Tree, adding .left of each node
                        // before depth-traversing to right-side of B-Tree, then adding .right of each
                        // node
                        // this works as pre-order - left only

                        if (n.left() != null) {
                            /* // * lNode in lambda now an AtomicReference
                            lNode.setNext(new ListNode(n.left().value(), null));
                            lNode = lNode.getNext();
                            dfs[0].accept(n.left());
                            */

                            // todo: confirm atomicRef.get() still returns ReferenceObject for .methodCall()
                            lNodeRef.get().setNext(new ListNode(n.left().value(), null));
                            lNodeRef.set(lNodeRef.get().getNext());
                            dfs[0].accept(n.left());
                        }

                        // * or, depth-traverse to right-side of B-Tree 1st
                        // this works as pre-order - right only

                        if (n.right() != null) {
                            lNodeRef.get().setNext(new ListNode(n.right().value(), null));
                            lNodeRef.set(lNodeRef.get().getNext());
                            dfs[0].accept(n.right());
                        }

                        // dfs - .left & .right of each node
                        // before depth-traversing to left-side of B-Tree, then to right-side

                        if (n.left() != null) {
                            lNodeRef.get().setNext(new ListNode(n.left().value(), null));
                            lNodeRef.set(lNodeRef.get().getNext());
                        }

                        if (n.left() != null) {
                            lNodeRef.get().setNext(new ListNode(n.right().value(), null));
                            lNodeRef.set(lNodeRef.get().getNext());
                        }

                        if (n.left() != null)
                            dfs[0].accept(n.left());

                        if (n.right() != null)
                            dfs[0].accept(n.right());

                    };

                    dfs[0].accept(root);

                    // dfs - best

                    dfs[0] = (n) -> {
                        if (n == null)
                            return;

                        // need to store only n.value()s into ListNodes
                        // so root.value() will be re-stored
                        // only recurse through n.left() & .right() nodes

                        // dfs - depth-traversing pre/post/in - orders
                        // these can work as left->right / vice-versa

                        // pre-order

                        lNodeRef.get().setValue(n.value());
                        lNodeRef.get().setNext(new ListNode(null, null));
                        lNodeRef.set(lNodeRef.get().getNext());

                        dfs[0].accept(n.left());
                        dfs[0].accept(n.right());

                        // post-order

                        dfs[0].accept(n.left());
                        dfs[0].accept(n.right());

                        lNodeRef.get().setValue(n.value());
                        lNodeRef.get().setNext(new ListNode(null, null));
                        lNodeRef.set(lNodeRef.get().getNext());

                        // in-order

                        dfs[0].accept(n.left());

                        lNodeRef.get().setValue(n.value());
                        lNodeRef.get().setNext(new ListNode(null, null));
                        lNodeRef.set(lNodeRef.get().getNext());

                        dfs[0].accept(n.right());

                    };

                    dfs[0].accept(root);

                    // now return LinkedList / ListNode root

                    return new LinkedList(lRoot, lNode); // or LinkedList(lRoot, lNodeRef.get())

                }

                public Tree.TreeNode flattenBinaryTreeToLinkedList2(Tree.TreeNode root) { // O(n) t ; O(1) s
                    // in-place; No LinkedList with n nodes required
                    return null;
                }

            }

            // Stacks

            // Queues

            // Heaps (max & min)

            // Binary Heaps

            // Priority Queues

            // Trees

            public static class Tree { // todo: Static even though it doesn't contain any custom-defined
                // @FunctionalInterface
                // Should be static because its child-class is static; and the child-class
                // instantiates super(..) in its constructor
                // * Child-class is static because it contains 1+ custom-defined
                // @FunctionalInterfaces

                public class TreeNode {

                    private Object value;
                    private TreeNode left;
                    private TreeNode right;

                    public TreeNode(Object v, TreeNode l, TreeNode r) {
                        this.value = v;
                        this.left = l;
                        this.right = r;
                    }

                    public Object value() {
                        return this.value;
                    }

                    public void value(Object node) {
                        this.value = node;
                    }

                    public TreeNode left() {
                        return this.left;
                    }

                    public void left(TreeNode node) {
                        this.left = node;
                    }

                    public TreeNode right() {
                        return this.right;
                    }

                    public void right(TreeNode node) {
                        this.right = node;
                    }

                }

                private TreeNode root;

                public Tree(TreeNode root) {
                    this.root = root;
                }

                private TreeNode lastNode = null;

                // 3rd-Party (Tutorial) DFS Traversal
                public void flattenBinaryTreeToLinkedList(TreeNode root) {
                    if (root == null)
                        return;

                    if (lastNode != null) {
                        lastNode.left(null);
                        lastNode.right(root);
                    }

                    lastNode = root;
                    TreeNode right = root.right; // root might be changed
                    this.flattenBinaryTreeToLinkedList(root.left);
                    this.flattenBinaryTreeToLinkedList(right);
                    // recurse on the right node instead, because root might be changed
                    // this.flattenBinaryTreeToLinkedList(root.right);
                }

                // DFS Divide & Conquer
                public TreeNode DCFlattenBinaryTreeToLinkedList(TreeNode root) {
                    if (root == null)
                        return null;

                    TreeNode left = this.DCFlattenBinaryTreeToLinkedList(root.left);
                    TreeNode right = this.DCFlattenBinaryTreeToLinkedList(root.right);

                    if (left != null) {
                        left.right = root.right;
                        root.right = root.left;
                        root.left = null;
                    }

                    if (right != null)
                        return right;
                    if (left != null)
                        return left;
                    return root;
                }

            }

            // Binary (Search) Trees

            public static class BST extends Tree {

                public BST(TreeNode root) {
                    super(root);
                }

                public Object cfs(TreeNode root, Object key) {

                    BiFunction<TreeNode, Object, Object> iteration =
                            (TreeNode node, Object k) -> {
                                while (node != null && (Integer) node.value != (Integer) k) {
                                    if ((Integer) k < (Integer) node.value)
                                        node = node.left;
                                    else
                                        node = node.right;
                                }
                                return node; // Will be null if not found
                            };

                    BiFunction<TreeNode, Object, Object>[] recursion = new BiFunction[1];
                    recursion[0] = (TreeNode node, Object k) -> {
                        if (node == null || (Integer) node.value == (Integer) k)
                            return node;

                        if ((Integer) k < (Integer) node.value)
                            return recursion[0].apply(node.left, k);
                        else
                            return recursion[0].apply(node.right, k);
                    };

                    Object it = iteration.apply(root, key);
                    Object re = recursion[0].apply(root, key);

                    System.out.println(it.toString());
                    System.out.println(re.toString());

                    return it; // re
                }

                // Q - With a Binary Tree, find a sub-tree with minimum sum

                public class MinimumSubTree {

                    // Option 1: DFS Traversal + Divide & Conquer
                    // using variable props

                    private TreeNode subTree = null;
                    private int subTreeSum = Integer.MAX_VALUE;

                    public TreeNode findSubTree(TreeNode root) {

                        Function<TreeNode, Integer>[] helper = new Function[1];
                        helper[0] = (node) -> {
                            if (node == null)
                                return 0;

                            // post-order DFS
                            int sum = helper[0].apply(node.left()) + helper[0].apply(node.right())
                                    + (Integer) node.value();
                            if (sum <= subTreeSum) {
                                subTreeSum = sum;
                                subTree = node;
                            }
                            return sum;
                        };

                        this.subTreeSum = helper[0].apply(root);
                        return this.subTree;
                    }

                    // Option 2: Divide & Conquer
                    // using Object prop (with multithreading)

                    class ResultType {
                        public TreeNode minSubTree;
                        public int sum, minSum;

                        public ResultType(TreeNode minSubTree, int minSum, int sum) {
                            this.minSubTree = minSubTree;
                            this.minSum = minSum;
                            this.sum = sum;
                        }
                    }

                    public TreeNode findSubTree2(TreeNode root) {

                        Function<TreeNode, ResultType>[] helper = new Function[1];
                        helper[0] = (node) -> {
                            if (node == null)
                                return new ResultType(null, Integer.MAX_VALUE, 0);

                            // Divide & Conquer - exec left & right concurrently
                            ResultType lRes = helper[0].apply(node.left());
                            ResultType rRes = helper[0].apply(node.right());
                            ResultType res = new ResultType(
                                    node,
                                    lRes.sum + rRes.sum + (Integer) node.value(),
                                    lRes.sum + rRes.sum + (Integer) node.value());

                            if (lRes.minSum <= res.minSum) {
                                res.minSum = lRes.minSum;
                                res.minSubTree = lRes.minSubTree;
                            }

                            if (rRes.minSum <= res.minSum) {
                                res.minSum = rRes.minSum;
                                res.minSubTree = rRes.minSubTree;
                            }

                            return res;

                        };

                        ResultType res = helper[0].apply(root);
                        return res.minSubTree;
                    }

                }

                // Q - Given a Binary Tree, return all root-to-leaf paths

                public static class BinaryTreePaths {

                    // Option 1 - ..
                    public List<String> binaryTreePaths(TreeNode root) {
                        List<String> paths = new ArrayList<>();
                        if (root == null)
                            return paths;
                        List<String> leftPaths = binaryTreePaths(root.left());
                        List<String> rightPaths = binaryTreePaths(root.right());

                        // todo: test implicit castings from .value()'s Object -> String

                        for (String path : leftPaths)
                            paths.add(root.value() + " -> " + path);
                        for (String path : rightPaths)
                            paths.add(root.value() + " -> " + path);

                        if (paths.size() == 0) // root is a leaf node
                            paths.add("" + root.value());

                        return paths;

                    }

                    @FunctionalInterface
                    interface Helper {
                        void help(TreeNode root, String path, List<String> result);
                    }

                    // Option 2 - Traversal
                    public List<String> binaryTreePaths2(TreeNode root) {
                        List<String> result = new ArrayList<String>();
                        if (root == null)
                            return result;

                        Helper[] helper = new Helper[1];
                        helper[0] = (r, p, res) -> {
                            if (r == null)
                                return;
                            if (r.left() == null && r.right() == null) {
                                res.add(p);
                                return;
                            }
                            if (r.left() != null)
                                helper[0].help(
                                        r.left(),
                                        p + " -> " + String.valueOf(r.left().value()),
                                        res);

                            if (r.right() != null)
                                helper[0].help(
                                        r.right(),
                                        p + " -> " + String.valueOf(r.right().value()),
                                        res);

                        };

                        helper[0].help(
                                root,
                                String.valueOf(root.value()),
                                result);

                        return result;

                    }

                }

                /**
                 * Q - Given the root and 2 nodes in a Binary Tree, find the Lowest Common
                 * Ancestor (LCA) of the 2 nodes
                 * The LCA is the node with the largest depth which is the ancestor of both
                 * nodes (the LCA is the branch node)
                 * Node 1 & Node 2 will exist in the tree
                 * A node is allowed to be a descendant of itself
                 */

                public static class LowestCommonAncestor {

                    /*
                     * Start from the tree root
                     * if LCA is found, return it
                     * else if n1 is found, return it
                     * else if n2 is found, return it
                     * else, return null
                     */

                    // Divide & Conquer
                    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode n1, TreeNode n2) {
                        if (root == null || root == n1 || root == n2)
                            return null;

                        // Divide
                        TreeNode left = lowestCommonAncestor(root.left(), n1, n2);
                        TreeNode right = lowestCommonAncestor(root.right(), n1, n2);

                        // Conquer
                        if (left == null && right == null)
                            return null;
                        if (left != null && right != null)
                            return root; // lca - recursive returns eventually meet at lca node
                        if (left != null)
                            return left;
                        if (right != null)
                            return right;
                        // todo: since valid left & right nodes keep getting returned, after 'Divide'
                        // their returns will eventually meet at the lca node

                        return null;

                    }

                    class ResultType {

                        private boolean n1Exists, n2Exists;
                        private TreeNode node;

                        ResultType(boolean n1, boolean n2, TreeNode n) {
                            n1Exists = n1;
                            n2Exists = n2;
                            node = n;
                        }

                        public boolean n1Exists() {
                            return n1Exists;
                        }

                        public boolean n2Exists() {
                            return n2Exists;
                        }

                        public TreeNode node() {
                            return node;
                        }

                    }

                    @FunctionalInterface
                    interface Helper {
                        ResultType help(TreeNode r, TreeNode a, TreeNode b);
                    }

                    public TreeNode lowestCommonAncestor2(TreeNode root, TreeNode n1, TreeNode n2) {

                        Helper[] helper = new Helper[1];
                        helper[0] = (r, a, b) -> {
                            if (r == null)
                                return new ResultType(false, false, null);

                            ResultType left = helper[0].help(r.left(), a, b);
                            ResultType right = helper[0].help(r.right(), a, b);

                            boolean aExists = left.n1Exists() || right.n1Exists() || r == a;
                            boolean bExists = left.n2Exists() || right.n2Exists() || r == b;

                            if (r == a || r == b)
                                return new ResultType(aExists, bExists, r);

                            if (left.node() != null && right.node() != null)
                                return new ResultType(aExists, bExists, r);
                            if (left.node() != null)
                                return new ResultType(aExists, bExists, left.node());
                            if (right.node() != null)
                                return new ResultType(aExists, bExists, right.node());

                            return new ResultType(aExists, bExists, null);

                        };

                        ResultType res = helper[0].help(root, n1, n2);
                        if (res.n1Exists() && res.n2Exists())
                            return res.node();
                        else
                            return null;

                    }

                }

                // Q - Flatten a Binary Tree (In-Place) to a Linked List in DFS Pre-order
                // Traversal
                // * Best Option

                public class FlattenBinaryTreeToLinkedList {

                    private TreeNode lastNode = null;

                    // DFS Traversal
                    public void flattenBinaryTreeToLinkedList(TreeNode root) { // O(n) t ; O(1) s (done in-place, no
                        // LinkedList data structure required)
                        if (root == null)
                            return;

                        if (lastNode != null) {
                            lastNode.left(null);
                            lastNode.right(root);
                        }

                        lastNode = root;
                        // Traversal drawback - root might be changed
                        TreeNode right = root.right();
                        flattenBinaryTreeToLinkedList(root.left());
                        flattenBinaryTreeToLinkedList(right);
                        // flattenBinaryTreeToLinkedList(root.left());
                        // flattenBinaryTreeToLinkedList(root.right());
                    }

                    // DFS Divide & Conquer - return last node in pre-order
                    public TreeNode DCFlattenBinaryTreeToLinkedList(TreeNode root) {
                        if (root == null)
                            return null;

                        TreeNode left = DCFlattenBinaryTreeToLinkedList(root.left());
                        TreeNode right = DCFlattenBinaryTreeToLinkedList(root.right());

                        // connect last left-side node to root.right()
                        if (left != null) {
                            left.right(root.right());
                            root.right(root.left());
                            root.left(null);
                        }

                        if (right != null)
                            return right;
                        if (left != null)
                            return left;

                        return root;

                    }

                }

                // Q - Find the kth-smallest element in a BST

                public static class KthSmallestElementBST {

                    private int result = 0;
                    private int index = 0;

                    public int kthSmallest(TreeNode root, int kth) {

                        BiConsumer<TreeNode, Integer>[] helper = new BiConsumer[1];
                        helper[0] = (r, k) -> {
                            if (r == null)
                                return;
                            helper[0].accept(r.left(), k);
                            index++;
                            if (index == k)
                                result = (Integer) r.value();
                            helper[0].accept(r.right(), k);
                        };

                        helper[0].accept(root, kth);
                        return result;

                    }

                    @FunctionalInterface
                    interface QuickSelect {
                        int onTree(TreeNode r, int k, Map<TreeNode, Integer> c);
                    }

                    public int kthSmallest2(TreeNode root, int kth) { // O(h) ~ O(n) t (h - tree height) ; O(n) s

                        BiFunction<TreeNode, Map<TreeNode, Integer>, Integer>[] countNodes = new BiFunction[1];
                        countNodes[0] = (r, c) -> {
                            if (r == null)
                                return 0;
                            int left = countNodes[0].apply(r.left(), c);
                            int right = countNodes[0].apply(r.right(), c);
                            int count = left + right + 1;
                            c.put(r, count);
                            return count;
                        };

                        QuickSelect[] quickSelect = new QuickSelect[1];
                        quickSelect[0] = (r, k, c) -> {
                            if (r == null)
                                return -1;
                            int left = r.left() == null ? 0 : c.get(r.left());
                            if (left >= k)
                                return quickSelect[0].onTree(r.left(), k, c);
                            if (left + 1 == k)
                                return (Integer) r.value();

                            return quickSelect[0].onTree(r.right(), k - left - 1, c);
                        };

                        Map<TreeNode, Integer> children = new HashMap<>();
                        countNodes[0].apply(root, children);
                        return quickSelect[0].onTree(root, kth, children);

                    }

                }

                // Q - Pre-order, In-order, Post-order / Iterator Traversals

                public class BinaryTreeTraversals {

                    // * DFS Traversals

                    // In-order Traversal

                    public class InOrder {

                        // todo: NB: Alt 3rd-Party (Tutorials) Logic - (Stack-Loop) iteration method for
                        // Tree-Traversals

                        public List<Integer> iterate(TreeNode root) { // O(n) t ; O(h ~ 1) s (h - max height)
                            // Stack takes in nodes until max-height h, and pops them out alongside algo;
                            // doesn't maintain all nodes for algo's exec
                            // List (despite n - total) is only used to record traversed values to be
                            // returned, not within algo's actual complexity
                            // could do away with List by just printing out node.value()s

                            List<Integer> res = new ArrayList<>();
                            if (root == null)
                                return res;

                            TreeNode node = root;
                            java.util.Stack<TreeNode> stack = new Stack<>();
                            // todo: Use Java's in-built Data-type (Stack) in this case
                            // because Trees is the current study

                            // TODO: Alt loop in-order logic
                            while (node != null || !stack.isEmpty()) {
                                if (node != null) {
                                    stack.push(node);
                                    node = node.left();
                                } else {
                                    node = stack.pop();
                                    res.add((Integer) node.value());
                                    node = node.right();
                                }
                            }
                            return res;
                        }

                        public List<Integer> recurse(TreeNode root) {
                            List<Integer> res = new ArrayList<>();
                            if (root == null)
                                return res;

                            BiConsumer<TreeNode, List<Integer>>[] helper = new BiConsumer[1];
                            helper[0] = (n, r) -> { // no need to pass List<Int> by reference
                                if (n == null)
                                    return;
                                if (n.left() != null)
                                    helper[0].accept(n.left(), r);
                                r.add((Integer) n.value()); // can also work with res directly
                                if (n.right() != null)
                                    helper[0].accept(n.right(), r);
                            };

                            helper[0].accept(root, res);
                            return res;
                        }

                    }

                    // Pre-Order Traversal

                    public class PreOrder {

                        public List<Integer> iterate(TreeNode root) {
                            List<Integer> res = new ArrayList<>();
                            if (root == null)
                                return res;

                            TreeNode node = root;
                            Stack<TreeNode> stack = new Stack<>();

                            // TODO: Alt Stack - Iteration pre-order logic
                            while (node != null || !stack.isEmpty()) {
                                if (node != null) {
                                    res.add((Integer) node.value());
                                    stack.push(node);
                                    node = node.left();
                                } else {
                                    node = stack.pop();
                                    node = node.right();
                                }
                            }
                            return res;
                        }

                        public List<Integer> recurse(TreeNode root) {
                            List<Integer> res = new ArrayList<>();
                            if (root == null)
                                return res;

                            Consumer<TreeNode>[] helper = new Consumer[1];
                            helper[0] = (node) -> {
                                if (node == null)
                                    return;
                                res.add((Integer) node.value()); // can work with res directly
                                if (node.left() != null)
                                    helper[0].accept(node.left());
                                if (node.right() != null)
                                    helper[0].accept(node.right());
                            };

                            helper[0].accept(root);
                            return res;
                        }

                    }

                    // Post-Order Traversal

                    public class PostOrder {

                        public List<Integer> iterate(TreeNode root) {
                            List<Integer> res = new ArrayList<>();
                            if (root == null)
                                return res;

                            TreeNode node = root;
                            Stack<TreeNode> stack = new Stack<>();

                            // TODO: Alt loop post-order logic - .right() to .left()

                            while (node != null || !stack.isEmpty()) {
                                if (node != null) {
                                    res.add(0, (Integer) node.value()); // todo: NB: this time, .add(0, ..) (prepend) to
                                    // beginning of list
                                    stack.push(node);
                                    node = node.right();
                                } else {
                                    node = stack.pop();
                                    node = node.left();
                                }
                            }
                            return res;
                        }

                        public List<Integer> recurse(TreeNode root) {
                            List<Integer> res = new ArrayList<>();
                            if (root == null)
                                return res;

                            BiConsumer<TreeNode, List<Integer>>[] helper = new BiConsumer[1];
                            helper[0] = (n, r) -> {
                                if (n == null)
                                    return;
                                helper[0].accept(n.left(), r);
                                helper[0].accept(n.right(), r);
                                r.add((Integer) n.value());
                            };

                            helper[0].accept(root, res);
                            return res;
                        }

                    }

                    // * BFS Traversals

                    // Level-Order Traversal

                    public class LevelOrder {

                        public List<Integer> iterate(TreeNode root) {
                            List<Integer> res = new ArrayList<>();
                            if (root == null)
                                return res;

                            TreeNode node = null;
                            TreeNode[] list = { root }; // have to define the array object before being passed into
                            // Arrays.asList()
                            java.util.Queue<TreeNode> queue = new ArrayDeque<>(java.util.Arrays.asList(list));
                            /*
                             * // passing in { .. } primitive array value causes a syntax symbol error
                             * java.util.Arrays.asList(
                             * { root } // todo: forced-indentation for your memory
                             * )
                             * );
                             */
                            // or - queue.add(root) after init (not .enqueue() / .push())
                            // * NB: Used Java's in-built Queue class, because Trees is the current study

                            while (node != null || !queue.isEmpty()) {
                                node = queue.poll(); // not .dequeue() / .pop();
                                res.add((Integer) node.value());
                                // todo: find out other Level-Order methods from .py
                            }
                            return res;
                        }

                    }

                    // * Other Traversals

                    // Iterator Traversal - In-order

                    /*
                     * Q - Design an iterator over a BST with following rules:
                     * Elements are visited in ascending order (in-order traversal)
                     * hasNext() & next() queries run in O(1) time in average
                     */

                    public class Iterator {

                        TreeNode node;
                        Stack<TreeNode> stack;

                        public Iterator(TreeNode root) {
                            node = root;
                            stack = new Stack<>();
                        }

                        // check if there's a next smallest-value node
                        private boolean hasNext() {
                            return node != null || !stack.isEmpty();
                        }

                        // return the next smallest-value node
                        public int next() {
                            while (hasNext()) {
                                if (node != null) {
                                    stack.push(node);
                                    node = node.left();
                                } else {
                                    node = stack.pop();
                                    int res = (Integer) node.value(); // can declare this var in the loop because it's
                                    // being returned in this iteration
                                    node = node.right();
                                    return res;
                                }
                            }
                            return Integer.MIN_VALUE;
                        }

                        public void test() {
                            Iterator it = new Iterator(new TreeNode(0, null, null));
                            System.out.println(it.next());
                            System.out.println(it.next());
                            System.out.println(it.hasNext());
                            System.out.println(it.next());
                            System.out.println(it.next());
                            System.out.println(it.hasNext());
                        }

                    }

                    // Successor - In-order

                    /*
                     * Q - Given a BST and a node in it, find the in-order successor of that node
                     * The successor of a node n is the node with the smallest key greater than
                     * n.value()
                     */

                    public class Successor {

                        TreeNode node;
                        Stack<TreeNode> stack;
                        TreeNode successor;

                        // General Method
                        public TreeNode successor(TreeNode root, TreeNode n) {
                            if (root == null || n == null)
                                return null;

                            stack = new Stack<>();
                            successor = null;
                            boolean flag = false;

                            while (!stack.isEmpty() || node != null) {
                                if (node != null) {
                                    stack.push(node);
                                    node = node.left();
                                } else {
                                    node = stack.pop();
                                    if (node.value() == n.value())
                                        flag = true;
                                    else if (flag) {
                                        successor = node;
                                        break;
                                    }
                                    node = node.right();
                                }
                            }
                            return successor;
                        }

                        // Iteration Method
                        public TreeNode successor2(TreeNode root, TreeNode n) {

                            successor = null;

                            while (root != null && root.value() != n.value()) {
                                // todo: reminder for generic casting issues with Object value()
                                if ((Integer) n.value() < (int) root.value()) {
                                    successor = root;
                                    root = root.left();
                                } else
                                    root = root.right();
                            }

                            if (root == null)
                                return null;
                            if (root.right() == null)
                                return successor;

                            root = root.right();
                            while (root.left() != null)
                                root = root.left();

                            return root;
                        }

                        // Recursive Method
                        public TreeNode successor3(TreeNode root, TreeNode n) {
                            if (root == null || n == null)
                                return null;

                            // todo: reminder for generic casting issues with Object value()
                            if ((int) n.value() >= (Integer) root.value())
                                return successor3(root.right(), n);
                            else {
                                TreeNode left = successor3(root.left(), n);
                                return (left != null) ? left : root;
                            }

                        }

                    }

                }

                // TODO: Q - Given a non-empty BST and a target value, find the value in the BST
                // that is closest to the target

                public class ClosestBST {

                    public Double closestValue(TreeNode root, Double target) {
                        if (root == null)
                            return 0.0; // can't return -1 (valid closest return value)

                        BiFunction<TreeNode, Double, TreeNode>[] lowerBound = new BiFunction[1];
                        lowerBound[0] = (r, t) -> {
                            if (r == null)
                                return null;

                            if (t <= (Double) r.value())
                                return lowerBound[0].apply(r.left(), t);

                            // target > root.value()
                            TreeNode lowerNode = lowerBound[0].apply(r.right(), t);
                            if (lowerNode != null)
                                return lowerNode;

                            return r;
                        };

                        BiFunction<TreeNode, Double, TreeNode>[] upperBound = new BiFunction[1];
                        upperBound[0] = (r, t) -> {
                            if (r == null)
                                return null;

                            if (t > (Double) r.value())
                                return upperBound[0].apply(r.right(), t);

                            // target <= root.value()
                            TreeNode upperNode = upperBound[0].apply(r.left(), t);
                            if (upperNode != null)
                                return upperNode;

                            return r;
                        };

                        TreeNode lowerNode = lowerBound[0].apply(root, target);
                        TreeNode upperNode = upperBound[0].apply(root, target);

                        if (lowerNode == null)
                            return (Double) upperNode.value();
                        if (upperNode == null)
                            return (Double) lowerNode.value();

                        if (target - (Double) lowerNode.value() > (Double) upperNode.value() - target)
                            return (Double) upperNode.value();

                        return (Double) lowerNode.value();

                    }

                }

                // TODO: Q - Given a non-empty BST and a target value, find k values that are
                // closest to the target

                /*
                 * Given target value is a floating point, you can assume k is always valid (k
                 * <= total nodes)
                 * Guaranteed to have only 1 unique set of k values in BST that are closest to
                 * target
                 * Assume the BST is balanced
                 *
                 * Attempt solving in < O(n) t
                 *
                 * Perhaps O(k + log n) t / O(k log n) t ?
                 *
                 */

                public class ClosestBST2 {

                    // In-order - O(n) t ; O(n) s
                    // Best-case - O(k + log n) t ; O(log n) s (because Tree is Balanced)

                    public List<Integer> closestKValues(TreeNode root, double target, int k) {
                        List<Integer> values = new ArrayList<>();
                        if (k == 0 || root == null)
                            return values;

                        Stack<TreeNode> lowerStack = createStack(root, target);
                        Stack<TreeNode> upperStack = new Stack<>();

                        upperStack.addAll(lowerStack);

                        if (target < (double) lowerStack.peek().value())
                            moveLower(lowerStack);
                        else
                            moveUpper(upperStack);

                        for (int i = 0; i < k; i++) {
                            if (lowerStack.isEmpty() ||
                                    !upperStack.isEmpty() &&
                                            // todo: check arithmetic order without parentheses in this case
                                            target - (double) lowerStack.peek().value() > (double) upperStack.peek()
                                                    .value() - target) {
                                values.add((Integer) upperStack.peek().value());
                                moveUpper(upperStack);
                            } else {
                                values.add((Integer) lowerStack.peek().value());
                                moveLower(lowerStack);
                            }
                        }

                        return values;
                    }

                    // Store the nodes on the path from root to target (not including target node)
                    private Stack<TreeNode> createStack(TreeNode root, double target) {
                        Stack<TreeNode> stack = new Stack<>();

                        while (root != null) {
                            stack.push(root);

                            if (target < (double) root.value())
                                root = root.left();
                            else
                                root = root.right();
                        }

                        return stack;
                    }

                    // Successor
                    private void moveUpper(Stack<TreeNode> stack) {
                        TreeNode node = stack.peek();

                        if (node.right() == null) {
                            node = stack.pop();
                            while (!stack.isEmpty() &&
                                    stack.peek().right() == node)
                                node = stack.pop();
                            return;
                        }

                        node = node.right();

                        while (node != null) {
                            stack.push(node);
                            node = node.left();
                        }
                    }

                    private void moveLower(Stack<TreeNode> stack) {
                        TreeNode node = stack.peek();

                        if (node.left() == null) {
                            node = stack.pop();
                            while (!stack.isEmpty() &&
                                    stack.peek().left() == node)
                                node = stack.pop();
                            return;
                        }

                        node = node.left();

                        while (node != null) {
                            stack.push(node);
                            node = node.right();
                        }
                    }

                }

                /*
                 * // TODO: Other BST Problems
                 *
                 * - Insert into / Delete from BST
                 * - Morris Algo
                 */

            }

            // Tries

            // Graphs

            // Bits

        }

        ////////////////////////////////////////
        // Cracking Coding Interview Qs
        ////////////////////////////////////////

        // Arrays & Strings

        // ...

        ////////////////////////////////////////
        // LEETCODE
        ////////////////////////////////////////

        //

        ////////////////////////////////////////
        // CODESIGNAL
        ////////////////////////////////////////

        // ARCADE TESTS (increasing difficulty)

        // SAMPLE INTERVIEW QUESTIONS

        // A top secret message containing uppercase letters from 'A' to 'Z' has been
        // encoded as numbers using the following mapping:
        // You are an FBI agent and you need to determine the total number of ways that
        // the message can be decoded.
        // Since the answer could be very large, take it modulo 109 + 7.

        int mapDecoding(String message) {
            String M = message;
            int Z = 1000000007; // 10^9+7
            int L = M.length();
            if (L <= 0)
                return 1; // empty string -> 1 valid decoding
            if (M.charAt(0) == '0')
                return 0; // invalid

            int[] P = new int[L];

            P[0] = (M.charAt(L - 1) == '0') ? 0 : 1; // base case

            for (int i = 1; i < L; i++) {
                int iC = L - 1 - i; // number index
                int iP = iC + 1; // 'past' index

                char cC = M.charAt(iC);
                char cP = M.charAt(iP);
                int cPN = Character.getNumericValue(cP);

                // 00, 30, etc are not valid sequences -> cannot be decoded
                if ((cC != '1' && cC != '2') && cP == '0')
                    return 0;

                if (i == 1) {
                    // second char decoding
                    if ((cC == '1' && cP != '0') || (cC == '2' && cPN > 0 && cPN < 7)) {
                        P[i] = 2;
                    } else if (cC == '0') {
                        P[i] = 0;
                    } else {
                        P[i] = 1;
                    }

                    continue;
                }

                if (cC == '0') {
                    P[i] = 0; // reset
                } else if ((cC == '1') || (cC == '2' && cPN > 0 && cPN < 7)) {
                    P[i] = ((P[i - 1] % Z) + (P[i - 2] % Z)) % Z; // mod trick
                } else {
                    P[i] = P[i - 1];
                }
            }

            // System.out.println(Arrays.toString(P));

            return P[L - 1];
        }

        ////////////////////////////////////////
        // TEST CASES
        ////////////////////////////////////////

        public static void main(String[] args) {
            System.out.println("Hello, World!");
        }

    }

}
