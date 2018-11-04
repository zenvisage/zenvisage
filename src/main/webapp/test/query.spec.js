var Utils = require('./testUtils.js');
var utils = new Utils();

describe('Testing Different Query Modality', function() {
  it('Input equation first rank should be Plymouth.', function() {
    utils.initialize();
    // Enter equation y = x^2 and press "Add" Button
    element(by.model('equation')).sendKeys("y=x^2");
    element(by.xpath('//button[. = "add"]')).click();
    browser.sleep(500); 
    utils.checkNthResultEquals(0,'city: Altoona (0.391)');
  });
  it("Upload Pattern Same as Parkville",function(){
    utils.initialize();
    element(by.css("#sidebar > div:nth-child(4) > h5 > button")).click();
    browser.sleep(100); // need to wait for modal to open up
    element(by.id('x-pattern')).sendKeys("1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12");
    browser.sleep(500);
    element(by.id('y-pattern')).sendKeys("142.52276611328125, 138.49191284179688, 139.62371826171875, 143.51437377929688, 144.98410034179688, 145.68357849121094, 147.85366821289062, 148.42364501953125, 148.06085205078125, 146.40948486328125, 144.39166259765625, 146.13348388671875");
    browser.sleep(500); // need to send keys 
    // Need to click upload button
    element(by.id('pattern-submit')).click();
    utils.checkNthResultEquals(0,'city: Parkville (1.0)');
  }); 
  it('Drag and Drop should correspond to first ranked item.', function() {
    utils.initialize();
    /////////////////////////////////////
    //// Test Oracle as Partial Logic////
    /////////////////////////////////////
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
  
});
