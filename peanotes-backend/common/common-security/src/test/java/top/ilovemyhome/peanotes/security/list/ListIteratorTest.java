package top.ilovemyhome.peanotes.security.list;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ListIterator;

public class ListIteratorTest {

    @Test
    public void test12(){
        List<Integer> list = List.of(2,3,4,1,5,6,8,10,10);

        ListIterator<Integer> listIterator = list.listIterator(list.size());

        while (listIterator.hasPrevious()) {
            System.out.println(listIterator.previous());
        }

    }
}
