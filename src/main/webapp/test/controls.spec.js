var Utils = require('./testUtils.js');
var utils = new Utils();

describe('Control Settings', function() {
  it("Switch to different similarity metrics",function(){
    utils.initialize()
    utils.selectCheckByID('Segmentation')
    utils.selectCheckByID('DTW')
    utils.selectCheckByID('MVIP')
  })
  it("Switch to different aggregation functions",function(){
    utils.initialize()
    utils.selectCheckByID('sum')
    utils.selectCheckByID('avg')
    utils.selectCheckByID('none')
  })
  it("Switch to different options",function(){
    utils.initialize()
    utils.selectCheckByID('considerRange')
    utils.selectCheckByID('showOriginalSketch')
    utils.selectCheckByID('showScatterplot'); utils.selectCheckByID('showScatterplot'); //Turn off
    utils.selectCheckByID('showBar')
    utils.selectCheckByID('flipY')
  })
});