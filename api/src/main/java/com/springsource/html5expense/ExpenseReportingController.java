/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.springsource.html5expense;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Roy Clarkson
 */
@Controller
@RequestMapping("/reports")
public class ExpenseReportingController {

    private final ExpenseReportingService service;

    @Inject
    public ExpenseReportingController(ExpenseReportingService expenseReportingService) {
        this.service = expenseReportingService;
    }

    /**
     * Create a new {@link ExpenseReport} with an associated description for the purpose
     * @param purpose the reason for the expense report. i.e. conference, business meal, etc.
     * @return the ID of the new expense report
     */
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody Long createReport(@RequestParam(required = true) String purpose) {
        return service.createReport(purpose);
    }

    /**
     * Retrieve a list of charges that can be associated with an {@link ExpenseReport}. 
     * These charges are not currently associated with any other expense report.
     * @return collection of {@link EligibleCharge} objects
     */
    @RequestMapping(value = "/eligible-charges", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody Collection<EligibleCharge> getEligibleCharges() {
        return service.getEligibleCharges();
    }

    /**
     * Associate expenses with an {@link ExpenseReport}
     * @param reportId the ID of the {@link ExpenseReport}
     * @param chargeIds the IDs of the {@link EligibleCharge} objects to associate with the expense report
     * @return 
     */
    @RequestMapping(value = "/{reportId}/expenses", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody Collection<Expense> createExpenses(@PathVariable Long reportId, @RequestParam(required = true) List<Long> chargeIds) {
        return service.createExpenses(reportId, chargeIds);
    }

    /**
     * Associate an image of a receipt with an {@link Expense} 
     * @param reportId the ID of the {@link ExpenseReport}
     * @param expenseId the ID of the {@link Expense}
     * @param receiptBytes the image of the receipt
     * @return the URI of the image
     */
    @RequestMapping(value = "/{reportId}/expenses/{expenseId}/receipt", method = RequestMethod.POST, consumes = "multipart/form-data")
    public @ResponseBody String attachReceipt(@PathVariable Long reportId, @PathVariable Integer expenseId, @RequestParam(required = true) byte[] receiptBytes) {
        return service.attachReceipt(reportId, expenseId, receiptBytes);
    }

    /**
     * Finalizes and submits the {@link ExpenseReport} for review
     * @param reportId the ID of the {@link ExpenseReport}
     */
    @RequestMapping(value = "/{reportId}", method = RequestMethod.POST)
    public void submitReport(@PathVariable Long reportId) {
        service.submitReport(reportId);
    }

    /**
     * Retrieves all of the open, or incomplete, expense reports for the user
     * @return list of {@link ExpenseReport} objects
     */
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<ExpenseReport> getOpenReports() {
        return service.getOpenReports();
    }

}