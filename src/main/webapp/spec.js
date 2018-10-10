describe('Zenvisage', function() {
  ////////////////////////////////////////////////////////////
  //// Test Oracle as Self (Visual Examination Hard Coded)////
  ////////////////////////////////////////////////////////////
  it('Title Equality Test', function() {
    // implicit and page load timeouts
    browser.manage().timeouts().pageLoadTimeout(400000);
    browser.manage().timeouts().implicitlyWait(250000);

    browser.get('http://127.0.01:80/');
    expect(browser.getTitle()).toEqual('Zenvisage');
  });
  // // Testing a basic interaction-independent function (TODO: Trouble with importing package)
  // it('should succeed', function() {
  //   // var Helper = require("./js/helper.js");
  //   var arr = separateTwoArrays([[1,2,3],[2,2,3]])
  //   // expect(arr).toEqual([[[1,2][2,2]],[[1,3][2,3]]]);
  //   expect(arr).toEqual([[[1,2],[2,2]],[[1,3],[2,3]]]);
  //   // expect(arr).toEqual([1,2,3]);
  // });

  it('Input equation first rank should be Plymouth.', function() {
    // implicit and page load timeouts
    initializeTest(); 
    // Enter equation y = x^2 and press "Add" Button
    element(by.model('equation')).sendKeys("y=x^2");
    element(by.xpath('//button[. = "add"]')).click();
    checkNthResultEquals(1,'city: Plymouth (0.383)');
  });
  
  /////////////////////////////////////
  //// Test Oracle as Partial Logic////
  /////////////////////////////////////
  it('Drag and Drop should correspond to first ranked item.', function() {
    // implicit and page load timeouts
    initializeTest(); 
    // Drag and Drop the first cluster 
    var target = element(by.id("draw-div"))
    var representative = element.all(by.css("#undraggable-representative-result-0>text")).get(0).getText().then(function(text){
      var endIdx = text.indexOf("(")-1
      return text.substring(0,endIdx)
    }) //undraggable result contains the text
    var dragged = element(by.id("representative-result-0"))
    browser.actions().dragAndDrop(dragged,target).mouseUp().perform();
    var rankOneScore; 
    var rankOne = element.all(by.css("#undraggable-result-0>text")).get(0).getText().then(function(text){
      var endIdx = text.indexOf("(")-1
      var rankOneCity  = text.substring(6,endIdx)
      var rankOneScore = text.substring(2+endIdx,text.length-1)
      // Check that the original drag and dropped item is ranked as #!
      expect(representative).toEqual(rankOneCity)
      // And that the similarity score is 1 
      expect(parseInt(rankOneScore)).toEqual(1)
    }); 
  });

  
  it('Check last similarity above cutoff', function() {
    // TODO: Vary similarity cutoff, check that the last ranked item is larger or equal to the similarity cutoff
    expect(1).toEqual(1)
  });

  it('Check that score@rank n > score@rank m, where n<m.', function() {
    initializeTest(); 
    // By default there is max 50 number of results, so we set 0<n<40 and create m by adding an integer 1<x<10
    var n = Math.floor(Math.random() * Math.floor(20));
    var x = Math.floor((Math.random() * ((19 + 1) - 1)) + 1);
    var m = n +x;
    // console.log(n,m)
    expect(n).toBeLessThan(m);
    element.all(by.css("#undraggable-result-"+n+">text")).get(0).getText().then(function(text){
      var rankN = extractScoreFromResults(text);
      element.all(by.css("#undraggable-result-"+m+">text")).get(0).getText().then(function(text){
        var rankM = extractScoreFromResults(text);
        // console.log(rankN["score"],rankM["score"])
        expect(rankM["score"]).toBeLessThanOrEqual(rankN["score"]);
      });
    });
  });
  function extractScoreFromResults(text){
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
  function checkScoreWithinRange(score){
    return ( score <= 1 && score >= 0 ) ? true : false
  }
  function checkNthResultEquals(N,equalToText){
    element(by.css('text[count="'+N+'"]')).getText().then(function(text){
      expect(text).toEqual(equalToText);
    });
  }

  // When set filter as city='Bristol', drag and drop first ranked item
  // Several TODOs : 
  // 0) Figure out how to do dataset changing before scope call? This doesn't seem to work: 
  //  $('#dataset-form-control option').val('real_estate');
  //  element(by.css('dataset-form-control option'))
  // 1) Why is the Cluster results for real_estate tutorial different in Selenium compared to the actual interface (Expected 'Barnegat Township (877 more like this)' to equal 'Catonsville (56 more like this)')
  // 2) Initialize with different dataset, check representative and outlier corresponds to the "correct visualizations"
  // 3) Write high level functions for checking equality for Rank 1, Rank 1 Cluster, Rank j Outlier, etc
  // 4) Check that scores are always between 0 and 1 
  // 5) Testing querying input modalities: 
  //    - Pattern Query 
  // 6) Smoothing: 
  //    - change algorithm --> vary smoothing constant
  // Done: 
  // 1) Clean up required initialization to function call , called everytime
  function initializeTest(){
    //maximize browser screen 
    var width = 1500;
    var height = 900;
    browser.driver.manage().window().setSize(width, height);
    //browser.driver.manage().window().maximize();

    browser.manage().timeouts().pageLoadTimeout(4000000);
    browser.manage().timeouts().implicitlyWait(2500000);

    browser.get('http://localhost:80/');
    // Initialize angular webapp and trigger rendering via onDatasetChange
    browser.executeScript("var scope = angular.element('#dataset-form-control').scope();scope.onDatasetChange();")
  }
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
