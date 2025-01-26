package org.example.samples;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;


/*
 * // TODO: Change Filename from 'Data-Algo - Java.java' to 'DataAlgoJava.java'
 * if you decide to make entire class 'public'
 */


/*
 * // TODO
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

class DataAlgoJava {

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
                BiFunction<Integer[], Integer, Integer>[] rBinarySearch = new BiFunction[1];
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

                rBinarySearch[0].apply(ArrayUtils.toObject(arr), 7);
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

                BiFunction<Integer[], Integer, Integer> findLowerClosest = (a, x) -> {
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

                int lowerClosestIndex = findLowerClosest.apply(ArrayUtils.toObject(arr), num);
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

                BiFunction<Character[][], Integer, Boolean> rowHasBlackPixel = (im, row) -> {
                    for (int i = 0; i < im[row].length; i++) {
                        if (im[row][i] == '1')
                            return true;
                    }
                    return false;
                };

                BiFunction<Character[][], Integer, Boolean> colHasBlackPixel = (im, col) -> {
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
                    if (rowHasBlackPixel.apply(ArrayUtils.toObject(img), mid))
                        end = mid;
                    else
                        start = mid;
                }
                if (rowHasBlackPixel.apply(ArrayUtils.toObject(img), end))
                    res[0] = end;
                if (rowHasBlackPixel.apply(ArrayUtils.toObject(img), start))
                    res[0] = start;

                // now, find bottom ..
                start = x;
                end = img.length - 1;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (rowHasBlackPixel.apply(ArrayUtils.toObject(img), mid))
                        start = mid;
                    else
                        end = mid;
                }
                if (rowHasBlackPixel.apply(ArrayUtils.toObject(img), start))
                    res[1] = start;
                if (rowHasBlackPixel.apply(ArrayUtils.toObject(img), end))
                    res[1] = end;

                // find left
                start = 0;
                end = y;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (colHasBlackPixel.apply(ArrayUtils.toObject(img), mid))
                        end = mid;
                    else
                        start = mid;
                }
                if (colHasBlackPixel.apply(ArrayUtils.toObject(img), end))
                    res[2] = end;
                if (colHasBlackPixel.apply(ArrayUtils.toObject(img), start))
                    res[2] = start;

                // find right
                start = y;
                end = img[0].length - 1;
                while (start + 1 < end) {
                    mid = start + (end - start) / 2;
                    if (colHasBlackPixel.apply(ArrayUtils.toObject(img), mid))
                        start = mid;
                    else
                        end = mid;
                }
                if (colHasBlackPixel.apply(ArrayUtils.toObject(img), start))
                    res[3] = start;
                if (colHasBlackPixel.apply(ArrayUtils.toObject(img), end))
                    res[3] = end;

                // finally, return the rectangle's area
                return (res[1] - res[0] + 1) * (res[3] - res[2] + 1);

            }

            public int findPeakItem(int[] arr) {

                BiFunction<Integer[], Integer, Integer> isPeak = (a, i) -> {
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
                    peak = isPeak.apply(ArrayUtils.toObject(arr), mid);
                    if (peak == 0)
                        return mid;
                    if (peak == 1)
                        start = mid;
                    if (peak == -1)
                        end = mid;
                    else
                        start = mid;
                }
                if (isPeak.apply(ArrayUtils.toObject(arr), start) == 0)
                    return start;
                if (isPeak.apply(ArrayUtils.toObject(arr), end) == 0)
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
                BiFunction<Integer[], Double, Integer> count = (ls, d) -> {
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
                    if (count.apply(ArrayUtils.toObject(locations), mid) > k)
                        left = mid;
                    else
                        right = mid;
                }
                if (count.apply(ArrayUtils.toObject(locations), left) == k)
                    return left;
                return right;
            }

        }

        ////////////////////////////////////////
        // OTHER ALGO'S
        ////////////////////////////////////////

        public static class DataStructures {

            public DataStructures() {}

            // Arrays & Strings // ! TODO: FIX ALL - .stream / .contains / .remove / ..

            public static class ArraysStrings {

                public static class Arrays {

                    public Arrays() {

                        // ! primitiveType[], AbstractType[], Arrays.staticMethods, System.array-staticmethods, .. 
                        // ! List<AbstractType>, ArrayList<AbstractType>, Stream<AbstractType>, .. 
                        // TODO: interconnecting methods, conversions, & algo's used between all (main three 1st) of them

                    /*

                        - use primitive-types only when working with raw data & algo's (most-optimized algorithms only)
                        - use Abstract-Types only when working with suface data & user-centric systems & platforms (& their algo's too)
                        - use combinations between them when coding exploratively
                        - use Arrays.staticMethods with Abstract-Types mainly (& only)
                        - for primitive-types, consider System.array-staticmethods instead, if an Array.staticMethod is heavily required
                            - else: implement the method, using raw data & algo's

                        Tips:

                        - eg. 0 - x.length - primitiveType[] | x.length() - String | x.size() - AbstractListType | ..

                        - default - always use primitive-types by default; use AbstractTypes only when necessary (eg. as <TypeArguments>, or AbstractType.methods required);
                            - make conversions between primitive (at right/initial time) - abstract when methods / other Abstract-Type<Arguments> / .. are required

                                - eg. Integer A = a / Integer.valueOf(a) -> (int -> Integer) / new Integer(primInt) -> Integer / 
                                    
                                    ! Arrays.from(primIntArr) -> Integer[] - wrong ('Arrays.from' in .js not .java)
                                    ! Arrays.asList(primIntArr) -> cannot convert into Integer[] ; only List<Integer> 
                                    ! & that can also be passed as a constructor argument of ArrayList<>(Arrays.asList(...))
                                    * to convert int[] to Integer[], create a new Integer[] & manually assign values within a loop (or with Arrays.copyOf & System.arraycopy)

                                    ! or use the extra Apache Commons Lang's 'ArrayUtils' library's static methods too
                                    * or use other libs: Guava, ..

                                    ! Apache Commons Lang eg: ArrayUtils.toObject(primIntArr) -> Integer[]
                                    * Guava eg: Ints.asList(primIntArray).toArray(new Integer[0 or primIntArr.length]) -> Integer[]

                                    * main / other examples 

                                    Integer[] A = new Integer[a.length]; List<Integer>/ArrayList<Integer> A = new ArrayList<>(); for-loop to assign a[i] -> all A[i] / 

                                    new ArrayList<>(Arrays.asList(primIntArr)) -> ArrayList<Integer> / 

                                    Arrays.asList(primIntArr) / Arrays.asList( variadic int args ..) -> List<Integer> / 
                                    
                                    Arrays.stream(primIntArr) -> IntStream / 
                                    Arrays.stream(primIntArr).boxed().toArray(Integer[]::new) -> Integer[] / 

                                    Arrays.stream(primIntArr).boxed().collect(Collectors.toList()) -> List<Integer> / 
                                    Arrays.stream(primIntArr).boxed().collect(Collectors.toCollection(ArrayList::new)) -> ArrayList<Integer> / 

                                    Arrays.asList(primIntArr).stream().mapToObj(Integer::valueOf).collect(Collectors.toList()) -> List<Integer> / 
                                    Arrays.asList(primIntArr).stream().mapToObj(Integer::valueOf).collect(Collectors.toCollection(ArrayList::new)) -> ArrayList<Integer> / 


                                    new Integer[] { primIntArr[0], primIntArr[1], [find out how to spread primIntArr here] } / ... 

                            - whenever getting values from AbstractType.methods are required to be primitive - (cast-to-primitive-type) for end-usage
                        
                        - problem-solution (game) examples:
                        
                        - eg. 1 - convert input primitive types to abstract types, then convert back to output primitive types (sub-optimal)
                        - eg. 2 - only work with primitive types (if passed as input); use custom methods (or System.static-methods) wherever necessary (most-optimal)
                        - eg. 3 - only work with abstract types (if passed as input); use in-built methods where necessary (acceptable - on surface systems level)
                        - eg. 4 - convert input abstract types to primitive types, (play around with Abstract-static methods still), then return output primitive types (or convert back to abstract outputs) - 'just to prove a point'
                        - eg. 5 - play around with combo's of both of them - use Abstract-types' static methods (Arrays/Strings/System) wherever necessary (for more experience)
                        - eg. 6 - compare changes in Big O for each manipulation, in order to know most optimal options when working with whichever data-type / problem-solution

                        - eg. 0 again - x.length - O(1) t - primitiveType[] | x.length() - O(n~1) t - String | x.size() - O(n~1) t - AbstractListType | ..

                        ! TODO: confirm obj.length() / .size() does't already store a 'length / size' property in its implementation first; else would be O(1) t by default
                        
                        ! TODO: convert primitive-type array (int[]) to AbstractType array (new Integer[]) - eg. ArrayUtils.toObject(arr)

                        ..

                     */

                    }

                    // ! wrong implementation
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
                                * (negative ? -1 : 1);
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

                    /**
                     * Given an array nums. We define a running sum of an array as runningSum[i] = sum(nums[0]…nums[i]).
                     * Return the running sum of nums.
                     */

                    public int[] runningSums(int[] nums) { // O(n) t ; O(1) s
                        int[] sums = new int[nums.length];
                        int sum = 0, i = 0;
                        for (int num : nums) {
                            sum += num;
                            sums[i++] = sum;
                        }
                        return sums;
                    }

                    // using a hashmap (cannot use an extra array; index (as hashmap values) also needed)
                    public int[] twoSum(int[] nums, int target) { // O(n) t ; O(n) s
                        HashMap<Integer, Integer> map = new HashMap<>();
                        int d;
                        for (int i = 0; i < nums.length; i++) {
                            d = target - nums[i];
                            if (map.containsKey(d))
                                return new int[] { map.get(d), i }; // ! new / raw int-array value
                            map.put(nums[i], i);
                        }
                        return null;
                    }

                    // sorting & 2-pointer
                    public int[] twoSum1(int[] nums, int target) { // O(n log n + (n/2 ~ n)) t ; O(1) s

                        // nums.sort(); // ! NO .sort() on primitive-type arrays
                        Arrays.sort(nums); // sorts primitive-type array in-place
                        // or: Arrays.sort(nums, Comparator.reverseOrder()); - sort in reverse order
                        // or: nums = Arrays.stream(nums).sorted().toArray(); - convert to stream, sort, then convert back to array


                        int i = 0, j = nums.length - 1, s;
                        while (i < j) {
                            s = nums[i] + nums[j];
                            if (s == target) return new int[] { i, j };
                            else if (s < target) i++;
                            else j--;
                        }
                        return null;
                    }

                    public int[] twoSum2(int[] nums, int target) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #1
                    public int[][][] twoSum3(int[] nums, int target) { // O(n) t ; O(3n ~ n - only 1 map actually needed; extra 2 arrays only hold results) s

                        int[][] ips = new int[nums.length][2];
                        int[][] nps = new int[nums.length][2];
                        Map<Integer, Integer> map = new HashMap<>();

                        int c = 0; // count for 1st index of both matrices (ips & nps)
                        int d;

                        for (int i = 0; i < nums.length; i++) {
                            d = target - nums[i];
                            if (map.containsKey(d)) {
                                ips[c][0] = map.get(d);
                                ips[c][1] = i;
                                nps[c][0] = d;
                                nps[c][1] = nums[i];
                                c++;
                            } else map.put(nums[i], i);
                        }

                        return new int[][][] { ips, nps };
                    }

                    public boolean containsDuplicates(int[] arr) { // O(n) t ; O(n) s
                        if (arr.length < 2) return false;
                        HashMap<Integer, Integer> map = new HashMap();
                        for (int a : arr) {
                            if (map.containsKey(a)) return true;
                            else map.put(a, 1);
                        }
                        return false;
                    }

                    public boolean containsDuplicates1(int[] arr) { // O(n) t ; O(n) s
                        if (arr.length < 2) return false;
                        HashMap<Integer, Integer> map = new HashMap();
                        boolean res = false;
                        arr.stream().map((int a) -> {
                            if (map.containsKey(a)) res = true; // ! find out how to break out of .map() via callback-iteration
                            else map.put(a, 1);
                        });
                        return res; // ! only correct if .map() can be broken out from an iteration
                    }

                    // working with same input array
                    public boolean containsDuplicates2(int[] arr) { // O(n) t ; O(1) s
                        if (arr.length < 2) return false;
                        ArrayList<Integer> a = Arrays.asList(arr); // ! find out: convert arr to list/array-list
                        boolean res = false;
                        a.stream().map((int n, int i) -> {
                            a.remove(i); // ! confirm .remove at index
                            if (a.contains(n)) res = true; // ! confirm .contains
                            // ! find-out: how to break out of .map cb-iteration
                        });
                        res = true;
                    }

                    public char containsDuplicates3(char[] arr) { // O(n) t ; O(1) s
                        char c;
                        for (int i = 0; i < arr.length; i++) {
                            c = arr[i];
                            arr.remove(i);
                            if (arr.contains(c))
                                return true;
                        }
                        return false;
                    }

                    // sorting, then checking adjacent values
                    public boolean containsDuplicates4(int[] arr) { // O(n log n + (n/? ~ n)) t ; O(1) s
                        if (arr.length < 2) return false;
                        Arrays.sort(arr);
                        for (int i = 1; i < arr.length; i++)
                            if (arr[i - 1] == arr[i])
                                return true;
                        return false;
                    }

                    // 3rd-Party (Tutorial) - LC #217
                    public boolean containsDuplicates5(int[] arr) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #217
                    public boolean containsDuplicates6(int[] arr) { // O(n) t ; O(n) s
                        if (arr.length <= 1) return false;
                        HashMap<Integer, Boolean> map = new HashMap<>();
                        for (int num : arr) {
                            if (map.containsKey(num))
                                return true;
                            map.put(num, true);
                        }
                        return false;
                    }

                    // first unique character
                    public char firstNonRepeatingChar(char[] arr) { // O(2n ~ n) t ; O(n) s
                        HashMap<Character, Integer> map = new HashMap<>();
                        char c;
                        for (int i = 0; i < arr.length; i++) {
                            c = arr[i];
                            if (map.containsKey(c))
                                map.put(c, map.get(c) + 1);
                            else map.put(c, 1);
                        }
                        for (int i = 0; i < arr.length; i++) {
                            c = arr[i];
                            if (
                                    map.containsKey(c)
                                            && map.get(c) == 1
                            ) return c;
                        }
                        return '-';
                    }

                    public char firstNonRepeatingChar1(char[] arr) {
                        return null;
                    }

                    public char firstNonRepeatingChar2(char[] arr) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #387
                    public char firstNonRepeatingChar3(char[] arr) {
                        return null;
                    }

                    // first non-repeating character
                    public char firstUniqueChar(char[] arr) {
                        return null;
                    }

                    public char firstUniqueChar1(char[] arr) {
                        return null;
                    }

                    public char firstUniqueChar2(char[] arr) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #387
                    public char firstUniqueChar3(char[] arr) {
                        return null;
                    }

                    public int majorityElement(int[] arr) { // O(n) t ; O(n) s
                        HashMap<Integer, Integer> map = new HashMap<>();
                        int max, majority;
                        Arrays.asList(arr).stream().map(n -> {
                            if (map.containsKey(n)) {
                                const int count = map.get(n) + 1;
                                map.put(n, count);
                                if (count > max) {
                                    max = count;
                                    majority = n;
                                }
                            } else map.put(n, 1);
                        });
                        return majority;
                    }

                    public int majorityElement1(int[] arr) {
                        HashMap<Integer, Integer> map = new HashMap<>();
                        int max, majority, n;
                        for (int i = 0; i < arr.length; i++) {
                            n = arr[i];
                            if (map.containsKey(n)) {
                                const int count = map.get(n) + 1;
                                map.put(n, count);
                                if (count > max) {
                                    max = count;
                                    majority = n;
                                }
                            } else map.put(n, 1);
                        }
                        return majority;
                    }

                    public int majorityElement2(int[] arr) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #169
                    public int majorityElement3(int[] arr) { // O(n + m ~ 2n ~ n) t ; O(n) s

                        Map<Integer, Integer> map = new HashMap<>();

                        // 1st pass: keep count of items in arr in hashmap
                        for (int num : arr)
                            if (!map.containsKey(num))
                                map.put(num, 1);
                            else map.put(num, map.get(num) + 1);

                        // 2nd pass: for each item in map, return key of count which is > n/2
                        int majority = (int) Math.floor((double) arr.length / 2.0);
                        int majorityElement = arr[0];

                        for (Map.Entry entry : map.entrySet())
                            if ((int) entry.getValue() > majority)
                                majorityElement = (int) entry.getKey();

                        return majorityElement;
                    }

                    public int removeElement(int[] arr, int elem) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #27

                    @FunctionalInterface
                    interface NumSwaps2 {
                        void swap(int[] nums, int front, int end);
                    }

                    public int removeElement1(int[] arr, int elem) { // O(n) t ; O(1) s

                        NumSwaps2 swap = (nums, front, end) -> {
                            int tmp = nums[front];
                            nums[front] = nums[end];
                            nums[end] = tmp;
                        };

                        int nonValidLastIndex = 0, runner = 0;

                        while (runner < arr.length) {
                            if (arr[runner] != elem)
                                swap.swap(arr, nonValidLastIndex++, runner);
                            runner++;
                        }

                        return nonValidLastIndex;
                    }

                    public int[] removeElements(int[] arr, int[] elems) { // O(2n ~ n) t ; O(n ~ 1) s - extra list not actually required; ArrayList forcely used for it methods
                        ArrayList<Integer> nums = Arrays.asList(arr); // O(n) t & s
                        // ! convert elems to List<Integer> type, before converting to stream, to map through (waste of execution-time)
                        // * best to use a for-loop for primitive types
                        Arrays.asList(elems).stream().map(e -> { // O(n)
                            if (nums.contains(e)) // O(n~1)
                                nums.remove(nums.indexOf(e)); // O(2n~1)
                        });
                        return nums.toArray(new int[0]); // O(n~1)
                    }

                    // without ArrayList / List conversion of 2nd array
                    public int[] removeElements1(int[] arr, int[] elems) { // O(2n ~ n) t ; O(n ~ 1) s
                        ArrayList<Integer> nums = Arrays.asList(arr); // O(n) t & s
                        for (int e : elems) { // O(n)
                            if (nums.contains(e)) // O(n~1)
                                nums.remove(nums.indexOf(e)); // O(2n~1)
                        }
                        return nums.toArray(new int[0]); // O(n~1)
                    }

                    // without ArrayList / List at all
                    public int[] removeElements2(int[] arr, int[] elems) { // O(en ~ 1) t ; O(n ~ 1) s
                        // e - length of smaller 'elems' array | n = length of larger 'arr' array

                        // even if new int[] is used,
                        // only copying items from input array to this array
                        // should keep space-complexity constant,
                        // ! only if old array is garbage-collected immediately after
                        int[] newArr = new int[arr.length - 1]; // less 1 item to be removed

                        int i;
                        for (int e : elems) {

                            // class 'Arrays' regular static method for confirming & finding index from primitive arrays
                            // ! .binarySearch - used to confirm if arr contains e, & find its index
                            // * i = Arrays.binarySearch(arr, e); // O(logn) t
                            // * O(nlogn + logn ~ (n + 1)logn) t - might assume that this method sorts arr first, before searching through it
                            // ! BUT, it actually doesn't
                            /**
                             * In Java, the Arrays.binarySearch() method assumes that the array passed to it is sorted in ascending order.
                             * If the array is not sorted, the behavior of Arrays.binarySearch() is undefined.
                             * There is no guarantee that it will correctly find the element you are looking for, and it may return incorrect results.
                             *
                             * If the array is not sorted, the search may fail to find the element or return an incorrect index.
                             */

                            // ! find a constant-time [O(1) t] method of confirming e in arr, & finding its index

                            if (i > -1) {
                                // ! now, remove e from arr, at index i
                                System.arraycopy(arr, 0, newArr, 0, i); // O(i ~ 1) t // * i (exclusive) - index to remove
                                System.arraycopy(arr, i + 1, newArr, i, arr.length - 1 - i); // O(n - i ~ 1) t
                            }

                        }

                        return newArr;
                    }

                    public int[] removeElements3(int[] arr, int[] elems) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #
                    public int[] removeElements4(int[] arr, int[] elems) {
                        return null;
                    }

                    // ! WRONG - padding zeroes to end - infinite loop
                    public int[] moveZeroes(int[] arr) { // O(n^2 ~ n) t ; O(1) s
                        int n;
                        for (int i = 0; i < arr.length; i++) {
                            n = arr[i];
                            if (n == 0) {
                                // ! wrong - popping out from index will disrupt iterations
                                // * (next iteration will skip this index's replacement item)
                                // ! best to use a manual while-loop & only iterate (increment 'i') when necessary
                                Arrays.remove(i, arr); // also O(n ~ 1) // ! confirm: static.remove at index

                                // arr[arr.length] = 0; // ! wrong - appending 0s also turns to an infinite loop

                                // ! make a copy of new array with incremented length, then set
                                arr = Arrays.copyOf(arr, arr.length + 1); // O(n)
                                arr[arr.length - 1] = 0;
                                // ! TODO: using System.arraycopy(5 args: fromArr, startIndex, toArray, startIndex, endIndex)
                            }
                        }
                        return arr;
                        // ! use a while-loop (manual iteration-control) if the same input array must be edited
                    }

                    // ! TEST: most optimal - using a manual while loop (only iterate when necessary)
                    public int[] moveZeroes1(int[] arr) { // O(n) t ; O(1) s
                        if (arr.length < 1) return null;
                        int i = 0, c = 0, n;

                        while (i < arr.length) { // predicate still required in case there's already no 0 in array
                            n = arr[i];

                            if (n == 0) {
                                c++; // count number of zeroes to calculate new end of array
                                Arrays.remove(i, arr); // O(n ~ 1) // ! confirm: Arrays.static remove at index
                                // ! wrong: cannot 'set' at array-length to append - out of bounds error
                                // arr[arr.length] = 0; // O(1) if possible in .java
                                Arrays.append(0, arr); // O(n ~ 1) // ! confirm: Arrays.static append to primitive array
                                // ! confirm: Arrays.static method work with primitive-type arrays by reference-pointers still / in-place
                                // todo: return array values if 'by copy'
                            }

                            // ! always base-case check here before iterating, in-case of a loop-break
                            if (arr.length - 1 - i == c) // ! to ensure that padded 0s are accounted for; to break out of manual while-loop
                                break;

                            // ! only move on to next iteration if n != 0; else continue with this iteration since 0 has been replaced by next item already
                            if (n != 0) i++;
                        }

                        return arr;
                    }

                    // ! TEST: using a manual while loop - working with Abstract-Types only
                    // * simplest & most optimal so far - // ! if tests pass
                    public int[] moveZeroes2(int[] arr) { // O(2n ~ n) t ; O(1) s
                        if (arr.length < 1) return null;

                        List<Integer> a = Arrays.asList(arr); // O(n) to instantiate from primitive to abstract // ! confirm: for sure
                        Integer i = 0, c = 0, n;

                        while (i < arr.length) { // a.length(), but still using primitive arr.length a bit faster
                            n = a.get(i); // O(n~1)
                            if (n == 0) {
                                if (arr.length - i <= c) break; // ! should be '==' but added <= in case of length-subtraction errors - Test & confirm
                                c++;
                                a.remove(i); // O(n~1)
                                a.add(0); // O(n~1)
                            } else i++;
                        }

                        return a.toArray(new int[a.size()]); // O(n~1) // ! confirm: how to convert abstract-type List to primitive-type array
                    }

                    // ! optimal - using an extra array
                    public int[] moveZeroes3(int[] arr) { // O(n) t ; O(n) s
                        List<Integer> a = Arrays.asList(arr);
                        List<Integer> z = new ArrayList<>();
                        Integer n;

                        for (int i = 0; i < arr.length; i++) {
                            n = a.get(i);
                            if (n == 0) {
                                a.remove(i);
                                z.add(0);
                            }
                        }

                        a.add(z); // ! confirm: concatenating List to List
                        // or: for (Integer n : z) { a.add(n); } // ! confirm: for-each loop on List<T> to T

                        return a.toArray(new int[a.size()]); // O(n~1) // ! confirm: how to convert abstract-type List to primitive-type array
                    }

                    // ! more optimal - only count zeroes (no extra array) // ! using primitive-types only
                    public int[] moveZeroes4(int[] arr) {

                        // ! since we're using primitive-types only, also avoid using Consumer-Interface in production
                        // * move this method outside
                        Consumer<Integer> remove = i  -> {

                            // TODO: Implement custom option

                        };

                        // ! Sample primitive-array remove-methods, with Arrays

                        Consumer<Integer> remove1 = i -> { // ! 2nd argument with default value - a = arr ?
                            int[] a = arr;
                            int indexToRemove = i; // Index to remove

                            // Check if index is valid
                            if (indexToRemove < 0 || indexToRemove >= a.length) {
                                throw new IndexOutOfBoundsException("Index is out of bounds");
                            }

                            // Using Arrays.copyOf() // ! not optimal
                            // int[] newArr = Arrays.copyOf(a, a.length - 1); // ! length - 1 due to removal
                            // ! but then, could not copy a's items to newArr due to an out-of-bounds exception

                            int[] newArr = new int[a.length - 1]; // ! so rather required to instantiate a new and empty array manually
                            System.arraycopy(a, 0, newArr, 0, indexToRemove); // ! then copy arr's items (from 0 to index to remove) to newArr
                            System.arraycopy(a, indexToRemove + 1, newArr, indexToRemove, a.length - indexToRemove - 1);
                            // ! then copy arr's remaining items from next index (from the removed 1) to the end

                            // Print the resulting array
                            System.out.println("Result using Arrays.copyOf(): " + Arrays.toString(newArr));
                        };

                        Consumer<Integer> remove2 = i -> {
                            int[] a = arr;
                            int indexToRemove = i; // Index to remove

                            // Check if index is valid
                            if (indexToRemove < 0 || indexToRemove >= a.length) {
                                throw new IndexOutOfBoundsException("Index is out of bounds");
                            }

                            // Using System.arraycopy()
                            int[] newArr = new int[a.length - 1];
                            System.arraycopy(a, 0, newArr, 0, indexToRemove);
                            System.arraycopy(a, indexToRemove + 1, newArr, indexToRemove, a.length - indexToRemove - 1);

                            // Print the resulting array
                            System.out.println("Result using System.arraycopy(): " + Arrays.toString(newArr));
                        };

                        /**

                         int c = 0, n;

                         // * Loop Option 1:
                         for (int i = 0; i < arr.length; i++) {
                         n = arr[i];
                         if (n == 0) {
                         c++;
                         remove.accept(i);
                         }
                         // ! this still needs to be optimized due to for-loop auto-iteration
                         // ! or: purposely decrement iterator, for next iteration
                         i--; // ! sub-optimal
                         }

                         */

                        // * Loop Option 2:
                        int i = 0, c = 0, n;

                        while (i < arr.length) {
                            n = arr[i];
                            if (n == 0) {
                                c++;
                                // exec 'remove' lambda (Consumer Functional-Interface)
                                remove1.accept(i); // ! TODO: Test: remove, remove1, & remove2
                            } else i++;
                        }

                        // * now concatenate padded zeroes, based on count

                        // create a new primitive int-array, with enough size to add padded 0s
                        int[] newArr = Arrays.copyOf(arr, arr.length + c);
                        // ! Arrays.copyOf(arr, arr.length + appendArray.length):
                        // ! Creates a new array newArr with a length equal to the sum of the lengths of arr and appendArray.
                        // ! Copies all elements from arr to newArr.

                        // * int[] newArr = new int[arr.length + c]; // ! also workable, but wouldn't copy arr's items into newArray
                        // * System.arraycopy(arr, 0, newArr, 0, arr.length); // ! also workable, and would also copy arr's items into newArray

                        // create new array with padded 0s
                        int[] paddedZeroes = new int[c];
                        // ! In Java, when you create a new array of primitive integers (int), such as int[] appendArray = new int[5];,
                        // ! the array is automatically initialized with default values. For int arrays, the default value is 0.

                        // now, copy padded-zeroes to the empty padded space in new-array
                        System.arraycopy(paddedZeroes, 0, newArr, arr.length, paddedZeroes.length);
                        // ! System.arraycopy(appendArray, 0, newArr, arr.length, appendArray.length):
                        // ! Copies elements from appendArray to newArr starting from index arr.length.
                        /**
                         * appendArray: The source array to copy from.
                         * 0: The starting index in appendArray from which to start copying.
                         * newArr: The destination array where elements are copied to.
                         * arr.length: The starting index in newArr where elements are copied to.
                         * appendArray.length: The number of elements to copy.
                         */

                        /* // ! this custom way also workable, in place of System.arraycopy
                        int i = arr.length;
                        for (int zero : paddedZeroes) {
                            newArr[i] = zero; i++;
                        }
                        */

                        return newArr;
                    }

                    // ! ?
                    public int[] moveZeroes5(int[] arr) {
                        return null;
                    }

                    // ! ?
                    public int[] moveZeroes6(int[] arr) {
                        return null;
                    }

                    // ! BEST (& Confirmed) - Iterative Swaps - iteratively reset only counting numbers, then pad the remaining space with 0s
                    public int[] moveZeroes7(int[] arr) { // O(2n ~ n) t ; O(1) s
                        // * 2 consecutive loops used, but much more closer to a 1-loop O(n) t (2nd loop with a much shorter length - 0 count
                        int i = 0, c = 0;
                        for (int n : arr)
                            if (n != 0) {
                                arr[i] = n;
                                i++;
                            } else c++; // * count number of 0s
                        for (int n : new int[c]) {
                            // ! i already a step ahead, due to last increment in previous loop's last iteration
                            arr[i++] = 0; // ! 'n' also 0, so can be used
                            // in Java, and empty array with a given length - init'd with 0s by default
                        }
                        return arr;
                    }

                    // ! BETTER - Iterative Swaps - without 0 count; just continue iteration until array's length
                    public int[] moveZeroes8(int[] arr) {
                        int i = 0;
                        for (int n : arr)
                            if (n != 0) {
                                arr[i] = n;
                                i++;
                            }
                        for (int j = i; j < arr.length; j++)
                            arr[j] = 0;
                        return arr;
                    }

                    // 3rd-Party (Tutorial) - LC #283 - using Iterative Swaps - while loop

                    @FunctionalInterface
                    interface NumSwaps1 {
                        void swap(int[] nums, int i, int j);
                    }

                    public int[] moveZeroes9(int[] arr) { // O(n) t ; O(1) s
                        if (arr.length <= 1) return arr;

                        NumSwaps1 swap = (nums, i, j) -> {
                            int tmp = nums[i];
                            nums[i] = nums[j];
                            nums[j] = tmp;
                        }; // todo: confirm if swap works with nums by reference, if it's a primitive array (not abstract-type)
                        // * "it shouldn't" so this could be wrong - check

                        int nonZeroLastIndex = 0, runner = 0;

                        while (runner < arr.length) {
                            if (arr[runner] != 0)
                                swap.swap(arr, nonZeroLastIndex++, runner);
                            runner++;
                        }

                        return arr;
                    }

                    // finding out multiple intersections (repeating intersections added)
                    public int[] intersections(int[] a1, int[] a2) { // O(n) t ; O(1) s - extra data-structure only used as result; not used for the algo's implementation itself (except for checking !.contains - which is insignificant)
                        return null;
                    }

                    public int[] intersections1(int[] a1, int[] a2) { // O() t ; O() s
                        return null;
                    }

                    // if different array-lengths, iterate through shorter array; append unique intersections
                    public int[] intersections2(int[] a1, int[] a2) { // O() t ; O() s
                        List<Integer> ints = new ArrayList<>(); // * confirm: primitive int-array instead
                        for (int n : a1.length > a2.length ? a2 : a1) { // O(n)
                            if ( // ! TODO: use a better method from 'Arrays' to check: array contains value
                                // ! .binarySearch sometimes returns -1 when array not sorted (even if value exists)
                                    Arrays.binarySearch(a1, n) >= 0
                                            && Arrays.binarySearch(a1, n) >= 0
                                            && !ints.contains(n)
                            ) ints.add(n); // O(n~1)
                        }
                        return ints.toArray(new int[ints.size()]); // O(n~1)
                        // ! confirm: .toArray with dummy primitive argument
                        // * whether list.size() / 0 is required for primitive-array's length
                    }

                    public int[] intersections3(int[] a1, int[] a2) { // O() t ; O() s
                        return null;
                    }

                    // if same array-lengths, iterate through 1 array, check its items in the other
                    public int[] intersections4(int[] a1, int[] a2) { // O() t ; O() s
                        return null;
                    }

                    public int[] intersections5(int[] a1, int[] a2) { // O() t ; O() s
                        return null;
                    }

                    // if same array-lengths, iterate through 1 array, using its indices to access the other, in constant time
                    public int[] intersections6(int[] a1, int[] a2) { // O() t ; O() s
                        return null;
                    }

                    public int[] intersections7(int[] a1, int[] a2) { // O() t ; O() s
                        return null;
                    }

                    // using a hashmap
                    public int[] intersections8(int[] a1, int[] a2) { // O() t ; O() s
                        return null;
                    }

                    public int[] intersections9(int[] a1, int[] a2) { // O() t ; O() s
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #349
                    public int[] intersections10(int[] a1, int[] a2) {
                        // O( n1 + n2 + m ~ 2n1 + n2 or 2n2 + n1 (since length of m should be length of shorter array; which should've been iterated through 1st)
                        // ~ 3n ~ n (if n = length of shorter array, with all distinct elements - so hashmap length == shorter array length) = n ) t ;
                        // O( m + i ~ m (i - intersections only hold results) ~ n (length of shorter array == length of m) = n ) s

                        if (a1.length == 0 || a2.length == 0) return null;
                        Map<Integer, Integer> m = new HashMap<>();

                        // 1st pass: add items in a1 to hashmap
                        // ! best to add items from the shorter array (less time & space)
                        // ! TODO: find shorter array between a1 & a2 for 1st pass

                        for (int n : a1)
                            if (!m.containsKey(n))
                                m.put(n, 1);

                        // 2nd pass: for each item in a1, mark ones that exist in hashmap as 0
                        int numInts = 0;
                        for (int n : a2)
                            if (m.containsKey(n) && m.get(n) == 1) {
                                m.put(n, 0);
                                numInts++;
                            }

                        // 3rd pass: loop through hashmap; if map[k, v]'s v = 0, then add to intersections array
                        int[] intersections = new int[numInts]; // ! instantiating exact space for exact number of intersections
                        int i = 0;
                        for (Map.Entry entry : m.entrySet())
                            if ((int) entry.getValue() == 0) {
                                intersections[i] = (int) entry.getKey();
                                i++;
                            }

                        return intersections;
                    }

                    // 3rd-Party (Tutorial) - LC #349
                    public int[] intersections11(int[] a1, int[] a2) { // O() t ; O() s
                        return null;
                    }

                    // Minimum Domino Rotations

                    @FunctionalInterface
                    interface NumSwaps {
                        int swap(int num, int[] a, int[] b);
                    }

                    // TODO: Test
                    int minimumDominoRotations(int[] a, int[] b) { // O(n) t ; O(1) s

                        NumSwaps numSwaps = (num, a1, b1) -> {
                            int nSwaps = 0;
                            for (int i = 0; i < a1.length; i++)
                                if (a1[i] != num && b1[i] != num)
                                    return Integer.MAX_VALUE;
                                else if (a1[i] != num)
                                    nSwaps++;
                            return nSwaps;
                        };

                        int minSwaps = Math.min(
                                numSwaps.swap(a[0], a, b),
                                numSwaps.swap(b[0], a, b)
                        );

                        minSwaps = Math.min(minSwaps, numSwaps.swap(a[0], b, a));
                        minSwaps = Math.min(minSwaps, numSwaps.swap(b[0], a, b));

                        return minSwaps == Integer.MAX_VALUE ? -1 : minSwaps;
                    }

                    int maximumPointsFromCards(int[] points, int k) {
                        int sum = 0, max = 0;
                        int n = points.length - 1;
                        int[] left = new int[k + 1];
                        int[] right = new int[k + 1];
                        left[0] = 0; right[0] = 0;

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

                    /*
                        ! TWO-POINTER ALGO'S
                        - same direction 2-pointers
                        - different direction 2-pointers
                            - reverse / 2-sum / partition Q types
                        - almost all 2-sum related Qs
                        - partition Qs
                            - quick select
                            - divided into 2 / 3 parts
                        - linked-list cycle Qs
                        - other sorting algo's
                            - quick & merge sort algo's
                    */

                    public class SortInteger {

                        /**
                         * @param A: an integer array
                         * @return: void
                         */
                        public void sortIntegers(int[] A) {
                            if (A == null || A.length == 0)
                                return;

                            quickSort(A, 0, A.length - 1);
                            mergeSort(A, 0, A.length - 1);
                        }

                        private void quickSort(int[] A, int start, int end) { // O(n log n) / O(n ^ 2) t | O(log n) s
                            if (start >= end) return;

                            int left = start, right = end;
                            int pivot = A[left + (right - left) / 2];
                            int temp;

                            while (left <= right) {
                                while (left <= right && A[left] < pivot)
                                    left++;
                                while (left <= right && A[right] > pivot)
                                    right--;
                                if (left <= right) {
                                    temp = A[left];
                                    A[left] = A[right];
                                    A[right] = temp;
                                    left++; right--;
                                }
                            }

                            // recurse
                            quickSort(A, start, right);
                            quickSort(A, left, end);
                        }

                        private void mergeSort(int[] A, int start, int end) { // O(n log n) t | O(n + log n) ~ O(n) s
                            if (start >= end) return;

                            int[] tmp = new int[A.length]; // * extra space-usage

                            // divide
                            int mid = start + (end - start) / 2;
                            mergeSort(A, start, mid);
                            mergeSort(A, mid + 1, end);

                            // merge (conquer)
                            int index = start;
                            int leftStart = start;
                            int rightStart = mid + 1;

                            while (leftStart <= mid && rightStart <= end) {
                                // increment values after access; all 3 start from exact beginnings
                                if (A[leftStart] <= A[rightStart])
                                    tmp[index++] = A[leftStart++];
                                else tmp[index++] = A[rightStart++]; // * remember this 'very good' coding
                            }

                            while (leftStart <= mid)
                                tmp[index++] = A[leftStart++];

                            while (rightStart <= end)
                                tmp[index++] = A[rightStart++];

                            for (int i = start; i <= end; i++) {
                                A[i] = tmp[i];

                                // TODO: ?

                            }

                            // TODO: ?

                            // * return A; - not required here; A sorted in-place
                        }

                    }

                    public class MoveZeroes {

                        /** // * Move Zeroes
                         *
                         * Given an array nums, write a function to move all 0's to the end of it
                         * while maintaining the relative order of the non-zero elements.
                         * 1. without making a copy of the array
                         * 2. minimize the total number of operations
                         *
                         */

                        /**
                         * @param A: an integer array
                         * @return: void
                         */
                        public void moveZeroes(int[] A) {
                            int left = 0, right = 0;

                            while (right < A.length) {
                                if (A[right] != 0) {
                                    if (left != right)
                                        A[left] = A[right];
                                    left++;
                                }
                                right++;
                            }

                            while (left < A.length) {
                                if (A[left] != 0)
                                    A[left] = 0;
                                left++;
                            }
                        }
                    }

                    public class TwoSum {

                        /**
                         * Design and implement a 2-Sum class.
                         * It should support the following operations: add & find.
                         * add - Add the number to an internal data structure.
                         * find - Find if there exists any pair of numbers which sum is equal to the value.
                         */

                        public class DataStructure {

                            private Map<Integer, Integer> counter;

                            public DataStructure() {
                                counter = new HashMap<Integer, Integer>();
                            }

                            // Add the number to an internal data structure
                            public void add(int number) {
                                counter.put(number, counter.getOrDefault(number, 0) + 1);
                            }

                            // FInd if there exists any pair of numbers which sum is equal to the value
                            public boolean find(int value) {
                                int num2, desiredCount;
                                for (Integer num1 : counter.keySet()) {
                                    num2 = value - num1;
                                    desiredCount = num1 == num2 ? 2 : 1;
                                    if (counter.getOrDefault(num2, 0) >= desiredCount)
                                        return true;
                                }
                                return false;
                            }

                        }

                        public void test() {
                            DataStructure obj = new DataStructure();
                            obj.add(7); obj.add(5); obj.add(7); obj.add(5);
                            obj.find(7);
                        }

                        public class TwoSum_InputArraySorted {

                            /** // * 2-Sum with input array sorted
                             *
                             * Given an array of integers that is already sorted in ascending order,
                             * find 2 numbers such that they add up to a specific target number.
                             * the function 2-sum should return indices of the 2 numbers such that they add up to the target,
                             * where index i1 must be less than i2.
                             *
                             * Constraint: returned answers (both i1 & i2) cannot be zero-based
                             *
                             */

                            /**
                             * @param nums: array of integers
                             * @param target: target = nums[i1] + nums[i2]
                             * @return: [i1 + 1, i2 + 1] (with i1 < i2)
                             */
                            public int[] twoSum(int[] nums, int target) {
                                if (nums == null || nums.length < 2)
                                    return new int[0];

                                int left = 0, right = nums.length - 1;

                                while (left < right) {
                                    if (nums[left] + nums[right] < target)
                                        left++;
                                    if (nums[left] + nums[right] > target)
                                        right--;
                                    if (nums[left] + nums[right] == target)
                                        return new int[] {
                                                left + 1,
                                                right + 1
                                        };
                                }

                                return new int[0];
                            }

                        }

                        public class TwoSumLessThanK {

                            /**
                             * Given an array A of integers and integer K,
                             * find maximum sum S such that there exists i < j
                             * with A[i] + A[j] = S & S <= K
                             * if no i, j exist satisfying this equation, return -1
                             */

                            public int twoSum(int[] A, int K) {
                                int max = -1; Arrays.sort(A);
                                int left = 0, right = A.length - 1;

                                while (left < right) {
                                    if (A[left] + A[right] < K) {
                                        max = Math.max(max, A[left] + A[right]);
                                        left++;
                                    } else right--;
                                }

                                return max;
                            }

                        }

                    }

                    public class ThreeSum {

                        /**
                         * Given an array A of n integers, are there elements a, b, c in A such that:
                         * a + b + c = 0 ? Find all unique triplets in the array which give the sum of zero.
                         * Elements in a triplet (a, b, c) must be in non-descending order.
                         * (i.e. a <= b <= c) The solution set must not contain duplicate triplets.
                         */

                        /**
                         * @param nums: array of n integers
                         * @return: List-matrix of integers - all unique triplets in @param 'nums' which give the sum of zero
                         */
                        public List<List<Integer>> threeSum(int[] nums) {
                            Arrays.sort(nums); // ! confirm: Arrays.sort static method - argument passed by reference - on AbstractTypes only ? primitiveTypes too ?
                            List<List<Integer>> res = new ArrayList<>();

                            for (int i = 0; i < nums.length; i++) {
                                if (i != 0 && nums[i] == nums[i - 1])
                                    continue;
                                findTwoSum(nums, i, res); // 'res' passed in by reference
                            }

                            return res;
                        }

                        private void findTwoSum(int[] nums, int index, List<List<Integer>> res) {
                            int left = index + 1, right = nums.length - 1;
                            int target = -nums[index];
                            int twoSum;
                            List<Integer> triplets;

                            while (left < right) {
                                twoSum = nums[left] + nums[right];
                                if (twoSum < target) left++;
                                else if (twoSum > target) right--;
                                else {
                                    triplets = new ArrayList<>();
                                    triplets.add(nums[index]);
                                    triplets.add(nums[left]);
                                    triplets.add(nums[right]);
                                    res.add(triplets);
                                    left++; right--;
                                    while (
                                            left < right
                                                    && nums[left] == nums[left - 1]
                                    ) left++;
                                }
                            }
                        }

                        // TODO: 3-Sum - smaller & 3-sum - closest

                    }


                    // TODO: 4-Sum


                    public class ValidTriangleNumber {

                        /**
                         * Given an array that consists of non-negative integers,
                         * count the number of triplets chosen from the array
                         * that can make triangles if we take them as side lengths of a triangle.
                         */

                        /**
                         * @param nums: array of integers
                         * @return: number of triplets chosen from the array, that can make triangles
                         */
                        public int triangleNumber(int[] nums) {
                            Arrays.sort(nums); // ! confirm: Arrays.sort static method - argument passed by reference - on AbstractTypes only ? primitiveTypes too ?
                            int count = 0, n = nums.length, i, l, r;

                            for (i = n - 1; i >= 2; i--) {
                                l = 0; r = i - 1;
                                while (l < r) {
                                    if (nums[l] + nums[r] > nums[i]) {
                                        count += r - l;
                                        r--;
                                    } else l++;
                                }
                            }

                            return count;
                        }

                    }

                    public class Partition {

                        public class PartitionArray_DisjointIntervals {

                            /**
                             * Given an array A, partition it into 2 (contiguous) sub-arrays left & right
                             * so that:
                             *      left & right sub-arrays are non-empty
                             *      every element in the left sub-array is less than or equal to every element in the right
                             *      left has the smallest possible size
                             *
                             * return the length of left after such a partitioning
                             * NB: it is guaranteed that such a partitioning exists
                             */

                            public int partition(int[] A) {
                                int N = A.length;
                                int[] maxLeft = new int[N];
                                int[] minRight = new int[N];
                                int m = A[0];

                                for (int i = 0; i < N; ++i) {
                                    m = Math.max(m, A[i]);
                                    maxLeft[i] = m;
                                }

                                m = A[N - 1];

                                for (int i = N - 1; i >= 0; --i) {
                                    m = Math.min(m, A[i]);
                                    minRight[i] = m;
                                }

                                for (int i = 1; i < N; ++i) {
                                    if (maxLeft[i - 1] <= minRight[i])
                                        return i;
                                }

                                return -1;
                            }

                        }

                    }

                    public class KthLargestElement {

                        /**
                         * Find K-th largest element in an array
                         *
                         * - Base on heap: O(n log n + k)
                         * - Sort the Array: O(n log n)
                         * - Quick Select: O(n) t | O(1) s on average-case
                         */

                        /**
                         * @param n: integer
                         * @param nums: array of integers
                         * @return: kth largest element
                         */
                        public int kthLargestElement(int k, int[] nums) {
                            if (nums == null || nums.length == 0)
                                return -1;
                            if (k <= 0 || k > nums.length)
                                return -1;
                            return quickSelect(nums, 0, nums.length - 1, k);
                        }

                        private int quickSelect(int[] nums, int start, int end, int k) {
                            if (start == end) return nums[start];

                            int left = start, right = end,
                                    mid = left + (right - left) / 2,
                                    pivot = nums[mid], tmp;

                            // from large to small
                            while (left <= right) {
                                while (left <= right && nums[left] > pivot)
                                    left++;
                                while (left <= right && nums[right] < pivot)
                                    right--;
                                if (left <= right) {
                                    tmp = nums[left];
                                    nums[left] = nums[right];
                                    nums[right] = tmp;
                                    left++; right--;
                                }
                            }

                            if (k <= right - start + 1)
                                return quickSelect(nums, start, right, k);
                            if (k > left - start)
                                return quickSelect(nums, left, end, k - left + start);

                            return nums[right + 1];
                        }

                    }

                    public class SortArrayByParity {

                        /**
                         * Given an array A of non-negative integers, return an array consisting of all th even elements of A,
                         * followed by all the odd elements of A.
                         *
                         * NB: Can return any result array that satisfies the condition.
                         */

                        public int[] sortArray(int[] A) {
                            int left = 0, right = A.length - 1, tmp;

                            while (left <= right) {
                                if (A[left] % 2 > A[right] % 2) {
                                    tmp = A[left];
                                    A[left] = A[right];
                                    A[right] = tmp;
                                }
                                if (A[left] % 2 == 0)
                                    left++;
                                if (A[right] % 2 == 1)
                                    right--;
                            }

                            return A;
                        }

                    }

                    public class SortColors {

                        /**
                         * Given an array nums with n objects colored red, white, or blue.
                         * sort them in-place so that objects of the same color are adjacent,
                         * with the colors in the order red, white, & blue.
                         *
                         * NB: can use the integers 0, 1, & 2 to represent the color red, white, & blue, respectively.
                         *
                         * Constraints:
                         *      - without using the the Array.sort method
                         *      - 1-pass algo in O(1) (constant) space only
                         *
                         * - sort array - O(n log n) t
                         * - count the numbers 0, 1, 2, two-pass
                         * - 2-pointers; O(n) t | O(1) s
                         */

                        /**
                         * @param nums: list of integers (0, 1, or 2)
                         * @return: void
                         */

                        /*
                            left: 0/1 border, right: 1/2 border
                            [0, left) all 0s,
                            [left, right] all 1s,
                            (right, end] all 2s
                        */

                        public void sortColors(int[] nums) {
                            int left = 0, right = nums.length - 1,
                                    mid = 0;

                            while (mid <= right) {
                                if (nums[mid] == 0) {
                                    swap(nums, mid, left);
                                    mid++; left++;
                                } else if (nums[mid] == 2) {
                                    swap(nums, mid, right);
                                    right--;
                                } else mid++;
                            }
                        }

                        private void swap(int[] a, int i, int j) {
                            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
                        }

                        /**
                         * what if there are k different colors?
                         *
                         * Given an array of n objects with k different colors (numbered from 1 to k),
                         * sort them so that objects of the same color are adjacent,
                         * with the colors in the order 1, 2, ... , k.
                         *
                         * NB: can use Quick Sort - O(n log k) t - k = number of different colors
                         * can also either use Rainbow or Counting Sort algo's
                         */

                        /**
                         * @param colors: list of integers
                         * @param k: integer
                         * @return: void
                         */

                        public void sortColors2(int[] colors, int k) {
                            if (colors == null || colors.length == 0)
                                return;
                            rainbowSort(colors, 0, colors.length - 1, 1, k);
                        }

                        private void rainbowSort(int[] arr, int left, int right, int from, int to) {
                            if (from == to) return;
                            if (left >= right) return;

                            int mid = (from + to) / 2;
                            int l = left, r = right, tmp;

                            while (l <= r) {
                                while (l <= r && arr[l] <= mid) l++;
                                while (l <= r && arr[r] > mid) r--;
                                if (l <= r) {
                                    tmp = arr[l];
                                    arr[l] = arr[r];
                                    arr[r] = tmp;
                                    l++; r--;
                                }
                            }

                            rainbowSort(arr, left, r, from, mid);
                            rainbowSort(arr, l, right, mid + 1, to);
                        }

                        // TODO: Other Sorting Algo's that can be used here
                        // Quick, Counting Sorting Algo's ; & Pancake, Sleep, Spaghetti, Bogo Sorting Algo's

                    }

                }

                public static class Strings {

                    public Strings() {

                        // ! String, StringBuilder, Strings.staticMethods, char[], Character[] ; List<String>, ArrayList<String>, Stream<String>, ..
                        // TODO: interconnecting methods, conversions, & algo's used between all (main 5 1st) of them

                        /*

                         * ..

                         */

                        /*

                            String str = "orange";

                            * Convert String to char[]
                            char[] charArray = str.toCharArray();

                            * Convert char[] to String
                            String str = new String(charArray);
                            String str1 = String.valueOf(charArray);
                            String str2 = new StringBuilder().append(charArray).toString();
                            ! confirm - String str3 = charArray.toString();

                            * Convert char[] to Character[]
                            Character[] charObjectArray = new Character[charArray.length];
                            for (int i = 0; i < charArray.length; i++) {
                                charObjectArray[i] = charArray[i];
                            }

                            * Convert Character[] to List<Character> with a manual loop
                            List<Character> charList = new ArrayList<>();
                            for (Character c : charObjectArray) {
                                charList.add(c);
                            }

                            * Convert String to List<Character> using streams
                            List<Character> charList = str.chars() // IntStream
                                .mapToObj(c -> (char) c)       // Stream<Character>
                                .collect(Collectors.toList()); // Collect to List<Character>

                            * Convert String to List<Character> using Apache Commons Lang
                            Character[] charObjectArray = ArrayUtils.toObject(str.toCharArray());
                            List<Character> charList = Arrays.asList(charObjectArray);

                            * Convert String to List<Character> using StringUtils and ArrayUtils
                            Character[] charObjectArray = ArrayUtils.toObject(StringUtils.toCharArray(str));
                            List<Character> charList = Arrays.asList(charObjectArray);

                            * Convert String to List<Character> using StringUtils and Streams
                            List<Character> charList = StringUtils
                                .toCharArray(str)        // char[]
                                .stream()                // Stream<Character>
                                .collect(Collectors.toList()); // Collect to List<Character>
                            
                            * Convert String to List<Character> using Java Streams
                            List<Character> charList = str.chars()               // IntStream
                                .mapToObj(c -> (char) c) // Convert int to char
                                .collect(Collectors.toList()); // Collect to List<Character>

                            * Convert String to List<Character> using traditional loop
                            List<Character> charList = new ArrayList<>();
                            for (int i = 0; i < str.length(); i++) {
                                charList.add(str.charAt(i));
                            }
                            
                            * Convert String to List<Character> using StringUtils and Streams
                            List<Character> charList = StringUtils
                                .toCharArray(str)                // char[]
                                .chars()                         // IntStream
                                .mapToObj(c -> (char) c)         // Convert int to char
                                .collect(Collectors.toList());  // Collect to List<Character>
                            
                            * Remove character at index, using StringBuilder
                            StringBuilder sb = new StringBuilder(str);
                            sb.deleteCharAt(indexToRemove);
                            str = sb.toString();
                        
                        */

                        String str = "orange";

                        String sortedString = StringUtils.sort(str); // * also from the Apache Commons Lang library

                        String sortedString1 = str.chars()
                                .sorted()
                                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                                .toString();

                        String sortedString2 = str.chars()
                                .mapToObj(c -> (char) c)
                                .sorted(Comparator.reverseOrder())
                                .map(String::valueOf)
                                .collect(Collectors.joining());

                    }

                    public String reorganizeString(String S) { // TODO: verify - O(n + n log n) t | O(n) s

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

                    public String reverse(String s) { // O(n) t ; O(1) s
                        return new StringBuilder(s).reverse().toString(); // .js s.split('').reverse().join('')
                    }

                    public String reverse1(String s) { // O(2n/2 ~ n) t ; O(1) s
                        int i = 0, j = s.length() - 1; // O(n) . length() - assumed
                        char tmp;
                        while (i < j) { // O(n/2)
                            tmp = s.charAt(i); // todo: confirm O(?) t
                            s.setCharAt(i, s.charAt(j));
                            s.setCharAt(j, tmp);
                            i++; j--;
                        }
                        return s;
                    }

                    public String reverse2(String s) { // O(3n/2 ~ n) t ; O(1) s
                        char[] sArr = s.toCharArray(); // O(n)
                        int i = 0, j = s.length - 1;
                        char tmp;
                        while (i < j) { // O(n/2)
                            tmp = sArr[i];
                            sArr[i++] = sArr[j];
                            sArr[j--] = tmp;
                        }
                        return new String(sArr); // O(n)
                    }

                    public String reverse3(String s) { // O(n) t ; O(1) s
                        String s1 = "";
                        for (int i = s.length() - 1; i >= 0; i--)
                            s += s.charAt(i);
                        return s1;
                    }

                    // 3rd-Party (Tutorial) - LC #344

                    @FunctionalInterface
                    interface CharacterSwaps {
                        void swap(char[] c, int a, int b);
                    }

                    public String reverse4(String s) { // O(n/2 ~ n) t ; O(1) s
                        if (s.length() <= 1) return null;

                        CharacterSwaps swap = (c, front, end) -> {
                            char tmp = c[front];
                            c[front] = c[end];
                            c[end] = tmp;
                        };

                        char[] cArr = s.toCharArray();
                        int front = 0, end = s.length() - 1;

                        while (front < end) {
                            swap.swap(cArr, front, end);
                            front++; end--;
                        }

                        return new String(cArr);
                    }

                    public String reverseWords(String s) {
                        List<String> words = Arrays.asList(s.split("\\s+"));
                        Collections.reverse(words);
                        return String.join(" ", words); // .js s.split(' ').reverse().join(' ')
                    }

                    public String reverseWords1(String s) {
                        List<String> words = Arrays.asList(
                            new StringBuilder(s).split(" ")
                        );
                        Collections.reverse(words);
                        return String.join(" ", words); // .js s.split(' ').reverse().join(' ')
                    }

                    public String reverseWords2(String s) {
                        return null;
                    }

                    @FunctionalInterface
                    interface Reverse {
                        void reverse(char[] c, int front, int end);
                    }

                    // 3rd-Party (Tutorial) - LC #557
                    public String reverseWords3(String s) { // ! O(n(n/2) ~ n) t ; O(n / n~1 ? - argue between extra char-array being required for algo's usage or not) s

                        // !! IMPORTANT !!

                        // ! argument: extra char array 'c' required for character manipulation; so input string 's' can be deleted from memory, taking 'c' as the new input char-array
                        // returning string (new String(c)) is just to hold the result back as a string for the function
                        // so space-complexity is still constant as O(1) s

                        // ! argument: LC still goes with O(n) s for char-array
                        // explanation: "we cannot avoid converting to char-array, so it'd be O(n) on an optimal solution"
                        // so confirm: if "space - 'complexity' " is about the "act" of using space, as compared to "actual space" being used in memory
                        // and confirm: what 'complexity' means in Big-O in general again - and how it depicts the cause / effect of change in time / space

                        // * also confirm: how possible it is to delete strings in memory manually, right after conversion
                        // confirm: by "optimal solution" we mean keeping input string 's' for other "future use"

                        // ! another argument: "conversion" of a data-structure to another data-structure for the sake of an algo
                        // ! is not the same as "using extra space" with another data-structure, alongside the input data-structure being worked with
                        // what is the effect of deleting the older data-structure, after conversion ?
                        // do we keep same constant space in use ? or are there any other misconceptions in-between ?

                        if (s.length() <= 1) return s;

                        Reverse reverse = (c, front, end) -> {
                            while (front < end) {
                                char tmp = c[front];
                                c[front] = c[end];
                                c[end] = tmp;
                                front++; end--;
                            }
                        };

                        int front = 0, end = 0;
                        char[] c = s.toCharArray();
                        // * delete s; // todo: delete 's' from memory; then change all 's.length()' usages to 'c.length'
                        // ! since "space-complexity might not be as important as time-complexity"
                        // * compare the time-cost of deleting unnecessary space to leaving it to be handled in-time by Garbage Collector

                        while (end < s.length()) {
                            if (c[end] == ' ') {
                                reverse.reverse(c, front, end - 1);
                                front = end + 1;
                            } else if (end == s.length() - 1)
                                reverse.reverse(c, front, end);
                            end++;
                        }

                        return new String(c); // or: c.toString();
                    }

                    // 3rd-Party (Tutorial) - LC #557
                    public String reverseWords4(String s) {
                        return null;
                    }

                    public boolean containsDuplicates(String s) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #
                    public boolean containsDuplicates(String s) {
                        return null;
                    }

                    // first non-repeating character
                    // using hashmap - cannot use an extra arraylist, because the char-count is needed (as hashmap values)
                    public int firstUniqueCharacter(String str) { // O(2n ~ n) t ; O(n) s
                        if (str.length() == 0) return -1;
                        else if (str.length() == 1) return 0;

                        HashMap<Character, Integer> map = new HashMap<>();

                        // only most-optimal way is to loop through entire array, storing char-counts 1st
                        // then iterate through it again, finding the first char with count = 1
                        for (char c : str.toCharArray())
                            if (map.containsKey(c))
                                map.put(c, map.get(c) + 1);
                            else map.put(c, 1);

                        for (int i = 0; i < str.length(); i++)
                            if (
                                    map.containsKey(str.charAt(i))
                                            && map.get(str.charAt(i)) == 1
                            ) return i;

                        return -1;
                    }

                    // using same input string - this works since we're finding out if a char doesn't exist in string anymore after removing it
                    // so opposite of the 'same input' algo for 'containsDuplicates' problem
                    public int firstUniqueCharacter1(String str) { // O(n) t ; O(1) s
                        if (str.length() == 0) return -1;
                        else if (str.length() == 1) return 0;
                        for (int i = 0; i < str.length(); i++) {

                            // str.remove(i); // ! String has no 'remove at index' method; use StringBuilder & .deleteCharAt / convert to List<Character> then .remove()

                            str = new StringBuilder(str).deleteCharAt(i).toString(); // ! confirmed

                            // String.join("", ArrayUtils.toObject(str.toCharArray()).remove(i)); // ! confirm

                            // Arrays.asList(ArrayUtils.toObject(StringUtils.toCharArray(str))).remove(i)

                            // String.join("", ); // ! confirm
                            // String.join("", Arrays.asList(str).remove(i)); // ! confirm

                            if (!str.contains(str.charAt(i)))
                                return i;
                        }
                        return -1;
                    }

                    // ! wrong to sort string if its exact order is required to find the 1st non-repeating
                    // this only finds the 1st non-repeating character when string is sorted alphabetically
                    // so 1st non-repeating character is also the earliest alphabet
                    public int firstUniqueCharacter2(String str) { // O(n log n + n) t ; O(1) s

                        if (str.length() == 0) return -1;
                        else if (str.length() == 1) return 0;

                        // str.sort(); // ! String has NO .sort method
                        // ! and cannot sort a string itself with Arrays.sort(str)
                        // * can only sort a String[] array with Arrays.sort(strArr [, Comparator.comparing(String::length)] )
                        // * can also sort a List<String> object with Collections.sort(obj)
                        // * also: List<Character> with Collections.sort(obj) // ! must convert string to .chars collected by List (check 'Strings' constructor)
                        // * str.chars().sorted().collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString()
                        // * str.chars().mapToObj(c -> (char) c).sorted(Comparator.reverseOrder()).map(String::valueOf).collect(Collectors.joining())
                        // * String sortedString = StringUtils.sort(str); // Requires Apache Commons Lang library


                        // ! must convert String to char[] array, to Arrays.sort, then convert back to a String

                        char[] chars = str.toCharArray();
                        Arrays.sort(chars); // * sorts chars in-place (& by reference pointers - figure out how, with 'char[]' primitive type)
                        str = new String(chars);

                        // ! better / faster options below ?
                        // * str = str.chars().sorted().collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
                        // * str = StringUtils.sort(str); // Requires Apache Commons Lang library

                        for (int i = 1; i < str.length(); i++)
                            if (str.charAt(i - 1) != str.charAt(i))
                                return i - 1;

                        return -1;
                    }

                    // 3rd-Party (Tutorial) - LC #387
                    public int firstUniqueCharacter3(String str) { // O(2n ~ n) t ; O(n) s
                        if (str.length() == 0) return -1;
                        else if (str.length() == 1) return 0; // return 1st 0 index

                        HashMap<Integer, Integer> map = new HashMap<>();

                        // store & increment all characters in map
                        for (char c : str.toCharArray())
                            if (map.containsKey((int) c))
                                map.put(
                                        (int) c,
                                        map.get((int) c) + 1
                                );
                            else map.put((int) c, 1);

                        // now, iterate again and find the earliest character with count = 1 in map
                        for (int i = 0; i < str.length(); i++)
                            if (
                                map.containsKey((int) str.charAt(i))
                                && map.get((int) str.charAt(i)) == 1
                            ) return i;

                        return -1;
                    }

                    // first unique character
                    public char firstNonRepeatingChar(String str) {
                        return null;
                    }

                    public char firstNonRepeatingChar1(String str) {
                        return null;
                    }

                    public char firstNonRepeatingChar2(String str) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #387
                    public char firstNonRepeatingChar3(String str) { // O(2n ~ n) t ; O(n) s
                        if (str.length() <= 1) return '-';
                        Map<Integer, Integer> map = new HashMap<>();
                        // 1st pass: increment character counts
                        for (char c : str.toCharArray()) {
                            if (!map.containsKey((int) c))
                                map.put((int) c, 1);
                            else map.put((int) c, map.get((int) c) + 1);
                        }
                        // 2nd pass: find 1st character with count = 1
                        for (int i = 0; i < str.length(); i++)
                            if (map.get((int) str.charAt(i)) == 1)
                                return str.charAt(i);
                        return '-';
                    }

                    public boolean isAnagram(String s1, String s2) {
                        return false;
                    }

                    // 3rd-Party (Tutorial) - LC #242
                    public boolean isAnagram1(String s, String t) { // O(2n + m ~ 3n ~ n) t ; O(n) s
                        if (s == null && t == null) return true;
                        if (s == null || t == null) return false;
                        if (s.length() != t.length()) return false;
                        if (s.isEmpty() && t.isEmpty()) return true;

                        Map<Integer, Integer> map = new HashMap<>();

                        // 1st pass: add chars in s to hashmap and keep count
                        for (char c : s.toCharArray())
                            if (!map.containsKey((int) c))
                                map.put((int) c, 1);
                            else map.put((int) c, map.get((int) c) + 1);

                        // 2nd pass: reduce counts of chars in t found in map
                        for (char c : t.toCharArray()) {
                            if (!map.containsKey((int) c))
                                return false;
                            map.put((int) c, map.get((int) c) - 1);
                        }

                        // 3rd pass: check for all counts in hashmap not 0, then true
                        for (Map.Entry entry : map.entrySet())
                            if ((int) entry.getValue() != 0)
                                return false;

                        return true;
                    }

                    // remove matches from 1 string
                    public boolean checkAnagrams(String s1, String s2) { // O(5n ~ n) t ; O(1) s
                        for (char c : s1.toCharArray()) // { // toCharArray - O(n) ? | loop - O(n)
                            if (s2.contains(c)) // even this could also be O(n) t
                                s2.remove(s2.indexOf(c)); // ! confirm: remove at index-of - both O(2n)
                            else return false;
                        // } // ! commented on purpose - also test this 1-line-scope syntax for - if-else
                        return true;
                    }
                    public boolean checkAnagrams1(String s1, String s2) {

                        // * Arrays.asList(s1.toCharArray(new char[s1.length()])).stream().map((c, i) -> {});

                        boolean res = true;

                        /*
                        Arrays.asList(s1.toCharArray()).stream().map((c, i) -> {
                            if (s2.contains(c))
                                s2.remove(s2.indexOf(c));
                            else res = false; // ! confirm: how to break from .map iteration
                        });
                        */

                        for (char c : s1.toCharArray())
                            if (s2.contains(c))
                                s2.remove(s2.indexOf(c));
                            else res = false;

                        return res;
                    }
                    public boolean checkAnagrams2(String s1, String s2) {
                        char c;
                        for (int i = 0; i < s1.length(); i++) {
                            c = s1.charAt(i);
                            if (s2.contains(c))
                                s2.remove(s2.indexOf(c));
                            else return false;
                        }
                        return true;
                    }
                    public boolean checkAnagrams3(String s1, String s2) {
                        return null;
                    }
                    public boolean checkAnagrams4(String s1, String s2) {
                        return null;
                    }
                    public boolean checkAnagrams5(String s1, String s2) {
                        return null;
                    }

                    // sort both strings & compare
                    public boolean checkAnagrams6(String s1, String s2) { // O(2n log n ~ nlogn) t ; O(1) s
                        // ! best & fastest way to sort strings
                        char[] c1 = s1.toCharArray(), c2 = s2.toCharArray();
                        Arrays.sort(c1); Arrays.sort(c2); // O(2nlogn)
                        s1 = new String(c1); s2 = new String(c2);
                        return s1.equals(s2); // s1 == s2; - O(n~1)
                    }
                    public boolean checkAnagrams7(String s1, String s2) {
                        return null;
                    }
                    public boolean checkAnagrams8(String s1, String s2) {
                        return null;
                    }
                    public boolean checkAnagrams9(String s1, String s2) {
                        return null;
                    }
                    public boolean checkAnagrams10(String s1, String s2) {
                        return null;
                    }
                    public boolean checkAnagrams11(String s1, String s2) {
                        return null;
                    }

                    // 3rd-Party (Tutorial) - LC #
                    public boolean checkAnagrams12(String s1, String s2) {
                        return null;
                    }

                    public class MinimumWindowSubstring {

                        /**
                         * Given to strings s1 & s2, return the minimum window in s1 which will contain all the characters in s2
                         * if there is no such window in s1 that covers all characters in s2, return an empty string ""
                         * NB: if there is such a window, it's guaranteed that there'll always be only 1 unique minimum window in s1
                         *
                         * Constraints:
                         *
                         * 1 <= s1.length, s2.length < 10^5
                         * s1 & s2 consist of English letters
                         */

                        /**
                         * @param source: string
                         * @param target: string
                         * @return: string to denote the minimum window, or ""
                         */

                        public String minWindow(String s1, String s2) {
                            if (s1 == null || s1.length() == 0)
                                return s1;
                            if (s2 == null || s2.length() == 0)
                                return s2;

                            HashMap<Character, Integer> targetCounts = new HashMap<>();

                            for (char c : s2.toCharArray())
                                targetCounts.put(c, targetCounts.getOrDefault(c, 0) + 1);

                            String res = "";
                            int minLen = Integer.MAX_VALUE;
                            int currLen = 0;
                            int i = 0, j = 0;
                            char c, sourceCh;

                            for (; i < s1.length(); i++) {
                                while (j < s1.length() && currLen < s2.length()) {
                                    c = s1.charAt(j);
                                    if (targetCounts.containsKey(c)) {
                                        if (targetCounts.get(c) > 0)
                                            currLen++;
                                        targetCounts.put(c, targetCounts.get(c) - 1);
                                    }
                                    j++;
                                }
                                if (currLen >= s2.length())
                                    if (j - i < minLen) {
                                        minLen = j - i;
                                        res = s1.substring(i, j);
                                    }
                                sourceCh = s1.charAt(i);
                                if (targetCounts.containsKey(sourceCh)) {
                                    targetCounts.put(sourceCh, targetCounts.get(sourceCh) + 1);
                                    if (targetCounts.get(sourceCh) > 0) currLen--;
                                }
                            }

                            return res;
                        }

                        // GitHub Copilot - Alt Option
                        public String minWindow1(String source, String target) {
                            if (source == null || target == null || source.length() == 0 || target.length() == 0)
                                return "";

                            Map<Character, Integer> targetCounts = new HashMap<>();
                            Map<Character, Integer> sourceCounts = new HashMap<>();
                            int left = 0, right = 0;
                            int minLen = Integer.MAX_VALUE;
                            int count = 0;
                            int start = 0;
                            int end = 0;
                            char c;
                            String result = "";

                            // O(n)
                            for (char t : target.toCharArray())
                                targetCounts.put(t, targetCounts.getOrDefault(t, 0) + 1);

                            // O(n)
                            while (right < source.length()) {
                                c = source.charAt(right);
                                if (targetCounts.containsKey(c)) {
                                    sourceCounts.put(c, sourceCounts.getOrDefault(c, 0) + 1);
                                    if (sourceCounts.get(c).equals(targetCounts.get(c)))
                                        count++;
                                }
                                right++;

                                // O(n)
                                while (count == targetCounts.size()) {
                                    if (right - left < minLen) {
                                        minLen = right - left;
                                        start = left;
                                        end = right;
                                    }
                                    c = source.charAt(left);
                                    if (targetCounts.containsKey(c)) {
                                        sourceCounts.put(c, sourceCounts.get(c) - 1);
                                        if (sourceCounts.get(c) < targetCounts.get(c))
                                            count--;
                                    }
                                    left++;
                                }
                            }

                            if (minLen != Integer.MAX_VALUE)
                                result = source.substring(start, end);

                            return result;
                        }

                    }

                    public class ValidPalindrome {

                        /**
                         * Given a non-empty string s, find out whether it can be made into a valid palindrome
                         *
                         * Constraint: can delete at-most 1 character
                         */

                        public boolean validPalindrome(String s) {
                            int left = 0, right = s.length() - 1;

                            while (left < right) {
                                if (s.charAt(left) != s.charAt(right))
                                    break;
                                left++; right --;
                            }

                            if (left >= right) return true;

                            return
                                    isSubPalindrome(s, left + 1, right)
                                            || isSubPalindrome(s, left, right - 1);
                        }

                        private boolean isSubPalindrome(String s, int left, int right) {
                            while (left < right) {
                                if (s.charAt(left) != s.charAt(right))
                                    return false;
                                left++; right--;
                            }
                            return true;
                        }

                    }

                }

            }

            // (Array) Lists & Tuples

            public static class ListsTuples {

                public static class Lists {}

                public static class ArrayLists {}

                public static class Tuples {}

            }

            // Sets & Sequences

            public static class SetsSequences {

                public static class Sets {}

                public static class Sequences {}

            }

            // WeakMaps & WeakSets

            public static class WeakStructures {

                public static class WeakMaps {}

                public static class WeakSets {}

            }

            // HashMaps & HashTables

            public static class HashStructures {

                public static class HashMaps {}

                public static class HashTables {}

            }

            // Matrices

            public static class Matrices {

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
                                : profit; // * Math.max(profit, i - min);
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
                        Queue<Integer[]> queue = new java.util.LinkedList<>();
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
                    Queue<Integer[]> queue = new java.util.LinkedList<>();
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

            }

            // Linked Lists

            public static class LinkedList {

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

                public LinkedList flattenBinaryTreeToLinkedList(Trees.Tree.TreeNode root) { // O(n) t ; O(n) s 
                    // (not done in-place; LinkedList with n nodes required)

                    if (root == null) return null;

                    ListNode lRoot = new ListNode(root.value(), null);

                    Trees.Tree.TreeNode node = null;
                    ListNode lNode = lRoot;

                    // bfs

                    // todo: Use Java's in-built Queue class, because Trees is the current study
                    java.util.Queue<Trees.Tree.TreeNode> q = new java.util.LinkedList<Trees.Tree.TreeNode>(); // not Queue();
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

                        node = (Trees.Tree.TreeNode) q.poll(); // not .dequeue() / .pop();
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
                         * node = (Trees.Tree.TreeNode) q.poll();
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

                    Consumer<Trees.Tree.TreeNode>[] dfs = new Consumer[1];

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

                public Trees.Tree.TreeNode flattenBinaryTreeToLinkedList2(Trees.Tree.TreeNode root) { // O(n) t ; O(1) s
                    // in-place; No LinkedList with n nodes required
                    return null;
                }

            }

            // Stacks

            public static class Stacks {}

            // Queues

            public static class Queues {}

            // Heaps (max & min)

            public static class HeapsMaxMin {

                public static class Heaps {}

                public static class MinHeaps {}

                public static class MaxHeaps {}

            }

            // Binary Heaps

            public static class BinaryHeaps {}

            // Priority Queues

            public static class PriorityQueues {}

            // Trees

            public static class Trees {

                public static class Tree {
                    // * needs to be accessed 'statically' by other classes (eg. 'BST') external from outer class 'Trees'

                    // even though this class doesn't contain any custom-defined Functional-Interfaces

                    // @FunctionalInterface
                    // Should be static because its child-class is static; and the child-class
                    // instantiates super(..) in its constructor
                    // * Child-class is static because it contains 1+ custom-defined
                    // @FunctionalInterfaces

                    public static class TreeNode {
                        // * also required to be static - needs to be accessed by 'BST' & other classes (external from outer classes 'Trees.Tree')

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

            }

            // Binary (Search) Trees

            public static class BST extends Trees.Tree {

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

            public static class Tries extends Trees.Tree {

                public Tries(TreeNode root) {
                    super(root);
                }

            }

            // Graphs

            public static class Graphs {}

            // Bits

            public static class Bits {}

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



/*

* // TODO: Java

Keywords - record, volatile, 

New Types - 

Strings - "" | Chars - ''

arr.length & str.length() - arr .length prop & str .length() method

var x = value / new Object(); - .jv, c#, js-ts, sw, go, dart, kt, scala, ..

Type.class - class-name as String

Type x = new Type[] { a, b, 1, 2 } - object-type array 'scope/closure'

* new Interface_or_Class_Type[<T>]() { cb overrides with optional { internal unnamed-scope } } - can use in-built Type-methods in either scope

int x = 15 / 10; - 1 (int floors 1.5 decimal quotient by-default - Math.floor not required)

Boolean.valueOf(x == null) .jv - new Boolean(x == null) .jv deprecated - Boolean(!u) .js

- Self-executing lambda - Supplier .get() Functional Interface
int x = ((Supplier<Integer>) () -> { .. return int; }).get();

- a method 'throw'ing new Exception' without handling (without try-catch / or in a un-'try'd catch block)
should have 'throws Exception' in its signature

class { public static var x;  static { perform 'runtime-like' ops in static env ? eg. x = 1; }  }

'record' in place of 'class' - immutable classes, for data-integrity
- for 'data-record' OOP-objects eg. BankAccount, Dto's, ..

public record A(String x, int y) {

[or static-only props | (non-)static meths ..]

    private static String z;

    public String z() { return z; } // * no this.z - static prop
    public void z(String zz) { z = zz; } // * custom setter
* .z prop not set on instantiation

* canonical-constructor auto-gen'd with A's own definition above
* auto-gen'd accessors - x(), y(), ..
* auto-gen'd method - equals, hashCode, toString

}

- convert (array)list to .stream 1st, then .map each item, then .collect the results list back
(List/ArrayList<T>) r = (List/ArrayList<T>) x.stream().map(x -> x * 2).collect(Collectors.toList()); - map() returns Stream; must be collected back into List

.forEach also in (Array)List classes - doesn't require .stream - returns void (exec lambda in foreach loop)
.stream().(count/forEach/map/reduce/findAny/findFirst/filter/sorted(cb?).toList/flatMap/collect/../combos)
.map(String::toUpperCase/List::stream/..) - also passed in as cbs (or pass in custom lambdas)

Collectors.(toList/..) - used within .collect(..), Collections.(singletonList/..) - generic for list-data-structures

x.stream().findAny(x -> x.prop == value) - returns <T> obj

new MultiValueMap<T, U>() { cb overrides } - .jv 'closure' instantiation / implementation of cb overrides ('lambda interface-implementation'), if interface (MultiValueMap)

class A extends B [extends C - wrong] implements D, E { } - cannot extend multiple parents, but can extend 1 & implement multiple interfaces / abstract classes

- in multi-threading, synchronize based on thread-locking on var
synchronized (var) {
    only 1 thread can exec this scope at a time;
    since all instances of this thread depend on this.a (preventing race conditions - (not deadlocks))
}
synchronized void func() { sync'd function }

* var.meth().<Type, Type, ..> meth(..) - generic builder-method call

* - Variable used in lambda expression should be final or effectively final

Sub-optimal solution 1 - Transform variable into final 1-element array - even though final var can be re-used inside lambda, its 1 element is not final, so can be updated

2 (better) - Move var into anonymous Object - 
var ref = new Object() { Type variable = value; }; .. ref.variable.prop/meth();

3 (worse) - using final only - final var may require value on definition -> final var cannot be re-assigned a new value
final Type x; (final var may require value on definition)
[x = value; in lambda (Error - final var cannot be re-assigned a new value)]; 

* 4 (best) - Atomic[Reference<Type>/Integer/Boolean/Long/../NO string/other regular types] - 
Atomic[Type] x = new Atomic[Type](initialValue=value);
AtomicReference<Type> x = new AtomicReference<>(value); // no initialValue named-arg

in lambda - x.set(value); - 'new Atomic[..]' instantiation not required when setting new value
x.get(); - value when required 

* method reference calls - with class-method - void meth(Type x) { .. } - any functional-interface type for 'meth' as callback can be used (meth must match functional interface definition for method-reference / call-back)
void someMeth( Consumer<Type> f ) - should be a Consumer for 'meth's definition
someMeth(obj::meth) / someMeth( (Type x) -> obj.meth(x) ) // * pass in method-reference call / callback lambda





*/




/* // TODO: SpringBoot - @Annotations, Lifecycles

TODO: @Annotations - Bean, Autowire, 

* // TODO: IntelliJ-Idea



* // TODO: IntelliJ - Config



* // TODO: IntelliJ - Issues



* // TODO: SpringBoot - Notes

- scaffolding generator service
    - spring initializr - start.spring.io & IntelliJ plugin



* // TODO: SpringBoot - Config

- with both Maven & Gradle



* // TODO: SpringBoot - Issues



* // TODO: SpringBoot - Main



* Libraries - springframework, springboot, javax (java-8), jakarta (java-? - 17/22/latest - similar components as 'javax'), 'jdbc', 

* 3rd-Party Libraries - lombok, 

* Classes - HttpHeaders, ResponseEntity, Principal, 
Properties, Session, Message, MimeBodyPart, Multipart, Transport, 
DefaultKafkaProducerFactoryCustomizer, DefaultKafkaConsumerFactoryCustomizer, 
..
Deprecated - ?
HttpSecurity, GrantedAuthority, 
Greeting, HelloMessage, 
ProducerFactory, 
..


* 'Language' Classes - Properties, Session, Message, 

* 3rd-Party Classes - MultipartFile, 

* Interfaces - HandlerInterceptor, 

* Special Data-Types - 

* Annotations / Directives - @Annotation - SpringBootApplication, RestController (api routes), Controller (web - static routes), RequestMapping((path/value)="/route", method=RequestMethod, (consumes,produces)="content-type"/MediaType,..), 
(Get/Post/Put/Patch/Delete)Mapping("/"), RequestParam([value=]"param"/required/defaultValue/), PathVariable("var"), RequestBody, ResponseStatus(HttpStatus), 
Value(""/"${message.property}"), 
CrossOrigin, 
Entity, Table, Repository, PersistenceContext, 
Service, Component, Bean (classes/methods), Qualifier, Autowired (props/methods), Lazy, 
Configuration, 
PostConstruct, 
ControllerAdvice, ExceptionHandler, MessagingGateway, 
SpringBootTest, ActiveProfiles, RunWith, Profile, 
EnableConfigServer, EnableAutoConfiguration, EnableConfigurationProperties, 
EnableScheduling, Scheduled(cron/fixedRate/fixedDelay/initialDelay/..), 
EnableEurekaServer, EnableEurekaClient, (both from spring.cloud.netflix lib), EnableZuulProxy, 
HystrixCommand, HystrixProperty, 
..
Deprecated - ?
EnableWebSecurity, EnableAuthorizationServer, EnableResourceServer, 
EnableWebSocketMessageBroker, 
MessageMapping, SendTo, 
..


* Functions - 

* Methods - Thread.(sleep/..), thread.(run/start/isAlive/setDaemon/interrupt/..), ; ResponseEntity.(ok/..), MediaType.(parseMediaType), 

* Enumerations - (HttpMethod/RequestMethod).(GET/POST/PUT/PATCH/DELETE/HEAD/OPTIONS/TRACE/..), HttpStatus.(OK/CREATED/ACCEPTED/NO_CONTENT/NOT_FOUND/REQUEST_TIMEOUT/BAD_GATEWAY/UNAUTHORIZED/..), 
MediaType.(APPLICATION_JSON_VALUE/APPLICATION_FORM_URLENCODED_VALUE/MULTIPART_FORM_DATA_VALUE/APPLICATION_XHTML_XML_VALUE/..), 
Message.RecipientType.(TO/..), 

* Thymeleaf-Html Directives - th:xx="value[= #{message.property} | ${anyVar.prop}]" - 
text="#{message.property}", if="${anyVar.prop}", action="@{/path}" (form), href (a), inline, 
Inline-Markup - [[${#springVar.prop}]], 






* Special Classes & Methods / Props - Class . meths(..) / props






* IDE Features


Scaffolding - 






* Notes

- in a @Component class, difference between @Autowired class props & @Lazy constructor/instantiation prop-values
    - & when to not use @PostConstruct with class' methods
        - only use PostConstruct with 'post-class-bootload' methods when they're called in static {scope}
- @Autowired members must be defined in valid Spring bean (@Component|@Service|...)
- 




* enum / switch cases for generics-validations




* ----------------------------------------------------------------------------------------------------------------------------------------------------------------


* Errors

java spring error - external SimpleController from @SpringBootApplication DemoApplication

Caused by: java.lang.IllegalStateException: Ambiguous mapping. Cannot map 'simpleController' method
    - multiple duplicate request-routes in SimpleController @RestController
        - even if duplicate routes may include differences in query-params (but not path-params, http-methods, etc - if routes are explicitly different)

- Circular Dependencies:
    - with Beans - 
        - The most common solution for circular dependencies in Spring is to use the @Lazy annotation.
        - This tells Spring to lazily initialize one or both beans, breaking the circular reference.

- Configuration Properties - @ConfigurationProperties(prefix="spring.dbproductservice") : "Prefix must be in canonical form"
    - @Qualifiers / @Bean names can take any form, but ConfigurationProperties' prefixes must be their canonical form versions
        - TODO: confirm both Qualifiers / Bean names & ConfigurationProperties' prefixes must be exact matches (even though they can have different case-forms)
    - Configuration property name 'spring.dbProductService' is not valid
    - Invalid characters: 'P', 'S' | Bean: dbProductService
    - Reason: 'Canonical names' should be kebab-case ('-' separated), lowercase alpha-numeric characters and must start with a letter
        - Action: Modify 'spring.dbProductService' so that it conforms to the canonical names requirements.
    - 
    - eg. @Qualifier("userRepoImpl") & @ConfigurationProperties(prefix="spring.dbproductservice")
    - 





*/







/*

* // TODO: Android Studio



* // TODO: A-Studio - Config



* // TODO: A-Studio - Issues



* // TODO: Android - Java - Notes

- Assets resources
    - https://romannurik.github.io/AndroidAssetStudio/index.html


* // TODO: A-Java - Config



* // TODO: A-Java - Issues



* // TODO: A-Java - Main


* Libraries - 

* Classes - 

* 3rd-Party Classes - 

* Special Data-Types - 

* Lifecycle Methods - 

* Functions - 

* Containers - 

* Components - 

* Shapes - 

* Props - 

* Animations - 

* Gestures - 

* Event Handlers - 

* Enumerations - 






* Specific Component-Prop-Enum Combos - 






* Special Classes & Methods / Props - Class . meths(..) / props






* IDE Features


* Scaffolding - 






* Notes





* enum / switch cases for generics-validations




*/