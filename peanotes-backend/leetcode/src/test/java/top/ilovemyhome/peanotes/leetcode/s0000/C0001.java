package top.ilovemyhome.peanotes.leetcode.s0000;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

public class C0001 {

    class Solution {
        public int[] twoSum(int[] nums, int target) {
            int[] answer = new int[2];
            int length = nums.length;
            for (int i = 0; i < length - 1; i++) {
                for (int j = i + 1; j < length; j++) {
                    int sum = nums[i] + nums[j];
                    if (sum == target) {
                        answer[0] = i;
                        answer[1] = j;
                    }
                }
            }
            return answer;
        }
    }

    @Test
    public void test1() {
        int[] result = solution.twoSum(new int[]{2, 7, 11, 15, 10, 12}, 17);
        System.out.println(Arrays.stream(result).boxed().collect(Collectors.toList()));
    }

    @BeforeEach
    public void init() {
        solution = new Solution();
    }

    private Solution solution;

}
