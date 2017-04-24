package cases;

import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhengfan on 2016/12/12 0012.
 */
public class ConditionTest {

    private final ReentrantLock lock = new ReentrantLock();

    java.util.concurrent.locks.Condition notFull = lock.newCondition();

    java.util.concurrent.locks.Condition notEmpty = lock.newCondition();


    public void put(){
    }

    public void take(){

    }



}
