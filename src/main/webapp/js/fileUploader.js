var datasetNameInput;
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
    datasetNameInput = $("#datasetNameInput").val();
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
  var datasetName = datasetNameInput;
  var xList = [];
  var yList = [];
  var zList = [];
 
  $(".x-types").each(function(){
      var selectedXOption = $(this).children("option").filter(":selected").text()
      console.log("test: ", selectedXOption);
      xList.push($(this).val() + " " + selectedXOption);

  });

  $(".y-types").each(function(){
       var selectedYOption = $(this).children("option").filter(":selected").text()
	     yList.push($(this).val() + " " + selectedYOption);

  });

  $(".z-types").each(function(){
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


  var xyzQuery = {datasetName:datasetNameInput, x:xList.toString(), y:yList.toString(), z:zList.toString()};
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

function filterUncheckAttributes(x,y,z){

  $("input:checkbox[name=x-checkbox]:not(:checked)").each(function(){
    if(x.includes($(this).val() + " " + "float")){
      var indexFloat = x.indexOf($(this).val() + " " + "float");
      x.splice(indexFloat, 1);
    }
    if(x.includes($(this).val() + " " + "int")){
      var indexFloat = x.indexOf($(this).val() + " " + "int");
      x.splice(indexFloat, 1);
    }
    if(x.includes($(this).val() + " " + "string")){
      var indexFloat = x.indexOf($(this).val() + " " + "string");
      x.splice(indexFloat, 1);
    }
        //  console.log("testx",x);
    })
  $("input:checkbox[name=y-checkbox]:not(:checked)").each(function(){
    if(y.includes($(this).val() + " " + "float")){
      var indexFloat = y.indexOf($(this).val() + " " + "float");
      y.splice(indexFloat, 1);
    }
    if(y.includes($(this).val() + " " + "int")){
      var indexFloat = y.indexOf($(this).val() + " " + "int");
      y.splice(indexFloat, 1);
    }
    if(y.includes($(this).val() + " " + "string")){
      var indexFloat = y.indexOf($(this).val() + " " + "string");
      y.splice(indexFloat, 1);
    }
  })
  $("input:checkbox[name=z-checkbox]:not(:checked)").each(function(){
    if(z.includes($(this).val() + " " + "float")){
      var indexFloat = z.indexOf($(this).val() + " " + "float");
      z.splice(indexFloat, 1);
    }
    if(z.includes($(this).val() + " " + "int")){
      var indexFloat = z.indexOf($(this).val() + " " + "int");
      z.splice(indexFloat, 1);
    }
    if(z.includes($(this).val() + " " + "string")){
      var indexFloat = z.indexOf($(this).val() + " " + "string");
      z.splice(indexFloat, 1);
    }
  })

    return {
        x: x,
        y: y,
        z: z
    };
};
