var Utils = require('./testUtils.js');
var utils = new Utils();

describe('Filter Constraint Checks', function() {

  it('Test String Filters', function() {
    utils.initialize()
    // Testing fix to issue #209
    checkFilterEqualTo('city="Champaign"','city: Champaign (0)')
    // Single Quote test
    checkFilterEqualTo("city='Champaign'",'city: Champaign (0)')

  });  

  it('Test Inequality Filters', function() {
    utils.initialize()
    // Inequality test
    checkFilterEqualTo('year>2014','city: Hackensack (0.9999997)')
  }); 

  it('Test Empty Filter', function() {
    utils.initialize()
    element(by.id('filter')).sendKeys("month<0");
    element(by.id('filter-submit')).click();
    browser.sleep(1000)
    // Check that error modal is present
    expect(element(by.id('errorModal')).isDisplayed()).toBe(true);
    // Click on show error
    element(by.css('#errorModalBody > p > button')).click();
    browser.sleep(500)
    // Check that error message is displayed
    expect(element(by.css('#errorModalText > pre')).getText()).toContain(
      "No resulting visualizations matches this filter. Please change the filter to relax the constraints.")
  }); 


  function checkFilterEqualTo(filterStr, result){
    element(by.id('filter')).clear(); // clear what is previously typed in the textbox
    element(by.id('filter')).sendKeys(filterStr);
    element(by.id('filter-submit')).click();
    browser.sleep(1000)
    utils.checkNthResultEquals(0,result);
  }
});