describe('Zenvisage', function() {
  // // Before each test load our api.users module
  // beforeEach(angular.mock.module('zenvisage'));
  
  browser.driver.manage().timeouts().implicitlyWait(50000);
  it('Title Equality Test', function() {
    
    // implicit and page load timeouts
    browser.manage().timeouts().pageLoadTimeout(400000);
    browser.manage().timeouts().implicitlyWait(250000);

    browser.get('http://127.0.01:80/');
    expect(browser.getTitle()).toEqual('Zenvisage');
  });
  // it('Representative Query Test', function() {
    
  //   // implicit and page load timeouts
  //   browser.manage().timeouts().pageLoadTimeout(400000);
  //   browser.manage().timeouts().implicitlyWait(250000);

  //   browser.get('http://127.0.01:80/');

  //   var firstRankResult;
  //   var draggedGraph = uploadToSketchpadNew( "representative-result-0", "representativeQuery")
  //   function uploadToSketchpadNewCallback(callback){
  //       uploadToSketchpadNew( "representative-result-0", "representativeQuery")
  //       callback()
  //   }
  //   function callback(){
  //      firstRankResult= userQueryDygraphsNew["result-0"]["data"]
  //   }
  //   console.log(draggedGraph)
  //   console.log(firstRankResult)
  //   var draggedGraph = uploadToSketchpadNewCallback(callback)

  //   // expect(arr).toEqual([[[1,2][2,2]],[[1,3][2,3]]]);
  //   expect(draggedGraph).toEqual(firstRankResult);
  // });
});
