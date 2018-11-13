var Utils = require('./testUtils.js');
var utils = new Utils();

describe('Basic Checks', function() {

  it('Title Equality Test', function() {
    utils.initialize();
    expect(browser.getTitle()).toEqual('Zenvisage');
  });
  it('Check that score@rank n > score@rank m, where n<m.', function() {
    utils.initialize()
    checkDescendingResultScore();
  });    

  it("Switch to different datasets and ensuring basic checks satisfied",function(){
    utils.initialize()
    // console.log(element.all(by.css('#dataset-form-control option')).count());
    var allOptions = element.all(by.css('#dataset-form-control option'))
    var totalOptionsCount = allOptions.count()
    expect(allOptions.count()).toEqual(5)
    for (i = 2; i <= 5; i++) {
      // Click open the drop down menu
      element(by.id('dataset-form-control')).click();
      //Select the second item on the drop down menu
      element(by.css('#dataset-form-control > option:nth-child('+i+')')).click();
      // Click on the side to close the drop down menu
      browser.actions().mouseMove({x: 100, y: 0}).click().perform(); 
      // trigger on dataset change
      browser.executeScript("var scope = angular.element('#dataset-form-control').scope();scope.onDatasetChange();")
      // .then(function(){
      //   basicChecks()  
      // });
      // Perform all the basic checks after outlier is rendered (rendering order is result --> representative --> outliers)
      var outlier = element(by.id("outlier-result-0"))
      outlier.isPresent().then(function(){
        basicChecks()
      })
    }
  });

  function basicChecks(){
    console.log("basic checked")
    checkDescendingResultScore();
  }
  function checkDescendingResultScore(){
    // By default there is max 50 number of results, so we set 0<n<40 and create m by adding an integer 1<x<10
    var n = Math.floor(Math.random() * Math.floor(20));
    var x = Math.floor((Math.random() * ((19 + 1) - 1)) + 1);
    var m = n +x;
    // console.log(n,m)
    expect(n).toBeLessThan(m);
    element.all(by.css("#undraggable-result-"+n+">text")).get(0).getText().then(function(text){
      var rankN = utils.extractScoreFromResults(text);
      element.all(by.css("#undraggable-result-"+m+">text")).get(0).getText().then(function(text){
        var rankM = utils.extractScoreFromResults(text);
        // console.log(rankN["score"],rankM["score"])
        expect(rankM["score"]).toBeLessThanOrEqual(rankN["score"]);
      });
    });  
  }
});