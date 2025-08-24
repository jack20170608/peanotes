package top.ilovemyhome.peanotes.leetcode.s0000;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class C0002 {

    class Solution {
        public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
            ListNode result = new ListNode();
            ListNode h1 = l1;
            ListNode h2 = l2;
            ListNode r = result;
            int sum = 0;
            while (true) {
                if (h1 == null && h2 == null && sum == 0) {
                    break;
                }
                if (h1 != null) {
                    sum += h1.val;
                    h1 = h1.next;
                }
                if (h2 != null) {
                    sum += h2.val;
                    h2 = h2.next;
                }
                int current = sum % 10;
                sum = sum / 10;
                ListNode n = new ListNode(current);
                r.next = n;
                r = n;
            }
            result = result.next;
            return result;
        }
    }


    @Test
    public void test1() {
        ListNode head = this.solution.addTwoNumbers(new ListNode(4), new ListNode(9));
        System.out.println(head);
    }

    @BeforeEach
    public void init(){
        this.solution = new Solution();
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        @Override
        public String toString() {
           return String.format("%s -> %s", this.val, this.next);
        }
    }

    private Solution solution;

}
