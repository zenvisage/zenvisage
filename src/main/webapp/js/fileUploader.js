$('#uploaderForm').on('submit', function(e) {
    document.getElementById("loadingEclipse_upload").style.display = "block";
    document.getElementById("submitButton").style.display = "none";
    e.preventDefault();
    var formData = new FormData(this);
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
    $.ajax({
        url : $(this).attr('action'),
        type: $(this).attr('method'),
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
            parseCSV(formData);
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

  $(".x-types").each(function(){
    if($(this).val() != "none"){
      xList.push($(this).val());
        console.log("x selected: ", $(this).val());
    }

  });

  $(".y-types").each(function(){
    if($(this).val() != "none"){
	     yList.push($(this).val());
        console.log("y selected: ", $(this).val());
      }
  });

  $(".z-types").each(function(){
    if($(this).val() != "none"){
	     zList.push($(this).val());
        console.log("z selected: ", $(this).val());
      }
  });

  $('#define-attributes').modal('toggle');


  var xyzQuery = {x:xList.toString(), y:yList.toString(), z:zList.toString()};
  console.log(xyzQuery);
  var myObject = JSON.stringify(xyzQuery);

  $.ajax({
      type: 'POST',
      url: '/zv/selectXYZ',
      data: myObject,
      contentType: 'application/json; charset=utf-8',
	  success: function (data) {

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
