package com.oms.pages.Clients;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.oms.base.BaseClass.getDriver;

public class customers {

    private ActionDriver actionDriver;

    WebDriver driver=getDriver();

    // Constructor initializes ActionDriver from BaseClass
    public customers(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }

 private By newClient= By.xpath("//button[@id='btn-new']") ;
    private By newClientPopup= By.xpath("//div[@class='bootbox modal fade bootbox-confirm in']//div[@class='modal-content']") ;
    private By newClientPopupyesButton= By.xpath("//button[@class='btn btn btn-sm btn-success pull-right' and text()='Yes']") ;
    private By businnessname= By.xpath("//input[@id='name']") ;
    private By chooseIndustryDropdown= By.xpath("") ;
    private By chooseCountryDropdown= By.xpath("") ;
    private By BusinnessTypeDropdown= By.xpath("") ;
    private By BusinnesIdnumber= By.xpath("") ;
    private By TaxIdNumber= By.xpath("") ;
    private By AccNo= By.xpath("") ;
    private By RelationshipMangerDropdown= By.xpath("") ;
    private By = By.xpath("") ;
    private By = By.xpath("") ;
    private By = By.xpath("") ;
}
