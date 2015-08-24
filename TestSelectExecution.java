package my.tools;

/*
 * TestSelectExecution.java
 *
 * Copyright: Copyright (c) 2010
 *
 * Company: Orsyp Logiciels Inc.
 */


import static java.lang.System.out;

import java.text.SimpleDateFormat;

import com.orsyp.SyntaxException;
import com.orsyp.api.ItemList;
import com.orsyp.api.execution.ExecutionFilter;
import com.orsyp.api.execution.ExecutionItem;
import com.orsyp.api.execution.ExecutionList;
import com.orsyp.api.execution.ExecutionStatus;
import com.orsyp.owls.impl.AbstractTestList;
import com.orsyp.owls.impl.execution.OwlsExecutionListImpl;

/**
 * Test for ExecutionList.extract() using OWLS implementation.
 *
 * @author vch
 * @since DUAS 6.0
 */
public class TestSelectExecution extends AbstractTestList<ExecutionItem> {

    private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm:ss");
    
    public static void main(String[] args) {

        init();
        try {
            new TestSelectExecution().test();
        } finally {
            cleanup();
        }
    }


    protected ItemList<ExecutionItem> initList() throws SyntaxException {
        
        ExecutionFilter filter = new ExecutionFilter();
        
        ExecutionList list = new ExecutionList(makeContext(), filter);
        list.setImpl(new OwlsExecutionListImpl());
        return list;
    }

    protected void print(ExecutionItem execution) {

        out.println();
        out.println(" ExecutionId");
        out.println("   Session : " + execution.getSessionName());
        out.println("   Uproc   : " + execution.getUprocName());
        out.println("   MU      : " + execution.getMuName());
        out.println("   Numsess : " + execution.getNumsess());
        out.println("   Numproc : " + execution.getNumproc());
        out.println();
        out.println(" Data");
        out.println("   Numlanc : " + execution.getNumlanc());
        out.println("   Status     : " +
                    getStatus(execution.getStatus()));
        out.println("   Step       : " + execution.getStep());
        out.println("   Relaunched : " + execution.isRelaunched());
        out.println("   Begin date : " +
                sdfDate.format(execution.getBeginDate()));
        out.println("   Begin hour : " +
                sdfHour.format(execution.getBeginDate()));
        out.println("   End date   : " +
        	(execution.getEndDate() == null? ""
        		: sdfDate.format(execution.getEndDate())));
        out.println("   End hour   : " +
        	(execution.getEndDate() == null? ""
        		: sdfHour.format(execution.getEndDate())));
        out.println("   Begin date : " +
                sdfDate.format(execution.getBeginDate()));
        if (execution.getProcessingDate() != null 
                && !execution.getProcessingDate().equals("00000000")
                && !execution.getProcessingDate().equals("")) {
            out.println("   Proc. date : "
                    + execution.getProcessingDate());
        } else {
            out.println("   NO Proc. date .");
        }

        out.println("   User       : " + execution.getUserName());
        out.println("   Author     : " + execution.getAuthorCode());

        out.println("   Queue      : " + execution.getQueue());
        out.println("   Priority   : " + execution.getPriority());
        out.println("   Num Entry  : " + execution.getEntry());
        out.println("   Uproc ver  : " + execution.getUprocVersion());
        out.println("   Sess. ver  : " + execution.getSessionVersion());
        out.println("   Info.      : " + execution.getInfo());
        out.println("   Severity   : " + execution.getSeverity());
        out.println("   Appli.     : " + execution.getApplication());

        out.println("   Sess. rank : " + execution.getRankInSession());
        out.println("   from task  : " + execution.isTaskOrigin());
        out.println("   task       : " + execution.getTaskName());
        out.println("   Task vers. : " + execution.getTaskVersion());
        out.println("   Domain   . : " + execution.getDomain());
        
        out.println("-------------------------------------------------------");
    }
    
    public static String getStatus(ExecutionStatus status) {

        if (status == ExecutionStatus.Pending) {
            return "Pending";
        } else if (status == ExecutionStatus.Started) {
            return "Started";
        } else if (status == ExecutionStatus.Running) {
            return "Running";
        } else if (status == ExecutionStatus.CompletionInProgress) {
            return "Completion in progress";
        } else if (status == ExecutionStatus.Aborted) {
            return "Aborted";
        } else if (status == ExecutionStatus.TimeOverrun) {
            return "Time overrun";
        } else if (status == ExecutionStatus.Refused) {
            return "Refused";
        } else if (status == ExecutionStatus.Completed) {
            return "Completed";
        } else if (status == ExecutionStatus.EventWait) {
            return "Event wait";
        } else if (status == ExecutionStatus.Launching) {
            return "Launching";
        } else if (status == ExecutionStatus.Held) {
            return "Held";
        } else {
            return "???";
        }
    }

}

