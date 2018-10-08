describe('Zenvisage', function() {
  // it('should have a title', function() {
  //   browser.get('http://localhost:80/');
  //   expect(browser.getTitle()).toEqual('Zenvisage');
  // });

  it('DragNDrop should be Louisville', function() {
    // debugger;
    // implicit and page load timeouts
    browser.manage().timeouts().pageLoadTimeout(4000000);
    browser.manage().timeouts().implicitlyWait(2500000);

    browser.driver.manage().timeouts().implicitlyWait(50000);
    browser.get('http://localhost:80/');

    browser.executeScript("$('#dataset-form-control option').val('real_estate');var scope = angular.element('#dataset-form-control').scope();scope.onDatasetChange();")

    element(by.model('equation')).sendKeys("y=x^2");
    element(by.xpath('//button[. = "add"]')).click();

    element(by.css('text[count="1"]')).getText().then(function(text){
      expect(text).toEqual('city: Plymouth (0.383)');
    });
    
  });
});



// var origFn = browser.driver.controlFlow().execute;
//
// browser.driver.controlFlow().execute = function() {
//   var args = arguments;
//
//   // queue 100ms wait
//   origFn.call(browser.driver.controlFlow(), function() {
//     return protractor.promise.delayed(100);
//   });
//
//   return origFn.apply(browser.driver.controlFlow(), args);
// };
