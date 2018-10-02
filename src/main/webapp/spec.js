describe('Zenvisage', function() {
  // it('should have a title', function() {
  //   browser.get('http://localhost:80/');
  //   expect(browser.getTitle()).toEqual('Zenvisage');
  // });

  it('DragNDrop should be Louisville', function() {
    debugger;
    // implicit and page load timeouts
    browser.manage().timeouts().pageLoadTimeout(4000000);
    browser.manage().timeouts().implicitlyWait(2500000);

    browser.driver.manage().timeouts().implicitlyWait(50000);
    browser.get('http://localhost:80/');

    // browser.executeAsyncScript(function (){
    element(by.model('equation')).sendKeys("y=x^2");
    element(by.xpath('//button[. = "add"]')).click();

    browser.pause();
    element(by.css('text[count="1"]')).getText().then(function(text){
      console.log(text)
    });
    // });

    //   toEqual('city: Louisville (0.43)'); 
    // });
    // .
    //   toEqual('city: Louisville (0.43)'); // This is wrong!
    // });
    // expect(element(by.id('row-0')).getText()).
    //   toEqual('city: Louisville (0.43)'); // This is wrong!
    // });
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
