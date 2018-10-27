package co.ppk.service;

import co.ppk.service.impl.BussinessManagerImpl;
import co.ppk.utilities.PropertyManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CheckPaymentsService implements DisposableBean, Runnable{
    private Thread thread;
    private volatile boolean someCondition;

    @Autowired
    private PropertyManager pm;

    CheckPaymentsService(){
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run(){
        final long timeInterval = Integer.valueOf(pm.getProperty("PAYMENTS.CHECK.INTERVAL.MINUTES")) * 60000;
        BusinessManager businessManager = new BussinessManagerImpl(pm);
        while(true){
            System.out.println("#################################### CHECKING PENDING PAYMENTS ####################################");
            businessManager.checkPendingPayments();
            try {
                Thread.sleep(timeInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy(){
        someCondition = false;
    }

//    public void run(){
//        final long timeInterval = CHECK_PAYMENTS_STATUS_INTERVAL * 60000;
//        BusinessManager businessManager = new BussinessManagerImpl();
//        do {
//            System.out.println("#################################### CHECKING PENDING PAYMENTS ####################################");
//            businessManager.checkPendingPayments();
//            try {
//                Thread.sleep(timeInterval);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } while (true);
//    }
}
