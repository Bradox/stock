package com.tresw.stock;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.tresw.stock.controller.StockControllerTest;
import com.tresw.stock.service.StockServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ StockControllerTest.class, StockServiceTest.class })
public class JunitTestSuite {

}