package top.ilovemyhome.peanotes.leetcode.s0000;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class C0003 {

    //The code
    class Solution {
        public int lengthOfLongestSubstring(String s) {
            if (Objects.isNull(s) || s.isEmpty()) {
                return 0;
            }
            int length = s.length();
            char [] sArray = s.toCharArray();
            //i => the start
            //j => the end
            int result = 1;
            for(int i =0; i < length; ) {
                int maxLength = 1;
                int startIdx = i;
                for (int endIdx = startIdx + 1; endIdx < length; endIdx++) {
                    int idx = indexOf(sArray, startIdx, endIdx, sArray[endIdx]);
                    if (idx != -1) {
                        startIdx = idx + 1;
                        break;
                    }
                    maxLength++;
                }
                result = Math.max(maxLength, result);
                i = startIdx > i ? startIdx : i + 1;
            }
            return result;
        }

        private int indexOf(char [] array, int s, int e, char c){
            int result = -1;
            for (int i = s; i < e; i++) {
                if (array[i] == c) {
                    result = i;
                    break;
                }
            }
            return result;
        }
    }

    @Test
    public void testIndexOf(){
        assertThat(solution.indexOf(new char[]{'b','c','d','e','f'}, 1, 4, 'a')).isEqualTo(-1);
        assertThat(solution.indexOf(new char[]{'b','c','d','e','f'}, 0, 1, 'b')).isEqualTo(0);
        assertThat(solution.indexOf(new char[]{'b','c','d','e','f'}, 0, 4, 'f')).isEqualTo(4);
    }


    @Test
    public void test() {
//        System.out.println(solution.lengthOfLongestSubstring("a"));
//        System.out.println(solution.lengthOfLongestSubstring("aa"));
//        System.out.println(solution.lengthOfLongestSubstring("aaaaa"));
//        System.out.println(solution.lengthOfLongestSubstring("abcabcbb"));
        System.out.println(solution.lengthOfLongestSubstring("pwwkew"));
    }

    @BeforeEach
    public void init(){
        this.solution = new Solution();
    }
    private Solution solution;

}
