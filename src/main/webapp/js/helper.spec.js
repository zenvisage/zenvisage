describe('Zenvisage', function() {
  var Users;
  
  // Before each test load our api.users module
  beforeEach(angular.mock.module('zenvisage'));

  // Before each test set our injected Users factory (_Users_) to our local Users variable
  
  // A simple test to verify the Users factory exists
  it('should succeed', function() {
    var arr = separateTwoArrays([[1,2,3],[2,2,3]])
    // expect(arr).toEqual([[[1,2][2,2]],[[1,3][2,3]]]);
    expect(arr).toEqual([[[1,2],[2,2]],[[1,3],[2,3]]]);
    // expect(arr).toEqual([1,2,3]);
  });

  // it('should succeed', function() {
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
  //   // expect(arr).toEqual([1,2,3]);
  // });
});
