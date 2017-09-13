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

  $("input[name='x-attributes']:checked").each(function(){
      console.log("x checked: ", $(this).val());
  });

  $("input[name='y-attributes']:checked").each(function(){
      console.log("y checked: ", $(this).val());
  });

  $("input[name='z-attributes']:checked").each(function(){
      console.log("z checked: ", $(this).val());
  });

  $('#define-attributes').modal('toggle');
});

$("#pattern-upload").on('submit', function(e) {
    $('#pattern-upload').modal('toggle');
});

$("#class-creation").on('submit', function(e) {
    $('#class-creation').modal('toggle');
});
