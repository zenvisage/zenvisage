var formData;
var datasetNameInput;
$('#uploaderForm').on('submit', function(e) {
    document.getElementById("loadingEclipse_upload").style.display = "block";
    document.getElementById("submitButton").style.display = "none";
    e.preventDefault();
    formData = new FormData(this);
    if (formData.get("csv").name == "" ) {
      alert("Please select corresponding files!");
      document.getElementById("loadingEclipse_upload").style.display = "none";
      document.getElementById("submitButton").style.display = "block";
      return;
    }
    else if (formData.get("csv").size > 100000000) {
      alert("Do not upload files over 100MB");
      document.getElementById("loadingEclipse_upload").style.display = "none";
      document.getElementById("submitButton").style.display = "block";
      return;
    }
    parseCSV(formData);
    datasetNameInput = $("#datasetNameInput").val();
    console.log('test:',$(this).attr('action'),$(this).attr('method'));
    log.info("dataset upload: ",$("#datasetNameInput").val())
});

// function getCheckedAttributes(){
//   $("input:checkbox[name=type]:checked").each(function(){
//       console.log($(this).val());
// }

$("#define-attributes").on('submit', function(e) {
  var xList = [];
  var yList = [];
  var zList = [];

  $(".types").each(function(){
      var selectedXOption = $(this).children("option").filter(":selected").text()
      xList.push($(this).val() + " " + selectedXOption);
      var selectedYOption = $(this).children("option").filter(":selected").text()
      yList.push($(this).val() + " " + selectedYOption);
      var selectedZOption = $(this).children("option").filter(":selected").text()
      zList.push($(this).val() + " " + selectedZOption);
  });

  var filteredLists = filterUncheckAttributes(xList,yList,zList)
  xList = filteredLists.x
  yList = filteredLists.y
  zList = filteredLists.z
        console.log("x selected: ", xList);
        console.log("y selected: ", yList);
        console.log("z selected: ", zList);
  $('#define-attributes').modal('toggle');
  $('#uploaderModal').modal('toggle');


  var xyzQuery = {datasetName:datasetNameInput, x:xList.toString(), y:yList.toString(), z:zList.toString()};
  console.log(xyzQuery);
  var myObject = JSON.stringify(xyzQuery);

  $.ajax({
      type: 'POST',
      url: '/zv/selectXYZ',
      data: myObject,
      contentType: 'application/json; charset=utf-8',
	  success: function (data) { console.log('called1');
      $.ajax({
          url : '/zv/fileUpload',
          type: 'POST',
          data: formData,
          processData: false,
          contentType: false,
          success: function (data) {console.log('called2');
              $('#dataset-form-control').append($("<option></option>")
                            .attr("value", formData.get("datasetName"))
                            .text( formData.get("datasetName")));
              document.getElementById("loadingEclipse_upload").style.display = "none";
              document.getElementById("submitButton").style.display = "block";
          },
          error: function (jXHR, textStatus, errorThrown) {
              alert(errorThrown);
              document.getElementById("loadingEclipse_upload").style.display = "none";
              document.getElementById("submitButton").style.display = "block";
          }
      });
      alert("Upload successful");
      },
      error: function (jXHR, textStatus, errorThrown) {

      }
  });


});

$("#pattern-upload").on('submit', function(e) {
    $('#pattern-upload').modal('toggle');
});

$("#class-creation").on('submit', function(e) {
    $('#class-creation').modal('toggle');
});
