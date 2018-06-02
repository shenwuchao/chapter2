package org.smart4j.chapter2.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.smart4j.chapter2.model.Customer;
import org.smart4j.chapter2.service.CustomerService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* customer单元测试
* */
public class CustomerServiceTest {

    private final CustomerService customerService;


    public CustomerServiceTest(CustomerService customerService) {
        this.customerService = customerService;
    }
    @Before
    public void init () {
        
    }

    @Test
    public void getCustomerListTest() {
        List<Customer> customerList = customerService.getCustomerList("");
        Assert.assertEquals(2, customerList.size());
    }

    @Test
    public void getCustomerTest () {
        long id = 1;
        Customer customer = customerService.getCustomer(id);
        Assert.assertNotNull(customer);
    }

    @Test
    public void createCustomerTest () {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        fieldMap.put("name", "customer3");
        fieldMap.put("contact", "John");
        fieldMap.put("telephone", "13423451290");
        boolean result = customerService.createCustomer(fieldMap);
        Assert.assertTrue(result);
    }
    @Test
    public void updateCustomerTest () {
        Map<String, Object> feildMap = new HashMap<String, Object>();
        long id = 2;
        feildMap.put("name", "customer22");
        boolean result = customerService.updateCustomer(id, feildMap);
        Assert.assertTrue(result);
    }

    @Test
    public void deleteCustomerTest() {
        long id = 3;
        boolean result = customerService.deleteCustomer(id);
        Assert.assertTrue(result);
    }
}
