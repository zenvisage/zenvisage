'use strict';

(function() {
   var Utils = function() {
    var utils = this;

    utils.initialize = function() {
      //maximize browser screen 
      var width = 1500;
      var height = 900;
      browser.driver.manage().window().setSize(width, height);
      //browser.driver.manage().window().maximize();

      browser.manage().timeouts().pageLoadTimeout(4000000);
      browser.manage().timeouts().implicitlyWait(2500000);

      browser.get('http://localhost:8080/');
      // Click on the "Do not show this message again" button
      element(by.id("close-intro-button")).click();
      // Initialize angular webapp and trigger rendering via onDatasetChange
      browser.executeScript("var scope = angular.element('#dataset-form-control').scope();scope.onDatasetChange();")
    };
    utils.extractScoreFromResults= function (text){
      // 'city: Plymouth (0.383)'
      var firstSplit = text.indexOf(":"); 
      var scoreSplit = text.indexOf("(");
      var attribute = text.substring(0,firstSplit);
      var attributeValue = text.substring(firstSplit+2,scoreSplit);
      var score = parseFloat(text.substring(scoreSplit+1,text.length-1));
      return {
              "attribute": attribute,
              "attributeValue":attributeValue,
              "score":score
            }
    }
    utils.checkScoreWithinRange = function (score){
      return ( score <= 1 && score >= 0 ) ? true : false
    }
    utils.checkNthResultEquals = function (N,equalToText){
      element(by.css('text[count="'+N+'"]')).getText().then(function(text){
        expect(text).toEqual(equalToText);
      });
    }
  }
  
 module.exports = function() {
    return new Utils();
  };
}());
