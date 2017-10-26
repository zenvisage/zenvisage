var formData;
var datasetNameInput;
$('#uploaderForm').on('submit', function(e) {
    e.preventDefault();
    formData = new FormData(this);
    if (formData.get("csv").name == "" ) {
      alert("Please select corresponding files!");
      return;
    }
    else if (formData.get("csv").size > 100000000) {
      alert("Do not upload files over 100MB");
      return;
    }
    parseCSV(formData);
    datasetNameInput = $("#datasetNameInput").val();
    console.log('test:',$(this).attr('action'),$(this).attr('method'));
    log.info("dataset upload: ",$("#datasetNameInput").val())
    $('#uploaderModal').modal('toggle');
});

// function getCheckedAttributes(){
//   $("input:checkbox[name=type]:checked").each(function(){
//       console.log($(this).val());
// }

$("#define-attributes").on('submit', function(e) {
     var attributeList =  [];
     var selectedAxis =  [];
  // var xList = [];
  // var yList = [];
  // var zList = [];

  $(".types").each(function(){
      var selectedXOption = $(this).children("option").filter(":selected").text()
      attributeList.push($(this).val() + " " + selectedXOption);
      selectedAxis.push(["true", "true", "true"])
      // var selectedYOption = $(this).children("option").filter(":selected").text()
      // yList.push($(this).val() + " " + selectedYOption);
      // var selectedZOption = $(this).children("option").filter(":selected").text()
      // zList.push($(this).val() + " " + selectedZOption);
  });

  var selectedAttributes = filterUncheckAttributes(attributeList,selectedAxis)
  var selectedAttributesParsed = []
  for (i = 0; i < selectedAttributes.length; i++) {
      temp = selectedAttributes[i].split(" ");
       selectedAttributesParsed.push({name:temp[0],type:temp[1],selectedX:temp[2],selectedY:temp[3],selectedZ:temp[4]})

  }

  $('#define-attributes').modal('toggle');

  var xyzQuery = {datasetName:datasetNameInput, variables:selectedAttributesParsed};
  console.log(xyzQuery);
  var myObject = JSON.stringify(xyzQuery);

  $.ajax({
      type: 'POST',
      url: '/zv/selectXYZ',
      data: myObject,
      contentType: 'application/json; charset=utf-8',
	  success: function (data) {
      $.ajax({
          url : '/zv/fileUpload',
          type: 'POST',
          data: formData,
          processData: false,
          contentType: false,
          success: function (data) {
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
        alert(errorThrown);
        document.getElementById("loadingEclipse_upload").style.display = "none";
        document.getElementById("submitButton").style.display = "block";

      }
  });


});

$("#pattern-upload").on('submit', function(e) {
    $('#pattern-upload').modal('toggle');
});

$("#class-creation").on('submit', function(e) {
    $('#class-creation').modal('toggle');
});
