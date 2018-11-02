var Utils = require('./testUtils.js');
var utils = new Utils();

describe('Testing Different Query Modality', function() {
    it("Upload Pattern Same as Parkville",function(){
      utils.initialize()
      var x = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]
      var y = [142.52276611328125, 138.49191284179688, 139.62371826171875, 143.51437377929688, 144.98410034179688, 145.68357849121094, 147.85366821289062, 148.42364501953125, 148.06085205078125, 146.40948486328125, 144.39166259765625, 146.13348388671875]
      element(by.css("#sidebar > div:nth-child(4) > h5 > button")).click()
      element(by.id('x-pattern')).sendKeys("1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12");
      element(by.id('y-pattern')).sendKeys("142.52276611328125, 138.49191284179688, 139.62371826171875, 143.51437377929688, 144.98410034179688, 145.68357849121094, 147.85366821289062, 148.42364501953125, 148.06085205078125, 146.40948486328125, 144.39166259765625, 146.13348388671875");
      // Need to click upload button
      element(by.id('pattern-submit')).click();
      utils.checkNthResultEquals(0,'city: Parkville (1.0)');
      // expect(sliderBar.getAttribute('value')).toEqual('7');
    }); 
    it('Input equation first rank should be Plymouth.', function() {
      // implicit and page load timeouts
      utils.initialize(); 
      // Enter equation y = x^2 and press "Add" Button
      element(by.model('equation')).sendKeys("y=x^2");
      element(by.xpath('//button[. = "add"]')).click();
      utils.checkNthResultEquals(1,'city: Plymouth (0.383)');
    });
    it('Drag and Drop should correspond to first ranked item.', function() {
      /////////////////////////////////////
      //// Test Oracle as Partial Logic////
      /////////////////////////////////////
      // implicit and page load timeouts
      utils.initialize(); 
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
